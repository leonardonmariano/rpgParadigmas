package rpg.dominio;

/**
 * Contrato segregado: so quem possui habilidade especial implementa (ISP).
 * Personagens simples, como o Inimigo comum, nao sao obrigados a ter este metodo.
 */
public interface ComHabilidadeEspecial {
    String getNomeHabilidade();

    /**
     * Pode devolver uma acao nao realizada (por exemplo, sem mana).
     * Nesse caso nada no jogo e alterado e o turno nao e gasto.
     */
    ResultadoAcao usarHabilidade(Destrutivel alvo);
}
