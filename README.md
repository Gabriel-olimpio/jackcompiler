
# JackCompiler

**Analisador Léxico e Sintático da Linguagem Jack**

Projeto desenvolvido para a disciplina de **Compiladores**, com o objetivo de implementar um analisador léxico (Scanner) e um analisador sintático (Parser) para a linguagem Jack, seguindo as especificações do projeto [nand2tetris](https://www.nand2tetris.org/).

O analisador sintático utiliza os tokens produzidos pelo Scanner para reconhecer as estruturas da linguagem e gerar uma representação em **XML**.

---

## Integrantes

| Nome | Matrícula |
|------|-----------|
| Gabriel André Barreto Olimpio dos Santos | 20260001301 |
| Hellen Neves Barbosa | 20260001348 |

## Tecnologias utilizadas

| Tecnologia | Utilização |
|------------|------------|
| Java 24 | Implementação do Scanner e Parser |
| Apache Maven | Compilação e gerenciamento do projeto |
| JUnit 5 | Testes automatizados |
| Visual Studio Code | Ambiente de desenvolvimento |

---

## 1. Funcionalidades implementadas

### Analisador Léxico (Scanner)

Responsável por ler o código-fonte Jack e identificar os tokens da linguagem:

- Palavras-chave (`keyword`);
- Símbolos (`symbol`);
- Identificadores (`identifier`);
- Constantes inteiras (`integerConstant`);
- Constantes de texto (`stringConstant`).

### Analisador Sintático (Parser)

Implementado utilizando a técnica de **descida recursiva (Recursive Descent Parsing)**.

O Parser reconhece:

- Declarações de classes e variáveis;
- Declarações de funções, métodos e construtores;
- Listas de parâmetros e variáveis locais;
- Comandos `let`, `if`, `while`, `do` e `return`;
- Expressões matemáticas e lógicas;
- Operadores binários e unários;
- Chamadas de sub-rotinas e acesso a arrays.

Também realiza a detecção de erros sintáticos, informando o token esperado, o token encontrado e sua localização (linha e coluna).

### Geração de XML

O analisador produz um arquivo XML com a estrutura sintática reconhecida.

Exemplo:

```xml
<class>
    <keyword> class </keyword>
    <identifier> Main </identifier>
    <symbol> { </symbol>
    ...
    <symbol> } </symbol>
</class>
```

O gerador trata os caracteres especiais de XML, como `&`, `<`, `>` e `"`.

---

## 2. Como compilar e executar

### Pré-requisitos

Para executar o projeto, é necessário ter instalado:

- **Java JDK 24 ou superior**;
- **Apache Maven**.

### Compilação

Abra o terminal na pasta raiz do projeto e execute:

```bash
mvn compile
```

### Execução do Parser

O programa recebe dois argumentos:

1. Caminho do arquivo `.jack` de entrada;
2. Caminho do arquivo `.xml` de saída.

**Exemplo de execução:**

```bash
java -cp target/classes Main src/test/parser/Square/Main.jack target/Main-geradoP.xml
```

**Arquivos utilizados:**

| Tipo | Caminho |
|------|---------|
| Entrada | `src/test/parser/Square/Main.jack` |
| Saída | `target/Main-geradoP.xml` |

O nome do arquivo XML de saída é definido pelo usuário no segundo argumento.

---

## 3. Validação com arquivos oficiais

O analisador sintático foi validado utilizando os programas de referência do **nand2tetris**.

Os XMLs gerados foram comparados com os arquivos oficiais, considerando a equivalência da estrutura das tags e desconsiderando diferenças de indentação.

### Resultados da validação

| Arquivo Jack | XML de referência | Resultado |
|--------------|-------------------|-----------|
| `Main.jack` | `MainP.xml` | Aprovado |
| `Square.jack` | `SquareP.xml` | Aprovado |
| `SquareGame.jack` | `SquareGameP.xml` | Aprovado |

**Resultado:** os três testes de comparação estrutural foram aprovados.

---

## 4. Testes automatizados

Os testes foram implementados utilizando **JUnit 5**.

Para executar todos os testes:

```bash
mvn test
```

### Testes do Scanner

**Arquivo:** `ScannerTest.java`

Contém três testes que verificam o reconhecimento de tokens da linguagem Jack.

### Testes do Parser

**Arquivo:** `ParserTest.java`

Contém três testes que verificam a equivalência estrutural dos XMLs gerados com os arquivos oficiais.

### Testes de erros sintáticos

**Arquivo:** `ParserErroTest.java`

Contém dois testes que verificam a rejeição de códigos Jack com erros sintáticos:

- Ausência de ponto e vírgula em uma instrução `let`;
- Ausência de fechamento de parêntese na declaração de uma função.

### Resumo dos resultados

| Classe de teste | Quantidade | Resultado |
|-----------------|------------|-----------|
| `ScannerTest` | 3 | Aprovados |
| `ParserTest` | 3 | Aprovados |
| `ParserErroTest` | 2 | Aprovados |
| **Total** | **8** | **8 aprovados** |

**Última validação registrada:** 27/10/2026.

```text
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

---

## 5. Desafios enfrentados

Durante o desenvolvimento do analisador sintático, as principais dificuldades encontradas foram:

**1. Entender a gramática Jack**

Compreender as regras da linguagem e como as diferentes estruturas se relacionam, principalmente declarações, comandos e expressões.

**2. Implementar os métodos do Parser**

Transformar as regras da gramática em métodos Java, utilizando a técnica de descida recursiva e garantindo o consumo correto dos tokens.

**3. Montar a estrutura XML**

Entender como organizar as tags XML conforme a gramática, respeitando a hierarquia dos elementos e o formato exigido pelos arquivos oficiais do nand2tetris.

---

## 6. Referências

- [nand2tetris — Site oficial](https://www.nand2tetris.org/)

