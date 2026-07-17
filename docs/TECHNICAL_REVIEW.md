# TECHNICAL_REVIEW — Generacion-Codigo-Destino-MIPS

> Auditoría técnica (Framework TPRF). Revisión completa y detallada en [`INFORME_TECNICO.md`](../INFORME_TECNICO.md) (ES) / [`TECHNICAL_REPORT.md`](../TECHNICAL_REPORT.md) (EN). Este documento consolida el veredicto, el cumplimiento del enunciado y los hallazgos de higiene verificados en esta pasada (2026-07-16).

## Veredicto

| Dimensión | Evaluación |
|---|---|
| Nivel demostrado | **Mid** (roza Mid+ en dominio de compiladores) |
| Arquitectura | Pipeline por fases con paquetes de responsabilidad única; backend MIPS con Façade + 7 traductores especializados |
| Calidad de código | Alta legibilidad; ~5,200 LOC Java tras eliminar Javadoc boilerplate |
| Pruebas | ✅ Verificado por ejecución: 36/36 pasan (`target/surefire-reports/pipeline.CompiladorTest.txt`), pero solo integración, aserciones sobre strings |
| CI/CD | ✅ GitHub Actions real (`.github/workflows/ci.yml`: Temurin 17, `mvn -B verify`) |

## Cumplimiento del enunciado (`docs/Proyecto 3 Compiladores.md`)

| Requisito | Estado | Evidencia |
|---|---|---|
| a) Preservar alcances P1/P2 (léxico, sintáctico, semántico) | ✅ | `lexico.flex`, `sintactico.cup`, `semantico/AnalizadorSemantico` |
| b) Veredicto de aceptación (gramática + sintaxis + semántica) | ✅ | `pipeline/Compilador.compilar` → conjunción de 4 condiciones; `salida/resultado_sintactico.txt` |
| c) Errores con recuperación en modo pánico | ✅ | Producciones `error` en `sintactico.cup` + heurística de inserción de `!` |
| d) Emitir `.asm` MIPS desde el código de 3 direcciones, ejecutable en QtSPIM | 🟦 estático / 🟨 QtSPIM | `mips/GeneradorMIPS` + `salida/30_clase_metodos.asm`; la corrida en QtSPIM no se re-ejecutó en esta revisión |
| Extra: clases (estructura y uso) en las 4 fases | ✅ parcial | `semantico/ClaseInfo`, `mips/TraductorObjetosMIPS`; sin herencia ni dispatch dinámico |
| Arreglos 2-D (ejemplo del enunciado) | ⛔ | `test/11_arreglos_enunciado.chip` listado como caso inválido |

## Fortalezas

1. Backend MIPS completo y modular: asignador de registros (`AdministradorRegistros`), convención de llamada por pila, modelo de objetos en heap con slot de vtable reservado, optimizador peephole.
2. Recuperación de errores diseñada (no accidental): puntos de sincronización + drenado de tokens tras error fatal para mantener reportes completos.
3. Ingeniería alrededor del código: build Maven que genera scanner/parser, fat JAR, corpus de 31 programas válidos/inválidos, CI verde.

## Debilidades y riesgos

| Riesgo | Severidad |
|---|---|
| Temporales como etiquetas globales `.data` → **no apto para recursión** (documentado en `AnalizadorIRMIPS.construirTablaDirecciones`) | Alta |
| Asignador de 6 registros sin *spilling* → expresiones complejas abortan | Media |
| Arreglos 2-D dentro de alcance pero rechazados | Media |
| Optimizador basado en strings, frágil ante cambios de formato | Baja-Media |

## Higiene del repositorio (pasada 2026-07-16)

- ⛔ 4 archivos basura **trackeados en git**: `programa/src/java/{ast,intermedio,mips,semantico}/.fuse_hidden*` (0 bytes) → eliminar con `git rm`.
- 🟨 README afirmaba "11,000 líneas"; corregido a ~5,200 (medido tras limpieza de Javadoc).
- ✅ Sin TODO/FIXME, sin secretos, `.gitignore` completo, `target/` no trackeado, LICENSE Apache-2.0.
- 🟨 Placeholders de screenshots en README sin imágenes reales en `docs/img/`.

## Recomendaciones priorizadas

Ver [`IMPROVEMENT_ROADMAP.md`](IMPROVEMENT_ROADMAP.md). P0: temporales en stack frames (recursión) y spilling de registros.
