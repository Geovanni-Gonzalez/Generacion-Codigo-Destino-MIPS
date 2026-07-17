

Instituto Tecnológico de Costa Rica
Ingeniería en Computación
Compiladores e Intérpretes
## Semestre I, 2026
## Profesor: Allan Rodríguez Dávila

## Proyecto #3
Generación Código Destino (MIPS)
## Introducción
Un grupo de desarrolladores desea crear un nuevo lenguaje imperativo, ligero, que le
permita realizar operaciones básicas para la configuración de chips, ya que esta es una
industria que sigue creciendo constantemente, y cada vez estos chips necesitan ser
configurados por lenguajes más ligeros y potentes. Es por esto que este grupo de
desarrolladores requiere desarrollar su propio lenguaje para el desarrollo de sistemas
empotrados.
Proyecto a desarrollar
Este proyecto comprende la fase de generación de código destino, el cuál será MIPS, para
la gramática descrita en el Proyecto I y el front-end de compilador generado en los
Proyectos I y II.  Se debe desarrollar el generador de código MIPS a partir del código tres
direcciones generado por el proyecto II.
Un programa escrito para este lenguaje está compuesto por una secuencia de
declaraciones de procedimientos, que contienen diferentes expresiones; todo programa
debe contener exactamente un método main.
Para comprobar el desarrollo de esta fase el programa a presentar deberá tomar un
archivo fuente y realizar lo siguiente:
a) Preservar y corregir los alcances de los Proyectos I y II
b) Indicar si el archivo fuente puede o no ser generado por la gramática. Tomando en
cuenta la gramática, sintaxis y semántica.
c) Reportar los errores léxicos, sintácticos y semánticos encontrados. Debe utilizar la
técnica de Recuperación en Modo Pánico.

d) Escribir en un archivo el código MIPS para el archivo fuente. El código generado
corresponde semánticamente al código fuente.
Gramática, Scanner, Parser, Analizador Semántico y Generador de Código intermedio
La gramática BNF que reconocerá el lenguaje será la descrita en el Proyecto I. Deberá
tomarse como base los archivos jflex y cup del Proyecto II, para utilizarlos como front-end.

Generación de Código MIPS
El desarrollo del generador de código destino debe escribir en un archivo el código MIPS
para el archivo fuente indicado. Debe permitir elegir el archivo y el nombre del archivo
destino, que contendrá el código mips, tendrá el mismo nombre que el archivo fuente
cambiándole la extensión del archivo (ejemplo test.asm). El código generado en el archivo
debe coincidir en forma semántica con el código fuente. La verificación de la ejecución del
código resultante en mips se hará por medio de QtSpim (corrrida).

La generación del código mips será a partir del código intermedio producido por el
proyecto en su segunda fase (Proyecto II).
Generación de código a partir de código intermedio no generado por el compilador.
NOTA: La opción 2 tendrá una puntuación diferenciada.

El código mips generado deberá correr en Qtspim. Tiene un valor de 82 puntos

## Puntos Extra
Se darán 2.5 puntos adicionales al entregar a más tardar el miércoles 24 de junio a las
11:55:55 PM el Documento de Requerimientos, ver plantilla suministrada en el Tec Digital.
Debe subirse en la documentación llamada “Proyecto III (archivos adicionales)” debajo de
la carpeta de “Proyectos”.

Se darán 10 puntos adicionales si incluye el manejo de clases –estructura y uso- (deberá
realizar análisis léxico, sintáctico, semántico y generación de código mips). Además, estará
condicionado alcanzar un 90% de los requerimientos funcionales del proyecto.
Aspectos técnicos
El proyecto deberá correr en java y utilizar las herramientas JFlex y Cup (Utilizadas en los
Proyectos I y II). En caso de requerir librerías adicionales para compilar y ejecutar el
programa, deberán especificarlo en la documentación, ya que de lo contrario se
descontarán puntos en la evaluación. Se debe incluir el archivo Flex y Cup, y el proyecto en
Java que utilice los archivos generados por JFlex y Cup.


## Documentación
La documentación es un aspecto de gran importancia en el desarrollo de programas,
especialmente en tareas relacionadas con el mantenimiento de estos.
Para la documentación interna, deberán incluir comentarios descriptivos para cada parte,
con sus entradas, salidas, restricciones y objetivo.

La documentación externa deberá incluir:
## 1. Portada.
- Manual de usuario: instrucciones de compilación, ejecución y uso.
- Pruebas de funcionalidad: incluir screenshots.
- Descripción del problema.
- Librerías usadas: creación de archivos, etc.
- Análisis de resultados: objetivos alcanzados, objetivos no alcanzados, y razones por
las cuales no se alcanzaron los objetivos (en caso de haberlos).
- Detalle de los algoritmos más importantes del proyecto.
- Bitácora (autogenerada en git, commit por usuario incluyendo comentario).
## Evaluación
La evaluación se va a centrar en dos elementos: programación y documentación.

El proyecto programado tiene un valor de 17.5% de la nota final, en el rubro de Proyectos.

Desglose de la evaluación del proyecto programado:
- Documentación interna 2 ptos.
- Documentación externa 6 ptos.
- Funcionalidad 82 ptos (ver detalle en Proyecto a Desarrollar)
- Revisión del proyecto (gestión del tiempo) 5 ptos.
- Hora de Entrega 5 ptos.
Forma de trabajo
El trabajo se debe realizar en parejas. En los casos que el profesor apruebe equipos de
más de dos integrantes, dichos equipos de trabajo deberán desarrollar producciones
gramaticales adicionales indicadas en los Proyectos 1 y 2.

Aspectos administrativos
Debe crear un archivo .zip (“PP3.zip”) que contenga únicamente un archivo info.txt y 2
carpetas llamadas documentacion y programa, en la primera deberá incluir el documento
de word (no pdf) solicitado y en la segunda los archivos y/o carpetas necesarias para la
implementación de este proyecto programado. El archivo info.txt debe contener la
siguiente información (cualidades):
a. Nombre del curso
b. Número de semestre y año lectivo
c. Nombre de los Estudiantes
d. Número de carnet de los estudiantes
e. Número de proyecto programado
f. Fecha de entrega
g. Estatus de la entrega (debe ser CONGRUENTE con la solución entregada):
[Deplorable|Regular|Buena|MuyBuena|Excelente|Superior]
## Entrega
Deberá subir el archivo antes mencionado al TEC Digital en el curso de COMPILADORES E
INTERPRETES GR 60, en la asignación llamada “P3” debajo del rubro de “Proyectos”.  En la
evaluación del Proyecto el rubro de “Hora de Entrega” valdrá por 5 puntos de la nota total
del proyecto, según la siguiente escala:
a. Si se entrega antes de las 11:55:55 PM del sábado 27 de junio de 2026, 5 puntos.
b. Si se entrega antes de las 11:55:55 AM del domingo 28 de junio de 2026, 2.5 puntos.
c. Si se entrega antes de las 11:55:55 PM del domingo 28 de junio de 2026, 0 puntos. Después
de este punto, NO SE ACEPTARÁN más trabajos.

Todo el contenido de cada proyecto debe ser 100% original y en caso de plagio todos los
integrantes del grupo tendrán nota cero. Todos los miembros del grupo deberán participar en el
desarrollo del proyecto y en la revisión, donde demuestren la autoría del proyecto.