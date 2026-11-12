package rpg.dominio.personagens;

import rpg.dominio.ComHabilidadeEspecial;
import rpg.dominio.Destrutivel;
import rpg.dominio.ResultadoAcao;

/** Guerreiro: muita vida e defesa. O Golpe Brutal causa ataque + 5. */
public class Guerreiro extends Personagem implements ComHabilidadeEspecial {
    private static final int BONUS_GOLPE_BRUTAL = 5;

    public Guerreiro(String nome) {
        super(nome, 40, 8, 2);
    }

    @Override
    public String getTipo() {
        return "Guerreiro";
    }

    @Override
    public String getNomeHabilidade() {
        return "Golpe Brutal";
    }

    @Override
    public ResultadoAcao usarHabilidade(Destrutivel alvo) {
        return causarDano(alvo, getAtaqueTotal() + BONUS_GOLPE_BRUTAL,
                getNome() + " usou Golpe Brutal em " + alvo.getNome() + "!");
    }
}
