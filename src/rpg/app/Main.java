package rpg.app;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import rpg.apresentacao.ConsoleJogo;
import rpg.apresentacao.EntradaSaida;
import rpg.dominio.cenario.Bau;
import rpg.dominio.itens.Arma;
import rpg.dominio.itens.PocaoCura;
import rpg.dominio.personagens.Arqueiro;
import rpg.dominio.personagens.Chefao;
import rpg.dominio.personagens.Guerreiro;
import rpg.dominio.personagens.Inimigo;
import rpg.dominio.personagens.Mago;
import rpg.dominio.personagens.Personagem;
import rpg.jogo.FabricaDePersonagens;
import rpg.jogo.Masmorra;
import rpg.jogo.SalaDeCombate;
import rpg.jogo.SalaDeDescanso;
import rpg.jogo.SalaDoTesouro;

/**
 * Ponto de entrada e "raiz de composicao": o unico lugar que escolhe as classes
 * concretas e as entrega prontas para o restante do jogo (DIP).
 */
public class Main {
    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            EntradaSaida interfaceUsuario = new ConsoleJogo(entrada, System.out);
            try {
                jogar(interfaceUsuario);
            } catch (NoSuchElementException e) {
                interfaceUsuario.mostrarMensagem(System.lineSeparator() + "Entrada encerrada. Fim do jogo.");
            }
        }
    }

    private static void jogar(EntradaSaida interfaceUsuario) {
        interfaceUsuario.mostrarMensagem("=== RPG: Aventura na Masmorra ===");
        String nome = interfaceUsuario.lerLinha("Nome do heroi: ").trim();
        if (nome.isEmpty()) {
            nome = "Heroi";
        }

        Personagem heroi = escolherClasse(interfaceUsuario, criarFabrica(), nome);
        heroi.adicionarItem(new PocaoCura());
        interfaceUsuario.mostrarMensagem("Voce recebeu uma Pocao de cura.");
        interfaceUsuario.mostrarMensagem(heroi.mostrarStatus());

        if (criarMasmorra().explorar(heroi, interfaceUsuario)) {
            interfaceUsuario.mostrarMensagem("");
            interfaceUsuario.mostrarMensagem("*** VITORIA! " + heroi.getNome()
                    + " derrotou o Rei Goblin e libertou a masmorra! ***");
        } else {
            interfaceUsuario.mostrarMensagem("");
            interfaceUsuario.mostrarMensagem("*** GAME OVER ***");
        }
    }

    private static Personagem escolherClasse(EntradaSaida interfaceUsuario,
                                             FabricaDePersonagens fabrica, String nome) {
        while (true) {
            interfaceUsuario.mostrarMensagem(fabrica.listarOpcoes());
            String opcao = interfaceUsuario.lerLinha("Classe: ").trim();
            if (fabrica.possuiOpcao(opcao)) {
                return fabrica.criar(opcao, nome);
            }
            interfaceUsuario.mostrarMensagem("Classe invalida. Tente novamente.");
        }
    }

    /** Para criar uma nova classe jogavel: nova subclasse + uma linha aqui. */
    public static FabricaDePersonagens criarFabrica() {
        FabricaDePersonagens fabrica = new FabricaDePersonagens();
        fabrica.registrar("1", "Guerreiro (vida 40, ataque 8, defesa 2) - Golpe Brutal",
                nome -> new Guerreiro(nome));
        fabrica.registrar("2", "Mago (vida 30, ataque 6, defesa 1) - Bola de Fogo",
                nome -> new Mago(nome));
        fabrica.registrar("3", "Arqueiro (vida 34, ataque 7, defesa 1) - Disparo Duplo",
                nome -> new Arqueiro(nome));
        return fabrica;
    }

    /** Monta as salas da aventura. Para criar uma nova sala: nova classe que implementa Sala. */
    public static Masmorra criarMasmorra() {
        Inimigo goblin = new Inimigo("Goblin", 24, 4, 1);
        goblin.equiparArma(new Arma("Adaga Enferrujada", 2));

        Inimigo esqueleto = new Inimigo("Esqueleto", 30, 5, 2);
        esqueleto.equiparArma(new Arma("Espada de Ferro", 3));

        Chefao reiGoblin = new Chefao("Rei Goblin", 55, 8, 3, "Golpe Esmagador", 6);

        Bau bau = new Bau("Bau Antigo", 10, List.of(new PocaoCura()));

        return new Masmorra("Masmorra do Rei Goblin", List.of(
                new SalaDeCombate(goblin, List.of(new PocaoCura())),
                new SalaDoTesouro(bau),
                new SalaDeCombate(esqueleto, List.of()),
                new SalaDeDescanso(),
                new SalaDeCombate(reiGoblin, List.of())));
    }
}
