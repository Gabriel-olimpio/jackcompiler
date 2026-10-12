# JackCompiler

Projeto desenvolvido para a disciplina de Compiladores.

O objetivo é implementar progressivamente um compilador para a linguagem Jack, seguindo a abordagem do **nand2tetris**.

## Etapas

- [x] Analisador léxico
- [ ] Analisador sintático
- [ ] Compilador

## Tecnologias

- Java 24
- Maven
- JUnit 5
- VS Code / IntelliJ IDEA

## Integrantes

- Gabriel André Barreto Olimpio dos Santos — 20260001301
- Hellen Neves Barbosa — 20260001348

## Instruções de utilização

**Pré-requisitos:** JDK 24 ou superior e Apache Maven.

Para compilar o projeto:

`mvn compile`

Para executar o analisador léxico, informe o caminho do arquivo `.jack`:

`java -cp target/classes Main src/test/Main.jack`

O programa realiza a análise léxica e apresenta os tokens reconhecidos em formato XML no terminal.

## Testes

### Analisador léxico

A validação utiliza duas abordagens complementares.

**1. Testes com arquivos oficiais**

Foram utilizados os programas `Main.jack`, `Square.jack` e `SquareGame.jack`, disponibilizados pelo nand2tetris.

Os XMLs produzidos pelo analisador foram comparados com os arquivos oficiais utilizando `git diff --no-index`.

| Arquivo gerado | Referência oficial | Resultado |
|---|---|---|
| Main-geradoT.xml | MainT.xml | Aprovado |
| Square-geradoT.xml | SquareT.xml | Aprovado |
| SquareGame-geradoT.xml | SquareGameT.xml | Aprovado |

As três comparações retornaram código `0`, confirmando a igualdade textual entre os resultados gerados e as referências oficiais.

**2. Testes unitários — JUnit 5**

Foram definidos três testes unitários para verificar:

- Reconhecimento de palavras reservadas;
- Reconhecimento de símbolos;
- Tratamento de comentários.

Para executar os testes:

`mvn test`

Os testes unitários complementam as comparações XML, verificando individualmente funcionalidades do Scanner.

### Vídeo de demonstração

[Assistir à demonstração dos testes](https://drive.google.com/file/d/1SknWYBSildiOdY8jOuBz_e61F8KVL-zB/view?usp=sharing)

