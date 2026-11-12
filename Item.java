/** Contrato de um item consumivel; nao exige metodos de armas ou armaduras. */
public interface Item {
    String getNome();

    /** Se a acao nao for realizada, o item deve deixar o personagem inalterado. */
    ResultadoAcao usar(Personagem personagem);
}
