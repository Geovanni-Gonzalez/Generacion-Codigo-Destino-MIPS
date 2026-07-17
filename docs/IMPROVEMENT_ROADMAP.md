# IMPROVEMENT_ROADMAP — Generacion-Codigo-Destino-MIPS

Backlog priorizado. Impacto/Esfuerzo: Alto/Medio/Bajo.

## Quick Wins

| # | Mejora | Impacto | Esfuerzo | Prioridad |
|---|---|---|---|---|
| 1 | `git rm` de los 4 archivos `.fuse_hidden*` trackeados en `programa/src/java/{ast,intermedio,mips,semantico}/` y añadir `.fuse_hidden*` al `.gitignore` | Medio | Bajo | P0 |
| 2 | Añadir screenshots reales en `docs/img/` (CLI, QtSPIM, reporte de errores) — el README ya tiene los placeholders | Medio | Bajo | P1 |
| 3 | GitHub Topics: `compiler`, `mips`, `jflex`, `cup`, `java`, `lalr-parser`, `code-generation` + descripción del repo | Medio | Bajo | P1 |
| 4 | Badge de CI dinámico (`actions/workflows/ci.yml/badge.svg`) en lugar del badge estático | Bajo | Bajo | P2 |

## Mejoras técnicas

| # | Mejora | Impacto | Esfuerzo | Prioridad |
|---|---|---|---|---|
| 5 | Temporales/locales en stack frames por llamada → habilita recursión (corrige el riesgo de corrección más grave) | Alto | Alto | P0 |
| 6 | Spilling de registros a pila (o allocator linear-scan) en `AdministradorRegistros` | Alto | Medio | P0 |
| 7 | Terminar arreglos 2-D de extremo a extremo, o retirarlos explícitamente del alcance declarado | Medio | Medio | P1 |
| 8 | Tests unitarios para `TablaDeSimbolos`, `AdministradorRegistros`, `OptimizadorMIPS` y cada traductor; tests golden-file del `.asm` | Medio | Medio | P1 |

## Mejoras arquitectónicas

| # | Mejora | Impacto | Esfuerzo | Prioridad |
|---|---|---|---|---|
| 9 | Tipo `MipsInstr` estructurado para que `OptimizadorMIPS` deje de parsear strings; añadir dead-store elimination y propagación de constantes | Medio | Medio | P2 |
| 10 | Extraer `EmisorAritmetico` común para la duplicación int/float en `TraductorOperacionesMIPS` | Bajo | Bajo | P2 |
| 11 | Herencia + dispatch dinámico (el slot de vtable ya está reservado en el layout de objetos) | Medio | Alto | P3 |

## Mejoras de GitHub

Ya presentes: README bilingüe con badges, LICENSE (Apache-2.0), CI, `.gitignore`, informes técnicos EN/ES. Faltan: Topics y descripción (item 3), screenshots (item 2), badge dinámico (item 4).
