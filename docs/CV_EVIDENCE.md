# CV_EVIDENCE — Generacion-Codigo-Destino-MIPS

Verifiable, interview-defensible material for the Master Resume. All claims are backed by code in this repository.

## Resume bullets (pick & adapt)

- Built an end-to-end compiler in Java for a custom imperative language: JFlex lexer, LALR parser (Java CUP) with panic-mode error recovery, scope-aware strongly-typed semantic analyzer, three-address intermediate representation, and a MIPS assembly backend runnable in QtSPIM.
- Designed a modular MIPS code generator (façade + 7 single-responsibility translators) implementing register allocation, a stack calling convention, a heap-based object model with field offsets, and a peephole optimization pass.
- Implemented compiler error recovery reporting multiple lexical/syntactic/semantic errors per run via grammar `error` productions, synchronization points, and a token-insertion heuristic.
- Automated the toolchain with Maven (scanner/parser generation, fat-JAR packaging) and GitHub Actions CI on JDK 17; validated with a JUnit 5 parameterized suite of 36 test executions over a 31-program valid/invalid corpus.

## Skills matrix

| Skill | Evidence (file → component) | Depth | Confidence |
|---|---|---|---|
| Compiler construction (lexing, LALR parsing, AST, IR, codegen) | `lexico.flex`, `sintactico.cup`, `ast/` (~30 node types), `intermedio/GeneradorCodigoIntermedio`, `mips/GeneradorMIPS` | Deep | High |
| Semantic analysis & type systems | `semantico/AnalizadorSemantico`, `TablaDeSimbolos` (scope stack, strong typing, return-path checks) | Deep | High |
| MIPS assembly & low-level memory models | `mips/Traductor*MIPS`, `AdministradorRegistros`, `EmisorDatosMIPS` (registers, syscalls, heap via `sbrk`, stack frames) | Deep | High |
| Error recovery engineering | `sintactico.cup` error productions; `pipeline/Compilador` token draining | Medium-Deep | High |
| Java (17, collections, generics, functional callbacks) | Whole codebase; `Consumer` error sinks, immutable DTOs | Deep | High |
| Design patterns (Pipeline, Façade, Composite, Strategy, DTO) | `pipeline/Compilador`, `mips/GeneradorMIPS`, `ast/Nodo` hierarchy | Medium-Deep | High |
| Build engineering (Maven multi-plugin, codegen, shade) | `programa/pom.xml` | Medium | High |
| CI/CD (GitHub Actions) | `.github/workflows/ci.yml` | Medium | High |
| Testing (JUnit 5 parameterized, test corpus design) | `src/test/java/pipeline/CompiladorTest.java`; 36/36 green (surefire report) | Medium | High |

## What this project proves

- Strongest evidence in the portfolio for **CS fundamentals applied at systems level**: this is the flagship project.
- First appearance of: IR design, register allocation, calling conventions, heap object layout, peephole optimization, parser/scanner generators.
- Reinforces: Java, Maven, CI, JUnit (shared with other Java projects).

## ATS keywords

Compiler design, lexical analysis, LALR parser, JFlex, Java CUP, abstract syntax tree, semantic analysis, symbol table, type checking, three-address code, intermediate representation, code generation, MIPS assembly, QtSPIM, register allocation, calling convention, peephole optimization, panic-mode error recovery, Java 17, Maven, JUnit 5, GitHub Actions, CI/CD, design patterns.
