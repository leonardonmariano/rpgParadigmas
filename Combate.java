public class Combate {
    private final Personagem heroi;
    private final Personagem inimigo;
    private final EntradaSaida interfaceUsuario;

    public Combate(Personagem heroi, Personagem inimigo, EntradaSaida interfaceUsuario) {
        this.heroi = heroi;
        this.inimigo = inimigo;
        this.interfaceUsuario = interfaceUsuario;
    }

    public void iniciar() {
        interfaceUsuario.mostrarMensagem("\nUm " + inimigo.getNome() + " apareceu!");
        while (heroi.estaVivo() && inimigo.estaVivo()) {
            boolean realizouAcao = executarTurnoJogador();
            if (realizouAcao && inimigo.estaVivo()) {
                apresentarResultado(inimigo.atacar(heroi));
            }
        }

        if (heroi.estaVivo()) {
            interfaceUsuario.mostrarMensagem("\nParabens! Voce venceu o " + inimigo.getNome() + "!");
        } else {
            interfaceUsuario.mostrarMensagem("\nVoce foi derrotado pelo " + inimigo.getNome() + "!");
        }
    }

    private boolean executarTurnoJogador() {
        interfaceUsuario.mostrarMensagem("\n1 - Ataque normal");
        interfaceUsuario.mostrarMensagem("2 - Habilidade especial");
        interfaceUsuario.mostrarMensagem("3 - Ver status");
        interfaceUsuario.mostrarMensagem("4 - Inventario / Usar item");
        String opcao = interfaceUsuario.lerLinha("Escolha: ");

        if (opcao.equals("1")) {
            return apresentarResultado(heroi.atacar(inimigo));
        } else if (opcao.equals("2")) {
            return apresentarResultado(heroi.usarHabilidade(inimigo));
        } else if (opcao.equals("3")) {
            interfaceUsuario.mostrarMensagem(heroi.mostrarStatus());
            interfaceUsuario.mostrarMensagem(inimigo.mostrarStatus());
        } else if (opcao.equals("4")) {
            return usarItem();
        } else {
            interfaceUsuario.mostrarMensagem("Opcao invalida. Tente novamente.");
        }
        return false;
    }

    private boolean usarItem() {
        interfaceUsuario.mostrarMensagem(heroi.mostrarInventario());
        if (heroi.getQuantidadeItens() == 0) {
            return false;
        }

        try {
            int numeroItem = Integer.parseInt(interfaceUsuario.lerLinha("Escolha o item (0 para voltar): "));
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
