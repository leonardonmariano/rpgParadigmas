import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleJogo implements EntradaSaida {
    private final Scanner entrada;
    private final PrintStream saida;

    public ConsoleJogo(Scanner entrada, PrintStream saida) {
        this.entrada = entrada;
        this.saida = saida;
    }

    @Override
    public String lerLinha(String mensagem) {
        saida.print(mensagem);
        return entrada.nextLine();
    }

    @Override
    public void mostrarMensagem(String mensagem) {
        saida.println(mensagem);
    }
}
