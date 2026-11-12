package rpg.dominio.itens;

import rpg.dominio.ResultadoAcao;
import rpg.dominio.personagens.Personagem;

/** Itens que podem ser equipados (ex.: armas). Uma pocao NAO e obrigada a ter este metodo. */
public interface Equipavel {
    ResultadoAcao equipar(Personagem personagem);
}
