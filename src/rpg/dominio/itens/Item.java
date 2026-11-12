package rpg.dominio.itens;

/**
 * Base abstrata de todo item do inventario: guarda apenas o que e comum (o nome).
 * O que o item FAZ fica em interfaces separadas: Usavel e Equipavel (ISP).
 */
public abstract class Item {
    private final String nome;

    protected Item(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    /** Texto curto mostrado no inventario. */
    public abstract String getDescricao();
}
