package rpg.jogo;

import java.util.ArrayList;
import java.util.List;
import rpg.apresentacao.EntradaSaida;
import rpg.dominio.personagens.Personagem;

/**
 * Sequencia de salas que o heroi precisa atravessar.
 * Depende apenas da interface Sala: nao sabe se e combate, tesouro ou descanso (OCP).
 */
public class Masmorra {
    private final String nome;
    private final List<Sala> salas;

    public Masmorra(String nome, List<Sala> salas) {
        this.nome = nome;
        this.salas = new ArrayList<>(salas);
    }

    /** Devolve true se o heroi atravessou todas as salas com vida. */
    public boolean explorar(Personagem heroi, EntradaSaida interfaceUsuario) {
        interfaceUsuario.mostrarMensagem("");
        interfaceUsuario.mostrarMensagem(heroi.getNome() + " entrou na " + nome + ".");
        for (int i = 0; i < salas.size(); i++) {
            Sala sala = salas.get(i);
            interfaceUsuario.mostrarMensagem("");
            interfaceUsuario.mostrarMensagem("===== Sala " + (i + 1) + "/" + salas.size()
                    + ": " + sala.getTitulo() + " =====");
            if (!sala.explorar(heroi, interfaceUsuario)) {
                return false;
            }
        }
        return true;
    }
}
