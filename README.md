
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

letStatement:
  'let' varName ('[' expression ']')? '=' expression ';'

ifStatement:
  'if' '(' expression ')' '{' statements '}'
  ('else' '{' statements '}')?

whileStatement:
  'while' '(' expression ')' '{' statements '}'

doStatement:
  'do' subroutineCall ';'

returnStatement:
  'return' expression? ';'

expression:
  term (op term)*

term:
  integerConstant | stringConstant | keywordConstant |
  varName | varName '[' expression ']' | subroutineCall |
  '(' expression ')' | unaryOp term

subroutineCall:
  subroutineName '(' expressionList ')' |
  (className | varName) '.' subroutineName '(' expressionList ')'

expressionList:
  (expression (',' expression)*)?

type:
  'int' | 'char' | 'boolean' | className

keywordConstant:
  'true' | 'false' | 'null' | 'this'

op:
  '+' | '-' | '*' | '/' | '&' | '|' | '<' | '>' | '='

unaryOp:
  '-' | '~'
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

Os testes verificam a organização e a sequência dos elementos XML, além dos valores dos tokens, conforme a estratégia de comparação de cada classe de teste.

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
- Na versão atual, o Scanner ainda não valida o intervalo de constantes inteiras de Jack (`0` a `32767`) nem rejeita adequadamente strings sem aspas de fechamento ou com quebra de linha.

## 8. Desafios enfrentados

### Integração entre Scanner e Parser

Garantir que o Parser consumisse corretamente os tokens do Scanner, utilizando métodos como `advance()`, `peek()`, `match()` e `consume()`.

### Interpretação de identificadores

Distinguir variáveis simples (`x`), acessos a arrays (`a[i]`) e chamadas de sub-rotina (`obj.metodo()`). O método `caseIdent()` verifica os tokens seguintes para reconhecer cada construção.

### Organização da descida recursiva

Coordenar métodos como `parseStatements()`, `parseExpression()` e `parseTerm()`, respeitando os limites de cada estrutura da gramática Jack.

### Validação da hierarquia XML

Diferenciar erros estruturais de diferenças apenas visuais de indentação, utilizando comparações automatizadas com os XMLs oficiais.

### Automatização dos testes

Ampliar a validação com JUnit 5, incluindo arquivos oficiais, estruturas específicas e entradas inválidas. A suíte passou a contar com 29 testes aprovados.

### Processamento de arquivos e pastas

Adaptar o `Main.java` para aceitar arquivos `.jack` individuais e diretórios, gerar automaticamente arquivos `XxxP.xml` e preservar saídas já existentes.

### Tratamento de erros sintáticos

Identificar tokens inesperados e informar o token esperado, o encontrado e sua posição (linha e coluna) no código-fonte.

### Organização do repositório

Atualizar o `.gitignore` e retirar arquivos de configuração da IDE do versionamento, mantendo o repositório mais limpo e portável.

## 9. Uso de Inteligência Artificial

Foi utilizado o **ChatGPT, da OpenAI**, como ferramenta de apoio durante o desenvolvimento e a revisão do JackCompiler. Nesta etapa de revisão, foi utilizado o modelo **GPT-6**.

O apoio incluiu esclarecimentos sobre a gramática Jack, sugestões e revisão de testes JUnit, auxílio na identificação de erros, comparação com saídas XML de referência e organização da documentação.

A dupla ficou responsável por analisar e adaptar as sugestões, integrar o código ao projeto, executar os testes e validar os resultados.

**Antes da entrega, os integrantes devem conferir se outros modelos de IA também foram utilizados e complementar esta declaração, se necessário.**

## 10. Referências

- [nand2tetris](https://www.nand2tetris.org/)
- [Project 10 — Compiler I: Syntax Analysis](https://www.nand2tetris.org/project10)
- [Especificações da atividade no Notion](https://profsergiocosta.notion.site/JackCompiler-Analisador-sint-tico-3f2cce975093809f8d03f7ec71a8d9c1)

