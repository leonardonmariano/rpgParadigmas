package rpg.dominio.personagens;

import rpg.dominio.ComHabilidadeEspecial;
import rpg.dominio.Destrutivel;
import rpg.dominio.ResultadoAcao;

/** Chefao: um Inimigo mais forte que tambem possui uma habilidade especial. */
public class Chefao extends Inimigo implements ComHabilidadeEspecial {
    private final String nomeHabilidade;
    private final int bonusHabilidade;

    public Chefao(String nome, int vida, int ataque, int defesa, String nomeHabilidade, int bonusHabilidade) {
        super(nome, vida, ataque, defesa);
        this.nomeHabilidade = nomeHabilidade;
        this.bonusHabilidade = bonusHabilidade;
    }

    @Override
    public String getTipo() {
        return "Chefao";
    }

    @Override
    public String getNomeHabilidade() {
        return nomeHabilidade;
    }

    @Override
    public ResultadoAcao usarHabilidade(Destrutivel alvo) {
        return causarDano(alvo, getAtaqueTotal() + bonusHabilidade,
                getNome() + " usou " + nomeHabilidade + " em " + alvo.getNome() + "!");
    }
}
