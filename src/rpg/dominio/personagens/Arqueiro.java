package rpg.dominio.personagens;

import rpg.dominio.ComHabilidadeEspecial;
import rpg.dominio.Destrutivel;
import rpg.dominio.ResultadoAcao;

/**
 * Arqueiro: o Disparo Duplo acerta duas flechas seguidas (cada uma com ataque + 2),
 * gastando uma flecha especial.
 */
public class Arqueiro extends Personagem implements ComHabilidadeEspecial {
    private static final int FLECHAS_MAXIMAS = 3;
    private static final int BONUS_POR_FLECHA = 2;

    private int flechasEspeciais;

    public Arqueiro(String nome) {
        super(nome, 34, 7, 1);
        this.flechasEspeciais = FLECHAS_MAXIMAS;
    }

    public int getFlechasEspeciais() {
        return flechasEspeciais;
    }

    @Override
    public String getTipo() {
        return "Arqueiro";
    }

    @Override
    public String getNomeHabilidade() {
        return "Disparo Duplo (1 flecha especial)";
    }

    @Override
    public ResultadoAcao usarHabilidade(Destrutivel alvo) {
        if (flechasEspeciais == 0) {
            return new ResultadoAcao(false, "Sem flechas especiais! Escolha outra acao.");
        }
        flechasEspeciais--;
        int danoPorFlecha = getAtaqueTotal() + BONUS_POR_FLECHA;
        ResultadoAcao primeira = causarDano(alvo, danoPorFlecha,
                getNome() + " usou Disparo Duplo em " + alvo.getNome() + "!");
        String mensagem = primeira.getMensagem();
        if (!alvo.estaDestruido()) {
            ResultadoAcao segunda = causarDano(alvo, danoPorFlecha, "Segunda flecha!");
            mensagem += System.lineSeparator() + segunda.getMensagem();
        }
        return new ResultadoAcao(true, mensagem);
    }

    /** Sobrescrita que amplia o descanso: tambem recupera as flechas especiais. */
    @Override
    public ResultadoAcao descansar() {
        ResultadoAcao descanso = super.descansar();
        flechasEspeciais = FLECHAS_MAXIMAS;
        return new ResultadoAcao(true, descanso.getMensagem()
                + System.lineSeparator() + getNome() + " recuperou as flechas especiais.");
    }

    @Override
    public String mostrarStatus() {
        return super.mostrarStatus() + " | Flechas especiais: " + flechasEspeciais + "/" + FLECHAS_MAXIMAS;
    }
}
