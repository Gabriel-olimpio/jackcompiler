
# JackCompiler

**Analisador Léxico e Sintático da Linguagem Jack**

Projeto acadêmico da disciplina de Compiladores, desenvolvido em Java, com base no Project 10 do [nand2tetris](https://www.nand2tetris.org/).

O projeto implementa duas etapas de análise de programas escritos na linguagem Jack:

1. **Scanner:** reconhece os tokens do código-fonte.
2. **Parser:** consome esses tokens, verifica a gramática Jack e gera uma árvore sintática representada em XML.

## Integrantes

| Integrante | Matrícula |
|---|---|
| Gabriel André Barreto Olimpio dos Santos | 20260001301 |
| Hellen Neves Barbosa | 20260001348 |

## Tecnologias

- Java 24
- Apache Maven
- JUnit 5
- Visual Studio Code e IntelliJ IDEA

## Estrutura do projeto

```text
src/
├── main/
│   └── java/
│       ├── Main.java
│       ├── Scanner.java
│       ├── Parser.java
│       ├── Token.java
│       ├── TokenType.java
│       └── XmlWriter.java
└── test/
    ├── java/
    │   ├── ScannerTest.java
    │   ├── ScannerXmlTest.java
    │   ├── ParserTest.java
    │   ├── ParserOfficialTest.java
    │   ├── ParserExtraTest.java
    │   ├── ParserErroTest.java
    │   └── MainIntegrationTest.java
    └── parser/
        ├── Square/
        ├── ExpressionLessSquare/
        └── ArrayTest/
```

## 1. Analisador léxico (Scanner)

O Scanner lê o código Jack e transforma seus elementos em tokens.

As categorias reconhecidas incluem:

- `keyword`: palavras reservadas;
- `symbol`: símbolos;
- `identifier`: identificadores;
- `integerConstant`: constantes inteiras;
- `stringConstant`: constantes de texto.

O Scanner também trata comentários e mantém informações de linha e coluna para auxiliar a identificação de erros.

### Validação do Scanner

O Scanner é utilizado internamente pelo Parser. Sua validação independente é realizada pelos testes JUnit.

Executar testes básicos:

```powershell
mvn "-Dtest=ScannerTest" test
```

Comparar os tokens gerados com os XMLs oficiais:

```powershell
mvn "-Dtest=ScannerXmlTest" test
```

Os testes `ScannerXmlTest` comparam os tokens dos programas `Main.jack`, `Square.jack` e `SquareGame.jack` com os arquivos de referência `MainT.xml`, `SquareT.xml` e `SquareGameT.xml`.

## 2. Analisador sintático (Parser)

O Parser utiliza a técnica de **descida recursiva (recursive descent parsing)**.

Cada estrutura principal da gramática é reconhecida por um método específico.

Entre os métodos implementados estão:

- `parseClass()`
- `parseClassVarDec()`
- `parseSubroutineDec()`
- `parseParameterList()`
- `parseSubroutineBody()`
- `parseVarDec()`
- `parseStatements()`
- `parseLet()`
- `parseIf()`
- `parseWhile()`
- `parseDo()`
- `parseReturn()`
- `parseExpression()`
- `parseTerm()`
- `parseExpressionList()`

### Gramática Jack

As principais regras reconhecidas incluem:

```text
class:
  'class' className '{'
  classVarDec* subroutineDec*
  '}'

classVarDec:
  ('static' | 'field') type varName
  (',' varName)* ';'

subroutineDec:
  ('constructor' | 'function' | 'method')
  ('void' | type) subroutineName
  '(' parameterList ')' subroutineBody

parameterList:
  (type varName (',' type varName)*)?

subroutineBody:
  '{' varDec* statements '}'

varDec:
  'var' type varName (',' varName)* ';'

statements:
  statement*

statement:
  letStatement | ifStatement |
  whileStatement | doStatement |
  returnStatement

expression:
  term (op term)*

expressionList:
  (expression (',' expression)*)?
```

O Parser também reconhece constantes, operadores unários, expressões entre parênteses, acesso a arrays e chamadas de sub-rotinas.

### Expressões e operadores

As expressões seguem a regra:

`expression: term (op term)*`

Conforme a gramática Jack, o Parser não aplica níveis tradicionais de precedência entre operadores binários. O agrupamento explícito é realizado por parênteses.

### Tratamento de erros

O Parser verifica os tokens esperados por meio de métodos auxiliares, como `consume()`.

Quando encontra uma construção sintaticamente inválida, lança uma exceção com informações sobre o token esperado, o encontrado e a localização.

Exemplo observado durante os testes:

```text
Esperado: SEMICOLON
Encontrado: RETURN
Lexema: return
Linha: 6
Coluna: 9
```

## 3. Geração do XML

O `XmlWriter.java` gera as tags correspondentes à estrutura sintática reconhecida.

Exemplo simplificado:

```xml
<class>
<keyword> class </keyword>
<identifier> Main </identifier>
<symbol> { </symbol>
<symbol> } </symbol>
</class>
```

O gerador realiza o escapamento dos caracteres especiais `&`, `<`, `>` e `"`.

O XML preserva a hierarquia das regras, mas não adiciona indentação visual automaticamente.

## 4. Compilação e execução

### Pré-requisitos

- JDK 24 ou superior;
- Apache Maven.

Verificação:

```powershell
java -version
mvn -version
```

### Compilar o projeto

Na raiz do repositório:

```powershell
mvn compile
```

### Executar com um arquivo Jack

É possível informar o arquivo de entrada e um nome personalizado para a saída:

```powershell
java -cp target/classes Main src/test/parser/Square/Main.jack target/Main-geradoP.xml
```

Nesse exemplo, o XML será gerado em:

`target/Main-geradoP.xml`

### Executar com saída automática

Ao informar apenas um arquivo `.jack`, o programa gera um XML com o padrão `XxxP.xml`, na mesma pasta do arquivo de entrada.

Para testar sem sobrescrever referências oficiais, prepare uma pasta separada:

```powershell
New-Item -ItemType Directory -Force target/exemplo | Out-Null
Copy-Item src/test/parser/Square/Main.jack target/exemplo/
```

Execute:

```powershell
java -cp target/classes Main target/exemplo/Main.jack
```

Saída:

`target/exemplo/MainP.xml`

### Executar com uma pasta

O programa também aceita uma pasta contendo vários arquivos `.jack`.

Prepare a pasta:

```powershell
New-Item -ItemType Directory -Force target/exemplo-pasta | Out-Null
Copy-Item src/test/parser/Square/*.jack target/exemplo-pasta/
```

Execute:

```powershell
java -cp target/classes Main target/exemplo-pasta
```

Serão gerados arquivos como:

- `MainP.xml`
- `SquareP.xml`
- `SquareGameP.xml`

O programa processa os arquivos `.jack` encontrados diretamente na pasta informada.

**Atenção:** no modo de saída automática, um XML existente não é sobrescrito. Nesse caso, o programa informa o conflito e interrompe o processamento.

## 5. Validação com arquivos oficiais

A análise sintática foi validada utilizando os conjuntos do Project 10 do nand2tetris.

| Conjunto | Arquivo | Resultado |
|---|---|---|
| Square | Main.jack | Aprovado |
| Square | Square.jack | Aprovado |
| Square | SquareGame.jack | Aprovado |
| ExpressionLessSquare | Main.jack | Aprovado |
| ExpressionLessSquare | Square.jack | Aprovado |
| ExpressionLessSquare | SquareGame.jack | Aprovado |
| ArrayTest | Main.jack | Aprovado |

As comparações verificam a equivalência do XML produzido em relação às referências, desconsiderando diferenças de formatação previstas nos testes.

## 6. Testes automatizados

Os testes foram desenvolvidos com JUnit 5.

Para executar todos os testes:

```powershell
mvn clean test
```

### Organização dos testes

| Classe | Testes | Finalidade |
|---|---:|---|
| ScannerTest | 3 | Reconhecimento de tokens e comentários |
| ScannerXmlTest | 3 | Comparação com XMLs léxicos oficiais |
| ParserTest | 3 | Validação com Square |
| ParserOfficialTest | 4 | Validação com ExpressionLessSquare e ArrayTest |
| ParserErroTest | 2 | Rejeição de entradas sintaticamente inválidas |
| ParserExtraTest | 10 | Estruturas sintáticas e erros específicos |
| MainIntegrationTest | 4 | Execução por arquivo, pasta e geração de XML |
| **Total** | **29** | **29 aprovados** |

### Testes específicos do Parser

Os testes adicionais verificam:

- Funções com múltiplos parâmetros;
- Chamadas sem argumentos;
- Chamadas com múltiplos argumentos;
- Leitura e escrita em arrays;
- Operadores unários;
- Comandos `if`, `else` e `while`;
- Escapamento XML e constantes de texto;
- Expressões com parênteses;
- Ausência de chave de fechamento;
- Expressões incompletas.

Os testes estruturais verificam a organização e a sequência dos elementos XML, além dos valores dos tokens.

### Testes de integração

A classe `MainIntegrationTest` verifica:

- Processamento de um arquivo individual;
- Processamento de uma pasta com dois arquivos;
- Proteção contra sobrescrita de XML;
- Nome personalizado para o arquivo de saída.

### Resultado registrado

Última execução completa em **07/11/2026**:

```text
Tests run: 29
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Esse resultado corresponde aos casos efetivamente executados e não representa uma comprovação exaustiva de todos os programas possíveis da linguagem Jack.

## 7. Limitações conhecidas

- O projeto realiza análise léxica e sintática, mas ainda não gera código VM.
- Não realiza análise semântica nem verificação de tipos.
- A análise sintática é interrompida no primeiro erro identificado.
- O XML gerado não recebe indentação automática.
- O processamento de pastas não percorre subpastas recursivamente.
- No modo automático, arquivos `XxxP.xml` já existentes são preservados, interrompendo a execução em caso de conflito.
- A interface `Main.java` executa o Parser; a validação independente do Scanner é disponibilizada pelos testes JUnit.

## 8. Desafios enfrentados

### 4. Integração entre Scanner e Parser
Garantir que o Parser consumisse corretamente os tokens gerados pelo Scanner, utilizando métodos como `advance()`, `peek()` e `consume()`.

### 5. Automatização dos testes
Criar testes JUnit para comparar os XMLs gerados com os arquivos oficiais do nand2tetris e verificar casos específicos da gramática.

### 6. Processamento de arquivos e pastas
Adaptar o `Main.java` para processar arquivos `.jack` individuais ou pastas completas, gerando automaticamente os respectivos arquivos `XxxP.xml`.

### 7. Tratamento de erros sintáticos
Identificar tokens inesperados e apresentar mensagens indicando o token esperado, o encontrado, a linha e a coluna do erro.

### 8. Organização do repositório
Configurar o `.gitignore` e remover arquivos específicos da IDE do versionamento, mantendo apenas os arquivos necessários ao projeto.

### Interpretação de identificadores

Uma das dificuldades técnicas foi distinguir as diferentes estruturas que podem começar com um identificador.

Por exemplo, `x` representa uma variável simples, `a[i]` representa um acesso a array e `obj.metodo()` representa uma chamada de sub-rotina.

Para reconhecer essas construções, o método `caseIdent()` consome o identificador e verifica o token seguinte, direcionando a análise para a estrutura adequada.

### Organização da descida recursiva

Outro desafio foi organizar a interação entre métodos como `parseStatements()`, `parseExpression()` e `parseTerm()`.

O consumo dos tokens precisa respeitar a gramática e identificar corretamente o início e o fim de cada construção.

Um exemplo verificado nos testes foi a ausência de `;` após uma atribuição, situação em que o Parser encontrou `RETURN` quando esperava `SEMICOLON` e apresentou a localização do problema.

### Validação da hierarquia XML

Na comparação inicial do arquivo gerado para `Main.jack` com sua referência, o `git diff` apontou diferenças relacionadas à indentação.

Como a especificação permite diferenças de formatação, foram utilizados testes automatizados para verificar a estrutura XML sem depender da indentação visual.

Também foram adicionados testes específicos para conferir estruturas aninhadas, listas vazias e a ordem dos tokens.

## 10. Referências

- [nand2tetris](https://www.nand2tetris.org/)
- [Project 10 — Compiler I: Syntax Analysis](https://www.nand2tetris.org/project10)
- [Especificações da atividade no Notion](https://profsergiocosta.notion.site/JackCompiler-Analisador-sint-tico-3f2cce975093809f8d03f7ec71a8d9c1)

