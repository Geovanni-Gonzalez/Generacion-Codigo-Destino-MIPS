# Technical Report — `Generacion-Codigo-Destino-MIPS`

> **🌐 English** · [Español](INFORME_TECNICO.md)  |  README: [English](README.md) · [Español](README.es.md)


> **Scope of this report.** An evidence-based technical evaluation of the repository as if reviewed by a Staff/Principal engineer during hiring. Every claim below is grounded in files actually present in the repo. Where I could not execute something, I say so explicitly.
>
> **Verification note.** The build could **not** be executed inside the review sandbox (only JDK 11 available, no Maven; the project targets JDK 17). All findings are from **static reading** of the source, the CUP/JFlex specs, the JUnit suite, the Maven build, the CI workflow, and the checked-in sample outputs under `programa/salida/`. The "36 tests" claim was verified structurally (see §8). The project's own CI (`.github/workflows/ci.yml`) runs `mvn verify` on Temurin JDK 17 and is the real green-build evidence.
>
> **Post-review changes applied (this pass).** Two items from this report were actioned: (1) **522 boilerplate Javadoc blocks** were removed across 73 files (comment-only edit — zero code lines changed, CRLF line endings preserved; the genuine one-line docs in `Operacion.java`, `TipoDato.java` and `GeneradorCodigoIntermedio.java` were kept). (2) The **recursion limitation is now documented in-code** at `AnalizadorIRMIPS.construirTablaDirecciones` and `TraductorFuncionesMIPS`. The structural *fix* (stack-frame temporaries) was intentionally **not** attempted blind, since the build can't be compiled/tested in this environment. The analytical sections below reflect the code **as originally reviewed**, with resolution status called out inline.

---

## Part A — Analysis

### 1. Project objective

**Problem solved.** The repository implements the **back-end (target-code generation)** phase of a compiler for a small imperative language, `.chip`. Given a source file it runs the full compiler pipeline and, for valid programs, emits three-address intermediate code (`.ic`) and **executable MIPS assembly** (`.asm`) runnable in QtSPIM.

**Context.** Academic — Project #3 of *Compilers & Interpreters* (IC5701, Tecnológico de Costa Rica, II-2026). It reuses the JFlex/CUP front-end from Projects I and II and adds IR + MIPS generation and a class module. Evidence: `info.txt`, `pom.xml` `groupId cr.ac.itcr`, and the `test/` corpus naming (`28–31_clase_*.chip`).

**Users / use cases.** Course staff grading the work; students learning compiler construction; and — repurposed — a **portfolio artifact** for recruiters. Primary use case is non-interactive/batch: `java -jar … source.chip [out]` → reports + `.ic` + `.asm` → load `.asm` in QtSPIM.

### 2. Architecture

**Style:** classic **phased / pipeline compiler**, single-process CLI, layered by package with one responsibility each. No web layer, no DB, no MVC — and correctly so.

**Data flow:** `.chip` → **Lexer** (`MiLexer`, JFlex) → **Parser** (`Parser`, CUP LALR) with **semantic analysis interleaved** → **AST** (`ProgramaNodo`) → **IR generator** (`GeneradorCodigoIntermedio` → `List<Instruccion>`) → **MIPS generator** (`GeneradorMIPS`) → peephole → `.asm`. Orchestrated by `pipeline.Compilador.compilar(Path)`, results carried in the `ResultadoCompilacion` DTO, artifacts written by `reporte.*` and `Main`.

**Module responsibilities:**

| Module | Responsibility | Key evidence |
|--------|----------------|--------------|
| `lexico/lexico.flex` | Tokenization, line/col, comment state, lexical errors | `%state COMMENT`, `getErroresLexicos()` |
| `sintactico/sintactico.cup` | LALR grammar + panic-mode recovery | `error` productions, `siguienteTokenConRecuperacion()` |
| `semantico/` | Scope stack, strong typing, `main` uniqueness, return checks | `TablaDeSimbolos` (1,036 LOC), `AnalizadorSemantico` (1,533 LOC) |
| `ast/` | ~30 node types (Composite) | `Nodo`, `ExpresionBinariaNodo`, `ClaseNodo`, … |
| `intermedio/` | IR model + generation | `GeneradorCodigoIntermedio` (978 LOC), `Operacion` enum |
| `mips/` | Backend façade + translators + register pool + emitters + optimizer | `GeneradorMIPS` switch on `Operacion`, 7 `Traductor*` |
| `reporte/` | Report/code writers | `EscritorReportes`, `EscritorCodigo`, `EscritorMIPS` |
| `pipeline/` | Orchestration, DTO, internal exception | `Compilador`, `ResultadoCompilacion`, `CompiladorInternoException` |

**Execution flow:** `Main` validates input/output paths → `Compilador` parses (semantics interleaved) → on fatal parser error the remaining tokens are drained so reports stay complete → **acceptance = `sintaxisCompleta && sin errores léxicos && parser.getNumErrores()==0 && sin errores semánticos`** (see `Compilador.compilar`) → only if accepted, IR + MIPS are generated → writers persist everything.

**Verdict:** The architecture is genuinely modular and the layering is real, not cosmetic. The `mips` package in particular shows deliberate decomposition (a façade dispatching to seven single-purpose translators) that a reviewer will recognize as an intentional design.

### 3. Technologies (complete inventory)

- **Language / platform:** Java 17, JVM.
- **Build tools / package manager:** Apache Maven; plugins: `maven-antrun-plugin` (invokes CUP jar), `jflex-maven-plugin` 1.9.1, `build-helper-maven-plugin` 3.4.0 (adds generated sources), `maven-compiler-plugin` 3.11.0, `maven-surefire-plugin` 3.5.4, `maven-shade-plugin` 3.5.0 (executable fat JAR, `mainClass Main`).
- **Compiler-construction libraries:** JFlex 1.9.1 (lexer generator), Java CUP 11a (parser generator, bundled jars in `src/lib/`), `com.github.vbmacher:java-cup-runtime:11b-20160615` (runtime dependency).
- **Testing:** JUnit Jupiter 5.10.2, `@ParameterizedTest` + `@MethodSource`.
- **Target / external tool:** MIPS assembly; QtSPIM / MARS simulator (execution/verification).
- **CI/CD:** GitHub Actions (`actions/checkout@v4`, `actions/setup-java@v4`, Temurin JDK 17, Maven cache, `mvn -B verify`).
- **IDE / dev config:** VS Code (`.vscode/launch.json`, `.vscode/tasks.json`), `compilador.debug` system property to toggle stack traces.
- **VCS:** Git/GitHub (41 commits); repo also contains a `.github/modernize/java-upgrade/` hook scaffold (`recordToolUse.ps1/.sh`).
- **Data formats:** `.chip` (source), `.ic` (IR), `.asm` (MIPS), `.txt` (reports), UTF-8 encoding, POSIX/Windows paths via `java.nio.file`.
- **Not present (correctly):** no DB, ORM, REST, sockets, containers, cloud, i18n frameworks, mocking library, concurrency/async.

### 4. Technical concepts applied

| Concept | Where | How & why | Skill shown |
|---------|-------|-----------|-------------|
| **LALR parsing** | `sintactico.cup` | CUP grammar, `scan with` wrapper | Formal parsing theory |
| **Panic-mode error recovery** | `sintactico.cup` `error` productions + `siguienteTokenConRecuperacion` | Sync to `:|`/`!`; *insert* `!` when a new statement starts without a terminator | Non-trivial parser engineering |
| **Symbol table / scope stack** | `TablaDeSimbolos` | Stack of hash maps; push on program/func/method/block, pop on exit; lookup top→bottom, O(depth) | Data-structure design |
| **Strong static typing** | `AnalizadorSemantico` | Recursive `evaluarTipo`, boolean-only conditions, return-path & single-`main` checks | Semantic analysis |
| **AST (Composite)** | `ast/` ~30 nodes | Tree of typed nodes traversed for IR gen | OO modeling |
| **Three-address IR** | `intermedio/` | Each binary expr → temporary + op; labels/jumps for control flow | IR design |
| **Register allocation (object pool)** | `AdministradorRegistros` | Free-list of `$t0–$t5`, acquire/release; throws when empty | Resource management |
| **Calling convention** | `TraductorFuncionesMIPS` | `$ra` save/restore, args pushed by caller, callee stack cleanup, `this` first | Systems/ABI knowledge |
| **Heap object model** | `TraductorObjetosMIPS`, IR gen | `new` → syscall 9 (`sbrk`), field offsets, reserved vtable word at 0 | Runtime modeling |
| **Peephole optimization** | `OptimizadorMIPS` | `sw`/`lw` (and `s.s`/`l.s`) to same label → `move`/`mov.s`; drop redundant load | Optimization basics |
| **Façade** | `Compilador`, `GeneradorMIPS` | Hide multi-step subsystems behind one call | Design patterns |
| **Strategy-style dispatch** | `GeneradorMIPS` switch → `Traductor*` | One translator per IR op family | SRP / patterns |
| **DTO / Result object** | `ResultadoCompilacion`, `ResultadoAnalisisMIPS` | Immutable transport between phases | Clean interfaces |
| **Functional programming** | `AnalizadorSemantico` ctor takes `Consumer<String>`; `this::nuevaEtiquetaInterna` method refs | Decoupled error sink & label factory | Modern Java |
| **Encapsulation / immutability** | `Collections.unmodifiableList` in lexer; final classes | Defensive exposure | Robust API design |
| **Custom exceptions w/ location** | `CompiladorInternoException` (line/col) | Structured internal-error reporting | Error handling |
| **File handling & validation** | `Main`, `Compilador.validarFuente` | `Files.exists/isRegularFile/isReadable`, UTF-8 buffered reader | Defensive I/O |
| **Configuration flag** | `Boolean.getBoolean("compilador.debug")` | Toggle stack traces | Operability |
| **Parameterized testing** | `CompiladorTest` | `@MethodSource` corpora | Test design |
| **CI/CD** | `ci.yml` | `mvn verify` on JDK 17 | DevOps |

Concepts **claimed by generic checklists but NOT present** (and shouldn't be forced): DI containers, Repository/DAO, Observer, Singleton, caching, serialization, sockets/IPC, concurrency/async, i18n, mocking, containers, cloud. Their absence is appropriate for a compiler.

### 5. Algorithms & data structures

- **LR(1)/LALR shift-reduce parsing** (CUP-generated automaton) with an explicit **panic-mode recovery** heuristic (`debeInsertarFinSentencia`, `puedeCerrarSentencia`, `puedeIniciarNuevaUnidad`).
- **Recursive AST traversal** (visitor-style) for IR generation; **recursive type evaluation** in semantics.
- **Scope-stack search**, O(d) in scope depth (a stack of hash maps).
- **Free-list register allocation** (object pool), O(1) acquire/release.
- **Single-pass peephole** window (size 2) over emitted lines.
- **DFA lexing** via JFlex; comment handling via a lexer state.
- **Structures:** `ArrayDeque`, `LinkedHashSet`, `ArrayList`, hash maps, enums (`Operacion`, `TipoDato`, `CategoriaSimb`), the AST tree, and the linear IR list.
- No sorting/backtracking/DP/graph-coloring — none required here; the register allocator notably does **not** do graph coloring or linear-scan.

### 6. Good practices

**Strong:** single-responsibility packages and classes; consistent naming (domain-driven Spanish, e.g. `TraductorControlMIPS`); small classes (most 40–300 LOC) with the two large files being the inherently large semantic analyzer and grammar; immutability/defensive copies; pinned dependency & plugin versions; deterministic build with `-expect 38` guard; a real valid/invalid **test corpus**; CI on the correct JDK; `.gitignore`, LICENSE, and checked-in sample outputs.

**Weak:** **Javadoc was auto-generated boilerplate** — nearly every method repeated *"Objetivo: Ejecutar la operacion X definida por Y … Restricciones: Ninguna."*, which was documentation *noise* that inflated line counts and hid the few methods deserving real docs. ✅ *Resolved in this pass: the 522 boilerplate blocks were removed; the meaningful one-line docs were kept.* Remaining: comments/identifiers drop Spanish accents (encoding-safe but reads oddly); tests assert against **raw output strings** (brittle); some translators duplicate int-vs-float emission logic.

### 7. Project quality

**Strengths.** A working, end-to-end compiler with every classic phase; a genuinely modular backend; real error recovery; an OO runtime model on MIPS (heap, offsets, `this`, constructors, static dispatch) that is *above* the usual student bar; professional build/CI hygiene; and a test corpus that is candid about scope — the class module is exercised (`28`/`30`) while the incomplete cases are marked as expected-to-reject.

**Weaknesses / technical debt / risks:**

1. **Global temporaries → not recursion-safe.** Temporaries and locals are emitted as **global `.data` labels** (`d_<func>_<name>`). A recursive or re-entrant call would clobber a caller's temporaries. This is a real semantic-equivalence risk for any recursive program. *(Evidence: the global-label scheme in `AnalizadorIRMIPS`/`EmisorDatosMIPS`, and the `d_<func>_<name>: .word 0` dumps in the checked-in `programa/salida/*.asm`.)*
2. **Register allocator has no spilling.** `AdministradorRegistros.obtenerRegistro()` **throws** `CompiladorInternoException("No hay registros temporales… $t0-$t5")` when the 6-register pool is exhausted — complex expressions fail to compile instead of spilling.
3. **2-D arrays incomplete.** The assignment's own array example `11_arreglos_enunciado.chip` (`int ~ matriz <<2,2>>`) is in the test suite's **`programasInvalidos`** list — i.e. the compiler is expected to *reject* it, despite "arreglos bidimensionales" being listed in scope. 1-D array ops exist in the IR (`DECL_ARRAY`, `STORE_ARRAY`).
4. **String-based optimizer.** `OptimizadorMIPS` parses emitted **text lines** rather than structured instructions; fragile to any formatting change and hard to extend.
5. **Test brittleness / gaps.** Only whole-program integration tests exist; assertions match substrings of IR/MIPS (e.g. `mips.contains("move $t0, $t2")`). No unit tests for the optimizer, register allocator, symbol table, or individual translators.
6. **38 shift/reduce conflicts** are silenced via `-expect 38` — managed, but the grammar remains ambiguous and a future edit can mask a real conflict.
7. **Documentation debt** — the boilerplate Javadoc (see §6). ✅ *Addressed in this pass.*

No evidence of concurrency bugs (single-threaded), injection, or resource leaks (readers use try-with-resources). Complexity is contained.

### 8. Compliance against the assignment

Requirements taken from the course assignment statement (`.chip` language, Project #3).

| # | Requirement | Status | Evidence |
|---|-------------|--------|----------|
| a | Preserve/correct lexical, syntactic & semantic scope of Projects I/II | ✔ | `lexico.flex`, `sintactico.cup`, `semantico/`; scope-stack symbol table |
| b | State whether source is generated by the grammar (grammar + syntax + semantics) | ✔ | `resultado_sintactico.txt`; acceptance conjunction in `Compilador.compilar` |
| c | Report lexical/syntactic/semantic errors via **panic-mode recovery** | ✔ | `error` productions + `siguienteTokenConRecuperacion`; `errores_report.txt`; tests `26/27` |
| d | Generate three-address intermediate code as translation basis | ✔ | `GeneradorCodigoIntermedio`; `.ic` output; `generaCodigoIntermedioEsperado` test |
| e | Write `.asm` (same base name) with **semantically-equivalent** MIPS, runnable in QtSPIM | ✔ | `mips/`, `EscritorMIPS`; `generaMipsConEstructuraEsperada` test; sample `.asm` in `programa/salida/` |
| f | Data types `int, float, bool, char, string` | ✔ | `TipoDato` enum; type checks in `AnalizadorSemantico` |
| g | Expressions with correct precedence (arith/rel/logic, `^`, `%`) | ✔ | CUP precedence; test asserts `5*2` before `+` |
| h | Control flow: `if/else`, `do-while`, `switch/case/default`, `break` | ✔ | `IfNodo`, `WhileNodo`, `SwitchNodo`, `BreakNodo`; `TraductorControlMIPS` |
| i | Functions with params/return; mandatory `__main__` | ✔ | `FuncionNodo`, `TraductorFuncionesMIPS`; single-main check |
| j | I/O `cin`/`cout` | ✔ | `EntradaNodo`/`SalidaNodo`; `TraductorIOMIPS` (syscalls) |
| k | **2-D arrays** | 🟡 | 1-D works (`DECL_ARRAY`/`STORE_ARRAY`); the 2-D enunciado example is in the **rejected** test set |
| l | Classes: fields, `new`/heap, offset access, `this`, constructors, static dispatch | 🟡 → ⭐ | `ClaseNodo`, `TraductorObjetosMIPS`, tests `28`/`30`; **bonus** module, but **no inheritance/polymorphism** (self-reported) |
| m | Automated test suite | ✔ | `CompiladorTest` — 36 executions |
| n | Report artifacts (tokens, symbol table, errors, verdict) | ✔ | `reporte/`, `salida/` samples |
| o | **CI/CD** | ⭐ | `ci.yml` — not required by the assignment |
| p | **Runnable fat JAR + Maven codegen build** | ⭐ | shade/antrun/jflex/build-helper plugins |
| q | **Peephole optimization** | ⭐ | `OptimizadorMIPS` — beyond "equivalent MIPS" |

**"36 tests" claim — verified structurally:** `CompiladorTest` = 5 `@Test` + `@ParameterizedTest programasValidos` (14 files) + `@ParameterizedTest programasInvalidos` (17 files) = **36 executions**, matching the design doc. (I counted the `@MethodSource` lists directly; the build itself was not run here — see verification note.)

### 9. Completeness

**Overall: ~90% of the mandatory scope, plus meaningful bonus work.**

- **Mandatory core (lex/syn/sem + panic recovery + IR + MIPS + types + expressions + control flow + functions + I/O + tests):** ~**100%** — all present with evidence and (per the project's CI) passing tests.
- **2-D arrays:** ~**40%** — 1-D functional, 2-D rejected. This is the clearest *shortfall vs. stated scope*.
- **Classes (bonus):** ~**70%** — structure + use + MIPS work; inheritance/polymorphism absent (self-reported, time-boxed).
- **Engineering extras (CI, fat JAR, optimizer, sample outputs):** exceed requirements.

A single weighted number is unavoidably subjective; **~90% mandatory completeness with a documented array gap and a partially-complete bonus class module** is the honest summary.

### 10. Skills demonstrated (recruiter-facing)

**Compilers / Language engineering** — JFlex lexing, CUP LALR parsing, **panic-mode error recovery**, AST design, three-address IR, target-code generation, register allocation, calling conventions, heap object layout, peephole optimization.

**Backend / Systems** — MIPS assembly, memory/register modeling, syscalls, integer + floating-point paths, ABI/stack discipline.

**Software engineering** — SRP package decomposition, design patterns (Façade, Composite, Strategy-style dispatch, DTO), immutability/defensive programming, custom exceptions with source location, generics, functional callbacks (`Consumer`, method references).

**Build & DevOps** — Maven multi-plugin build with source generation and fat-JAR packaging; **GitHub Actions CI** on JDK 17.

**Testing** — JUnit 5 parameterized suite over a valid/invalid corpus; assertions across IR and MIPS output.

**Tooling** — Git workflow (41 commits), VS Code debug/tasks configs, checked-in sample outputs.

*(No frontend, DB, or cloud skills are claimed — none are evidenced.)*

---

## Part B — Technical evaluation report

### B.1 Technical evaluation

This is a **substantive systems project**, not CRUD glue. It demonstrates end-to-end compiler construction with a working MIPS backend and an OO runtime model — material that requires understanding automata, LR parsing, type systems, IR, and assembly ABIs simultaneously. The engineering around the code (modular backend, Maven codegen build, CI, test corpus) is above typical coursework. The main things holding it back from "excellent" are the **global-temporary design flaw** (recursion-unsafe), the **no-spill register allocator**, the **incomplete 2-D arrays**, and **documentation/testing that don't match the ambition of the code**.

### B.2 Architecture review

The phased pipeline is the right architecture and is implemented cleanly. Phase boundaries are explicit (`ResultadoCompilacion`), and the backend's façade-plus-translators structure is a genuine strength that made the class module addable without touching the front-end. Two architectural concerns: (1) the **memory model** uses global labels for everything, which couples correctness to non-recursion — a stack-frame model would be the principled fix; (2) the **optimizer operates on strings**, sitting at the wrong abstraction level (it should consume the IR or a structured MIPS instruction list). Neither is fatal for the assignment's programs, but both are the kind of thing a Staff reviewer will probe.

### B.3 Code quality

Readable, consistently named, well-decomposed. The single biggest quality detractor is **auto-generated Javadoc boilerplate** on nearly every method — it actively reduces signal and would be flagged in review. Tests are present and meaningful but **integration-only and string-brittle**. Duplication is minor (int/float emission). No obvious correctness bugs in the reviewed paths; error handling in `Main`/`Compilador` is careful (path validation, token draining on fatal parser error, `compilador.debug` flag).

### B.4 Assignment compliance

See §8 matrix. Mandatory requirements met with evidence; **2-D arrays partial**; **classes partial-but-bonus**; several items exceed scope (CI, fat JAR, optimizer). The test corpus is candid about the gaps (incomplete cases are marked expected-to-reject), which is a credibility plus.

### B.5 Risks

| Risk | Severity | Note |
|------|----------|------|
| Recursion clobbers global temporaries | **High** | Any recursive `.chip` program can produce semantically wrong MIPS |
| Register exhaustion aborts compilation | **Medium** | Complex expressions throw instead of spilling |
| 2-D arrays rejected despite being in scope | **Medium** | Scope/implementation mismatch |
| String-based optimizer fragility | **Low-Med** | Breaks on formatting changes; hard to extend |
| Grammar ambiguity hidden by `-expect 38` | **Low** | Future edits can mask real conflicts |
| Documentation debt (boilerplate Javadoc) | **Low** | Hurts maintainability & review impression |

### B.6 Recommendations

1. Make the runtime **recursion-safe**: allocate temporaries/locals in per-call **stack frames**, not global `.data`.
2. Add **register spilling** (spill to stack) or a linear-scan allocator.
3. Finish **2-D arrays** end-to-end (or explicitly remove them from stated scope to keep scope/implementation honest).
4. Re-base the **optimizer on structured instructions** and add dead-store/constant-propagation peepholes.
5. Add **unit tests** for `TablaDeSimbolos`, `AdministradorRegistros`, `OptimizadorMIPS`, and each translator; add **golden-file** `.asm` tests.
6. ✅ *Done —* **boilerplate Javadoc removed**. Follow-up: add real docs on the non-obvious methods (`GeneradorMIPS.traducir` switch, `AnalizadorSemantico.evaluarTipo`, the panic-recovery wrapper).
7. Optionally add a **"what did NOT get generated and why"** note in `errores_report.txt` for register-exhaustion cases.

### B.7 Suggested refactorings

- Extract a shared `EmisorAritmetico` for the duplicated int/float emission in `TraductorOperacionesMIPS`.
- Introduce a `MipsInstr` value type so `OptimizadorMIPS` stops string-parsing.
- Replace global-label allocation in `AnalizadorIRMIPS`/`EmisorDatosMIPS` with a frame-offset model.
- Centralize the register banks (`$t*`, `$f*`) behind one allocator abstraction with spill support.

### B.8 Prioritized improvements

| # | Improvement | Impact | Effort | Priority |
|---|-------------|--------|--------|----------|
| 1 | Stack-frame temporaries (recursion-safe) | High | High | **P0** |
| 2 | Register spilling | High | Medium | **P0** |
| 3 | Finish 2-D arrays (or de-scope) | Medium | Medium | **P1** |
| 4 | Unit + golden-file tests | Medium | Medium | **P1** |
| 5 | Structured-instruction optimizer | Medium | Medium | **P2** |
| 6 | ~~Remove boilerplate Javadoc~~ ✅ done / write real docs on non-obvious methods | Low-Med | Low | **P1** |
| 7 | More peepholes (dead store, const prop) | Low | Medium | **P3** |
| 8 | Inheritance + dynamic dispatch (vtable slot already reserved) | Medium | High | **P3** |

---

## Part C — Recruiter verdict

### Estimated developer level: **Mid** (solid Mid; brushing Mid+ on domain knowledge, held back by testing/docs maturity)

**Why Mid and not Junior/Junior+ (evidence-based):**

- Implemented a **complete multi-phase compiler with a real MIPS backend** — LALR parsing, panic-mode recovery, a typed scope-stack symbol table, a three-address IR, register allocation, a stack calling convention, and a **heap OO object model**. This breadth of CS fundamentals, applied and working, is well beyond Junior scope.
- **Deliberate architecture:** a façade backend delegating to seven single-responsibility translators, immutable DTOs between phases, custom located exceptions. That's design intent, not accident.
- **Engineering maturity around the code:** Maven build that *generates* the scanner/parser and packages a fat JAR, **CI on the correct JDK**, a curated valid/invalid **test corpus**, and checked-in sample outputs.

**Why not Mid+/Senior (evidence-based):**

- A **design-level correctness flaw** (global temporaries → recursion-unsafe) and a **register allocator that aborts instead of spilling** show the runtime model wasn't pushed to full generality.
- **Testing is integration-only and string-brittle**; no unit tests for the trickiest components. A Mid+/Senior would test the optimizer, allocator, and symbol table directly.
- **Documentation was auto-generated boilerplate** — a maturity/craft signal reviewers weight (now removed, but the codebase still lacks real docs on its hardest methods).
- A **stated-scope item (2-D arrays) is unimplemented** and quietly marked as a rejected test.

### First-five-minutes impression on GitHub

> **Honest answer:** Strong and credible, with a couple of visible seams.
>
> In the first 30 seconds the repo reads *professional*: a badged README, a `programa/` layout with clearly-named packages (`ast`, `semantico`, `intermedio`, `mips`, `pipeline`, `reporte`), a Maven build, a green-looking CI workflow, checked-in sample outputs, and a LICENSE. A recruiter or engineer immediately thinks "this person built a real compiler, not a tutorial clone." Opening `mips/` and seeing seven focused translators plus a peephole optimizer *raises* the estimate — this is systems work with taste.
>
> Then the seams appear. *(At original review time, skimming any class the **repetitive boilerplate Javadoc** stood out and slightly cheapened the impression; that has since been removed.)* A careful reviewer who opens `CompiladorTest` notices the tests are **whole-program string assertions** and that `11_arreglos_enunciado.chip` sits in the **invalid** list — a tell that 2-D arrays don't work. Anyone who knows compilers will ask "where do locals/temporaries live?" and, on finding they're **global `.data` labels**, will immediately note it can't do recursion safely.
>
> **Net:** in five minutes this lands as a **strong Mid-level portfolio piece** — clearly hireable, clearly capable of hard systems work, and clearly still with room to grow on testing discipline, runtime generality, and documentation craft. It would earn an interview at most of the companies named, and the working CI plus a candid test corpus would count in the candidate's favor. The fastest credibility wins before sharing it widely: fix/annotate the recursion limitation, delete the boilerplate Javadoc, and either finish or explicitly de-scope 2-D arrays.

---

*Prepared as an evidence-based review. Every finding maps to a file in the repository; no functionality was assumed or invented. The build was not executed in-review (JDK 11 / no Maven in the sandbox); the project's own GitHub Actions CI on JDK 17 is the authoritative green-build signal.*
