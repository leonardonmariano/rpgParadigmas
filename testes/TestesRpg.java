import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import rpg.apresentacao.EntradaSaida;
import rpg.app.Main;
import rpg.dominio.ComHabilidadeEspecial;
import rpg.dominio.Destrutivel;
import rpg.dominio.cenario.Bau;
import rpg.dominio.itens.Arma;
import rpg.dominio.itens.Equipavel;
import rpg.dominio.itens.PocaoCura;
import rpg.dominio.itens.Usavel;
import rpg.dominio.personagens.Arqueiro;
import rpg.dominio.personagens.Chefao;
import rpg.dominio.personagens.Guerreiro;
import rpg.dominio.personagens.Inimigo;
import rpg.dominio.personagens.Mago;
import rpg.dominio.personagens.Personagem;
import rpg.jogo.Combate;
import rpg.jogo.FabricaDePersonagens;

/**
 * Testes automaticos usando apenas o Java (sem JUnit).
 * Uma verificacao incorreta lanca AssertionError e encerra com falha.
 */
public class TestesRpg {
    private static int cenarios;
    private static int verificacoes;

    private static void verificar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static void testar(String nome, Runnable teste) {
        teste.run();
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

    /** Substitui o teclado e o terminal usando a mesma interface do jogo (prova do DIP). */
    private static class EntradaSaidaSimulada implements EntradaSaida {
        private final Queue<String> entradas = new ArrayDeque<>();
        private final StringBuilder saida = new StringBuilder();

        EntradaSaidaSimulada(String... opcoes) {
            entradas.addAll(List.of(opcoes));
        }

        @Override
        public String lerLinha(String mensagem) {
            saida.append(mensagem);
            if (entradas.isEmpty()) {
                throw new AssertionError("O jogo pediu uma entrada inesperada");
            }
            return entradas.remove();
        }

        @Override
        public void mostrarMensagem(String mensagem) {
            saida.append(mensagem).append(System.lineSeparator());
        }

        String getSaida() {
            return saida.toString();
        }
    }

    /**
     * Jogador automatico simples: cura com pouca vida, equipa armas melhores,
     * usa a habilidade especial e, se ela falhar, ataca normalmente.
     */
    private static class JogadorAutomatico implements EntradaSaida {
        private final Personagem heroi;
        private final StringBuilder saida = new StringBuilder();
        private boolean habilidadeFalhou = false;
        private String itemEscolhido = "0";

        JogadorAutomatico(Personagem heroi) {
            this.heroi = heroi;
        }

        @Override
        public void mostrarMensagem(String mensagem) {
            saida.append(mensagem).append(System.lineSeparator());
            if (mensagem.contains("Escolha outra acao")) {
                habilidadeFalhou = true;
            }
        }

        @Override
        public String lerLinha(String mensagem) {
            saida.append(mensagem).append(System.lineSeparator());
            if (mensagem.startsWith("1 - Golpear")) {
                return "1";
            }
            if (mensagem.startsWith("Escolha o item")) {
                return itemEscolhido;
            }
            String inventario = heroi.mostrarInventario();
            int pocao = posicaoDoItem(inventario, "Pocao de cura");
            if (heroi.getVida() * 100 / heroi.getVidaMaxima() <= 40 && pocao > 0) {
                itemEscolhido = String.valueOf(pocao);
                return "4";
            }
            int arma = posicaoDeArmaMelhor(inventario);
            if (arma > 0) {
                itemEscolhido = String.valueOf(arma);
                return "4";
            }
            if (habilidadeFalhou) {
                habilidadeFalhou = false;
                return "1";
            }
            return "2";
        }

        private int posicaoDoItem(String inventario, String nome) {
            String[] linhas = inventario.split("\\R");
            for (int i = 1; i < linhas.length; i++) {
                if (linhas[i].contains(nome)) {
                    return i;
                }
            }
            return 0;
        }

        private int posicaoDeArmaMelhor(String inventario) {
            int bonusAtual = heroi.getArmaEquipada() == null ? 0 : heroi.getArmaEquipada().getBonusAtaque();
            String[] linhas = inventario.split("\\R");
            for (int i = 1; i < linhas.length; i++) {
                int inicio = linhas[i].indexOf("(+");
                if (inicio >= 0) {
                    int bonus = Integer.parseInt(linhas[i].substring(inicio + 2, linhas[i].indexOf(' ', inicio)));
                    if (bonus > bonusAtual) {
                        return i;
                    }
                }
            }
            return 0;
        }

        String getSaida() {
            return saida.toString();
        }
    }

    private static String executarMain(String entrada) {
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
        return captura.toString(StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        testar("Atributos iniciais das classes", () -> {
            Guerreiro guerreiro = new Guerreiro("G");
            Mago mago = new Mago("M");
            Arqueiro arqueiro = new Arqueiro("A");
            verificar(guerreiro.getVida() == 40 && guerreiro.getAtaqueTotal() == 8 && guerreiro.getDefesa() == 2, "Guerreiro");
            verificar(mago.getVida() == 30 && mago.getAtaqueTotal() == 6 && mago.getDefesa() == 1 && mago.getMana() == 20, "Mago");
            verificar(arqueiro.getVida() == 34 && arqueiro.getAtaqueTotal() == 7 && arqueiro.getFlechasEspeciais() == 3, "Arqueiro");
        });

        testar("Defesa reduz o dano e a vida nunca fica negativa", () -> {
            Guerreiro guerreiro = new Guerreiro("G");
            Inimigo goblin = new Inimigo("Goblin", 24, 4, 1);
            guerreiro.atacar(goblin);
            verificar(goblin.getVida() == 17, "8 de ataque - 1 de defesa = 7");
            verificar(guerreiro.receberDano(1) == 0 && guerreiro.getVida() == 40, "Dano menor que a defesa");
            guerreiro.receberDano(500);
            verificar(guerreiro.getVida() == 0 && guerreiro.estaDestruido() && !guerreiro.estaVivo(), "Morte");
        });

        testar("Golpe Brutal = ataque + 5", () -> {
            Inimigo alvo = new Inimigo("Alvo", 50, 1, 2);
            verificar(new Guerreiro("G").usarHabilidade(alvo).foiRealizada(), "Habilidade falhou");
            verificar(alvo.getVida() == 39, "13 - 2 = 11 de dano");
        });

        testar("Bola de Fogo gasta mana e falha sem mana, sem causar dano", () -> {
            Mago mago = new Mago("M");
            Inimigo alvo = new Inimigo("Alvo", 200, 1, 1);
            for (int i = 0; i < 4; i++) {
                verificar(mago.usarHabilidade(alvo).foiRealizada(), "Mana acabou cedo");
            }
            verificar(alvo.getVida() == 200 - 4 * 15 && mago.getMana() == 0, "Dano/mana");
            verificar(!mago.usarHabilidade(alvo).foiRealizada(), "Funcionou sem mana");
            verificar(alvo.getVida() == 140, "Tentativa sem mana causou dano");
        });

        testar("Disparo Duplo acerta duas vezes e gasta flechas", () -> {
            Arqueiro arqueiro = new Arqueiro("A");
            Inimigo alvo = new Inimigo("Alvo", 100, 1, 2);
            verificar(arqueiro.usarHabilidade(alvo).foiRealizada(), "Disparo falhou");
            verificar(alvo.getVida() == 86 && arqueiro.getFlechasEspeciais() == 2, "Duas flechas de (7+2)-2 = 7");
            arqueiro.usarHabilidade(alvo);
            arqueiro.usarHabilidade(alvo);
            verificar(!arqueiro.usarHabilidade(alvo).foiRealizada(), "Disparou sem flechas");
            Inimigo fraco = new Inimigo("Fraco", 3, 1, 0);
            Arqueiro outro = new Arqueiro("B");
            String mensagem = outro.usarHabilidade(fraco).getMensagem();
            verificar(!mensagem.contains("Segunda flecha"), "Segunda flecha em alvo ja derrotado");
        });

        testar("Arma: equipar, trocar (agregacao) e soltar", () -> {
            Guerreiro heroi = new Guerreiro("G");
            Arma adaga = new Arma("Adaga", 2);
            Arma espada = new Arma("Espada", 3);
            heroi.adicionarItem(adaga);
            heroi.adicionarItem(espada);
            verificar(heroi.usarItem(1).foiRealizada() && heroi.getAtaqueTotal() == 10, "Adaga +2");
            verificar(heroi.getQuantidadeItens() == 1, "Adaga saiu do inventario");
            verificar(heroi.usarItem(1).foiRealizada() && heroi.getAtaqueTotal() == 11, "Espada +3");
            verificar(heroi.mostrarInventario().contains("Adaga"), "Arma antiga voltou ao inventario");
            verificar(heroi.desequiparArma() == espada && heroi.getAtaqueTotal() == 8, "Soltar arma");
        });

        testar("Pocao: cura ate o maximo, e consumida e recusa vida cheia", () -> {
            Mago mago = new Mago("M");
            mago.adicionarItem(new PocaoCura());
            verificar(!mago.usarItem(1).foiRealizada() && mago.getQuantidadeItens() == 1, "Vida cheia nao gasta pocao");
            mago.receberDano(7);
            verificar(mago.usarItem(1).foiRealizada() && mago.getVida() == 30, "Cura limitada ao maximo");
            verificar(mago.getQuantidadeItens() == 0, "Pocao consumida");
            verificar(!mago.usarItem(1).foiRealizada() && !mago.usarItem(0).foiRealizada(), "Indice invalido");
        });

        testar("ISP: pocao nao e equipavel e arma nao e consumivel", () -> {
            verificar(!(new PocaoCura() instanceof Equipavel) && new PocaoCura() instanceof Usavel, "Pocao");
            Object arma = new Arma("Arma", 1);
            verificar(!(arma instanceof Usavel) && arma instanceof Equipavel, "Arma");
            verificar(!(new Inimigo("I", 1, 1, 0) instanceof ComHabilidadeEspecial), "Inimigo comum sem habilidade");
        });

        testar("DIP: o mesmo atacar() funciona contra um Bau", () -> {
            Bau bau = new Bau("Bau", 10, List.of(new PocaoCura()));
            Destrutivel alvo = bau;
            Guerreiro heroi = new Guerreiro("G");
            heroi.atacar(alvo);
            verificar(bau.getResistencia() == 2 && bau.retirarItens().isEmpty(), "Bau inteiro nao entrega itens");
            heroi.atacar(alvo);
            verificar(bau.estaDestruido() && bau.retirarItens().size() == 1, "Itens sobrevivem ao bau");
        });

        testar("Descanso polimorfico: vida, mana e flechas", () -> {
            Mago mago = new Mago("M");
            mago.receberDano(21);
            mago.usarHabilidade(new Inimigo("I", 100, 1, 0));
            mago.descansar();
            verificar(mago.getVida() == 25 && mago.getMana() == 20, "Mago descansou");
            Arqueiro arqueiro = new Arqueiro("A");
            arqueiro.usarHabilidade(new Inimigo("I", 100, 1, 0));
            arqueiro.descansar();
            verificar(arqueiro.getFlechasEspeciais() == 3, "Arqueiro recuperou flechas");
        });

        testar("LSP: qualquer Personagem funciona no lugar do tipo base", () -> {
            List<Personagem> todos = List.of(new Guerreiro("G"), new Mago("M"), new Arqueiro("A"),
                    new Inimigo("I", 20, 5, 1), new Chefao("C", 40, 6, 2, "Esmagar", 4));
            for (Personagem p : todos) {
                Inimigo alvo = new Inimigo("Alvo", 100, 1, 0);
                verificar(p.atacar(alvo).foiRealizada() && alvo.getVida() < 100, "Ataque " + p.getTipo());
                p.receberDano(5 + p.getDefesa());
                verificar(p.getVida() == p.getVidaMaxima() - 5, "Dano " + p.getTipo());
                verificar(p.descansar().foiRealizada() && p.getVida() == p.getVidaMaxima(), "Descanso " + p.getTipo());
                verificar(p.mostrarStatus().contains(p.getNome()), "Status " + p.getTipo());
            }
        });

        testar("Fabrica cria a classe escolhida", () -> {
            FabricaDePersonagens fabrica = Main.criarFabrica();
            verificar(fabrica.criar("1", "X") instanceof Guerreiro, "1");
            verificar(fabrica.criar("2", "X") instanceof Mago, "2");
            verificar(fabrica.criar("3", "X") instanceof Arqueiro, "3");
            verificar(!fabrica.possuiOpcao("9"), "Opcao inexistente");
        });

        testar("Combate: status, item cancelado e opcao invalida nao gastam turno", () -> {
            Guerreiro heroi = new Guerreiro("Teste");
            heroi.adicionarItem(new PocaoCura());
            Inimigo alvo = new Inimigo("Boneco", 10, 3, 0);
            EntradaSaidaSimulada io = new EntradaSaidaSimulada("3", "x", "4", "0", "4", "abc", "4", "7", "2");
            verificar(new Combate(heroi, alvo, io).iniciar(), "Heroi deveria vencer");
            verificar(!io.getSaida().contains("Boneco atacou"), "Inimigo agiu em turno nao gasto");
            verificar(contar(io.getSaida(), "Item invalido") == 2, "Validacao de itens");
            verificar(io.getSaida().contains("Opcao invalida"), "Validacao do menu");
        });

        testar("Chefao usa a habilidade a cada 3 turnos", () -> {
            Guerreiro heroi = new Guerreiro("Teste");
            Chefao chefao = new Chefao("Chefe", 60, 4, 0, "Esmagar", 6);
            EntradaSaidaSimulada io = new EntradaSaidaSimulada("1", "1", "1", "1", "1", "1", "1", "1");
            verificar(new Combate(heroi, chefao, io).iniciar(), "Heroi deveria vencer");
            verificar(contar(io.getSaida(), "usou Esmagar") == 2, "7 turnos do chefe = 2 habilidades");
            verificar(contar(io.getSaida(), "Chefe atacou") == 5, "Demais turnos sao ataques normais");
        });

        String[] classes = {"Guerreiro", "Mago", "Arqueiro"};
        for (int i = 0; i < classes.length; i++) {
            final String codigo = String.valueOf(i + 1);
            final String classe = classes[i];
            testar("Masmorra completa jogando bem - " + classe, () -> {
                Personagem heroi = Main.criarFabrica().criar(codigo, "Teste");
                heroi.adicionarItem(new PocaoCura());
                JogadorAutomatico jogador = new JogadorAutomatico(heroi);
                boolean venceu = Main.criarMasmorra().explorar(heroi, jogador);
                String saida = jogador.getSaida();
                verificar(venceu, classe + " deveria conseguir vencer jogando bem:\n" + saida);
                verificar(saida.contains("Rei Goblin usou Golpe Esmagador"), "Chefao usou habilidade");
                verificar(saida.contains("deixou cair Adaga Enferrujada"), "Espolio do Goblin");
                verificar(saida.contains("Bau Antigo se partiu"), "Bau quebrado");
                System.out.println("     vida final do " + classe + ": " + heroi.getVida() + "/" + heroi.getVidaMaxima());
            });
        }

        testar("Masmorra: so ataques normais e sem itens leva a derrota", () -> {
            for (String codigo : new String[]{"1", "2", "3"}) {
                Personagem heroi = Main.criarFabrica().criar(codigo, "Teste");
                EntradaSaida io = new EntradaSaida() {
                    @Override
                    public String lerLinha(String mensagem) {
                        return "1";
                    }

                    @Override
                    public void mostrarMensagem(String mensagem) {
                    }
                };
                verificar(!Main.criarMasmorra().explorar(heroi, io), "Chefao facil demais para " + heroi.getTipo());
            }
        });

        testar("Main: nome vazio, classe invalida e fim de entrada", () -> {
            String saida = executarMain("\n9\n2\n");
            verificar(saida.contains("Heroi [Mago]"), "Nome padrao e classe escolhida");
            verificar(saida.contains("Classe invalida"), "Classe invalida pede de novo");
            verificar(saida.contains("Entrada encerrada. Fim do jogo."), "Fim de entrada tratado");
        });

        System.out.println("RESULTADO: " + cenarios + " cenarios e " + verificacoes + " verificacoes aprovados.");
    }
}
