package rpg.apresentacao;

/**
 * Abstracao da interacao com o jogador. O jogo depende desta interface,
 * e nao de Scanner/System.out diretamente (DIP).
 */
public interface EntradaSaida {
    String lerLinha(String mensagem);

    void mostrarMensagem(String mensagem);
}
