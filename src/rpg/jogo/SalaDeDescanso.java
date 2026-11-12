package rpg.jogo;

import rpg.apresentacao.EntradaSaida;
import rpg.dominio.personagens.Personagem;

/**
 * Fogueira para descansar. Chama heroi.descansar() sem saber a classe do heroi:
 * cada classe descansa do seu jeito (polimorfismo dinamico).
 */
public class SalaDeDescanso implements Sala {
    @Override
    public String getTitulo() {
        return "Fogueira";
    }

    @Override
    public boolean explorar(Personagem heroi, EntradaSaida interfaceUsuario) {
        interfaceUsuario.mostrarMensagem("Voce encontrou uma fogueira e parou para descansar.");
        interfaceUsuario.mostrarMensagem(heroi.descansar().getMensagem());
        interfaceUsuario.mostrarMensagem(heroi.mostrarStatus());
        return true;
    }
}
