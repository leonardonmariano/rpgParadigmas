package rpg.dominio;

/**
 * Contrato de tudo que pode ser alvo de um ataque: personagens, baus, etc.
 * Os ataques dependem desta abstracao, e nao de uma classe concreta (DIP).
 */
public interface Destrutivel {
    String getNome();

    /** Aplica o dano e devolve quanto dano foi realmente sofrido. */
    int receberDano(int valor);

    boolean estaDestruido();
}
