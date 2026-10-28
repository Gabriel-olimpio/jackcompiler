# JackCompiler

Projeto desenvolvido para a disciplina de Compiladores.

O objetivo é implementar progressivamente um compilador para a linguagem Jack, seguindo a abordagem do **nand2tetris**.

## Etapas

- [x] Analisador léxico
- [x] Analisador sintático
- [ ] Compilador

## Tecnologias

- Java 24
- Maven
- JUnit 5
- VS Code / IntelliJ IDEA

## Integrantes

- Gabriel André Barreto Olimpio dos Santos — 20260001301
- Hellen Neves Barbosa — 20260001348

3. Funcionalidades implementadas

Analisador léxico (Scanner)

Responsável por ler o código-fonte Jack e identificar os tokens da linguagem:

Palavras-chave (keywords)

Símbolos (symbols)

Identificadores (identifiers)

Constantes inteiras (integerConstant)

Constantes de texto (stringConstant)

Analisador sintático (Parser)

Implementado por meio da técnica de descida recursiva (Recursive Descent Parsing).

O Parser reconhece as principais estruturas da gramática Jack, incluindo:

Declarações de classes e variáveis;

Declarações de funções, métodos e construtores;

Listas de parâmetros e variáveis locais;

Comandos let, if, while, do e return;

Expressões matemáticas e lógicas;

Operadores binários e unários;

Chamadas de sub-rotinas e acesso a arrays.

O analisador também identifica erros sintáticos, apresentando informações sobre o token esperado, o token encontrado, a linha e a coluna do erro.

Geração de XML

A saída do Parser é um arquivo XML com a hierarquia de tags estabelecida pelo nand2tetris.

O arquivo apresenta estruturas como class, subroutineDec, statements, expression e term.

O gerador também realiza o tratamento dos caracteres especiais de XML, como &, <, > e ".

4. Como compilar e executar

Pré-requisitos

Java JDK 24 ou superior

Apache Maven instalado

Compilação

Abra o terminal na pasta raiz do projeto e execute:

mvn compile

Execução do Parser

O programa recebe dois argumentos: o caminho do arquivo Jack de entrada e o caminho do arquivo XML de saída.

Exemplo:

java -cp target/classes Main src/test/parser/Square/Main.jack target/Main-geradoP.xml

Nesse exemplo:

Arquivo de entrada: src/test/parser/Square/Main.jack

Arquivo de saída: target/Main-geradoP.xml

O nome do arquivo XML de saída é definido pelo usuário no segundo argumento e pode ser alterado conforme a necessidade.

5. Validação com arquivos oficiais

O analisador sintático foi validado utilizando os programas de referência disponibilizados pelo nand2tetris.

Os XMLs gerados foram comparados com os arquivos oficiais, considerando a equivalência da estrutura das tags e desconsiderando diferenças de indentação.

Arquivo Jack

XML de referência

Resultado

Main.jack

MainP.xml

Aprovado

Square.jack

SquareP.xml

Aprovado

SquareGame.jack

SquareGameP.xml

Aprovado

Os três testes de comparação XML foram aprovados.

6. Testes automatizados

Os testes foram desenvolvidos utilizando JUnit 5 e podem ser executados pelo Maven.

Para executar todos os testes:

mvn test

Testes do Scanner

A classe ScannerTest.java possui três testes que verificam o reconhecimento de tokens da linguagem Jack.

Testes do Parser

A classe ParserTest.java possui três testes que verificam a equivalência estrutural dos arquivos XML gerados com os arquivos de referência.

Testes de erros sintáticos

A classe ParserErroTest.java possui dois testes que verificam se o Parser identifica e rejeita códigos Jack com erros de sintaxe:

Ausência de ponto e vírgula em uma instrução let;

Ausência de fechamento de parêntese em uma declaração de função.

Resultados obtidos

Classe de teste

Testes

Resultado

ScannerTest

3

Aprovados

ParserTest

3

Aprovados

ParserErroTest

2

Aprovados

Total

8

Todos aprovados

Resultado da execução em 27/10/2026: BUILD SUCCESS

Foram executados 8 testes, sem falhas e sem erros.

7. Desafios enfrentados

Durante o desenvolvimento do analisador sintático, as principais dificuldades encontradas foram:

1. Entender a gramática Jack

Compreender as regras da linguagem e como cada estrutura se relaciona com as demais, principalmente a organização das classes, declarações, comandos e expressões.

2. Implementar cada método do Parser

Transformar as regras da gramática em métodos Java, utilizando a técnica de descida recursiva e garantindo que os tokens fossem consumidos corretamente durante a análise.

3. Montar a estrutura XML

Entender como organizar as tags XML de acordo com a gramática, respeitando a hierarquia dos elementos e o formato exigido pelos arquivos de referência do nand2tetris.

8. Referências

nand2tetris — Site oficial