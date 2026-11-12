public class Mago extends Personagem {
    private int mana;

    public Mago(String nome) {
        super(nome, 30, 6, 1);
        mana = 15;
    }

    @Override
    public ResultadoAcao usarHabilidade(Personagem alvo) {
        if (mana < 5) {
            return new ResultadoAcao(false, "Mana insuficiente! Escolha outro ataque.");
        }

        mana -= 5;
        return executarAtaque(alvo, getAtaque() + 10, getNome() + " usou Bola de Fogo!");
    }

    @Override
    public String mostrarStatus() {
        return super.mostrarStatus() + System.lineSeparator() + "Mana: " + mana;
    }
}
