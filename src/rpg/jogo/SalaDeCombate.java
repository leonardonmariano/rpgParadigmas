package rpg.jogo;

import java.util.ArrayList;
import java.util.List;
import rpg.apresentacao.EntradaSaida;
import rpg.dominio.itens.Arma;
import rpg.dominio.itens.Item;
import rpg.dominio.personagens.Personagem;

/** Sala com um inimigo. Se o heroi vencer, recolhe a arma do inimigo e as recompensas. */
public class SalaDeCombate implements Sala {
    private final Personagem inimigo;
    private final List<Item> recompensas;

    public SalaDeCombate(Personagem inimigo, List<Item> recompensas) {
        this.inimigo = inimigo;
        this.recompensas = new ArrayList<>(recompensas);
    }

    @Override
    public String getTitulo() {
        return "Combate contra " + inimigo.getNome();
    }

    @Override
    public boolean explorar(Personagem heroi, EntradaSaida interfaceUsuario) {
        Combate combate = new Combate(heroi, inimigo, interfaceUsuario);
        if (!combate.iniciar()) {
            return false;
        }

        // Agregacao: o inimigo foi derrotado, mas a arma dele continua existindo.
        Arma armaDoInimigo = inimigo.desequiparArma();
        if (armaDoInimigo != null) {
            heroi.adicionarItem(armaDoInimigo);
            interfaceUsuario.mostrarMensagem(inimigo.getNome() + " deixou cair " + armaDoInimigo.getNome()
                    + ". Voce guardou no inventario.");
        }
        for (Item recompensa : recompensas) {
            heroi.adicionarItem(recompensa);
            interfaceUsuario.mostrarMensagem("Voce encontrou: " + recompensa.getNome() + ".");
        }
        recompensas.clear();
        return true;
    }
}
