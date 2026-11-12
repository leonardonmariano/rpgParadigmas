package rpg.jogo;

import rpg.apresentacao.EntradaSaida;
import rpg.dominio.personagens.Personagem;

/**
 * Uma etapa da masmorra. Novos tipos de sala podem ser criados implementando
 * esta interface, sem alterar a classe Masmorra (OCP).
 */
public interface Sala {
    String getTitulo();

    /** Executa a sala e devolve true se o heroi pode continuar a exploracao. */
    boolean explorar(Personagem heroi, EntradaSaida interfaceUsuario);
}
