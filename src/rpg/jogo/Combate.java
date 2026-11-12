package rpg.jogo;

import rpg.apresentacao.EntradaSaida;
import rpg.dominio.ComHabilidadeEspecial;
import rpg.dominio.ResultadoAcao;
import rpg.dominio.personagens.Personagem;

/**
 * Conduz um combate em turnos entre o heroi e um inimigo.
 * Recebe a interface EntradaSaida pelo construtor (DIP) e trata
 * heroi e inimigo apenas como Personagem (LSP).
 */
public class Combate {
    /** Inimigos com habilidade especial a usam a cada 3 turnos. */
    private static final int INTERVALO_HABILIDADE_INIMIGO = 3;

    private final Personagem heroi;
    private final Personagem inimigo;
    private final EntradaSaida interfaceUsuario;
    private int turnosDoInimigo = 0;

    public Combate(Personagem heroi, Personagem inimigo, EntradaSaida interfaceUsuario) {
        this.heroi = heroi;
        this.inimigo = inimigo;
        this.interfaceUsuario = interfaceUsuario;
    }

    /** Executa o combate ate alguem cair. Devolve true se o heroi venceu. */
    public boolean iniciar() {
        interfaceUsuario.mostrarMensagem(inimigo.getNome() + " apareceu!");
        interfaceUsuario.mostrarMensagem(inimigo.mostrarStatus());

        while (heroi.estaVivo() && inimigo.estaVivo()) {
            boolean heroiAgiu = executarTurnoDoHeroi();
            if (heroiAgiu && inimigo.estaVivo()) {
                executarTurnoDoInimigo();
            }
        }

        if (heroi.estaVivo()) {
            interfaceUsuario.mostrarMensagem("Voce derrotou " + inimigo.getNome() + "!");
            return true;
        }
        interfaceUsuario.mostrarMensagem(heroi.getNome() + " foi derrotado por " + inimigo.getNome() + "...");
        return false;
    }

    /** Devolve true se a acao escolhida gastou o turno. */
    private boolean executarTurnoDoHeroi() {
        interfaceUsuario.mostrarMensagem("");
        interfaceUsuario.mostrarMensagem("1 - Ataque normal");
        interfaceUsuario.mostrarMensagem("2 - Habilidade especial" + descreverHabilidadeDoHeroi());
        interfaceUsuario.mostrarMensagem("3 - Ver status");
        interfaceUsuario.mostrarMensagem("4 - Inventario / Usar item");
        String opcao = interfaceUsuario.lerLinha("Escolha: ").trim();

        switch (opcao) {
            case "1":
                return apresentarResultado(heroi.atacar(inimigo));
            case "2":
                return usarHabilidadeDoHeroi();
            case "3":
                interfaceUsuario.mostrarMensagem(heroi.mostrarStatus());
                interfaceUsuario.mostrarMensagem(inimigo.mostrarStatus());
                return false;
            case "4":
                return usarItem();
            default:
                interfaceUsuario.mostrarMensagem("Opcao invalida. Tente novamente.");
                return false;
        }
    }

    private String descreverHabilidadeDoHeroi() {
        if (heroi instanceof ComHabilidadeEspecial especial) {
            return " - " + especial.getNomeHabilidade();
        }
        return " (indisponivel)";
    }

    private boolean usarHabilidadeDoHeroi() {
        if (heroi instanceof ComHabilidadeEspecial especial) {
            return apresentarResultado(especial.usarHabilidade(inimigo));
        }
        interfaceUsuario.mostrarMensagem(heroi.getNome() + " nao possui habilidade especial.");
        return false;
    }

    private void executarTurnoDoInimigo() {
        turnosDoInimigo++;
        ResultadoAcao resultado = null;
        boolean vezDaHabilidade = turnosDoInimigo % INTERVALO_HABILIDADE_INIMIGO == 0;
        if (vezDaHabilidade && inimigo instanceof ComHabilidadeEspecial especial) {
            resultado = especial.usarHabilidade(heroi);
        }
        if (resultado == null || !resultado.foiRealizada()) {
            resultado = inimigo.atacar(heroi);
        }
        apresentarResultado(resultado);
    }

    private boolean usarItem() {
        interfaceUsuario.mostrarMensagem(heroi.mostrarInventario());
        if (heroi.getQuantidadeItens() == 0) {
            return false;
        }
        try {
            String texto = interfaceUsuario.lerLinha("Escolha o item (0 para voltar): ").trim();
            int numeroItem = Integer.parseInt(texto);
            if (numeroItem == 0) {
                return false;
            }
            return apresentarResultado(heroi.usarItem(numeroItem));
        } catch (NumberFormatException e) {
            interfaceUsuario.mostrarMensagem("Item invalido. Tente novamente.");
            return false;
        }
    }

    private boolean apresentarResultado(ResultadoAcao resultado) {
        if (!resultado.getMensagem().isEmpty()) {
            interfaceUsuario.mostrarMensagem(resultado.getMensagem());
        }
        return resultado.foiRealizada();
    }
}
