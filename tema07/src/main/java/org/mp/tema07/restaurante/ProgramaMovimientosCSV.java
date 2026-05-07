package org.mp.tema07.restaurante;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ProgramaMovimientosCSV {

    private static final Scanner teclado = new Scanner(System.in);

    private static final List<Movimiento> movimientos = new ArrayList<>();
    private static List<Movimiento> seleccion = new ArrayList<>();

    private static String[] cabecera = new String[0];
    private static int columnaConcepto = -1;
    private static int columnaImporte = -1;

    public static void main(String[] args) {
        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1:
                    cargarDatosCSV();
                    break;
                case 2:
                    buscarPorConcepto();
                    break;
                case 3:
                    sumarSeleccion();
                    break;
                case 0:
                    System.out.println("Fin del programa.");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== MENÚ =====");
        System.out.println("1. Cargar datos CSV");
        System.out.println("2. Buscar movimiento por expresión regular en concepto");
        System.out.println("3. Sumar importes de los asientos seleccionados");
        System.out.println("0. Salir");
    }

    private static void cargarDatosCSV() {
        File archivo = seleccionarArchivoCSV();

        if (archivo == null) {
            System.out.println("No se ha seleccionado ningún archivo CSV.");
            return;
        }

        System.out.print("Carácter de separación: ");
        String separador = teclado.nextLine();

        if (separador.isEmpty()) {
            System.out.println("El separador no puede estar vacío.");
            return;
        }

        System.out.print("Región / locale, por ejemplo es_ES, en_US: ");
        String region = teclado.nextLine().trim();

        Locale locale = crearLocale(region);

        try {
            leerCSV(archivo, separador, locale);
            preguntarColumnas();

            seleccion = new ArrayList<>();
            System.out.println("CSV cargado correctamente.");
            System.out.println("Número de movimientos cargados: " + movimientos.size());

        } catch (FileNotFoundException e) {
            System.out.println("No se ha encontrado el archivo.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error al cargar el CSV: " + e.getMessage());
        }
    }

    private static File seleccionarArchivoCSV() {
    	// Directorio actual.
        File directorioActual = Path.of("").toAbsolutePath().toFile();

        while (true) {
            System.out.println();
            System.out.println("Directorio actual: " + directorioActual.getAbsolutePath());
            System.out.println("Contenido:");

            List<File> visibles = obtenerDirectoriosYCsv(directorioActual);

            System.out.println("0. Salir");
            System.out.println("1. [DIR] ..");

            for (int i = 0; i < visibles.size(); i++) {
                File f = visibles.get(i);
                String tipo = f.isDirectory() ? "[DIR]" : "[CSV]";
                System.out.printf("%d. %-5s %s%n", i + 2, tipo, f.getName());
            }

            System.out.print("Seleccione directorio o fichero CSV: ");
            String entrada = teclado.nextLine().trim();

            int opcion;

            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debe introducir un número.");
                continue;
            }

            if (opcion == 0) 
            	return null;
            
            if (opcion == 1) {
                File padre = directorioActual.getParentFile();
                if (padre != null) {
                    directorioActual = padre;
                }
                continue;
            }

            if (opcion < 1 || opcion > (visibles.size() + 1)) {
                System.out.println("Selección no válida.");
                continue;
            }

            File seleccionado = visibles.get(opcion - 2);

            if (seleccionado.isDirectory()) {
                directorioActual = seleccionado;
            } else {
                return seleccionado;
            }
        }
    }

    private static List<File> obtenerDirectoriosYCsv(File directorio) {
        File[] archivos = directorio.listFiles();
        List<File> resultado = new ArrayList<>();

        if (archivos == null) {
            return resultado;
        }

        for (File f : archivos) {
            if (f.isDirectory() || esCSV(f)) {
                resultado.add(f);
            }
        }

        return resultado;
    }

    private static boolean esCSV(File f) {
        return f.isFile() && f.getName().toLowerCase().endsWith(".csv");
    }

    private static Locale crearLocale(String texto) {
        if (texto == null || texto.isBlank()) {
            return Locale.getDefault();
        }

        String normalizado = texto.trim().replace('_', '-');
        Locale locale = Locale.forLanguageTag(normalizado);

        if (locale.getLanguage().isEmpty()) {
            return Locale.getDefault();
        }

        return locale;
    }

    private static void leerCSV(File archivo, String separador, Locale locale)
            throws FileNotFoundException {

        movimientos.clear();
        cabecera = new String[0];
        columnaConcepto = -1;
        columnaImporte = -1;

        try (Scanner scArchivo = new Scanner(archivo)) {
            if (!scArchivo.hasNextLine()) {
                throw new IllegalArgumentException("El archivo está vacío.");
            }

            String lineaCabecera = scArchivo.nextLine();
            cabecera = dividirLinea(lineaCabecera, separador);

            if (cabecera.length == 0) {
                throw new IllegalArgumentException("No se ha podido leer la cabecera.");
            }

            int numeroLinea = 1;

            while (scArchivo.hasNextLine()) {
                numeroLinea++;
                String linea = scArchivo.nextLine();

                if (linea.isBlank()) {
                    continue;
                }

                String[] campos = dividirLinea(linea, separador);

                Movimiento movimiento = new Movimiento(numeroLinea, campos, locale);
                movimientos.add(movimiento);
            }
        }
    }

    private static String[] dividirLinea(String linea, String separador) {
        String[] campos = linea.split(Pattern.quote(separador));

        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].trim();
        }

        return campos;
    }
    
    private static void preguntarColumnas() {
        System.out.println();
        System.out.println("Columnas existentes:");

        for (int i = 0; i < cabecera.length; i++) {
            System.out.printf("%d. %s%n", i + 1, cabecera[i]);
        }

        columnaConcepto = leerColumna("Seleccione la columna de concepto: ") - 1;
        columnaImporte = leerColumna("Seleccione la columna de importe: ") - 1;

        for (Movimiento m : movimientos) {
            m.setConcepto(columnaConcepto);
            m.setImporte(columnaImporte);
        }
    }

    private static int leerColumna(String mensaje) {
        int columna;

        do {
            columna = leerEntero(mensaje);

            if (columna < 1 || columna > cabecera.length) {
                System.out.println("Columna no válida.");
            }

        } while (columna < 1 || columna > cabecera.length);

        return columna;
    }

    private static void buscarPorConcepto() {
        if (!hayDatosCargados()) {
            return;
        }

        while (true) {
            System.out.println();
            System.out.print("Expresión regular para concepto, cadena vacía para terminar: ");
            String expresion = teclado.nextLine();

            if (expresion.isEmpty()) {
                break;
            }

            try {
                Pattern patron = Pattern.compile(expresion);
                seleccion = new ArrayList<>();

                for (Movimiento m : movimientos) {
                    if (patron.matcher(m.getConcepto()).matches()) {
                        seleccion.add(m);
                    }
                }

                mostrarMovimientos(seleccion);

            } catch (PatternSyntaxException e) {
                System.out.println("Expresión regular incorrecta: " + e.getDescription());
            }
        }
    }

    private static void sumarSeleccion() {
        if (!hayDatosCargados()) {
            return;
        }

        List<Movimiento> datosASumar;

        if (seleccion == null || seleccion.isEmpty()) {
            datosASumar = movimientos;
            System.out.println("No hay selección activa. Se usa por defecto ^.*$.");
        } else {
            datosASumar = seleccion;
        }

        double suma = 0.0;
        int correctos = 0;
        int erroneos = 0;

        for (Movimiento m : datosASumar) {
            if (m.tieneImporteValido()) {
                suma += m.getImporte();
                correctos++;
            } else {
                erroneos++;
                System.out.println("Importe no válido en línea " + m.getNumeroLinea()
                        + ": " + m.getTextoImporte());
            }
        }

        System.out.println();
        System.out.println("Asientos sumados: " + correctos);
        System.out.println("Asientos con importe incorrecto: " + erroneos);
        System.out.printf(Locale.US, "Suma de importes: %.2f%n", suma);
    }

    private static boolean hayDatosCargados() {
        if (movimientos.isEmpty() || columnaConcepto < 0 || columnaImporte < 0) {
            System.out.println("Primero debe cargar un CSV.");
            return false;
        }

        return true;
    }

    private static void mostrarMovimientos(List<Movimiento> lista) {
        System.out.println();
        System.out.println("Coincidencias: " + lista.size());

        for (Movimiento m : lista) {
            System.out.println(m);
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = teclado.nextLine().trim();

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debe introducir un número entero.");
            }
        }
    }

    private static class Movimiento {
        private final int numeroLinea;
        private final String[] campos;
        private final Locale locale;

        private String concepto = "";
        private String textoImporte = "";
        private double importe = 0.0;
        private boolean importeValido = false;

        public Movimiento(int numeroLinea, String[] campos, Locale locale) {
            this.numeroLinea = numeroLinea;
            this.campos = campos;
            this.locale = locale;
        }

        public void setConcepto(int columnaConcepto) {
            concepto = obtenerCampo(columnaConcepto);
        }

        public void setImporte(int columnaImporte) {
            textoImporte = obtenerCampo(columnaImporte);

            try (Scanner scImporte = new Scanner(textoImporte)) {
                scImporte.useLocale(locale);

                if (scImporte.hasNextDouble()) {
                    importe = scImporte.nextDouble();
                    importeValido = true;
                } else {
                    importeValido = false;
                }
            }
        }

        private String obtenerCampo(int columna) {
            if (columna < 0 || columna >= campos.length) {
                return "";
            }

            return campos[columna];
        }

        public int getNumeroLinea() {
            return numeroLinea;
        }

        public String getConcepto() {
            return concepto;
        }

        public double getImporte() {
            return importe;
        }

        public String getTextoImporte() {
            return textoImporte;
        }

        public boolean tieneImporteValido() {
            return importeValido;
        }

        @Override
        public String toString() {
            return "Línea " + numeroLinea
                    + " | Concepto: " + concepto
                    + " | Importe: " + textoImporte;
        }
    }
}