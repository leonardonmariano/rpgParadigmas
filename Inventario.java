import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private final List<Item> itens = new ArrayList<>();

    public void adicionarItem(Item item) {
        itens.add(item);
    }

    public int getQuantidadeItens() {
        return itens.size();
    }

    public String mostrar() {
        if (itens.isEmpty()) {
            return "Inventario vazio.";
        }

        StringBuilder descricao = new StringBuilder("Inventario:");
        for (int i = 0; i < itens.size(); i++) {
            descricao.append(System.lineSeparator()).append(i + 1)
                    .append(" - ").append(itens.get(i).getNome());
        }
        return descricao.toString();
    }

    public ResultadoAcao usarItem(int numeroItem, Personagem personagem) {
        if (numeroItem < 1 || numeroItem > itens.size()) {
            return new ResultadoAcao(false, "Item invalido. Tente novamente.");
        }

        Item item = itens.get(numeroItem - 1);
        ResultadoAcao resultado = item.usar(personagem);
        if (resultado.foiRealizada()) {
            itens.remove(numeroItem - 1);
        }
        return resultado;
    }
}
