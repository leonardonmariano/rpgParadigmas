package rpg.dominio.personagens;

import rpg.dominio.Destrutivel;
import rpg.dominio.ResultadoAcao;
import rpg.dominio.itens.Arma;
import rpg.dominio.itens.Inventario;
import rpg.dominio.itens.Item;

/**
 * Base abstrata de todos os combatentes. Nao pode ser instanciada diretamente,
 * pois "Personagem" sozinho e um conceito incompleto.
 */
public abstract class Personagem implements Destrutivel {
    private final String nome;
    private int vida;
    private final int vidaMaxima;
    private final int ataqueBase;
    private final int defesa;

    // COMPOSICAO: o inventario e criado aqui dentro e so existe junto com o personagem.
    private final Inventario inventario = new Inventario();

    // AGREGACAO: a arma existe fora do personagem e pode trocar de dono.
    private Arma armaEquipada;

    protected Personagem(String nome, int vida, int ataqueBase, int defesa) {
        this.nome = nome;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.ataqueBase = ataqueBase;
        this.defesa = Math.max(0, defesa);
    }

    /** Cada subclasse informa o seu tipo (Guerreiro, Mago, Inimigo...). */
    public abstract String getTipo();

    @Override
    public String getNome() {
        return nome;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getAtaqueBase() {
        return ataqueBase;
    }

    public int getDefesa() {
        return defesa;
    }

    public Arma getArmaEquipada() {
        return armaEquipada;
    }

    /** Ataque base mais o bonus da arma equipada (se houver). */
    public int getAtaqueTotal() {
        int bonus = (armaEquipada != null) ? armaEquipada.getBonusAtaque() : 0;
        return ataqueBase + bonus;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    @Override
    public boolean estaDestruido() {
        return vida == 0;
    }

    /** O alvo pode ser qualquer Destrutivel: outro personagem, um bau... (DIP). */
    public ResultadoAcao atacar(Destrutivel alvo) {
        return causarDano(alvo, getAtaqueTotal(), nome + " atacou " + alvo.getNome() + "!");
    }

    /** Procedimento comum usado pelo ataque normal e pelas habilidades das subclasses. */
    protected ResultadoAcao causarDano(Destrutivel alvo, int dano, String descricao) {
        int danoSofrido = alvo.receberDano(dano);
        String mensagem = descricao + System.lineSeparator()
                + alvo.getNome() + " recebeu " + danoSofrido + " de dano.";
        return new ResultadoAcao(true, mensagem);
    }

    @Override
    public int receberDano(int valor) {
        int danoSofrido = Math.max(0, valor - defesa);
        vida = Math.max(0, vida - danoSofrido);
        return danoSofrido;
    }

    /** Recupera vida sem passar do maximo. Devolve quanto foi realmente recuperado. */
    public int curar(int quantidade) {
        int vidaAnterior = vida;
        vida = Math.min(vidaMaxima, vida + Math.max(0, quantidade));
        return vida - vidaAnterior;
    }

    /**
     * Descanso em uma fogueira: recupera metade da vida maxima.
     * Subclasses podem ampliar o descanso (o Mago recupera mana, o Arqueiro, flechas).
     */
    public ResultadoAcao descansar() {
        int recuperada = curar(vidaMaxima / 2);
        return new ResultadoAcao(true, nome + " descansou e recuperou " + recuperada + " de vida.");
    }

    /** Equipa uma nova arma e devolve a anterior (ou null), que continua existindo. */
    public Arma equiparArma(Arma novaArma) {
        Arma anterior = armaEquipada;
        armaEquipada = novaArma;
        return anterior;
    }

    /** Solta a arma (por exemplo, ao ser derrotado). A arma nao e destruida: pode virar espolio. */
    public Arma desequiparArma() {
        Arma arma = armaEquipada;
        armaEquipada = null;
        return arma;
    }

    // A gestao dos itens e delegada ao Inventario (SRP).
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
        String arma = (armaEquipada == null) ? ""
                : " (" + armaEquipada.getNome() + " +" + armaEquipada.getBonusAtaque() + ")";
        return nome + " [" + getTipo() + "] - Vida: " + vida + "/" + vidaMaxima
                + " | Ataque: " + getAtaqueTotal() + arma
                + " | Defesa: " + defesa;
    }
}
