Estou desenvolvendo, em dupla, um projeto da disciplina de Compiladores da faculdade. O projeto é um compilador para a linguagem Jack, baseado no nand2tetris.

## Objetivo geral

O projeto será desenvolvido incrementalmente:

1. Analisador léxico / Scanner
2. Analisador sintático / Parser
3. Compilador Jack completo com geração de código VM

A primeira entrega é o analisador léxico, com prazo em 12/10/2026.

## Tecnologias

- Java 24
- Maven
- IntelliJ IDEA Community Edition
- Git/GitHub

O repositório será reutilizado durante todas as próximas etapas do compilador.

## Requisitos da primeira entrega

O analisador léxico deve ler arquivos `.jack` e reconhecer:

- `keyword`
- `symbol`
- `integerConstant`
- `stringConstant`
- `identifier`

Deve ignorar:

- espaços
- tabs
- quebras de linha
- comentários `//`
- comentários `/* ... */`

As palavras reservadas da linguagem Jack devem ser reconhecidas corretamente.

Os símbolos devem seguir a especificação Jack completa, incluindo:

`{ } ( ) [ ] . , ; + - * / & | < > = ~`

Os números inteiros válidos de Jack estão no intervalo de 0 a 32767.

Strings são delimitadas por aspas duplas, mas as aspas não fazem parte do conteúdo do token.

## Estrutura desejada

Quero manter boa separação de responsabilidades pensando no parser futuro.

A ideia inicial é ter algo próximo de:

- `TokenType`
- `Token`
- `JackTokenizer`
- posteriormente uma classe separada para gerar XML de teste
- posteriormente `CompilationEngine`, `SymbolTable` e `VMWriter`

O lexer NÃO deve depender de XML. Ele deve produzir tokens, e outra classe deve transformar esses tokens em XML quando necessário.

`Token` pode conter:

- tipo
- lexema
- linha
- coluna

Também podemos manter um token interno `EOF`, embora ele não deva aparecer no XML oficial.

## Estratégia de testes

Pretendemos usar a estratégia oficial do nand2tetris:

`.jack → tokenizer → *T.xml`

e comparar a saída gerada com os XML oficiais:

- `MainT.xml`
- `SquareT.xml`
- `SquareGameT.xml`

Também queremos testes unitários para componentes individuais do lexer.

## Git

O professor exige pelo menos 10 commits coerentes e incrementais.

Portanto, o desenvolvimento deve ser realmente gradual, com commits correspondentes a funcionalidades reais, por exemplo:

- estrutura inicial
- modelo de tokens
- símbolos e whitespace
- números
- identificadores e keywords
- strings
- comentários
- erros léxicos
- saída XML
- testes
- documentação

## Forma de acompanhamento MUITO IMPORTANTE

Não quero que você simplesmente implemente o projeto inteiro por mim.

O objetivo é eu aprender Compiladores e conseguir explicar o código ao professor.

Em cada etapa, siga esta abordagem:

1. Inspecione primeiro o estado atual do repositório.
2. Explique o conceito que estamos prestes a implementar.
3. Explique o problema e a lógica necessária.
4. Dê pistas, pseudocódigo ou pequenos exemplos.
5. Peça para EU implementar a parte central quando isso fizer sentido.
6. Depois revise o meu código e explique erros ou melhorias.
7. Só avance quando a etapa atual estiver entendida e funcionando.

Pode editar diretamente coisas mecânicas e de baixo valor pedagógico, como configuração, `.gitignore` ou pequenas correções, mas NÃO implemente silenciosamente as partes centrais do compilador.

Nas partes principais — lexer, comentários, parser descendente recursivo, tabela de símbolos e geração de VM — priorize me ensinar e me fazer escrever o código.

Se eu enviar uma implementação, revise não apenas se funciona, mas também:

- separação de responsabilidades;
- clareza;
- possibilidade de reutilização no parser;
- erros de casos extremos;
- aderência à linguagem Jack;
- qualidade para apresentação ao professor.

## Estado atual

O repositório foi criado no GitHub, inicialmente vazio.

Estamos começando pelo primeiro passo: estruturar o projeto Java/Maven e depois criar `TokenType` e `Token`.

Antes de fazer qualquer alteração, inspecione o repositório para verificar exatamente o que já existe.