public class Guerreiro extends Personagem {
    public Guerreiro(String nome) {
        super(nome, 40, 8, 2);
    }

    @Override
    public ResultadoAcao usarHabilidade(Personagem alvo) {
        return executarAtaque(alvo, getAtaque() + 5, getNome() + " usou Golpe Brutal!");
    }
}
