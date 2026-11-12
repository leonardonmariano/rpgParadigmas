public class Personagem {
    private final String nome;
    private int vida;
    private final int vidaMaxima;
    private final int ataque;
    private final int defesa;
    private final Inventario inventario = new Inventario();

    public Personagem(String nome, int vida, int ataque) {
        this(nome, vida, ataque, 0);
    }

    public Personagem(String nome, int vida, int ataque, int defesa) {
        this.nome = nome;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.ataque = ataque;
        this.defesa = Math.max(0, defesa);
    }

    public String getNome() {
        return nome;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getDefesa() {
        return defesa;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public ResultadoAcao atacar(Personagem alvo) {
        return executarAtaque(alvo, ataque, nome + " atacou " + alvo.getNome() + "!");
    }

    protected ResultadoAcao executarAtaque(Personagem alvo, int dano, String descricao) {
        int danoRecebido = alvo.receberDano(dano);
        String mensagem = descricao + System.lineSeparator()
                + alvo.getNome() + " recebeu " + danoRecebido + " de dano.";
        return new ResultadoAcao(true, mensagem);
    }

    public int receberDano(int dano) {
        int danoRecebido = Math.max(0, dano - defesa);
        vida = Math.max(0, vida - danoRecebido);
        return danoRecebido;
    }

    public int curar(int quantidade) {
        int vidaAnterior = vida;
        vida = Math.min(vidaMaxima, vida + Math.max(0, quantidade));
        return vida - vidaAnterior;
    }

    public void adicionarItem(Item item) {
        inventario.adicionarItem(item);
    }

    public int getQuantidadeItens() {
        return inventario.getQuantidadeItens();
    }

    public String mostrarInventario() {
        return inventario.mostrar();
    }

    public ResultadoAcao usarItem(int numeroItem) {
        return inventario.usarItem(numeroItem, this);
    }

    public String mostrarStatus() {
        return nome + " - Vida: " + vida + "/" + vidaMaxima + " | Defesa: " + defesa;
    }

    /**
     * Pode retornar uma acao nao realizada, sem alterar o estado ou gastar o turno.
     * Personagens sem habilidade especial seguem esse mesmo contrato.
     */
    public ResultadoAcao usarHabilidade(Personagem alvo) {
        return new ResultadoAcao(false, "");
    }
}
