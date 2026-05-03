# Clases

## **1\. Representación de rutas y ficheros**

**`java.io.File`** Representa de forma abstracta rutas de ficheros y directorios. No es el contenido del fichero, sino una referencia de ruta (`fid`): puede preguntar si existe, si es directorio, crear, borrar o convertir a `Path`. Oracle lo define como una representación abstracta de nombres de ruta de ficheros y directorios. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/File.html))

**`java.nio.file.Path`** Representa una ruta del sistema de ficheros en la API NIO. Es más **moderna** que `File` para trabajar con rutas, composición de rutas, rutas absolutas, relativas, nombres, padres y operaciones con `Files`. La documentación indica que `Path` localiza un fichero en un sistema de ficheros y puede interoperar con `File` mediante `toPath()` y `toFile()`. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Path.html))

**`java.nio.files.Files`** Contiene únicamente métodos estáticos para el manejo de ficheros. [(Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Files.html))

## **2\. Excepciones de entrada/salida**

**`java.io.IOException`** Es la excepción base para errores de entrada/salida: fallos al leer, escribir, cerrar streams, acceder a ficheros, etc. Justifica su existencia porque agrupa errores de I/O recuperables o declarables mediante `throws`.

**`java.io.FileNotFoundException`** Especialización de `IOException` para indicar que un fichero no se puede abrir: no existe, es un directorio cuando se esperaba fichero, no hay permisos, o no puede crearse/abrirse. Aparece típicamente en constructores de `FileInputStream`, `FileOutputStream`, `FileReader`, `PrintWriter`, etc.; por ejemplo, `FileOutputStream` puede lanzarla si el destino no puede abrirse o crearse. ([Oracle Docs](https://docs.oracle.com/javase/8/docs/api/java/io/FileOutputStream.html))

## **3\. Lectura de texto: caracteres**

**`java.io.Reader`** Clase base **abstracta** para leer caracteres. Existe porque el tratamiento de texto implica codificación (juego de caracteres), formato (conforme a sistema operativo, `\n`, `\r\n`), y localización (conforme a región/idioma, relacionado con puntos de miles y decimales, moneda, y fechas principalmente). De ella derivan lectores como `InputStreamReader`, `FileReader` y `BufferedReader`. ([Oracle Docs](https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html))

**`java.io.InputStream`** Clase base **abstracta** para leer bytes (**binario**) como **flujo** de datos desde cualquier origen: fichero, red, memoria, etc. Justifica su existencia porque unifica la lectura **binaria** mediante métodos comunes como `read()`, `read(byte[])` y `close()`. De ella derivan clases como `FileInputStream`, `FilterInputStream` y, directamente, `ObjectInputStream`.

**`java.io.InputStreamReader`** Puente entre bytes y caracteres. Toma un `InputStream` y decodifica bytes usando un charset. Es esencial cuando el origen real es binario, pero el programa necesita texto. Oracle lo describe explícitamente como un puente de byte streams a character streams. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/InputStreamReader.html))

**`java.io.FileReader`** **Especialización** de `InputStreamReader` para leer caracteres directamente desde un fichero. Es cómoda para texto simple, aunque en código moderno suele preferirse controlar charset explícitamente con `InputStreamReader` o `Files.newBufferedReader`. Dependen de la **codificación de caracteres por defecto**. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/FileReader.html))

**`java.io.BufferedReader`** Envuelve un `Reader` y añade **buffer**. Sirve para reducir accesos costosos al origen y permite lectura por líneas con `readLine()`. Hace lectura secuencial, aunque puede avanzar a cierta posición del flujo. ([Oracle Docs](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html))

**`java.util.Scanner`** **Analizador de texto** por tokens. Sirve para leer texto y convertirlo a tipos primitivos o cadenas usando delimitadores y expresiones regulares. Puede leer desde `File`, `InputStream` o `Readable`, y puede usar `Locale` para interpretar formatos numéricos. Oracle lo define como un scanner de texto simple que parsea tipos primitivos y strings mediante expresiones regulares. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Scanner.html))

**`java.util.Locale`** Representa configuración regional: idioma, país y convenciones culturales. En gestión de ficheros aparece indirectamente con `Scanner`, por ejemplo para parsear números con coma decimal o punto decimal según región.

## **4\. Escritura de texto: caracteres**

**`java.io.Writer`** Clase base **abstracta** para escribir caracteres. Existe para separar escritura textual de escritura binaria. Define operaciones como `write`, `flush` y `close`. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Writer.html))

**`java.io.FileWriter`** Escribe caracteres en un fichero. Puede escribir en modo append. Como `FileReader`. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/FileWriter.html))

**`java.io.PrintWriter`** Escritor textual de alto nivel para `print`, `println`, `printf` y `format`. Existe para facilitar la salida formateada de texto hacia ficheros, writers u otros destinos. Oracle lo define como una clase que imprime representaciones formateadas de objetos a un stream de salida textual, y aclara que no escribe bytes crudos. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/PrintWriter.html))

## **5\. Lectura binaria: bytes**

**`java.io.InputStream`** Clase base **abstracta** para todos los **flujos** de entrada de bytes (binario). Existe para leer datos binarios independientemente de si vienen de fichero, red, memoria, etc. Sus métodos clave son `read`, `read(byte[])`, `readAllBytes`, `transferTo` y `close`. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/InputStream.html))

**`java.io.FileInputStream`** Lee bytes de forma **secuencial** desde un fichero. Es apropiado para datos binarios: imágenes, audio, documentos serializados, etc. También puede ser la base de otras clases como `InputStreamReader`, `BufferedInputStream`, `DataInputStream` u `ObjectInputStream`. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/FileInputStream.html))

**`java.io.FilterInputStream`** Clase decoradora base para streams de entrada. Contiene otro `InputStream` protegido (`in`) y delega en él, permitiendo añadir funcionalidad sin cambiar el origen. Justifica la jerarquía de filtros como `BufferedInputStream` y `DataInputStream`.

**`java.io.BufferedInputStream`** Añade buffer a un `InputStream`. Reduce llamadas al sistema o al dispositivo al leer bloques internamente. Es útil cuando se lee mucho dato binario en pequeñas operaciones.

**`java.io.DataInputStream`** Lee tipos primitivos Java (`int`, `double`, `boolean`, `UTF`, etc.) desde un `InputStream` de forma portable. Existe para interpretar bytes como datos tipados, no solo como bytes sueltos.

**`java.io.ObjectInputStream`** Deserializa objetos y datos primitivos previamente escritos por `ObjectOutputStream`. Existe para reconstruir grafos de objetos desde un stream binario, siempre que los objetos sean serializables. La documentación de `ObjectOutputStream` indica la relación inversa: los objetos escritos pueden leerse o reconstituirse con `ObjectInputStream`. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectOutputStream.html))

## **6\. Escritura binaria: bytes**

**`java.io.OutputStream`** Clase base **abstracta** para todos los flujos de salida de bytes. Existe para escribir datos binarios hacia un destino: fichero, red, memoria, etc. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/OutputStream.html))

**`java.io.FileOutputStream`** Escribe bytes en un fichero. Es apropiado para datos binarios crudos, como imágenes o binarios. ([Oracle Docs](https://docs.oracle.com/javase/8/docs/api/java/io/FileOutputStream.html))

**`java.io.FilterOutputStream`** Clase decoradora base para streams de salida. Contiene otro `OutputStream` protegido (`out`) y delega en él. Sirve como base para añadir comportamiento: buffering, escritura de primitivos, etc.

**`java.io.BufferedOutputStream`** Añade buffer a un `OutputStream`. Evita que cada `write` pequeño llegue directamente al sistema subyacente; acumula bytes y los vuelca con `flush()` o `close()`.

**`java.io.DataOutputStream`** Escribe tipos primitivos Java en formato binario portable sobre un `OutputStream`. Existe como pareja natural de `DataInputStream`.

**`java.io.ObjectOutputStream`** Serializa objetos y tipos primitivos hacia un `OutputStream`. Existe para persistir o transmitir grafos de objetos Java. Oracle indica que escribe tipos primitivos y grafos de objetos, y que esos objetos pueden reconstituirse con `ObjectInputStream`; también exige que los objetos soporten `Serializable`. ([Oracle Docs](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectOutputStream.html))

# Tabla comparativa

| Paquete | Clase | Lectura | Escritura | Buffer | Secuencial | Aleatoria | Texto / binario |
| ----- | ----- | ----- | ----- | ----- | ----- | ----- | ----- |
| `java.io` | `File` | ❌ | ❌ | ❌ | ❌ | ❌ | Ruta / metadatos |
| `java.nio.file` | `Path` | ❌ | ❌ | ❌ | ❌ | ❌ | Ruta / metadatos |
| `java.util` | `Scanner` | ✔️ | ❌ | ✔️ interno | ✔️ | ❌ | Texto |
| `java.io` | `FileReader` | ✔️ | ❌ | ❌ | ✔️ | ❌ | Texto |
| `java.io` | `BufferedReader` | ✔️ | ❌ | ✔️ | ✔️ | ❌ | Texto |
| `java.io` | `InputStreamReader` | ✔️ | ❌ | ❌ | ✔️ | ❌ | Texto |
| `java.io` | `FileInputStream` | ✔️ | ❌ | ❌ | ✔️ | ❌\* | Binario |
| `java.io` | `FilterInputStream` | ✔️ | ❌ | ❌ | ✔️ | ❌ | Binario |
| `java.io` | `BufferedInputStream` | ✔️ | ❌ | ✔️ | ✔️ | ❌ | Binario |
| `java.io` | `DataInputStream` | ✔️ | ❌ | ❌ | ✔️ | ❌ | Binario tipado |
| `java.io` | `ObjectInputStream` | ✔️ | ❌ | ❌ | ✔️ | ❌ | Binario / objetos |
| `java.io` | `FileWriter` | ❌ | ✔️ | ❌ | ✔️ | ❌ | Texto |
| `java.io` | `PrintWriter` | ❌ | ✔️ | ✔️ interno | ✔️ | ❌ | Texto |
| `java.io` | `FileOutputStream` | ❌ | ✔️ | ❌ | ✔️ | ❌\* | Binario |
| `java.io` | `FilterOutputStream` | ❌ | ✔️ | ❌ | ✔️ | ❌ | Binario |
| `java.io` | `BufferedOutputStream` | ❌ | ✔️ | ✔️ | ✔️ | ❌ | Binario |
| `java.io` | `DataOutputStream` | ❌ | ✔️ | ❌ | ✔️ | ❌ | Binario tipado |
| `java.io` | `ObjectOutputStream` | ❌ | ✔️ | ❌ | ✔️ | ❌ | Binario / objetos |
| `java.util` | `Locale` | ❌ | ❌ | ❌ | ❌ | ❌ | Formato regional |
| `java.io` | `RandomAccessFile` | ✔️ | ✔️ | ❌ | ✔️ | ✔️ | Binario / primitivos |
| `java.nio.channels` | `FileChannel` | ✔️ | ✔️ | ❌ | ✔️ | ✔️ | Binario |
| `java.nio` | `MappedByteBuffer` | ✔️ | ✔️ | ✔️ memoria | ❌ | ✔️ | Binario |
| `java.nio.file` | `Files` | ✔️ | ✔️ | depende del método | ✔️ | ✔️ con `SeekableByteChannel` | Texto / binario |
| `java.nio.channels` | `SeekableByteChannel` | ✔️ | ✔️ | ❌ | ✔️ | ✔️ | Binario |

