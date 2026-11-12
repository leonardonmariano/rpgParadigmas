package rpg.dominio.personagens;

import rpg.dominio.ComHabilidadeEspecial;
import rpg.dominio.Destrutivel;
import rpg.dominio.ResultadoAcao;

/** Mago: pouca vida, mas a Bola de Fogo causa ataque + 10 gastando mana. */
public class Mago extends Personagem implements ComHabilidadeEspecial {
    private static final int MANA_MAXIMA = 20;
    private static final int CUSTO_BOLA_DE_FOGO = 5;
    private static final int BONUS_BOLA_DE_FOGO = 10;

    private int mana;

    public Mago(String nome) {
        super(nome, 30, 6, 1);
        this.mana = MANA_MAXIMA;
    }

    public int getMana() {
        return mana;
    }

    @Override
    public String getTipo() {
        return "Mago";
    }

    @Override
    public String getNomeHabilidade() {
        return "Bola de Fogo (" + CUSTO_BOLA_DE_FOGO + " de mana)";
    }

    @Override
    public ResultadoAcao usarHabilidade(Destrutivel alvo) {
        if (mana < CUSTO_BOLA_DE_FOGO) {
            return new ResultadoAcao(false, "Mana insuficiente! Escolha outra acao.");
        }
        mana -= CUSTO_BOLA_DE_FOGO;
        return causarDano(alvo, getAtaqueTotal() + BONUS_BOLA_DE_FOGO,
                getNome() + " lancou Bola de Fogo em " + alvo.getNome() + "!");
    }

    /** Sobrescrita que amplia o comportamento da classe mae: tambem restaura a mana. */
    @Override
    public ResultadoAcao descansar() {
        ResultadoAcao descanso = super.descansar();
        mana = MANA_MAXIMA;
        return new ResultadoAcao(true, descanso.getMensagem()
                + System.lineSeparator() + getNome() + " restaurou toda a mana.");
    }

    @Override
    public String mostrarStatus() {
        return super.mostrarStatus() + " | Mana: " + mana + "/" + MANA_MAXIMA;
    }
}
