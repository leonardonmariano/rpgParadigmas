# Relatorio da etapa SOLID — RPG: Aventura na Masmorra

## Objetivo e resultado

O codigo existente foi reorganizado sem alterar as regras da primeira etapa. Foram preservados Guerreiro, Mago, Goblin, ataques normais, Golpe Brutal, Bola de Fogo, mana, defesa, inventario, pocao, consultas de status e resultados de vitoria e derrota.

As classes foram separadas em arquivos individuais para facilitar a leitura. Nao foram adicionados frameworks, bibliotecas externas, inimigos, fases ou novos sistemas de jogo.

## Analise do codigo anterior

- **SRP:** `Main.main()` reunia configuracao, leitura do teclado e controle dos turnos. `Personagem` reunia atributos, regras de combate, armazenamento de itens e impressao no terminal. Havia responsabilidades concretas para separar.
- **OCP:** ja existia polimorfismo em `usarHabilidade()` e na classe abstrata `Item`. Essa possibilidade de extensao foi preservada e o contrato dos itens passou a ser uma interface.
- **LSP:** os herois ja eram referenciados como `Personagem`. Era necessario explicitar que uma habilidade pode nao ser realizada, como acontece com falta de mana ou com personagens sem habilidade, e testar esse contrato nos tres tipos.
- **ISP:** nao havia uma interface grande que precisasse ser dividida. O contrato dos itens ja era pequeno; foi mantido com apenas as operacoes necessarias.
- **DIP:** o controle do combate usava diretamente `Scanner` e `System.out`. Essa era uma dependencia concreta relevante para substituir por uma interface simples e testavel.

## Arquivos, classes e interfaces

`Main.java` foi modificado. Os outros arquivos da tabela foram criados; as classes existentes foram movidas, preservando seus nomes.

| Arquivo | Tipo | Responsabilidade |
| --- | --- | --- |
| [Main.java](Main.java) | Classe `Main` | Ler a configuracao inicial e montar os objetos do jogo. |
| [Personagem.java](Personagem.java) | Classe `Personagem` | Manter atributos e regras comuns de ataque, defesa, vida e cura; delegar a gestao de itens. |
| [Guerreiro.java](Guerreiro.java) | Classe `Guerreiro` | Definir os atributos do Guerreiro e o Golpe Brutal. |
| [Mago.java](Mago.java) | Classe `Mago` | Definir os atributos do Mago, a mana e a Bola de Fogo. |
| [Inimigo.java](Inimigo.java) | Classe `Inimigo` | Definir os atributos do Goblin. |
| [Inventario.java](Inventario.java) | Classe `Inventario` | Armazenar, listar, selecionar e consumir itens. |
| [Item.java](Item.java) | Interface `Item` | Exigir somente `getNome()` e `usar(Personagem)`. |
| [PocaoCura.java](PocaoCura.java) | Classe `PocaoCura` | Implementar o efeito da pocao de cura. |
| [ResultadoAcao.java](ResultadoAcao.java) | Classe `ResultadoAcao` | Transportar a mensagem da acao e informar se ela gastou o turno. |
| [EntradaSaida.java](EntradaSaida.java) | Interface `EntradaSaida` | Exigir somente leitura de uma linha e exibicao de uma mensagem. |
| [ConsoleJogo.java](ConsoleJogo.java) | Classe `ConsoleJogo` | Implementar a leitura e a escrita no terminal. |
| [Combate.java](Combate.java) | Classe `Combate` | Conduzir as escolhas, os turnos e o resultado do combate. |
| [testes/TestesRegressao.java](testes/TestesRegressao.java) | Classe `TestesRegressao` | Executar testes automaticos usando apenas o Java. |

Tambem foi criado este `RELATORIO_SOLID.md`. A pasta `build/` contem os arquivos `.class` gerados na validacao.

Nos testes ha uma classe interna `EntradaSaidaSimulada` e duas implementacoes anonimas de `Item`. Elas sao substitutos usados exclusivamente pelos testes e nao adicionam itens ao jogo.

## Onde os cinco principios foram aplicados

### SRP — Responsabilidade unica

- `Inventario.adicionarItem()`, `getQuantidadeItens()`, `mostrar()` e `usarItem()` concentram a gestao da colecao. `Personagem` apenas delega essas operacoes, sem manipular indices ou a lista de itens.
- `Combate.iniciar()` e `executarTurnoJogador()` concentram o fluxo dos turnos, antes presente em `Main.main()`.
- `ConsoleJogo.lerLinha()` e `mostrarMensagem()` concentram a interacao com teclado e terminal. `Personagem`, suas subclasses, `Inventario` e `PocaoCura` nao imprimem mensagens nem leem entradas.
- `Main.main()` monta as dependencias e configura a partida. `ResultadoAcao` permite que as regras devolvam resultados sem realizar a impressao.

A formatacao de pequenas mensagens e descricoes ainda fica proxima dos objetos que as produzem. A limitacao e explicada adiante; nao foi criada uma camada adicional de apresentacao para este jogo de terminal.

### OCP — Aberto para extensao, fechado para modificacao

- `Inventario` armazena `List<Item>` e `usarItem()` chama `item.usar(personagem)`. Nao existem testes de tipo nem referencias a `PocaoCura` dentro dele. Outra implementacao de `Item` pode ser utilizada sem modificar a gestao do inventario ou o combate.
- `Combate.executarTurnoJogador()` chama `Personagem.usarHabilidade()` por polimorfismo. O controle de turnos nao conhece as formulas do Golpe Brutal, da Bola de Fogo ou o custo de mana.
- `Personagem.executarAtaque()` oferece um procedimento comum para aplicar dano com defesa e produzir o resultado, utilizado pelas duas habilidades.

Essa abertura foi testada com outra implementacao de `Item`, existente apenas no teste. O menu inicial de `Main` continua explicitando as duas classes atuais; cadastrar outra opcao nesse menu exigiria uma alteracao na configuracao inicial.

### LSP — Substituicao de Liskov

- `Combate` recebe tanto o heroi quanto o inimigo pelo tipo `Personagem`, sem conversoes para Guerreiro, Mago ou Inimigo.
- Ataque, defesa, cura, limites de vida e inventario seguem as mesmas regras herdadas em todos os tipos.
- O contrato documentado em `Personagem.usarHabilidade()` admite uma acao nao realizada, sem alterar o estado ou gastar o turno. `Inimigo` herda esse comportamento. `Mago.usarHabilidade()` retorna uma falha quando falta mana, sem causar dano ou consumir mana adicional. `Guerreiro.usarHabilidade()` retorna uma acao realizada.
- `Combate.apresentarResultado()` interpreta uniformemente `ResultadoAcao.foiRealizada()`. Nao pressupoe que toda tentativa de habilidade tenha sucesso.
- `Mago.mostrarStatus()` acrescenta a mana ao status comum, mantendo as informacoes de `Personagem`.

Os testes de contrato executam ataques, habilidades, dano, cura, consumo de item e morte em Guerreiro, Mago e Inimigo referenciados como `Personagem`.

### ISP — Segregacao de interfaces

- `Item` exige somente `getNome()` e `usar(Personagem)`. Uma pocao nao precisa implementar operacoes de equipamento, ataque, armadura ou mana.
- `EntradaSaida` exige somente `lerLinha(String)` e `mostrarMensagem(String)`. Tanto o console real quanto o substituto dos testes implementam exatamente essas duas operacoes.

Nao foram criadas interfaces para cada classe nem operacoes sem uso. `Item` substitui a antiga classe abstrata, que guardava apenas o nome e declarava o uso; esse nome agora e fornecido pela implementacao concreta.

### DIP — Inversao de dependencia

- O construtor `Combate(Personagem, Personagem, EntradaSaida)` recebe a interface de interacao por parametro. A classe nao instancia `ConsoleJogo`, nao importa `Scanner` e nao acessa `System.in` ou `System.out`.
- `ConsoleJogo` implementa `EntradaSaida`. `Main.main()` escolhe essa implementacao e a entrega ao combate; a montagem do programa e o lugar onde ficam as escolhas concretas.
- `Inventario` tambem depende do contrato `Item`, em vez de depender de `PocaoCura`.

Os testes substituem `ConsoleJogo` por `EntradaSaidaSimulada` e executam o combate completo sem teclado nem escrita direta no terminal. Isso demonstra a independencia da logica em relacao ao mecanismo de entrada e saida.

## Regras preservadas

| Personagem | Vida maxima | Ataque | Defesa | Habilidade |
| --- | ---: | ---: | ---: | --- |
| Guerreiro | 40 | 8 | 2 | Golpe Brutal: ataque + 5. |
| Mago | 30 | 6 | 1 | Bola de Fogo: ataque + 10, custo de 5 de mana, mana inicial 15. |
| Goblin | 49 | 7 | 2 | Sem habilidade especial. |

- Dano recebido: `maximo(0, dano - defesa)`; a vida nao fica abaixo de zero.
- Cada heroi recebe uma pocao inicial que recupera ate 15 de vida, sem ultrapassar a vida maxima.
- O item usado com sucesso e removido do inventario. Usar a pocao com vida cheia continua consumindo o item e o turno, recuperando zero de vida.
- Ataques, habilidades realizadas e itens usados gastam o turno. O Goblin so responde se continuar vivo.
- Consultar status ou inventario vazio, cancelar, escolher uma opcao invalida e tentar uma habilidade sem mana nao gastam o turno.
- Nome vazio continua virando `Heroi`, e uma classe invalida encerra o jogo.

Para separar regras de impressao, `atacar()`, `usarHabilidade()` e `usarItem()` agora devolvem `ResultadoAcao`. `receberDano()` e `curar()` devolvem quantidades numericas; `mostrarStatus()` e `mostrarInventario()` devolvem texto. Todos os pontos de chamada do projeto foram atualizados.

## Compilacao e execucao

Na pasta do projeto, com o JDK instalado:

```powershell
javac -encoding UTF-8 *.java
java Main
```

Para compilar o jogo e os testes em uma pasta separada:

```powershell
javac -encoding UTF-8 -d build *.java testes/TestesRegressao.java
java -cp build TestesRegressao
```

Os testes nao exigem JUnit nem ativacao de `assert`. Uma verificacao incorreta lanca `AssertionError` e encerra o processo com falha. As transcricoes sao salvas em uma pasta temporaria informada ao final da execucao.

## Testes realizados e resultados

Ambiente: OpenJDK Temurin 21.0.13, Windows, terminal PowerShell.

1. **Antes da refatoracao:** o codigo atual foi lido, copiado para uma pasta temporaria e executado com os testes da primeira etapa. Os 20 cenarios e as 106 verificacoes passaram.
2. **Depois da refatoracao:** os testes da primeira etapa foram adaptados aos retornos dos metodos, mantendo suas expectativas de jogo. Foram acrescentados testes dos contratos de Personagem, da substituicao de entrada e saida e da extensao de Item.
3. **Suite reproduzivel entregue:** 27 cenarios, 150 verificacoes e 13 execucoes de `Main` aprovados. Essas execucoes incluem 12 combates completos e um encerramento por classe invalida.
4. **Comparacao adicional com a versao anterior:** as 13 transcricoes completas de entrada e saida foram comparadas e sao identicas. Incluindo esse cenario adicional, foram aprovados 28 cenarios e 163 verificacoes.
5. **Execucao pelo comando `java Main`:** validada na pasta que contem os `.class`, com uma partida de Guerreiro concluida em vitoria.

Cobertura dos testes:

| Grupo | Resultado |
| --- | --- |
| Atributos e construtor original de Personagem | Aprovado. |
| Ataques normais dos dois herois e do Goblin | Aprovado. |
| Defesa, dano totalmente bloqueado e vida limitada a zero | Aprovado. |
| Golpe Brutal e Bola de Fogo com reducao pela defesa | Aprovado. |
| Consumo de mana e tentativa sem mana, sem perder turno | Aprovado. |
| Cura de 15, cura parcial e limite de vida maxima | Aprovado nos dois herois. |
| Pocao com vida cheia | Aprovado; recupera zero, consome o item e gasta o turno. |
| Remocao de itens, tentativa de reutilizacao e inventarios independentes | Aprovado. |
| Vitoria e derrota com Guerreiro e Mago | Aprovado. |
| Status, cancelamento, inventario vazio e entradas invalidas | Aprovado, sem turno extra para o Goblin. |
| Contrato de Personagem em Guerreiro, Mago e Inimigo | Aprovado. |
| Combate com outra implementacao de EntradaSaida | Aprovado nos dois herois, sem escrita direta no terminal. |
| Inventario com outra implementacao de Item | Aprovado, sem modificar as classes do jogo. |
| Item de teste que recusa o uso | Aprovado; preserva item, estado e turno. |
| Transcricoes anteriores e posteriores | Todas as 13 comparacoes identicas. |

## Limitacoes e pontos futuros

- A selecao inicial de Guerreiro ou Mago permanece explicita em `Main.main()`. OCP foi aplicado aos comportamentos do inventario e do combate; nao significa que o menu nunca precise mudar.
- Os modelos retornam mensagens em portugues e descricoes de status/inventario. A leitura e a escrita estao separadas, mas uma futura interface grafica ou traducao poderia justificar separar tambem essa formatacao. Fazer isso agora adicionaria mais classes sem necessidade do jogo atual.
- O fechamento inesperado da entrada pelo usuario, como EOF, ainda nao recebe tratamento especial em `ConsoleJogo.lerLinha()`, assim como ocorria antes.
- A compilacao padrao e os testes passaram. Uma verificacao opcional com `-Xlint:all` nao foi concluida: o compilador retornou erro ao gravar arquivos `.class` neste ambiente. A causa especifica nao foi determinada; essa opcao nao faz parte dos comandos necessarios para executar o projeto.

Nenhuma funcionalidade foi removida. Nao houve commit, push ou publicacao no GitHub.
