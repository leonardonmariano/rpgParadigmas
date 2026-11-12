package rpg.jogo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import rpg.dominio.personagens.Personagem;

/**
 * Fabrica (padrao Factory) que cria o heroi a partir da opcao escolhida no menu.
 * Para adicionar uma nova classe jogavel basta registra-la; esta classe nao muda (OCP).
 */
public class FabricaDePersonagens {
    private final Map<String, String> descricoes = new LinkedHashMap<>();
    private final Map<String, Function<String, Personagem>> construtores = new LinkedHashMap<>();

    public void registrar(String codigo, String descricao, Function<String, Personagem> construtor) {
        descricoes.put(codigo, descricao);
        construtores.put(codigo, construtor);
    }

    public boolean possuiOpcao(String codigo) {
        return construtores.containsKey(codigo);
    }

    public String listarOpcoes() {
        StringBuilder texto = new StringBuilder("Escolha a classe:");
        for (Map.Entry<String, String> opcao : descricoes.entrySet()) {
            texto.append(System.lineSeparator())
                    .append(opcao.getKey()).append(" - ").append(opcao.getValue());
        }
        return texto.toString();
    }

    public Personagem criar(String codigo, String nome) {
        if (!possuiOpcao(codigo)) {
            throw new IllegalArgumentException("Classe inexistente: " + codigo);
        }
        return construtores.get(codigo).apply(nome);
    }
}
