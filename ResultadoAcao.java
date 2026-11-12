/** Informa ao combate se a acao gastou o turno e qual mensagem deve ser exibida. */
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
