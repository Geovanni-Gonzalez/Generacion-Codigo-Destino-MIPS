<div align="center">

# Generación de Código Destino MIPS
### Un compilador multi-fase con backend MIPS para el lenguaje `.chip`

**Compilador académico escrito desde cero en Java que toma un programa fuente en el lenguaje del curso (`.chip`), ejecuta el pipeline completo léxico → sintáctico → semántico, emite código intermedio de tres direcciones y genera código ensamblador MIPS ejecutable en QtSPIM/MARS.**

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Build](https://img.shields.io/badge/build-Maven-blue)](https://maven.apache.org/)
[![Lexer](https://img.shields.io/badge/lexer-JFlex%201.9.1-informational)](https://jflex.de/)
[![Parser](https://img.shields.io/badge/parser-Java%20CUP%20LALR-informational)](http://www2.cs.tum.edu/projects/cup/)
[![Tests](https://img.shields.io/badge/tests-JUnit%205%20%C2%B7%2036%20casos-success)](#pruebas)
[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-black)](.github/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-ver%20LICENSE-lightgrey)](LICENSE)

**🌐 Español** · [English](README.md)  |  📄 Informe técnico: [Español](INFORME_TECNICO.md) · [English](TECHNICAL_REPORT.md)

</div>

---

## Resumen

`Generacion-Codigo-Destino-MIPS` es un compilador completo, de extremo a extremo, para un pequeño lenguaje imperativo (`.chip`) orientado a enseñar conceptos de *configuración de chips / sistemas empotrados*. Corresponde al **Proyecto #3** del curso *Compiladores e Intérpretes* (IC5701, Tecnológico de Costa Rica) y reutiliza y extiende el front-end construido en los Proyectos I y II.

No es un compilador de juguete: implementa todas las fases clásicas de un compilador real — un analizador léxico generado con JFlex, un analizador sintáctico LALR generado con Java CUP con **recuperación en modo pánico**, un analizador semántico con **tipado fuerte** y alcances, un árbol de sintaxis abstracta, una **representación intermedia de tres direcciones** y un **generador de código MIPS** modular con asignación de registros, modelo de objetos en heap, convención de llamada por pila y un paso de optimización *peephole*.

Aproximadamente **11.000 líneas de Java escritas a mano** en 74 archivos fuente, más una gramática CUP de ~1.700 líneas y una especificación JFlex de ~260 líneas, todo integrado por un build de Maven que genera el scanner/parser, produce un JAR ejecutable y corre una suite JUnit 5 en CI.

---

## Problema que resuelve

Un equipo quiere un lenguaje imperativo ligero para configurar chips y construir sistemas empotrados. Este proyecto entrega el **back-end (generación de código destino)** de la cadena de herramientas de ese lenguaje. Concretamente, dado un archivo fuente `.chip` el compilador debe:

1. Preservar y corregir los alcances léxico, sintáctico y semántico de los Proyectos I y II.
2. Indicar si el fuente puede *ser generado por la gramática*, considerando gramática, sintaxis **y** semántica.
3. Reportar errores léxicos, sintácticos y semánticos mediante **recuperación en modo pánico** (reportar la mayor cantidad de errores en vez de abortar al primero).
4. Emitir un archivo MIPS **semánticamente equivalente** al fuente y ejecutable en QtSPIM — producido *a partir* del propio código intermedio del compilador.

Si el programa es válido, el compilador escribe el código intermedio (`.ic`) y el código destino (`.asm`). Si es inválido, no se genera código y se reportan los errores.

---

## Objetivos

**General.** Construir, en Java y con JFlex y CUP, el generador de código destino MIPS del lenguaje `.chip`, partiendo del código de tres direcciones, de modo que la salida sea semánticamente equivalente al fuente y ejecutable en QtSPIM.

**Específicos.**

- Preservar/corregir las fases léxica, sintáctica y semántica de los proyectos previos.
- Determinar si un fuente es generado por la gramática (sintaxis + semántica).
- Reportar errores léxicos/sintácticos/semánticos mediante recuperación en modo pánico.
- Generar código intermedio de tres direcciones como base de la traducción.
- Escribir un archivo `.asm` (mismo nombre base que el fuente) con MIPS equivalente.
- Incorporar el manejo de clases (estructura y uso) a través de las cuatro fases y traducirlo a MIPS.
- Validar todo con una suite de pruebas automatizada.

---

## Funcionalidades

| Área | Qué hace |
|------|----------|
| **Análisis léxico** | Scanner JFlex con seguimiento de línea/columna, manejo de estado de comentarios (incluida la detección de *comentario multilínea sin cerrar*) y reporte por token. |
| **Análisis sintáctico** | Gramática LALR (Java CUP). 38 conflictos shift/reduce benignos resueltos por desplazamiento, gestionados con `-expect 38`. |
| **Recuperación de errores** | **Modo pánico** real: decenas de producciones `error` sincronizan a puntos seguros (`:|`, `!`), más una heurística que *inserta* un terminador de sentencia faltante (`!`) para seguir analizando. |
| **Semántica** | Tabla de símbolos por alcances apilados, tipado fuerte, condiciones solo booleanas, verificación de `return` obligatorio, unicidad de `__main__`. |
| **Tipos** | `int`, `float`, `bool`, `char`, `string`. |
| **Expresiones** | Operadores aritméticos, relacionales y lógicos con precedencia correcta (incl. `^` potencia, `%` módulo). |
| **Control de flujo** | `if/else`, `do-while`, `switch/case/default`, `break`. |
| **Funciones** | Parámetros, valores de retorno, procedimiento `__main__` obligatorio. |
| **Arreglos** | Arreglos unidimensionales (declaración, escritura/lectura indexada). *(Los 2-D se parsean pero no están soportados por completo — ver [Limitaciones](#limitaciones).)* |
| **E/S** | `cin` / `cout` mapeados a syscalls de MIPS. |
| **Clases** | Layout de campos, `new` con asignación en heap (`sbrk`, syscall 9), acceso a campos por offset, `this`, constructores y **despacho estático de métodos**. |
| **Código intermedio** | Código de tres direcciones legible, escrito en `.ic`. Las constantes **no** se pliegan de forma intencional, dejando visible cada temporal y operación. |
| **Backend MIPS** | Emisión de `.data`/`.text`, rutas de enteros y punto flotante, convención de llamada por pila y un **optimizador peephole** (`sw`/`lw` redundante → `move`). |
| **Herramientas** | Build de Maven (autogenera scanner/parser), JAR ejecutable (shade), suite JUnit 5, CI con GitHub Actions. |

---

## Arquitectura

El compilador es una **arquitectura por fases (pipeline)** clásica. Cada fase es un paquete independiente con una única responsabilidad, y un orquestador delgado (`pipeline.Compilador`) los conecta y transporta los resultados en un DTO (`ResultadoCompilacion`).

```
              ┌──────────────────────────────────────────────────────────────┐
  fuente ───► │  LÉXICO (JFlex)        → tokens + errores léxicos             │
  .chip       │  SINTÁCTICO (CUP,LALR) → AST (ProgramaNodo) + errores sint.   │
              │      │  entrelazado con                                       │
              │      └► ANÁLISIS SEMÁNTICO → tabla de símbolos + errores sem. │
              │  GEN. INTERMEDIO       → tres direcciones (List<Instruccion>) │
              │  GEN. MIPS             → líneas .asm                          │
              │      ├ AnalizadorIRMIPS  (recolecta decls, propaga tipos)     │
              │      ├ Traductor*MIPS    (traductores por operación)          │
              │      ├ Emisor*MIPS       (emisores .data / .text)             │
              │      └ OptimizadorMIPS   (paso peephole)                     │
              └──────────────────────────────────────────────────────────────┘
                             │                    │                 │
                     tokens_report.txt      <base>.ic         <base>.asm
                     tabla_simbolos.txt   (intermedio)    (código destino MIPS)
                     errores_report.txt
                     resultado_sintactico.txt
```

**Capas / paquetes**

| Paquete | Responsabilidad |
|---------|-----------------|
| `lexico/` (JFlex) | Especificación de tokens → `MiLexer` generado. |
| `sintactico/` (CUP) | Gramática LALR + recuperación en modo pánico → `Parser`, `sym` generados. |
| `ast/` | ~30 tipos de nodo inmutables que forman el árbol de sintaxis abstracta (Composite). |
| `semantico/` | `TablaDeSimbolos` (pila de alcances), `AnalizadorSemantico`, `Simbolo`, `ClaseInfo`. |
| `intermedio/` | `GeneradorCodigoIntermedio`, `Instruccion`, `Operacion` (modelo IR + generación). |
| `mips/` | Backend: fachada `GeneradorMIPS` que despacha a `Traductor*MIPS`, `AdministradorRegistros`, `Emisor*MIPS`, `OptimizadorMIPS`. |
| `reporte/` | Escritores de reportes/artefactos (`EscritorReportes`, `EscritorCodigo`, `EscritorMIPS`, `ReportadorErrores`). |
| `pipeline/` | Orquestador `Compilador`, DTO `ResultadoCompilacion`, `CompiladorInternoException`. |

---

## Tecnologías

**Lenguaje y plataforma:** Java 17 · JVM.
**Build y empaquetado:** Apache Maven · `maven-antrun-plugin` (ejecuta CUP) · `jflex-maven-plugin` · `build-helper-maven-plugin` (agrega fuentes generadas) · `maven-compiler-plugin` · `maven-surefire-plugin` · `maven-shade-plugin` (JAR ejecutable).
**Herramientas de construcción de compiladores:** JFlex 1.9.1 (generador léxico) · Java CUP 11a/11b (generador sintáctico LALR) · `java-cup-runtime` (runtime del parser).
**Pruebas:** JUnit Jupiter 5.10.2 (pruebas parametrizadas).
**Destino / simulación:** ensamblador MIPS · QtSPIM / MARS.
**CI y herramientas:** GitHub Actions (Temurin JDK 17, `mvn verify`) · configuraciones de VS Code · Git.
**Formatos de datos:** `.chip` (fuente), `.ic` (intermedio), `.asm` (destino), `.txt` (reportes).

---

## Conceptos técnicos aplicados

- **Teoría de compiladores:** análisis léxico por autómatas finitos, **análisis LALR**, recuperación en modo pánico, construcción de AST, **representación intermedia de tres direcciones**, generación de código destino.
- **Análisis semántico:** tabla de símbolos por alcances apilados (pila de mapas hash), tipado estático fuerte, inferencia de tipos sobre árboles de expresión, verificación de rutas de control/retorno.
- **Generación de código:** asignación de registros (pool tipo *free-list* de `$t0–$t5`), modelo de memoria (etiquetas globales en `.data`), **convención de llamada por pila** (guardar/restaurar `$ra`, argumentos apilados por el llamador, limpieza en el llamado), **modelo de objetos en heap** (`sbrk`, offsets de campos, palabra de vtable reservada), **optimización peephole**.
- **POO y diseño:** encapsulamiento, composición, polimorfismo por despacho de nodos, inmutabilidad/copias defensivas (`Collections.unmodifiableList`), excepciones propias con ubicación en el fuente.
- **Patrones de diseño:** Pipeline/fases, Fachada (`Compilador`, `GeneradorMIPS`), Composite (AST), recorrido estilo Visitor, traductores por operación estilo Strategy, DTO/objetos de resultado, emisores estilo Builder.
- **Ingeniería de software:** descomposición en paquetes guiada por SRP, genéricos y colecciones, callbacks funcionales (`Consumer` como sumidero de errores, referencias a métodos como fábricas de etiquetas), automatización de build, **CI/CD**, pruebas parametrizadas.

---

## Capturas (placeholders)

> Agrega imágenes a una carpeta `docs/img/` y referéncialas aquí.

| Vista | Placeholder |
|-------|-------------|
| Ejecución CLI y artefactos generados | `docs/img/cli-run.png` |
| `.asm` generado en QtSPIM | `docs/img/qtspim-registers.png` |
| Reporte de errores (modo pánico) | `docs/img/error-report.png` |

Los artefactos de muestra generados (tokens, tabla de símbolos, errores, IR y MIPS) para `30_clase_metodos.chip` están versionados en [`programa/salida/`](programa/salida/) como referencia.

---

## Requisitos

- JDK **17** o superior (`JAVA_HOME` configurado, `java`/`mvn` en el `PATH`)
- Apache **Maven 3.9+**
- *(Opcional)* **QtSPIM** o **MARS** para ejecutar el `.asm` generado

---

## Instalación

```bash
git clone https://github.com/Geovanni-Gonzalez/Generacion-Codigo-Destino-MIPS.git
cd Generacion-Codigo-Destino-MIPS/programa
mvn clean package
```

El build ejecuta JFlex y CUP para generar el scanner/parser en `target/generated-sources/`, compila todo, corre las pruebas y empaqueta un JAR ejecutable con `maven-shade-plugin`.

> Un build **exitoso** termina con `BUILD SUCCESS` y CUP reporta **38 conflicts detected** (esperado). Cualquier otro número detiene la generación por diseño.

---

## Ejecución

```bash
java -jar target/proyecto-compiladores-1.0-SNAPSHOT.jar <fuente.chip> [directorio_salida]
```

- `<fuente.chip>` — **obligatorio**, ruta al archivo fuente.
- `[directorio_salida]` — **opcional**, por defecto `salida/`.

Ejemplo:

```bash
java -jar target/proyecto-compiladores-1.0-SNAPSHOT.jar test/30_clase_metodos.chip salida
```

**Salidas generadas** (en el directorio de salida):

| Archivo | Contenido |
|---------|-----------|
| `tokens_report.txt` | Tokens reconocidos (tabla, lexema, línea, columna). |
| `tabla_simbolos.txt` | Tabla de símbolos del análisis. |
| `errores_report.txt` | Errores léxicos / sintácticos / semánticos, con conteos. |
| `resultado_sintactico.txt` | Veredicto de aceptación. |
| `<base>.ic` | Código intermedio de tres direcciones. |
| `<base>.asm` | Código destino MIPS (ejecutable en QtSPIM). |

El archivo destino conserva el nombre base del fuente (`test.chip → test.asm`). Si el programa tiene errores, **no** se producen `.ic` ni `.asm`.

---

## Estructura del proyecto

```txt
Generacion-Codigo-Destino-MIPS/
├─ programa/
│  ├─ pom.xml
│  ├─ src/
│  │  ├─ java/
│  │  │  ├─ Main.java              # Punto de entrada CLI
│  │  │  ├─ ast/                   # ~30 tipos de nodo AST (Composite)
│  │  │  ├─ semantico/             # análisis por alcances y tipado fuerte
│  │  │  ├─ intermedio/            # IR de tres direcciones + generación
│  │  │  ├─ mips/                  # backend MIPS (fachada + traductores + optimizador)
│  │  │  ├─ reporte/               # escritores de reportes / código
│  │  │  └─ pipeline/              # orquestación + DTO de resultado
│  │  ├─ lexico/lexico.flex        # especificación JFlex
│  │  ├─ sintactico/sintactico.cup # gramática LALR de CUP (~1.700 líneas)
│  │  ├─ lib/                      # jars de CUP / JFlex incluidos
│  │  └─ test/java/pipeline/       # suite JUnit 5
│  ├─ test/                        # programas .chip (corpus válido e inválido)
│  └─ salida/                      # salidas de muestra generadas
├─ .github/workflows/ci.yml        # CI: JDK 17 + mvn verify
├─ LICENSE
└─ README.md
```

---

## Flujo de funcionamiento

1. **CLI (`Main`)** valida los argumentos y la ruta del fuente, crea el directorio de salida.
2. **`Compilador.compilar`** abre el fuente como UTF-8, construye el `MiLexer` y ejecuta el `Parser` de CUP (el análisis semántico se entrelaza durante el parseo).
3. Si el parseo falla de forma fatal, se drena el flujo de tokens restante para que los reportes de tokens/errores queden completos.
4. La **aceptación** es la conjunción de: sintaxis completa **Y** sin errores léxicos **Y** sin errores sintácticos **Y** sin errores semánticos.
5. Solo si se acepta: la **generación de IR** recorre el AST hacia código de tres direcciones, luego el **generador MIPS** analiza el IR (`AnalizadorIRMIPS`), emite `.data`/`.text`, traduce cada instrucción con los traductores especializados y ejecuta el paso peephole.
6. Los **escritores de reportes** persisten tokens, tabla de símbolos, errores, veredicto, `.ic` y `.asm`.

---

## Decisiones de diseño

- **IR de dos niveles antes de MIPS.** Generar código de tres direcciones primero (en vez de emitir MIPS directo desde el AST) aísla lo específico de máquina en el backend y hace el pipeline testeable a nivel de IR. *(Exigido por el enunciado y aprovechado deliberadamente.)*
- **Sin plegado de constantes en el IR.** Se mantiene verboso a propósito para que temporales y precedencia de operadores sean visibles en `.ic` — mejor para revisión/calificación y para validar el backend.
- **Backend modular.** `GeneradorMIPS` es una fachada que despacha según la operación del IR a traductores enfocados (`TraductorMemoria`, `TraductorOperaciones`, `TraductorControl`, `TraductorIO`, `TraductorFunciones`, `TraductorObjetos`, `TraductorTransferencia`). Cada uno es legible e intercambiable de forma independiente.
- **Modo pánico sobre fallo temprano.** El parser está diseñado para continuar y reportar *muchos* errores, usando producciones `error` de la gramática y la heurística de inserción de terminador.
- **Modelo de objetos con slot de vtable reservado.** La palabra 0 de cada objeto se reserva para una futura vtable; los campos empiezan en el offset 4 — un layout con visión de futuro aunque el despacho sea actualmente estático.
- **Build determinista con versiones fijas** y `-expect 38` para detectar cambios accidentales en la gramática.

---

## Patrones utilizados

Pipeline/Fases · Fachada · Composite (AST) · recorrido estilo Visitor · traductores por operación estilo Strategy · DTO / objeto de resultado · emisores estilo Builder · pool de objetos (asignador de registros).

---

## Desafíos encontrados

- **Conflictos de gramática.** Llegar a una gramática LALR viable con una sintaxis distintiva (`|: :|`, `<| |>`, `<< >>`, `~`, `!`) produjo 38 conflictos shift/reduce que hubo que entender y aceptar como benignos.
- **Recuperación de errores.** Diseñar puntos de sincronización y la heurística de inserción de `!` para que el parser se recupere sin cascadas de errores espurios.
- **Modelo de objetos en MIPS.** Mapear semántica OO (asignación en heap, `this`, offsets, constructores, despacho estático) a MIPS con una convención de llamada por pila.
- **Rutas de flotante vs entero.** Mantener consistentes dos familias de registros/instrucciones (`$t*`/`add` vs `$f*`/`add.s`) a lo largo del backend.

---

## Limitaciones

- Los **arreglos 2-D** del propio ejemplo del enunciado están actualmente **rechazados** (solo los arreglos 1-D se traducen de extremo a extremo).
- **Clases:** sin herencia ni polimorfismo; el acceso a campos dentro de métodos es `this.campo` explícito; los campos deben declararse antes que los métodos; no hay campos/parámetros/retornos de tipo objeto (salvo `this`); no hay `free`; no hay verificación de puntero nulo.
- El **asignador de registros** es un *free-list* de 6 registros (`$t0–$t5`) **sin spilling** — las expresiones muy complejas lanzan un error interno en vez de derramar a memoria.
- Los **temporales son etiquetas globales de `.data`**, por lo que el modelo actual **no es seguro para recursión** (una llamada recursiva pisaría los temporales del llamador). *Esta limitación está documentada en el código en `AnalizadorIRMIPS.construirTablaDirecciones` y `TraductorFuncionesMIPS`.*

---

## Posibles mejoras

- Derramar registros a la pila (o agregar un asignador *linear-scan*) en vez de fallar cuando se agotan `$t0–$t5`.
- Mover temporales/locales a **marcos de pila** por llamada para habilitar recursión y reentrancia.
- Completar los **arreglos 2-D** de extremo a extremo y manejar límites.
- Reemplazar el optimizador peephole basado en texto por un paso sobre instrucciones estructuradas; agregar más peepholes (eliminación de stores muertos, propagación de constantes).
- Probar unitariamente traductores individuales, el optimizador y el asignador de registros, no solo la integración de programa completo.

## Trabajo futuro

Herencia y despacho dinámico (el slot de vtable ya está reservado) · composición de objetos · una pequeña biblioteca estándar · niveles opcionales de optimización/plegado de constantes · un arnés de pruebas *golden-file* para la salida `.asm`.

---

## Habilidades demostradas

**Compiladores / Ingeniería de lenguajes** — generación de léxico y parser LALR (JFlex/CUP), recuperación en modo pánico, diseño de AST, IR de tres direcciones, generación de código destino, asignación de registros, convenciones de llamada, layout de objetos en heap.

**Backend / Sistemas** — generación de ensamblador MIPS, modelado de memoria y registros, syscalls, manejo de punto flotante, un optimizador peephole.

**Ingeniería de software** — descomposición limpia en paquetes (SRP), patrones de diseño (Fachada, Composite, Strategy, DTO), inmutabilidad/programación defensiva, excepciones propias con ubicación, genéricos y callbacks funcionales.

**Build y DevOps** — build de Maven multi-plugin con generación de fuentes y empaquetado JAR, CI con GitHub Actions en JDK 17.

**Pruebas** — suite JUnit 5 parametrizada sobre un corpus de programas válidos/inválidos (36 ejecuciones), aserciones sobre la salida IR y MIPS.

---

## Pruebas

```bash
mvn test        # corre la suite JUnit
mvn verify      # verificación completa (usada por CI)
```

La suite (`src/test/java/pipeline/CompiladorTest.java`) corre **36 ejecuciones**: 5 pruebas explícitas (programa mínimo, estructura del IR, estructura del MIPS, clase básica, métodos/constructor/despacho) más 14 programas válidos y 17 inválidos impulsados por `@ParameterizedTest`. Las aserciones verifican los veredictos de aceptación e inspeccionan la salida IR y MIPS.

---

## Lecciones aprendidas

- Una frontera de fases limpia (AST → IR → MIPS) rinde frutos: el backend se volvió testeable y el modelo de objetos se agregó sin perturbar el front-end.
- La recuperación de errores es un problema de *diseño*, no un añadido — los puntos de sincronización deben elegirse deliberadamente.
- Traductores pequeños con una sola responsabilidad son mucho más fáciles de razonar que un generador monolítico.
- Elegir dónde **no** optimizar (sin plegado de constantes en el IR) puede ser tan valioso como optimizar, cuando la meta es la inspeccionabilidad.

---

## Licencia

Ver [`LICENSE`](LICENSE).

## Autor

**Geovanni González Aguilar** — Ingeniería en Computación, Tecnológico de Costa Rica.
GitHub: [Geovanni-Gonzalez](https://github.com/Geovanni-Gonzalez)
