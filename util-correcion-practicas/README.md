# Utilidades MP

**Nota**: Se ha realizado mediante *vibe coding*.

Utilidades Python:

- `clonar_alumnos.py`: lee el Excel de notas y clona los repositorios de alumnos aprobados.
- `evaluar_alumnos.py`: evalua commits y tests Java Maven por sesion, usando los tests del repositorio del profesor.

## Requisitos

Ficheros necesarios en este directorio:

- `NotasMP2026.xlsx`: hoja de calculo con las notas.
- `config.json`: configuracion de Excel, GitHub, sesiones, tests y paralelismo.
- `requirements.txt`: dependencias Python.
- `clonar_alumnos.py`.
- `evaluar_alumnos.py`.

Fichero/directorio necesario fuera de este directorio:

- `../MP2026Profesores`: repositorio del profesor con los tests reales en `src/test/java/org/mp/sesionXX`.

Herramientas necesarias instaladas en el sistema:

- Python 3.
- Git.
- Maven (`mvn`).
- Java compatible con los proyectos, actualmente Java 21 segun los `pom.xml`.

## Entorno Python

El entorno virtual se debe crearlo:

```bash
python3 -m venv .venv
.venv/bin/pip install -r requirements.txt
```

## Configuración

El fichero `config.json` contiene:

- Datos GitHub para clonado.
- Fichero Excel y hoja a leer.
- Carpeta destino de repos de alumnos.
- Repositorio del profesor con tests reales.
- Numero de procesos paralelos para evaluacion (`max_workers`).
- Fechas, semanas y codigos de entrega por sesión.

Campos principales:

```json
"excel": {
  "file": "NotasMP2026.xlsx",
  "sheet": "NotasMP2026"
},
"clone": {
  "destination": "alumnos",
  "suffix": "MP2026"
},
"evaluation": {
  "teacher_repo": "../MP2026Profesores",
  "max_workers": 12,
  "results_json": "resultados_evaluacion.json",
  "results_xlsx": "NotasMP2026_evaluado.xlsx"
}
```

## Utilidad 1: clonar alumnos

Comando de prueba, no clona nada:

```bash
.venv/bin/python clonar_alumnos.py --dry-run
```

Comando real:

```bash
.venv/bin/python clonar_alumnos.py
```

Funcionamiento:

- Lee `NotasMP2026.xlsx`.
- Usa la hoja configurada en `config.json`.
- Toma nombre y apellidos de la columna B.
- Solo procesa alumnos con columna E mayor que 5.
- Construye el nombre del repo normalizando mayusculas y quitando tildes.
- Clona en la carpeta `alumnos/`.
- Si el repo ya existe localmente, lo salta.

Salida creada:

- `alumnos/`: carpeta con los repositorios clonados.

## Utilidad 2: evaluar alumnos

Comando para evaluar todos los repos:

```bash
.venv/bin/python evaluar_alumnos.py
```

Comando para evaluar un solo repo:

```bash
.venv/bin/python evaluar_alumnos.py --repo ZapataRojasMiguelMP2026
```

Funcionamiento:

- Recorre los repos en `alumnos/`.
- Para cada alumno y sesion, comprueba si hay commit con los codigos/fechas configurados.
- Usa los tests reales del profesor desde `../MP2026Profesores`.
- Copia temporalmente cada clase `*Test.java` del profesor dentro del repo del alumno.
- Ejecuta Maven contra el codigo del alumno.
- Ejecuta las clases de test de forma aislada para que un test que no compile no bloquee toda la sesion.
- Restaura los tests originales del alumno al terminar.
- Oculta temporalmente tests mal colocados en `src/main/java` para que no rompan la compilacion Maven.
- Escribe resultados en JSON y en una copia del Excel.

Paralelismo:

- Se configura con `evaluation.max_workers` en `config.json`.
- Actualmente esta configurado a `12`.
- El paralelismo es por repositorio: hasta 12 repos se evaluan a la vez.

Salidas creadas:

- `resultados_evaluacion.json`: resultado detallado por alumno y sesion.
- `NotasMP2026_evaluado.xlsx`: copia del Excel original con columnas nuevas al final.

Columnas añadidas al Excel:

- `sesion01 commit`, `sesion01 test`.
- `sesion02 commit`, `sesion02 test`.
- ...
- `sesion10 commit`, `sesion10 test`.

Valores de commit:

- `correcto`: hay commit en la ventana configurada.
- `NO`: no se ha encontrado commit valido.

Valores de test:

- Formato `tests correctos / tests totales`.
- Los totales salen de los tests del profesor, por lo que son iguales para todos los alumnos en cada sesion.
- Si una sesion no tiene tests del profesor, aparece `0 / 0`.

## Ficheros generados

Por `clonar_alumnos.py`:

- `alumnos/<RepoAlumno>/`.

Por `evaluar_alumnos.py`:

- `resultados_evaluacion.json`.
- `NotasMP2026_evaluado.xlsx`.

Ficheros temporales:

- Durante la evaluacion se usan directorios temporales dentro de `target/` de cada alumno.
- El script los elimina al terminar.

## Notas

- `NotasMP2026.xlsx` no se modifica directamente; se crea `NotasMP2026_evaluado.xlsx`.
- El aviso de `openpyxl` sobre validaciones de datos indica que la copia puede perder reglas de validacion de Excel, pero no afecta a los valores generados.
- Si un resultado aparece como `0 / N`, normalmente significa que ningun test de esa sesion llego a pasar o que hubo errores de compilacion contra los tests del profesor.
- El detalle del estado esta en `resultados_evaluacion.json`, campo `status`.
