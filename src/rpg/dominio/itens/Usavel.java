package rpg.dominio.itens;

import rpg.dominio.ResultadoAcao;
import rpg.dominio.personagens.Personagem;

/** Itens consumiveis (ex.: pocoes). Sao removidos do inventario depois de usados. */
public interface Usavel {
    /** Se a acao nao for realizada, o item deve deixar o personagem inalterado. */
    ResultadoAcao usar(Personagem personagem);
}
