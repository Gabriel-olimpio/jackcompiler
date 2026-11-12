
# JackCompiler

Compilador da linguagem **Jack** desenvolvido em **Java 24** para a disciplina de **Compiladores**, com base nos projetos **10 (Análise Sintática)** e **11 (Geração de Código VM)** do [nand2tetris](https://www.nand2tetris.org/).

O projeto realiza análise léxica e sintática de arquivos `.jack`, mantém a geração de XML para a etapa anterior e produz arquivos `.vm` executáveis na máquina virtual do nand2tetris.

## Integrantes

| Integrante | Matrícula |
| --- | --- |
| Gabriel André Barreto Olimpio dos Santos | 20260001301 |
| Hellen Neves Barbosa | 20260001348 |

## Tecnologias utilizadas

- **Java 24:** implementação do compilador.
- **Apache Maven:** compilação e gerenciamento do projeto.
- **JUnit 5:** testes unitários e de integração.
- **VM Emulator (nand2tetris):** execução dos programas compilados.
- **Git e GitHub:** versionamento e colaboração.

## Funcionalidades

| Componente | Responsabilidade |
| --- | --- |
| `Scanner` | Identifica tokens, palavras-chave, símbolos, constantes e comentários. |
| `Parser` | Analisa a gramática Jack e permite gerar a representação sintática em XML. |
| `SymbolTable` | Gerencia símbolos de classe e sub-rotina, categorias, tipos e índices. |
| `VMWriter` | Escreve instruções VM: pilha, operações, saltos, funções, chamadas e retornos. |
| `CompilationEngine` | Traduz a estrutura da linguagem Jack para instruções VM. |
| `Main` | Processa um arquivo `.jack` ou os arquivos `.jack` contidos diretamente em uma pasta. |

A tradução para VM contempla:

- Expressões aritméticas, lógicas e relacionais;
- Comandos `let`, `do`, `if/else`, `while` e `return`;
- Variáveis `static`, `field`, `argument` e `var`;
- Funções, métodos e construtores;
- Chamadas de sub-rotinas e chamadas entre objetos;
- Leitura e escrita em arrays;
- Criação e manipulação de strings com as rotinas da biblioteca Jack.

## Estrutura do projeto

```text
jackcompiler/
├── pom.xml
├── README.md
├── scripts/
│   └── verificar-project11.ps1
└── src/
    ├── main/
    │   └── java/
    │       ├── Main.java
    │       ├── Scanner.java
    │       ├── Parser.java
    │       ├── CompilationEngine.java
    │       ├── SymbolTable.java
    │       ├── Symbol.java
    │       ├── SymbolKind.java
    │       ├── VMWriter.java
    │       ├── Segment.java
    │       ├── ArithmeticCommand.java
    │       ├── Token.java
    │       ├── TokenType.java
    │       └── XmlWriter.java
    └── test/
        ├── java/             # Testes JUnit
        └── parser/           # Programas Jack e XMLs de referência
```

O diretório `target/` é criado pelo Maven e não precisa ser versionado.

## Pré-requisitos

- **JDK 24**, conforme configurado em `pom.xml`;
- **Apache Maven**;
- **VM Emulator** do pacote nand2tetris, para os testes funcionais.

Verifique a instalação do Java e do Maven:

```powershell
java -version
mvn -version
```

## Como executar

Abra o PowerShell na raiz do projeto, onde está o arquivo `pom.xml`.

### 1. Compilar o projeto

```powershell
mvn clean compile
```

### 2. Gerar VM de um único arquivo Jack

```powershell
java -cp target/classes Main "C:\caminho\Seven\Main.jack"
```

O compilador gera `Main.vm` na mesma pasta do arquivo de entrada.

### 3. Gerar VM de todos os arquivos Jack de uma pasta

```powershell
java -cp target/classes Main "C:\caminho\projects\11\Square"
```

Nesse caso, são gerados os arquivos `Main.vm`, `Square.vm` e `SquareGame.vm`.

**Observações:**

- O processamento é feito somente nos arquivos `.jack` diretamente contidos na pasta; subpastas não são percorridas.
- Arquivos `.vm` existentes podem ser atualizados em uma nova compilação.
- A saída VM só é escrita depois de a tradução do arquivo correspondente terminar com sucesso.

### 4. Gerar XML da análise sintática (Project 10)

```powershell
java -cp target/classes Main "src/test/parser/Square/Main.jack" "target/MainP.xml"
```

Também é possível usar o modo explícito:

```powershell
java -cp target/classes Main --xml "src/test/parser/Square/Main.jack" "target/MainP.xml"
```

**Resumo:** com um argumento, o programa gera `.vm`; com dois argumentos ou com `--xml`, gera XML.

## Testes automatizados

O projeto utiliza **JUnit 5** para validar componentes isolados e o funcionamento integrado do compilador.

Para executar todos os testes:

```powershell
mvn clean test
```

**Última execução da suíte informada (11/11/2026):**

```text
Tests run: 70, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

| Classe | Quantidade | O que verifica |
| --- | ---: | --- |
| `ScannerTest` | 3 | Reconhecimento de tokens. |
| `ScannerXmlTest` | 3 | Saída léxica e referências XML. |
| `ParserTest` | 3 | Estruturas sintáticas. |
| `ParserOfficialTest` | 4 | Arquivos de referência. |
| `ParserExtraTest` | 10 | Casos adicionais da gramática. |
| `ParserErroTest` | 2 | Tratamento de entradas inválidas. |
| `SymbolTableTest` | 10 | Escopos, categorias e índices. |
| `VMWriterTest` | 14 | Geração de comandos VM e validação de parâmetros. |
| `CompilationEngineVmTest` | 14 | Tradução Jack → VM, incluindo condições, laços, métodos, construtores, strings e arrays. |
| `MainIntegrationTest` | 7 | Entrada por arquivo/pasta, recompilação e preservação de saídas em caso de erro. |
| **Total** | **70** | **Sem falhas na execução informada.** |

É possível executar testes específicos pelo PowerShell:

```powershell
mvn "-Dtest=VMWriterTest" test
mvn "-Dtest=SymbolTableTest" test
mvn "-Dtest=CompilationEngineVmTest" test
mvn "-Dtest=MainIntegrationTest" test
```

Os testes automatizados verificam a geração de comandos e várias situações de tradução. A validação final também exige executar os programas compilados no VM Emulator.

## Validação funcional — Project 11

Foram utilizados os seis conjuntos oficiais de teste do [Project 11](https://www.nand2tetris.org/project11).

### Compilar os programas oficiais

O projeto possui um script PowerShell que prepara os arquivos `.jack`, compila cada conjunto e verifica se os arquivos `.vm` correspondentes foram gerados:

```powershell
.\scripts\verificar-project11.ps1 -PastaProject11 "C:\caminho\nand2tetris\projects\11"
```

As saídas são organizadas em:

```text
target/project11/
├── Seven/
├── ConvertToBin/
├── Square/
├── Average/
├── Pong/
└── ComplexArrays/
```

A compilação dos seis conjuntos foi realizada com sucesso, gerando **11 arquivos `.vm`**.

### Resultado dos programas

Os resultados funcionais abaixo foram **confirmados pela equipe por meio do VM Emulator**.

| Programa | Arquivos `.vm` | Verificação funcional | Status |
| --- | ---: | --- | --- |
| **Seven** | 1 | Exibição do valor `7` na tela. | **Aprovado** |
| **ConvertToBin** | 1 | Conversão de valores na memória RAM; entradas `13` e `42` conferidas. | **Aprovado** |
| **Square** | 3 | Execução do programa e interação com o quadrado. | **Aprovado** |
| **Average** | 1 | Execução do programa de cálculo da média. | **Aprovado** |
| **Pong** | 4 | Execução e funcionamento do jogo. | **Aprovado** |
| **ComplexArrays** | 1 | Execução do programa e conferência dos resultados. | **Aprovado** |

### Etapa 1 — Seven e ConvertToBin

- **Seven:** o código VM foi carregado no emulador e exibiu `7`, resultado esperado de `1 + (2 * 3)`.
- **ConvertToBin:** o programa leu um número em `RAM[8000]` e escreveu os 16 bits nas posições `RAM[8001]` até `RAM[8016]`. Foram conferidas as entradas `13` e `42`.

A tag Git `etapa1` é utilizada como marco da conclusão desses dois testes. Sua publicação pode ser conferida com:

```powershell
git tag --list etapa1
git ls-remote --tags origin etapa1
```

### Etapa 2 — Square, Average, Pong e ComplexArrays

Nesta etapa foram verificados os recursos mais avançados da linguagem: construtores, métodos, chamadas entre objetos, arrays, strings e interação com a biblioteca Jack.

Os programas foram compilados com o JackCompiler e, conforme a validação realizada pela equipe, executados no VM Emulator.

> A geração de arquivos `.vm` e a execução funcional são verificações diferentes. O script `verificar-project11.ps1` comprova a geração; o funcionamento é avaliado no VM Emulator.

## Desafios e decisões técnicas

- **Análise sintática e geração VM:** integrar o reconhecimento da linguagem Jack à produção de instruções para a máquina virtual.
- **Gerenciamento de símbolos:** separar o escopo da classe e da sub-rotina, com índices independentes por categoria.
- **Funções, métodos e construtores:** tratar os argumentos, a referência `this` e a alocação de memória de objetos.
- **Arrays e strings:** traduzir operações com endereços de memória e chamadas à biblioteca Jack.
- **Controle de fluxo:** gerar rótulos para comandos `if/else` e `while`.
- **Testes e regressões:** atualizar testes antigos baseados em XML, ampliar a cobertura da geração VM e permitir recompilação sem perder a saída anterior quando a tradução falha.

## Uso de inteligência artificial

Ferramentas de **inteligência artificial, incluindo o ChatGPT (OpenAI)**, foram utilizadas como **apoio pontual durante o desenvolvimento**.

O uso incluiu:

- **Implementação:** auxílio na elaboração e revisão de **partes da classe `CompilationEngine`**, especialmente na compreensão e tradução de construções Jack para comandos VM.
- **Testes:** apoio na criação, revisão e organização de **alguns testes unitários e de integração**, além da interpretação de falhas e resultados.
- **Organização do projeto:** auxílio na estruturação de atividades, planejamento das verificações e organização das etapas de desenvolvimento.
- **Documentação:** apoio na organização e revisão deste README.

As sugestões produzidas com IA foram utilizadas como material de apoio ao trabalho da equipe. A responsabilidade pela integração do código, revisão das alterações e validação dos resultados permanece com os integrantes.

Os testes foram executados com Maven/JUnit e os programas oficiais foram verificados no VM Emulator.

## Demonstração e entrega

A apresentação do projeto deve contemplar a compilação dos programas Jack, a inspeção dos arquivos VM gerados, a execução no VM Emulator e a explicação das principais decisões de implementação e do uso de IA.

**Vídeo de demonstração:** inserir aqui o link da apresentação final (10 a 20 minutos, com participação dos dois integrantes).

O histórico de desenvolvimento e a tag da Etapa 1 podem ser consultados no [repositório do projeto](https://github.com/Gabriel-olimpio/jackcompiler).

## Referências

- [nand2tetris — Project 10: Syntax Analysis](https://www.nand2tetris.org/project10)
- [nand2tetris — Project 11: Code Generation](https://www.nand2tetris.org/project11)
- [nand2tetris — Software e VM Emulator](https://www.nand2tetris.org/software)
- [Especificação da disciplina — JackCompiler: Geração de Código VM](https://profsergiocosta.notion.site/JackCompiler-Gera-o-de-c-digo-VM-400cce975093806697d5ce9da6abc62b)
