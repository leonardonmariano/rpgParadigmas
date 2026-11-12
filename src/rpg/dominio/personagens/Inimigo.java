package rpg.dominio.personagens;

/**
 * Inimigo comum: apenas um Personagem, sem habilidade especial.
 * Por isso NAO implementa ComHabilidadeEspecial (ISP).
 */
public class Inimigo extends Personagem {
    public Inimigo(String nome, int vida, int ataque, int defesa) {
        super(nome, vida, ataque, defesa);
    }

    @Override
    public String getTipo() {
        return "Inimigo";
    }
}
