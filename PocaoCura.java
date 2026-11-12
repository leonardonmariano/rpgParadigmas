public class PocaoCura implements Item {
    @Override
    public String getNome() {
        return "Pocao de cura";
    }

    @Override
    public ResultadoAcao usar(Personagem personagem) {
        int vidaRecuperada = personagem.curar(15);
        String mensagem = personagem.getNome() + " usou " + getNome() + "!"
                + System.lineSeparator() + personagem.getNome()
                + " recuperou " + vidaRecuperada + " de vida.";
        return new ResultadoAcao(true, mensagem);
    }
}
