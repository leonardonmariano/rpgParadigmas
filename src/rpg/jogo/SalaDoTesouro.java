package rpg.jogo;

import rpg.apresentacao.EntradaSaida;
import rpg.dominio.cenario.Bau;
import rpg.dominio.itens.Item;
import rpg.dominio.personagens.Personagem;

/**
 * Sala com um bau trancado. O heroi usa o MESMO metodo atacar() dos combates,
 * pois o bau tambem e Destrutivel (DIP + polimorfismo).
 */
public class SalaDoTesouro implements Sala {
    private final Bau bau;

    public SalaDoTesouro(Bau bau) {
        this.bau = bau;
    }

    @Override
    public String getTitulo() {
        return "Sala do tesouro";
    }

    @Override
    public boolean explorar(Personagem heroi, EntradaSaida interfaceUsuario) {
        interfaceUsuario.mostrarMensagem("Voce encontrou um " + bau.getNome()
                + " trancado (resistencia " + bau.getResistencia() + ").");

        while (!bau.estaDestruido()) {
            String opcao = interfaceUsuario.lerLinha("1 - Golpear o bau | 2 - Seguir em frente: ").trim();
            if (opcao.equals("1")) {
                interfaceUsuario.mostrarMensagem(heroi.atacar(bau).getMensagem());
            } else if (opcao.equals("2")) {
                interfaceUsuario.mostrarMensagem("Voce deixou o bau para tras.");
                return true;
            } else {
                interfaceUsuario.mostrarMensagem("Opcao invalida. Tente novamente.");
            }
        }

        interfaceUsuario.mostrarMensagem(bau.getNome() + " se partiu!");
        for (Item item : bau.retirarItens()) {
            heroi.adicionarItem(item);
            interfaceUsuario.mostrarMensagem("Voce pegou: " + item.getNome() + ".");
        }
        return true;
    }
}
