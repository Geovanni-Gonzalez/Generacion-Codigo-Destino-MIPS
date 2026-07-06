# Generacion de Codigo Destino MIPS

Compilador academico desarrollado en Java para el curso de Compiladores e Interpretes. El proyecto procesa programas escritos en el lenguaje fuente del curso, ejecuta analisis lexico, sintactico y semantico, genera codigo intermedio y produce codigo destino MIPS.

## Valor tecnico

- Pipeline de compilacion completo: lexer, parser, AST, analisis semantico, codigo intermedio y backend MIPS.
- Generacion automatica de lexer y parser con JFlex y CUP.
- Tabla de simbolos, reportes de errores y validaciones semanticas.
- Traduccion a MIPS con modulos separados para memoria, funciones, control de flujo, objetos, E/S y operaciones.
- Suite de casos `.chip` para validar programas correctos y errores esperados.
- Integracion continua con GitHub Actions y Maven.

## Tecnologias

- Java 17
- Maven
- JFlex
- Java CUP
- JUnit 5
- MIPS / QtSPIM o MARS para inspeccion del codigo generado

## Estructura

```txt
Generacion-Codigo-Destino-MIPS/
  programa/
    pom.xml
    src/
      java/
        ast/          Nodos del arbol de sintaxis abstracta
        intermedio/   Representacion y generacion de codigo intermedio
        mips/         Backend de generacion MIPS
        pipeline/     Orquestacion del compilador
        reporte/      Escritura de reportes y salidas
        semantico/    Tabla de simbolos y analisis semantico
      lexico/         Especificacion JFlex
      sintactico/     Gramatica CUP
      test/           Pruebas JUnit
    test/             Programas fuente .chip
    salida/           Ejemplo de salidas generadas
  documentacion/      Documentacion externa del proyecto
  .github/workflows/  CI
```

## Requisitos

- JDK 17 o superior
- Maven 3.9 o superior

## Instalacion

```bash
git clone https://github.com/Geovanni-Gonzalez/Generacion-Codigo-Destino-MIPS.git
cd Generacion-Codigo-Destino-MIPS/programa
mvn clean package
```

El proceso de build genera automaticamente los archivos derivados de JFlex y CUP dentro de `target/`.

## Uso

Ejecutar el compilador con un archivo fuente:

```bash
java -jar target/proyecto-compiladores-1.0-SNAPSHOT.jar test/30_clase_metodos.chip salida
```

El segundo argumento es opcional. Si no se indica, se usa el directorio `salida/`.

## Salidas generadas

Para cada ejecucion se producen reportes y artefactos como:

- `tokens_report.txt`
- `tabla_simbolos.txt`
- `errores_report.txt`
- `resultado_sintactico.txt`
- codigo intermedio `.ic`
- codigo MIPS `.asm`

## Pruebas

```bash
mvn test
```

Tambien puede ejecutarse la verificacion completa:

```bash
mvn verify
```

## Ejemplo

Archivo fuente:

```bash
programa/test/30_clase_metodos.chip
```

Salida esperada principal:

```bash
programa/salida/30_clase_metodos.asm
```

## Estado

Proyecto academico finalizado y mantenido como muestra tecnica de construccion de compiladores, generacion de codigo intermedio y traduccion a MIPS.

## Autor

Geovanni Gonzalez Aguilar  
Ingenieria en Computacion  
GitHub: [Geovanni-Gonzalez](https://github.com/Geovanni-Gonzalez)
