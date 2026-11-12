package rpg.dominio.itens;

import rpg.dominio.ResultadoAcao;
import rpg.dominio.personagens.Personagem;

/**
 * Arma: um Item Equipavel que aumenta o ataque.
 * Relacao de AGREGACAO com Personagem: a arma existe antes e depois do dono,
 * pode cair no chao quando um inimigo e derrotado e ser equipada por outro.
 */
public class Arma extends Item implements Equipavel {
    private final int bonusAtaque;

    public Arma(String nome, int bonusAtaque) {
        super(nome);
        this.bonusAtaque = bonusAtaque;
    }

    public int getBonusAtaque() {
        return bonusAtaque;
    }

    @Override
    public String getDescricao() {
        return "+" + bonusAtaque + " de ataque, equipavel";
    }

    @Override
    public ResultadoAcao equipar(Personagem personagem) {
        Arma anterior = personagem.equiparArma(this);
        String mensagem = personagem.getNome() + " equipou " + getNome()
                + " (+" + bonusAtaque + " de ataque).";
        if (anterior != null) {
            // A arma antiga nao some: volta para o inventario.
            personagem.adicionarItem(anterior);
            mensagem += System.lineSeparator() + anterior.getNome() + " voltou para o inventario.";
        }
        return new ResultadoAcao(true, mensagem);
    }
}
