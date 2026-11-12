import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestesRegressao {
    private static int cenarios;
    private static int verificacoes;
    private static int combates;
    private static Path resultados;

    private static void verificar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static void testar(String nome, Runnable teste) {
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            teste.run();
        } finally {
            System.setOut(original);
        }
        cenarios++;
        System.out.println("OK - " + nome);
    }

    private static int contar(String texto, String trecho) {
        int quantidade = 0;
        int posicao = 0;
        while ((posicao = texto.indexOf(trecho, posicao)) >= 0) {
            quantidade++;
            posicao += trecho.length();
        }
        return quantidade;
    }

    private static String jogar(String nome, String classe, String... opcoes) {
        String entrada = nome + "\n" + classe + "\n" + String.join("\n", opcoes) + "\n";
        InputStream entradaOriginal = System.in;
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream captura = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(captura, true, StandardCharsets.UTF_8));
            Main.main(new String[0]);
        } finally {
            System.setIn(entradaOriginal);
            System.setOut(saidaOriginal);
        }
        String saida = captura.toString(StandardCharsets.UTF_8);
        try {
            Files.writeString(resultados.resolve("partida-" + (++combates) + ".txt"),
                    "ENTRADA:\n" + entrada + "\nSAIDA:\n" + saida, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return saida;
    }

    private static void conferirCombate(String saida, boolean venceu, int respostas) {
        verificar(saida.contains(venceu ? "Parabens! Voce venceu o Goblin!"
                : "Voce foi derrotado pelo Goblin!"), "Resultado do combate incorreto");
        verificar(!saida.contains(venceu ? "Voce foi derrotado" : "Parabens!"),
                "Resultados contraditorios");
        verificar(contar(saida, "Goblin atacou ") == respostas,
                "Quantidade incorreta de turnos do Goblin");
    }

    // Substitui teclado e terminal apenas nos testes, usando a mesma interface do jogo.
    private static class EntradaSaidaSimulada implements EntradaSaida {
        private final java.util.Queue<String> entradas = new java.util.ArrayDeque<>();
        private final StringBuilder saida = new StringBuilder();

        EntradaSaidaSimulada(String... opcoes) {
            for (String opcao : opcoes) entradas.add(opcao);
        }

        @Override
        public String lerLinha(String mensagem) {
            saida.append(mensagem);
            if (entradas.isEmpty()) throw new AssertionError("O combate pediu uma entrada inesperada");
            return entradas.remove();
        }

        @Override
        public void mostrarMensagem(String mensagem) {
            saida.append(mensagem).append(System.lineSeparator());
        }
    }
    public static void main(String[] args) throws Exception {
        resultados = args.length > 0 ? Path.of(args[0]) : Files.createTempDirectory("rpg-testes-");
        testar("Construtores e valores originais preservados", () -> {
            Personagem base = new Personagem("Base", 20, 4);
            Guerreiro guerreiro = new Guerreiro("Teste");
            Mago mago = new Mago("Teste");
            Inimigo goblin = new Inimigo("Goblin");
            verificar(base.getDefesa() == 0 && base.getVidaMaxima() == 20,
                    "Construtor antigo incompativel");
            verificar(guerreiro.getVida() == 40 && guerreiro.getAtaque() == 8
                    && guerreiro.getDefesa() == 2, "Guerreiro incorreto");
            verificar(mago.getVida() == 30 && mago.getAtaque() == 6
                    && mago.getDefesa() == 1, "Mago incorreto");
            verificar(goblin.getVida() == 49 && goblin.getAtaque() == 7
                    && goblin.getDefesa() == 2, "Goblin incorreto");
        });
        testar("Defesa, ataques e limites de dano", () -> {
            Guerreiro guerreiro = new Guerreiro("Teste");
            Mago mago = new Mago("Teste");
            Inimigo goblin = new Inimigo("Goblin");
            guerreiro.atacar(goblin);
            verificar(goblin.getVida() == 43, "Ataque do guerreiro ignora defesa");
            mago.atacar(goblin);
            verificar(goblin.getVida() == 39, "Ataque do mago ignora defesa");
            goblin.atacar(guerreiro);
            goblin.atacar(mago);
            verificar(guerreiro.getVida() == 35, "Defesa do guerreiro incorreta");
            verificar(mago.getVida() == 24, "Defesa do mago incorreta");
            guerreiro.receberDano(1);
            guerreiro.receberDano(2);
            guerreiro.receberDano(-5);
            verificar(guerreiro.getVida() == 35, "Dano bloqueado alterou a vida");
            guerreiro.receberDano(100);
            verificar(guerreiro.getVida() == 0 && !guerreiro.estaVivo(), "Vida negativa ou morte incorreta");
        });
        testar("Golpe Brutal preservado com defesa", () -> {
            Inimigo goblin = new Inimigo("Goblin");
            verificar(new Guerreiro("Teste").usarHabilidade(goblin).foiRealizada(), "Habilidade falhou");
            verificar(goblin.getVida() == 38, "Dano do Golpe Brutal incorreto");
        });
        testar("Bola de Fogo, mana e tentativa sem mana", () -> {
            Mago mago = new Mago("Teste");
            Inimigo goblin = new Inimigo("Goblin");
            for (int i = 0; i < 3; i++) verificar(mago.usarHabilidade(goblin).foiRealizada(), "Mana insuficiente antes da hora");
            verificar(goblin.getVida() == 7, "Dano da Bola de Fogo incorreto");
            verificar(!mago.usarHabilidade(goblin).foiRealizada(), "Habilidade funcionou sem mana");
            verificar(goblin.getVida() == 7, "Tentativa sem mana causou dano");
        });
        for (int classe = 1; classe <= 2; classe++) {
            final int tipo = classe;
            testar("Cura maxima e consumo unico - " + (tipo == 1 ? "Guerreiro" : "Mago"), () -> {
                Personagem heroi = tipo == 1 ? new Guerreiro("Teste") : new Mago("Teste");
                heroi.adicionarItem(new PocaoCura());
                heroi.receberDano(heroi.getDefesa() + 6);
                verificar(heroi.usarItem(1).foiRealizada(), "Pocao nao foi usada");
                verificar(heroi.getVida() == heroi.getVidaMaxima(), "Cura ultrapassou ou nao alcancou vida maxima");
                verificar(heroi.getQuantidadeItens() == 0, "Pocao nao foi removida");
                heroi.receberDano(heroi.getDefesa() + 20);
                int vidaAntes = heroi.getVida();
                verificar(!heroi.usarItem(1).foiRealizada(), "Pocao consumida foi reutilizada");
                verificar(heroi.getVida() == vidaAntes, "Item consumido curou novamente");
                heroi.adicionarItem(new PocaoCura());
                verificar(heroi.usarItem(1).foiRealizada(), "Segunda pocao nao foi usada");
                verificar(heroi.getVida() == vidaAntes + 15, "Pocao nao curou 15");
                heroi.curar(-5);
                verificar(heroi.getVida() == vidaAntes + 15, "Cura negativa retirou vida");
            });
        }
        testar("Selecao de itens, varios itens e inventarios independentes", () -> {
            Guerreiro heroi = new Guerreiro("Teste");
            Mago outro = new Mago("Outro");
            heroi.adicionarItem(new PocaoCura());
            heroi.adicionarItem(new PocaoCura());
            verificar(!heroi.usarItem(0).foiRealizada() && !heroi.usarItem(-1).foiRealizada() && !heroi.usarItem(3).foiRealizada(), "Indice invalido aceito");
            verificar(heroi.getQuantidadeItens() == 2, "Item invalido consumiu inventario");
            verificar(outro.getQuantidadeItens() == 0, "Inventario compartilhado entre personagens");
            verificar(heroi.usarItem(2).foiRealizada() && heroi.getQuantidadeItens() == 1, "Selecao do segundo item incorreta");
            verificar(heroi.usarItem(1).foiRealizada() && heroi.getQuantidadeItens() == 0, "Item restante inacessivel");
            verificar(heroi.getVida() == 40, "Pocao com vida cheia ultrapassou maximo");
        });
        testar("Guerreiro vence com Golpe Brutal", () -> {
            String saida = jogar("Teste", "1", "2", "2", "2", "2", "2");
            conferirCombate(saida, true, 4);
            verificar(contar(saida, "usou Golpe Brutal!") == 5, "Numero de habilidades incorreto");
        });
        testar("Mago vence com habilidades e ataques", () -> {
            String saida = jogar("Teste", "2", "2", "2", "2", "1", "1");
            conferirCombate(saida, true, 4);
            verificar(contar(saida, "usou Bola de Fogo!") == 3, "Numero de habilidades incorreto");
            verificar(contar(saida, "Teste atacou Goblin!") == 2, "Numero de ataques incorreto");
        });
        testar("Guerreiro perde usando apenas ataques normais", () -> {
            String saida = jogar("Teste", "1", "1", "1", "1", "1", "1", "1", "1", "1");
            conferirCombate(saida, false, 8);
            verificar(contar(saida, "Teste atacou Goblin!") == 8, "Numero de ataques incorreto");
        });
        testar("Mago perde usando apenas ataques normais", () -> {
            String saida = jogar("Teste", "2", "1", "1", "1", "1", "1");
            conferirCombate(saida, false, 5);
        });
        testar("Guerreiro usa pocao, limita cura e nao reutiliza item", () -> {
            String saida = jogar("Teste", "1", "1", "2", "4", "1", "4", "3", "2", "2", "2");
            conferirCombate(saida, true, 5);
            verificar(saida.contains("recuperou 10 de vida."), "Cura parcial do guerreiro incorreta");
            verificar(saida.contains("Teste - Vida: 35/40 | Defesa: 2"), "Status apos pocao incorreto");
            verificar(saida.contains("Inventario vazio."), "Inventario nao ficou vazio");
            verificar(contar(saida, "usou Pocao de cura!") == 1, "Pocao usada mais de uma vez");
        });
        testar("Mago usa pocao, nao reutiliza item e respeita falta de mana", () -> {
            String saida = jogar("Teste", "2", "1", "2", "4", "1", "4", "3", "2", "2", "2", "3", "1");
            conferirCombate(saida, true, 5);
            verificar(saida.contains("recuperou 12 de vida."), "Cura parcial do mago incorreta");
            verificar(saida.contains("Teste - Vida: 24/30 | Defesa: 1"), "Status apos pocao incorreto");
            verificar(saida.contains("Inventario vazio."), "Inventario nao ficou vazio");
            verificar(saida.contains("Mana insuficiente!") && saida.contains("Mana: 0"), "Mana nao preservada");
            verificar(contar(saida, "usou Pocao de cura!") == 1, "Pocao usada mais de uma vez");
        });
        testar("Pocao permite ao Guerreiro vencer usando ataques normais", () -> {
            String saida = jogar("Teste", "1", "1", "1", "1", "4", "1", "1", "1", "1", "1", "1", "1");
            conferirCombate(saida, true, 9);
            verificar(saida.contains("recuperou 15 de vida."), "Cura completa do guerreiro incorreta");
            verificar(contar(saida, "Teste atacou Goblin!") == 9, "Numero de ataques incorreto");
        });
        testar("Mago vence apos cura de 15 e combina ataques e habilidades", () -> {
            String saida = jogar("Teste", "2", "1", "1", "1", "4", "1", "2", "2", "2");
            conferirCombate(saida, true, 6);
            verificar(saida.contains("recuperou 15 de vida."), "Cura completa do mago incorreta");
        });
        for (int classe = 1; classe <= 2; classe++) {
            final String tipo = String.valueOf(classe);
            testar("Status, cancelamento e entradas invalidas nao gastam turno - classe " + tipo, () -> {
                String[] fim = tipo.equals("1") ? new String[]{"2", "2", "2", "2", "2"}
                        : new String[]{"2", "2", "2", "1", "1"};
                String[] inicio = {"3", "x", "4", "0", "4", "9", "4", "-1", "4", "abc", "4", "2147483648", "3"};
                String[] opcoes = new String[inicio.length + fim.length];
                System.arraycopy(inicio, 0, opcoes, 0, inicio.length);
                System.arraycopy(fim, 0, opcoes, inicio.length, fim.length);
                String saida = jogar("Teste", tipo, opcoes);
                conferirCombate(saida, true, 4);
                verificar(contar(saida, "Item invalido.") == 4, "Validacao de itens incorreta");
                verificar(saida.contains("Opcao invalida."), "Validacao do menu perdida");
                String status = tipo.equals("1") ? "Teste - Vida: 40/40" : "Teste - Vida: 30/30";
                verificar(contar(saida, status) == 2, "Consulta ou erro gastou turno");
                verificar(!saida.contains("usou Pocao de cura!"), "Consulta consumiu pocao");
            });
        }
        testar("Pocao com vida cheia e consumida e gasta turno", () -> {
            String saida = jogar("Teste", "1", "4", "1", "4", "3", "2", "2", "2", "2", "2");
            conferirCombate(saida, true, 5);
            verificar(saida.contains("recuperou 0 de vida."), "Cura com vida cheia incorreta");
            verificar(saida.contains("Inventario vazio."), "Item cheio nao consumido");
            verificar(saida.contains("Teste - Vida: 35/40"), "Pocao nao gastou turno");
        });
        testar("Classe invalida preservada", () -> {
            String saida = jogar("Teste", "9");
            verificar(saida.contains("Classe invalida. Fim do jogo."), "Classe invalida nao rejeitada");
            verificar(!saida.contains("Um Goblin apareceu!"), "Combate iniciou com classe invalida");
        });
        testar("Nome vazio mantem Heroi", () -> {
            String saida = jogar("   ", "1", "2", "2", "2", "2", "2");
            conferirCombate(saida, true, 4);
            verificar(saida.contains("Heroi usou Golpe Brutal!"), "Nome padrao perdido");
        });
        Personagem[] personagens = {new Guerreiro("Teste"), new Mago("Teste"), new Inimigo("Goblin")};
        int[] danosHabilidade = {13, 16, 0};
        for (int i = 0; i < personagens.length; i++) {
            final Personagem personagem = personagens[i];
            final int danoHabilidade = danosHabilidade[i];
            testar("LSP - contrato de Personagem em " + personagem.getClass().getSimpleName(), () -> {
                Personagem alvo = new Personagem("Alvo", 100, 0);
                int vidaMaxima = personagem.getVidaMaxima();
                verificar(personagem.atacar(alvo).foiRealizada(), "Ataque comum nao foi realizado");
                verificar(alvo.getVida() == 100 - personagem.getAtaque(), "Ataque comum violou contrato");
                int vidaAlvo = alvo.getVida();
                ResultadoAcao habilidade = personagem.usarHabilidade(alvo);
                verificar(habilidade.foiRealizada() == (danoHabilidade > 0), "Contrato de habilidade violado");
                verificar(alvo.getVida() == vidaAlvo - danoHabilidade, "Habilidade alterou o alvo incorretamente");
                personagem.receberDano(personagem.getDefesa() + 7);
                verificar(personagem.getVida() == vidaMaxima - 7, "Defesa violou contrato");
                verificar(personagem.curar(3) == 3, "Cura violou contrato");
                personagem.adicionarItem(new PocaoCura());
                verificar(personagem.usarItem(1).foiRealizada(), "Uso polimorfico de item falhou");
                verificar(personagem.getVida() == vidaMaxima && personagem.getQuantidadeItens() == 0,
                        "Cura maxima ou consumo violou contrato");
                personagem.receberDano(1000);
                verificar(personagem.getVida() == 0 && !personagem.estaVivo(), "Morte violou contrato");
            });
        }
        for (int classe = 1; classe <= 2; classe++) {
            final int tipo = classe;
            testar("DIP - combate sem teclado e sem terminal, classe " + tipo, () -> {
                Personagem heroi = tipo == 1 ? new Guerreiro("Teste") : new Mago("Teste");
                Personagem inimigo = new Inimigo("Goblin");
                EntradaSaidaSimulada interfaceTeste = tipo == 1
                        ? new EntradaSaidaSimulada("2", "2", "2", "2", "2")
                        : new EntradaSaidaSimulada("2", "2", "2", "1", "1");
                ByteArrayOutputStream terminal = new ByteArrayOutputStream();
                PrintStream original = System.out;
                try {
                    System.setOut(new PrintStream(terminal));
                    new Combate(heroi, inimigo, interfaceTeste).iniciar();
                } finally {
                    System.setOut(original);
                }
                verificar(terminal.size() == 0, "Combate ou modelo escreveu diretamente no terminal");
                verificar(interfaceTeste.saida.toString().contains("Parabens!"), "Interface substituta nao recebeu resultado");
                verificar(interfaceTeste.entradas.isEmpty(), "Combate nao consumiu as entradas esperadas");
                verificar(inimigo.getVida() == 0, "Inimigo nao foi derrotado");
                verificar(heroi.getVida() == (tipo == 1 ? 20 : 6), "Turnos alterados pela interface substituta");
            });
        }
        testar("OCP e ISP - inventario aceita outra implementacao de Item", () -> {
            final int[] usos = {0};
            Item itemTeste = new Item() {
                @Override
                public String getNome() { return "Item de teste"; }

                @Override
                public ResultadoAcao usar(Personagem personagem) {
                    usos[0]++;
                    return new ResultadoAcao(true, "Efeito de teste.");
                }
            };
            Personagem heroi = new Guerreiro("Teste");
            heroi.adicionarItem(itemTeste);
            verificar(heroi.mostrarInventario().contains("1 - Item de teste"), "Inventario depende de PocaoCura");
            ResultadoAcao resultado = heroi.usarItem(1);
            verificar(resultado.foiRealizada() && resultado.getMensagem().equals("Efeito de teste."),
                    "Implementacao substituta nao foi utilizada");
            verificar(usos[0] == 1 && heroi.getQuantidadeItens() == 0, "Consumo do item substituto incorreto");
            verificar(!heroi.usarItem(1).foiRealizada() && usos[0] == 1, "Item substituto reutilizado");
        });
        testar("Uso recusado preserva item, personagem e turno", () -> {
            Item itemTeste = new Item() {
                @Override
                public String getNome() { return "Item indisponivel de teste"; }

                @Override
                public ResultadoAcao usar(Personagem personagem) {
                    return new ResultadoAcao(false, "Uso recusado.");
                }
            };
            Personagem heroi = new Guerreiro("Teste");
            heroi.adicionarItem(itemTeste);
            EntradaSaidaSimulada interfaceTeste = new EntradaSaidaSimulada("4", "1", "2", "2", "2", "2", "2");
            new Combate(heroi, new Inimigo("Goblin"), interfaceTeste).iniciar();
            verificar(heroi.getQuantidadeItens() == 1, "Uso recusado consumiu item");
            verificar(heroi.getVida() == 20, "Uso recusado gastou turno");
            verificar(interfaceTeste.saida.toString().contains("Uso recusado."), "Falha nao foi apresentada");
        });
        if (args.length > 1) {
            testar("Transcricoes das 13 execucoes identicas a etapa anterior", () -> {
                try {
                    for (int i = 1; i <= combates; i++) {
                        String arquivo = "partida-" + i + ".txt";
                        String antes = Files.readString(Path.of(args[1]).resolve(arquivo), StandardCharsets.UTF_8);
                        String depois = Files.readString(resultados.resolve(arquivo), StandardCharsets.UTF_8);
                        verificar(antes.equals(depois), "Transcricao alterada: " + arquivo);
                    }
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        System.out.println("RESULTADO: " + cenarios + " cenarios, " + verificacoes
                + " verificacoes e " + combates + " execucoes do Main aprovados.");
        System.out.println("Evidencias: " + resultados);
    }
}
