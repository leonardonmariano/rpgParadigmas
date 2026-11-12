import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            EntradaSaida interfaceUsuario = new ConsoleJogo(entrada, System.out);
            interfaceUsuario.mostrarMensagem("=== RPG: Aventura na Masmorra ===");
            String nome = interfaceUsuario.lerLinha("Nome do heroi: ");
            if (nome.trim().isEmpty()) {
                nome = "Heroi";
            }

            interfaceUsuario.mostrarMensagem("Escolha a classe: 1 - Guerreiro | 2 - Mago");
            String classe = interfaceUsuario.lerLinha("");
            Personagem heroi;
            if (classe.equals("1")) {
                heroi = new Guerreiro(nome);
            } else if (classe.equals("2")) {
                heroi = new Mago(nome);
            } else {
                interfaceUsuario.mostrarMensagem("Classe invalida. Fim do jogo.");
                return;
            }

            heroi.adicionarItem(new PocaoCura());
            interfaceUsuario.mostrarMensagem("Voce recebeu uma Pocao de cura (recupera ate 15 de vida).");
            Personagem goblin = new Inimigo("Goblin");
            Combate combate = new Combate(heroi, goblin, interfaceUsuario);
            combate.iniciar();
        }
    }
}
