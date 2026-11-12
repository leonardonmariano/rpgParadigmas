package rpg.dominio.cenario;

import java.util.ArrayList;
import java.util.List;
import rpg.dominio.Destrutivel;
import rpg.dominio.itens.Item;

/**
 * Objeto inanimado que pode ser atacado: implementa Destrutivel sem herdar de Personagem.
 * Mostra o DIP na pratica: Personagem.atacar(Destrutivel) funciona com um bau sem mudar nada.
 * Relacao de AGREGACAO com Item: quando o bau quebra, os itens continuam existindo.
 */
public class Bau implements Destrutivel {
    private final String nome;
    private int resistencia;
    private final List<Item> itens;

    public Bau(String nome, int resistencia, List<Item> itens) {
        this.nome = nome;
        this.resistencia = resistencia;
        this.itens = new ArrayList<>(itens);
    }

    @Override
    public String getNome() {
        return nome;
    }

    public int getResistencia() {
        return resistencia;
    }

    @Override
    public int receberDano(int valor) {
        int danoSofrido = Math.max(0, valor);
        resistencia = Math.max(0, resistencia - danoSofrido);
        return danoSofrido;
    }

    @Override
    public boolean estaDestruido() {
        return resistencia == 0;
    }

    /** Entrega os itens guardados. Enquanto o bau estiver inteiro, nada sai dele. */
    public List<Item> retirarItens() {
        if (!estaDestruido()) {
            return new ArrayList<>();
        }
        List<Item> retirados = new ArrayList<>(itens);
        itens.clear();
        return retirados;
    }
}
