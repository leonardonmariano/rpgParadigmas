package rpg.dominio;

/**
 * Resultado de uma acao do jogo: diz se a acao foi realizada (e gastou o turno)
 * e qual mensagem deve ser mostrada. Assim as regras nao precisam imprimir nada (SRP).
 */
public class ResultadoAcao {
    private final boolean realizada;
    private final String mensagem;

    public ResultadoAcao(boolean realizada, String mensagem) {
        this.realizada = realizada;
        this.mensagem = mensagem;
    }

    public boolean foiRealizada() {
        return realizada;
    }

    public String getMensagem() {
        return mensagem;
    }
}
