package rpg.dominio.itens;

import java.util.ArrayList;
import java.util.List;
import rpg.dominio.ResultadoAcao;
import rpg.dominio.personagens.Personagem;

/** Unica responsabilidade: guardar, listar e utilizar os itens de um personagem (SRP). */
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
            Item item = itens.get(i);
            descricao.append(System.lineSeparator())
                    .append(i + 1).append(" - ").append(item.getNome())
                    .append(" (").append(item.getDescricao()).append(")");
        }
        return descricao.toString();
    }

    /**
     * Usa (Usavel) ou equipa (Equipavel) o item escolhido, numerado a partir de 1.
     * Assim como no exemplo do professor com ComHabilidadeEspecial, o instanceof
     * verifica qual contrato segregado o item cumpre.
     */
    public ResultadoAcao usarItem(int numeroItem, Personagem personagem) {
        if (numeroItem < 1 || numeroItem > itens.size()) {
            return new ResultadoAcao(false, "Item invalido. Tente novamente.");
        }

        Item item = itens.get(numeroItem - 1);
        ResultadoAcao resultado;
        if (item instanceof Usavel usavel) {
            resultado = usavel.usar(personagem);
        } else if (item instanceof Equipavel equipavel) {
            resultado = equipavel.equipar(personagem);
        } else {
            resultado = new ResultadoAcao(false, item.getNome() + " nao pode ser usado.");
        }

        if (resultado.foiRealizada()) {
            itens.remove(item);
        }
        return resultado;
    }
}
