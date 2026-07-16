# Informe Técnico — `Generacion-Codigo-Destino-MIPS`

> **🌐 Español** · [English](TECHNICAL_REPORT.md)  |  README: [Español](README.es.md) · [English](README.md)

> **Alcance de este informe.** Evaluación técnica basada en evidencia del repositorio, como si lo revisara un ingeniero Staff/Principal durante un proceso de contratación. Toda afirmación se sustenta en archivos realmente presentes en el repo. Cuando algo no se pudo ejecutar, se indica de forma explícita.
>
> **Nota de verificación.** El build **no** pudo ejecutarse dentro del entorno de revisión (solo había JDK 11, sin Maven; el proyecto exige JDK 17). Todos los hallazgos provienen de la **lectura estática** del código, las especificaciones CUP/JFlex, la suite JUnit, el build de Maven, el workflow de CI y las salidas de muestra versionadas en `programa/salida/`. La afirmación de "36 pruebas" se verificó de forma estructural (ver §8). El propio CI del proyecto (`.github/workflows/ci.yml`) corre `mvn verify` sobre Temurin JDK 17 y es la evidencia real de build en verde.
>
> **Cambios aplicados tras la revisión (esta pasada).** Se accionaron dos ítems de este informe: (1) se eliminaron **522 bloques de Javadoc boilerplate** en 73 archivos (edición solo de comentarios — cero líneas de código cambiadas, finales de línea CRLF preservados; se conservaron los docs de una línea genuinos en `Operacion.java`, `TipoDato.java` y `GeneradorCodigoIntermedio.java`). (2) La **limitación de recursión ahora está documentada en el código** en `AnalizadorIRMIPS.construirTablaDirecciones` y `TraductorFuncionesMIPS`. La *corrección* estructural (temporales en marco de pila) **no** se intentó a ciegas, porque el build no se puede compilar/probar en este entorno. Las secciones analíticas de abajo reflejan el código **tal como se revisó originalmente**, con el estado de resolución indicado en línea.

---

## Parte A — Análisis

### 1. Objetivo del proyecto

**Problema que resuelve.** El repositorio implementa la fase de **back-end (generación de código destino)** de un compilador para un pequeño lenguaje imperativo, `.chip`. Dado un archivo fuente ejecuta el pipeline completo del compilador y, para programas válidos, emite código intermedio de tres direcciones (`.ic`) y **ensamblador MIPS ejecutable** (`.asm`) en QtSPIM.

**Contexto.** Académico — Proyecto #3 de *Compiladores e Intérpretes* (IC5701, Tecnológico de Costa Rica, II-2026). Reutiliza el front-end JFlex/CUP de los Proyectos I y II y agrega generación de IR + MIPS y un módulo de clases. Evidencia: `info.txt`, `pom.xml` `groupId cr.ac.itcr`, y el nombrado del corpus `test/` (`28–31_clase_*.chip`).

**Usuarios / casos de uso.** El personal del curso que califica; estudiantes que aprenden construcción de compiladores; y — reutilizado — un **artefacto de portafolio** para reclutadores. El caso de uso principal es no interactivo/por lotes: `java -jar … fuente.chip [salida]` → reportes + `.ic` + `.asm` → cargar el `.asm` en QtSPIM.

### 2. Arquitectura

**Estilo:** **compilador por fases / pipeline** clásico, CLI de un solo proceso, en capas por paquete con una responsabilidad cada uno. Sin capa web, sin BD, sin MVC — y correctamente así.

**Flujo de datos:** `.chip` → **Léxico** (`MiLexer`, JFlex) → **Sintáctico** (`Parser`, CUP LALR) con **análisis semántico entrelazado** → **AST** (`ProgramaNodo`) → **Generador de IR** (`GeneradorCodigoIntermedio` → `List<Instruccion>`) → **Generador MIPS** (`GeneradorMIPS`) → peephole → `.asm`. Orquestado por `pipeline.Compilador.compilar(Path)`, con resultados transportados en el DTO `ResultadoCompilacion` y artefactos escritos por `reporte.*` y `Main`.

**Responsabilidades de módulos:**

| Módulo | Responsabilidad | Evidencia clave |
|--------|-----------------|-----------------|
| `lexico/lexico.flex` | Tokenización, línea/columna, estado de comentario, errores léxicos | `%state COMMENT`, `getErroresLexicos()` |
| `sintactico/sintactico.cup` | Gramática LALR + recuperación en modo pánico | producciones `error`, `siguienteTokenConRecuperacion()` |
| `semantico/` | Pila de alcances, tipado fuerte, unicidad de `main`, chequeos de retorno | `TablaDeSimbolos` (1.036 LOC), `AnalizadorSemantico` (1.533 LOC) |
| `ast/` | ~30 tipos de nodo (Composite) | `Nodo`, `ExpresionBinariaNodo`, `ClaseNodo`, … |
| `intermedio/` | Modelo IR + generación | `GeneradorCodigoIntermedio` (978 LOC), enum `Operacion` |
| `mips/` | Fachada backend + traductores + pool de registros + emisores + optimizador | switch de `GeneradorMIPS` sobre `Operacion`, 7 `Traductor*` |
| `reporte/` | Escritores de reportes/código | `EscritorReportes`, `EscritorCodigo`, `EscritorMIPS` |
| `pipeline/` | Orquestación, DTO, excepción interna | `Compilador`, `ResultadoCompilacion`, `CompiladorInternoException` |

**Flujo de ejecución:** `Main` valida rutas de entrada/salida → `Compilador` parsea (semántica entrelazada) → ante error fatal del parser se drenan los tokens restantes para que los reportes queden completos → **aceptación = `sintaxisCompleta && sin errores léxicos && parser.getNumErrores()==0 && sin errores semánticos`** (ver `Compilador.compilar`) → solo si se acepta, se generan IR + MIPS → los escritores persisten todo.

**Veredicto:** La arquitectura es genuinamente modular y las capas son reales, no cosméticas. El paquete `mips` en particular muestra una descomposición deliberada (una fachada que despacha a siete traductores de único propósito) que un revisor reconocerá como diseño intencional.

### 3. Tecnologías (inventario completo)

- **Lenguaje / plataforma:** Java 17, JVM.
- **Herramientas de build / gestor de paquetes:** Apache Maven; plugins: `maven-antrun-plugin` (invoca el jar de CUP), `jflex-maven-plugin` 1.9.1, `build-helper-maven-plugin` 3.4.0 (agrega fuentes generadas), `maven-compiler-plugin` 3.11.0, `maven-surefire-plugin` 3.5.4, `maven-shade-plugin` 3.5.0 (JAR ejecutable *fat*, `mainClass Main`).
- **Librerías de construcción de compiladores:** JFlex 1.9.1 (generador léxico), Java CUP 11a (generador sintáctico, jars incluidos en `src/lib/`), `com.github.vbmacher:java-cup-runtime:11b-20160615` (dependencia de runtime).
- **Pruebas:** JUnit Jupiter 5.10.2, `@ParameterizedTest` + `@MethodSource`.
- **Destino / herramienta externa:** ensamblador MIPS; simulador QtSPIM / MARS (ejecución/verificación).
- **CI/CD:** GitHub Actions (`actions/checkout@v4`, `actions/setup-java@v4`, Temurin JDK 17, caché de Maven, `mvn -B verify`).
- **IDE / configuración de desarrollo:** VS Code (`.vscode/launch.json`, `.vscode/tasks.json`), propiedad de sistema `compilador.debug` para alternar trazas de pila.
- **VCS:** Git/GitHub (41 commits); el repo también contiene un scaffold de hooks `.github/modernize/java-upgrade/` (`recordToolUse.ps1/.sh`).
- **Formatos de datos:** `.chip` (fuente), `.ic` (IR), `.asm` (MIPS), `.txt` (reportes), codificación UTF-8, rutas POSIX/Windows vía `java.nio.file`.
- **Ausentes (correctamente):** sin BD, ORM, REST, sockets, contenedores, cloud, frameworks de i18n, librería de mocking, concurrencia/asincronía.

### 4. Conceptos técnicos aplicados

| Concepto | Dónde | Cómo y por qué | Habilidad demostrada |
|----------|-------|----------------|----------------------|
| **Análisis LALR** | `sintactico.cup` | Gramática CUP, envoltorio `scan with` | Teoría formal de parsing |
| **Recuperación en modo pánico** | producciones `error` + `siguienteTokenConRecuperacion` | Sincroniza a `:|`/`!`; *inserta* `!` cuando inicia una nueva sentencia sin terminador | Ingeniería de parsers no trivial |
| **Tabla de símbolos / pila de alcances** | `TablaDeSimbolos` | Pila de mapas hash; push por programa/func/método/bloque, pop al salir; búsqueda tope→base, O(profundidad) | Diseño de estructuras de datos |
| **Tipado estático fuerte** | `AnalizadorSemantico` | `evaluarTipo` recursivo, condiciones solo booleanas, chequeos de retorno y unicidad de `main` | Análisis semántico |
| **AST (Composite)** | `ast/` ~30 nodos | Árbol de nodos tipados recorrido para generar IR | Modelado OO |
| **IR de tres direcciones** | `intermedio/` | Cada expr binaria → temporal + op; etiquetas/saltos para control | Diseño de IR |
| **Asignación de registros (pool)** | `AdministradorRegistros` | *Free-list* de `$t0–$t5`, adquirir/liberar; lanza al agotarse | Gestión de recursos |
| **Convención de llamada** | `TraductorFuncionesMIPS` | Guardar/restaurar `$ra`, argumentos apilados por el llamador, limpieza en el llamado, `this` primero | Conocimiento de sistemas/ABI |
| **Modelo de objetos en heap** | `TraductorObjetosMIPS`, gen. IR | `new` → syscall 9 (`sbrk`), offsets de campos, palabra de vtable reservada en 0 | Modelado de runtime |
| **Optimización peephole** | `OptimizadorMIPS` | `sw`/`lw` (y `s.s`/`l.s`) a la misma etiqueta → `move`/`mov.s`; elimina la carga redundante | Fundamentos de optimización |
| **Fachada** | `Compilador`, `GeneradorMIPS` | Ocultan subsistemas multi-paso tras una sola llamada | Patrones de diseño |
| **Despacho estilo Strategy** | switch de `GeneradorMIPS` → `Traductor*` | Un traductor por familia de operación IR | SRP / patrones |
| **DTO / objeto de resultado** | `ResultadoCompilacion`, `ResultadoAnalisisMIPS` | Transporte inmutable entre fases | Interfaces limpias |
| **Programación funcional** | ctor de `AnalizadorSemantico` recibe `Consumer<String>`; referencias `this::nuevaEtiquetaInterna` | Sumidero de errores y fábrica de etiquetas desacoplados | Java moderno |
| **Encapsulamiento / inmutabilidad** | `Collections.unmodifiableList` en el lexer; clases final | Exposición defensiva | Diseño de API robusta |
| **Excepciones propias con ubicación** | `CompiladorInternoException` (línea/col) | Reporte estructurado de errores internos | Manejo de errores |
| **Manejo y validación de archivos** | `Main`, `Compilador.validarFuente` | `Files.exists/isRegularFile/isReadable`, lector buffered UTF-8 | E/S defensiva |
| **Bandera de configuración** | `Boolean.getBoolean("compilador.debug")` | Alterna trazas de pila | Operabilidad |
| **Pruebas parametrizadas** | `CompiladorTest` | Corpus con `@MethodSource` | Diseño de pruebas |
| **CI/CD** | `ci.yml` | `mvn verify` en JDK 17 | DevOps |

Conceptos **que los checklists genéricos reclaman pero NO están presentes** (y no deberían forzarse): contenedores de DI, Repository/DAO, Observer, Singleton, caché, serialización, sockets/IPC, concurrencia/asincronía, i18n, mocking, contenedores, cloud. Su ausencia es apropiada para un compilador.

### 5. Algoritmos y estructuras de datos

- **Parsing shift-reduce LR(1)/LALR** (autómata generado por CUP) con una heurística explícita de **recuperación en modo pánico** (`debeInsertarFinSentencia`, `puedeCerrarSentencia`, `puedeIniciarNuevaUnidad`).
- **Recorrido recursivo del AST** (estilo Visitor) para generar IR; **evaluación recursiva de tipos** en semántica.
- **Búsqueda en pila de alcances**, O(d) en profundidad de alcances (pila de mapas hash).
- **Asignación de registros por free-list** (pool de objetos), O(1) adquirir/liberar.
- **Peephole de una pasada** con ventana de tamaño 2 sobre las líneas emitidas.
- **Análisis léxico por DFA** vía JFlex; manejo de comentarios con un estado del lexer.
- **Estructuras:** `ArrayDeque`, `LinkedHashSet`, `ArrayList`, mapas hash, enums (`Operacion`, `TipoDato`, `CategoriaSimb`), el árbol AST y la lista lineal de IR.
- Sin ordenamiento/backtracking/DP/coloreo de grafos — ninguno se requiere aquí; el asignador de registros notablemente **no** hace coloreo de grafos ni *linear-scan*.

### 6. Buenas prácticas

**Fuertes:** paquetes y clases de responsabilidad única; nombrado consistente (español orientado al dominio, p. ej. `TraductorControlMIPS`); clases pequeñas (la mayoría 40–300 LOC), siendo los dos archivos grandes el analizador semántico y la gramática, inherentemente grandes; inmutabilidad/copias defensivas; versiones fijas de dependencias y plugins; build determinista con guarda `-expect 38`; un **corpus** real válido/inválido; CI sobre el JDK correcto; `.gitignore`, LICENSE y salidas de muestra versionadas.

**Débiles:** **el Javadoc era boilerplate autogenerado** — casi cada método repetía *"Objetivo: Ejecutar la operacion X definida por Y … Restricciones: Ninguna."*, que era *ruido* documental que inflaba el conteo de líneas y ocultaba los pocos métodos que sí merecen documentación. ✅ *Resuelto en esta pasada: se eliminaron los 522 bloques boilerplate; se conservaron los docs de una línea significativos.* Pendiente: comentarios/identificadores omiten acentos en español (seguro para codificación pero se lee raro); las pruebas afirman sobre **cadenas crudas de salida** (frágiles); algunos traductores duplican la lógica de emisión entero-vs-flotante.

### 7. Calidad del proyecto

**Fortalezas.** Un compilador funcional de extremo a extremo con todas las fases clásicas; un backend genuinamente modular; recuperación de errores real; un modelo de objetos OO sobre MIPS (heap, offsets, `this`, constructores, despacho estático) *por encima* del listón típico estudiantil; higiene profesional de build/CI; y un corpus de pruebas franco respecto al alcance (el módulo de clases se ejercita en `28`/`30` mientras los casos incompletos se marcan como de rechazo esperado).

**Debilidades / deuda técnica / riesgos:**

1. **Temporales globales → no seguro para recursión.** Los temporales y locales se emiten como **etiquetas globales de `.data`** (`d_<func>_<nombre>`). Una llamada recursiva o reentrante pisaría los temporales del llamador. Es un riesgo real de equivalencia semántica para cualquier programa recursivo. *(Evidencia: el esquema de etiquetas globales en `AnalizadorIRMIPS`/`EmisorDatosMIPS`, y los volcados `d_<func>_<nombre>: .word 0` en los `programa/salida/*.asm` versionados.)* ✅ *Documentado en el código en esta pasada.*
2. **El asignador de registros no hace spilling.** `AdministradorRegistros.obtenerRegistro()` **lanza** `CompiladorInternoException("No hay registros temporales… $t0-$t5")` al agotarse el pool de 6 registros — las expresiones complejas fallan la compilación en vez de derramar.
3. **Arreglos 2-D incompletos.** El propio ejemplo del enunciado `11_arreglos_enunciado.chip` (`int ~ matriz <<2,2>>`) está en la lista **`programasInvalidos`** de la suite — es decir, se espera que el compilador lo *rechace*, pese a que "arreglos bidimensionales" figura en el alcance. Las operaciones de arreglos 1-D existen en el IR (`DECL_ARRAY`, `STORE_ARRAY`).
4. **Optimizador basado en cadenas.** `OptimizadorMIPS` parsea **líneas de texto** emitidas en vez de instrucciones estructuradas; frágil ante cualquier cambio de formato y difícil de extender.
5. **Fragilidad/huecos de pruebas.** Solo existen pruebas de integración de programa completo; las aserciones hacen match de subcadenas de IR/MIPS (p. ej. `mips.contains("move $t0, $t2")`). Sin pruebas unitarias del optimizador, el asignador de registros, la tabla de símbolos o los traductores individuales.
6. **38 conflictos shift/reduce** silenciados con `-expect 38` — gestionados, pero la gramática sigue siendo ambigua y una edición futura puede enmascarar un conflicto real.
7. **Deuda documental** — el Javadoc boilerplate (ver §6). ✅ *Atendida en esta pasada.*

Sin evidencia de bugs de concurrencia (mono-hilo), inyección o fugas de recursos (los lectores usan try-with-resources). La complejidad está contenida.

### 8. Comparación contra el enunciado

Requisitos tomados del enunciado del curso (lenguaje `.chip`, Proyecto #3).

| # | Requisito | Estado | Evidencia |
|---|-----------|--------|-----------|
| a | Preservar/corregir alcances léxico, sintáctico y semántico de Proyectos I/II | ✔ | `lexico.flex`, `sintactico.cup`, `semantico/`; tabla de símbolos por alcances |
| b | Indicar si el fuente es generado por la gramática (gramática + sintaxis + semántica) | ✔ | `resultado_sintactico.txt`; conjunción de aceptación en `Compilador.compilar` |
| c | Reportar errores léxicos/sintácticos/semánticos con **recuperación en modo pánico** | ✔ | producciones `error` + `siguienteTokenConRecuperacion`; `errores_report.txt`; pruebas `26/27` |
| d | Generar código intermedio de tres direcciones como base de traducción | ✔ | `GeneradorCodigoIntermedio`; salida `.ic`; prueba `generaCodigoIntermedioEsperado` |
| e | Escribir `.asm` (mismo nombre base) con MIPS **semánticamente equivalente**, ejecutable en QtSPIM | ✔ | `mips/`, `EscritorMIPS`; prueba `generaMipsConEstructuraEsperada`; `.asm` de muestra en `programa/salida/` |
| f | Tipos de dato `int, float, bool, char, string` | ✔ | enum `TipoDato`; chequeos en `AnalizadorSemantico` |
| g | Expresiones con precedencia correcta (arit/rel/lóg, `^`, `%`) | ✔ | precedencia CUP; prueba afirma `5*2` antes que `+` |
| h | Control de flujo: `if/else`, `do-while`, `switch/case/default`, `break` | ✔ | `IfNodo`, `WhileNodo`, `SwitchNodo`, `BreakNodo`; `TraductorControlMIPS` |
| i | Funciones con params/retorno; `__main__` obligatorio | ✔ | `FuncionNodo`, `TraductorFuncionesMIPS`; chequeo de único main |
| j | E/S `cin`/`cout` | ✔ | `EntradaNodo`/`SalidaNodo`; `TraductorIOMIPS` (syscalls) |
| k | **Arreglos 2-D** | 🟡 | 1-D funciona (`DECL_ARRAY`/`STORE_ARRAY`); el ejemplo 2-D del enunciado está en el conjunto de **rechazados** |
| l | Clases: campos, `new`/heap, acceso por offset, `this`, constructores, despacho estático | 🟡 → ⭐ | `ClaseNodo`, `TraductorObjetosMIPS`, pruebas `28`/`30`; módulo **bonus**, pero **sin herencia/polimorfismo** |
| m | Suite de pruebas automatizada | ✔ | `CompiladorTest` — 36 ejecuciones |
| n | Artefactos de reporte (tokens, tabla de símbolos, errores, veredicto) | ✔ | `reporte/`, muestras en `salida/` |
| o | **CI/CD** | ⭐ | `ci.yml` — no exigido por el enunciado |
| p | **JAR ejecutable + build de Maven con codegen** | ⭐ | plugins shade/antrun/jflex/build-helper |
| q | **Optimización peephole** | ⭐ | `OptimizadorMIPS` — más allá de "MIPS equivalente" |

**Afirmación de "36 pruebas" — verificada estructuralmente:** `CompiladorTest` = 5 `@Test` + `@ParameterizedTest programasValidos` (14 archivos) + `@ParameterizedTest programasInvalidos` (17 archivos) = **36 ejecuciones**, coincidiendo con la documentación. (Conté las listas `@MethodSource` directamente; el build en sí no se ejecutó aquí — ver nota de verificación.)

### 9. Nivel de completitud

**Global: ~90% del alcance obligatorio, más trabajo bonus significativo.**

- **Núcleo obligatorio (léx/sint/sem + modo pánico + IR + MIPS + tipos + expresiones + control de flujo + funciones + E/S + pruebas):** ~**100%** — todo presente con evidencia y (según el CI del proyecto) pruebas en verde.
- **Arreglos 2-D:** ~**40%** — 1-D funcional, 2-D rechazado. Es el déficit más claro *frente al alcance declarado*.
- **Clases (bonus):** ~**70%** — estructura + uso + MIPS funcionan; herencia/polimorfismo ausentes (por límite de tiempo).
- **Extras de ingeniería (CI, JAR ejecutable, optimizador, salidas de muestra):** superan lo requerido.

Un número ponderado único es inevitablemente subjetivo; **~90% de completitud obligatoria con un hueco documentado en arreglos y un módulo bonus de clases parcialmente completo** es el resumen honesto.

### 10. Habilidades demostradas (orientado a reclutadores)

**Compiladores / Ingeniería de lenguajes** — análisis léxico con JFlex, parsing LALR con CUP, **recuperación en modo pánico**, diseño de AST, IR de tres direcciones, generación de código destino, asignación de registros, convenciones de llamada, layout de objetos en heap, optimización peephole.

**Backend / Sistemas** — ensamblador MIPS, modelado de memoria/registros, syscalls, rutas de enteros + punto flotante, disciplina de ABI/pila.

**Ingeniería de software** — descomposición en paquetes por SRP, patrones de diseño (Fachada, Composite, despacho estilo Strategy, DTO), inmutabilidad/programación defensiva, excepciones propias con ubicación, genéricos, callbacks funcionales (`Consumer`, referencias a métodos).

**Build y DevOps** — build de Maven multi-plugin con generación de fuentes y empaquetado JAR; **CI con GitHub Actions** en JDK 17.

**Pruebas** — suite JUnit 5 parametrizada sobre un corpus válido/inválido; aserciones sobre la salida IR y MIPS.

**Herramientas** — flujo de trabajo Git (41 commits), configuraciones de depuración/tareas de VS Code, salidas de muestra versionadas.

*(No se reclaman habilidades de frontend, BD o cloud — no hay evidencia de ellas.)*

---

## Parte B — Informe de evaluación técnica

### B.1 Evaluación técnica

Es un **proyecto de sistemas sustancial**, no pegamento CRUD. Demuestra construcción de compiladores de extremo a extremo con un backend MIPS funcional y un modelo de runtime OO — material que exige entender autómatas, parsing LR, sistemas de tipos, IR y ABIs de ensamblador simultáneamente. La ingeniería alrededor del código (backend modular, build de Maven con codegen, CI, corpus de pruebas) está por encima del típico trabajo de curso. Lo que lo separa de "excelente" es el **defecto de diseño de temporales globales** (inseguro para recursión), el **asignador de registros sin spilling**, los **arreglos 2-D incompletos** y una **documentación/pruebas que no están a la altura de la ambición del código**.

### B.2 Revisión de arquitectura

El pipeline por fases es la arquitectura correcta y está implementado con limpieza. Las fronteras de fases son explícitas (`ResultadoCompilacion`), y la estructura de fachada-más-traductores del backend es una fortaleza real que permitió agregar el módulo de clases sin tocar el front-end. Dos preocupaciones arquitectónicas: (1) el **modelo de memoria** usa etiquetas globales para todo, lo que acopla la corrección a la no-recursión — un modelo de marcos de pila sería la corrección de fondo; (2) el **optimizador opera sobre cadenas**, en el nivel de abstracción equivocado (debería consumir el IR o una lista estructurada de instrucciones MIPS). Ninguno es fatal para los programas del enunciado, pero ambos son el tipo de cosas que un revisor Staff va a sondear.

### B.3 Calidad del código

Legible, nombrado consistente, bien descompuesto. El mayor detractor de calidad era el **Javadoc boilerplate autogenerado** en casi cada método — reducía la señal y sería marcado en revisión; ✅ *eliminado en esta pasada*. Las pruebas están presentes y son significativas pero **solo de integración y frágiles por cadenas**. La duplicación es menor (emisión entero/flotante). Sin bugs de corrección obvios en las rutas revisadas; el manejo de errores en `Main`/`Compilador` es cuidadoso (validación de rutas, drenaje de tokens ante error fatal del parser, bandera `compilador.debug`).

### B.4 Cumplimiento del enunciado

Ver matriz §8. Requisitos obligatorios cumplidos con evidencia; **arreglos 2-D parciales**; **clases parciales-pero-bonus**; varios ítems superan el alcance (CI, JAR ejecutable, optimizador). El corpus de pruebas es franco sobre los huecos (los casos incompletos se marcan como de rechazo esperado), lo cual es un plus de credibilidad.

### B.5 Riesgos

| Riesgo | Severidad | Nota |
|--------|-----------|------|
| La recursión pisa temporales globales | **Alta** | Cualquier programa `.chip` recursivo puede producir MIPS semánticamente incorrecto |
| El agotamiento de registros aborta la compilación | **Media** | Las expresiones complejas lanzan en vez de derramar |
| Arreglos 2-D rechazados pese a estar en alcance | **Media** | Desajuste entre alcance e implementación |
| Fragilidad del optimizador basado en cadenas | **Baja-Media** | Se rompe ante cambios de formato; difícil de extender |
| Ambigüedad de gramática oculta por `-expect 38` | **Baja** | Ediciones futuras pueden enmascarar conflictos reales |
| Deuda documental (Javadoc boilerplate) | **Baja** | ✅ Atendida en esta pasada |

### B.6 Recomendaciones

1. Hacer el runtime **seguro para recursión**: asignar temporales/locales en **marcos de pila** por llamada, no en `.data` global.
2. Agregar **spilling de registros** (derramar a la pila) o un asignador *linear-scan*.
3. Completar los **arreglos 2-D** de extremo a extremo (o quitarlos explícitamente del alcance declarado para mantener honesto el desajuste alcance/implementación).
4. Re-basar el **optimizador sobre instrucciones estructuradas** y agregar peepholes de store-muerto/propagación de constantes.
5. Agregar **pruebas unitarias** de `TablaDeSimbolos`, `AdministradorRegistros`, `OptimizadorMIPS` y cada traductor; agregar pruebas **golden-file** de `.asm`.
6. ✅ *Hecho —* **Javadoc boilerplate eliminado**. Seguimiento: documentar de verdad los métodos no obvios (switch de `GeneradorMIPS.traducir`, `AnalizadorSemantico.evaluarTipo`, el envoltorio de recuperación en modo pánico).
7. Opcional: agregar una nota de **"qué NO se generó y por qué"** en `errores_report.txt` para los casos de agotamiento de registros.

### B.7 Refactorizaciones sugeridas

- Extraer un `EmisorAritmetico` compartido para la emisión duplicada entero/flotante en `TraductorOperacionesMIPS`.
- Introducir un tipo-valor `MipsInstr` para que `OptimizadorMIPS` deje de parsear cadenas.
- Reemplazar la asignación de etiquetas globales en `AnalizadorIRMIPS`/`EmisorDatosMIPS` por un modelo de offsets de marco.
- Centralizar los bancos de registros (`$t*`, `$f*`) tras una sola abstracción de asignador con soporte de spilling.

### B.8 Priorización de mejoras

| # | Mejora | Impacto | Esfuerzo | Prioridad |
|---|--------|---------|----------|-----------|
| 1 | Temporales en marco de pila (seguro para recursión) | Alto | Alto | **P0** |
| 2 | Spilling de registros | Alto | Medio | **P0** |
| 3 | Completar arreglos 2-D (o quitar del alcance) | Medio | Medio | **P1** |
| 4 | Pruebas unitarias + golden-file | Medio | Medio | **P1** |
| 5 | Optimizador sobre instrucciones estructuradas | Medio | Medio | **P2** |
| 6 | ~~Eliminar Javadoc boilerplate~~ ✅ hecho / docs reales en métodos no obvios | Bajo-Medio | Bajo | **P1** |
| 7 | Más peepholes (store muerto, propagación de constantes) | Bajo | Medio | **P3** |
| 8 | Herencia + despacho dinámico (el slot de vtable ya está reservado) | Medio | Alto | **P3** |

---

## Parte C — Veredicto de reclutador

### Nivel estimado del desarrollador: **Mid** (Mid sólido; rozando Mid+ por el conocimiento de dominio, frenado por la madurez de pruebas/documentación)

**Por qué Mid y no Junior/Junior+ (basado en evidencia):**

- Implementó un **compilador multi-fase completo con un backend MIPS real** — parsing LALR, recuperación en modo pánico, una tabla de símbolos tipada por alcances, un IR de tres direcciones, asignación de registros, una convención de llamada por pila y un **modelo de objetos OO en heap**. Esta amplitud de fundamentos de CS, aplicada y funcionando, está muy por encima del alcance Junior.
- **Arquitectura deliberada:** un backend fachada que delega en siete traductores de responsabilidad única, DTOs inmutables entre fases, excepciones propias con ubicación. Es intención de diseño, no accidente.
- **Madurez de ingeniería alrededor del código:** build de Maven que *genera* el scanner/parser y empaqueta un JAR ejecutable, **CI sobre el JDK correcto**, un **corpus** curado válido/inválido y salidas de muestra versionadas.

**Por qué no Mid+/Senior (basado en evidencia):**

- Un **defecto de corrección a nivel de diseño** (temporales globales → inseguro para recursión) y un **asignador de registros que aborta en vez de derramar** muestran que el modelo de runtime no se llevó a plena generalidad.
- Las **pruebas son solo de integración y frágiles por cadenas**; sin pruebas unitarias de los componentes más delicados. Un Mid+/Senior probaría directamente el optimizador, el asignador y la tabla de símbolos.
- La **documentación era boilerplate autogenerado** — una señal de madurez/artesanía que los revisores ponderan (ahora eliminada, pero el código aún carece de docs reales en sus métodos más difíciles).
- Un **ítem del alcance declarado (arreglos 2-D) está sin implementar** y silenciosamente marcado como prueba de rechazo.

### Impresión en los primeros cinco minutos en GitHub

> **Respuesta honesta:** Fuerte y creíble, con un par de costuras visibles.
>
> En los primeros 30 segundos el repo se lee *profesional*: un README con badges, un layout `programa/` con paquetes claramente nombrados (`ast`, `semantico`, `intermedio`, `mips`, `pipeline`, `reporte`), un build de Maven, un workflow de CI con buena pinta, salidas de muestra versionadas y un LICENSE. Un reclutador o ingeniero piensa de inmediato "esta persona construyó un compilador de verdad, no un clon de tutorial". Abrir `mips/` y ver siete traductores enfocados más un optimizador peephole *sube* la estimación — es trabajo de sistemas con criterio.
>
> Luego aparecen las costuras. *(Al momento de la revisión original, al hojear cualquier clase el **Javadoc boilerplate repetitivo** resaltaba y abarataba levemente la impresión; eso ya fue eliminado.)* Un revisor cuidadoso que abre `CompiladorTest` nota que las pruebas son **aserciones de cadenas de programa completo** y que `11_arreglos_enunciado.chip` está en la lista de **inválidos** — una señal de que los arreglos 2-D no funcionan. Quien conozca compiladores preguntará "¿dónde viven los locales/temporales?" y, al encontrar que son **etiquetas globales de `.data`**, notará de inmediato que no puede hacer recursión de forma segura.
>
> **Neto:** en cinco minutos aterriza como una **pieza de portafolio Mid sólida** — claramente contratable, claramente capaz de trabajo de sistemas difícil, y con margen para crecer en disciplina de pruebas, generalidad del runtime y artesanía documental. Conseguiría una entrevista en la mayoría de las empresas nombradas, y el CI funcional más un corpus de pruebas franco jugarían a favor del candidato. Las victorias de credibilidad más rápidas antes de difundirlo ampliamente: corregir/anotar la limitación de recursión, eliminar el Javadoc boilerplate, y completar o descartar explícitamente los arreglos 2-D.

---

*Preparado como una revisión basada en evidencia. Cada hallazgo mapea a un archivo del repositorio; no se asumió ni inventó funcionalidad. El build no se ejecutó en la revisión (JDK 11 / sin Maven en el entorno); el propio CI de GitHub Actions sobre JDK 17 es la señal autoritativa de build en verde.*
