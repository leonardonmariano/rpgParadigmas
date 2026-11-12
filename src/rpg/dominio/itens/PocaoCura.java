package rpg.dominio.itens;

import rpg.dominio.ResultadoAcao;
import rpg.dominio.personagens.Personagem;

/** Pocao de cura: e um Item e e Usavel, mas nao e Equipavel. */
public class PocaoCura extends Item implements Usavel {
    private static final int CURA = 15;

    public PocaoCura() {
        super("Pocao de cura");
    }

    @Override
    public String getDescricao() {
        return "recupera ate " + CURA + " de vida";
    }

    @Override
    public ResultadoAcao usar(Personagem personagem) {
        if (personagem.getVida() == personagem.getVidaMaxima()) {
            return new ResultadoAcao(false, personagem.getNome() + " ja esta com a vida cheia.");
        }
        int vidaRecuperada = personagem.curar(CURA);
        String mensagem = personagem.getNome() + " usou " + getNome() + "!"
                + System.lineSeparator() + personagem.getNome()
                + " recuperou " + vidaRecuperada + " de vida.";
        return new ResultadoAcao(true, mensagem);
    }
}
