package Cliente.Grafo;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapaDelMar {
    private GridPane gridPane;
    private int[][] matrizAdyacencia;
    private int[][] matrizTipos;
    private boolean[] matrizDestruccion;

    public MapaDelMar(GridPane gridPane ,int numIslas) {
        /**
         * Se crea una matriz vacia
         */
        this.gridPane = gridPane;
        matrizAdyacencia = new int[numIslas][numIslas];
        matrizTipos = new int[numIslas][numIslas];
        matrizDestruccion = new boolean[numIslas];

        for (int i = 0; i < numIslas; i++) {
            for (int j = 0; j < numIslas; j++) {
                matrizTipos[i][j] = TipoIsla.VACIA;
            }
            matrizDestruccion[i] = false;
        }
    }

    // Método para inicializar el GridPane con los elementos de la matriz de tipos
    public void inicializarGrid() {
        for (int row = 0; row < matrizTipos.length; row++) {
            for (int col = 0; col < matrizTipos[row].length; col++) {
                Label label = new Label(String.valueOf(matrizTipos[row][col]));
                gridPane.add(label, col, row);
            }
        }
    }



    public void conectarIslas(int isla1, int isla2) {
        /**
         * Se hace una estructura que permite hacer la coneccion entre dos islas
         */
        matrizAdyacencia[isla1][isla2] = 1;
        matrizAdyacencia[isla2][isla1] = 1;
    }

    public void asignarTipoIsla(int isla, int tipo) {
        matrizTipos[isla][isla] = tipo;
    }

    public void destruirIsla(int isla) {
        matrizDestruccion[isla] = true;
    }

    public void restaurarIsla(int isla) {
        matrizDestruccion[isla] = false;
    }



    public void mostrarAdyacencias() {
        System.out.println("Matriz de Adyacencia:");
        for (int i = 0; i < matrizAdyacencia.length; i++) {
            for (int j = 0; j < matrizAdyacencia[i].length; j++) {
                System.out.print(matrizAdyacencia[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void mostrarTipos() {
        System.out.println("Matriz de Tipos:");
        for (int i = 0; i < matrizTipos.length; i++) {
            System.out.print("Isla " + i + ": ");
            switch (matrizTipos[i][i]) {
                case TipoIsla.FUENTE_ENERGIA -> System.out.println("Fuente de Energía");
                case TipoIsla.FABRICA -> System.out.println("Fábrica");
                case TipoIsla.TEMPLO -> System.out.println("Templo");
                case TipoIsla.MERCADO -> System.out.println("Mercado");
                default -> System.out.println("Vacía");
            }
        }
    }

    // Lo usamos para poder enviar esto por el servidor :)
    public String serializar() {
        StringBuilder sb = new StringBuilder();
        sb.append("Adyacencia:");
        for (int i = 0; i < matrizAdyacencia.length; i++) {
            for (int j = 0; j < matrizAdyacencia[i].length; j++) {
                sb.append(matrizAdyacencia[i][j]);
                if (j < matrizAdyacencia[i].length - 1) {
                    sb.append(",");
                }
            }
            sb.append(";");
        }
        sb.append("Tipos:");
        for (int i = 0; i < matrizTipos.length; i++) {
            sb.append(matrizTipos[i][i]);
            if (i < matrizTipos.length - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    // Esto lo uso para obtener la matriz que me llega del servidor :)
    public void deserializar(String data) {
        String[] partes = data.split("Tipos:");
        String adyacenciaPart = partes[0].replace("Adyacencia:", "");
        String tiposPart = partes[1];
        String[] filasAdyacencia = adyacenciaPart.split(";");
        for (int i = 0; i < filasAdyacencia.length; i++) {
            String[] valores = filasAdyacencia[i].split(",");
            for (int j = 0; j < valores.length; j++) {
                matrizAdyacencia[i][j] = Integer.parseInt(valores[j]);
            }
        }
        String[] tipos = tiposPart.split(",");
        for (int i = 0; i < tipos.length; i++) {
            matrizTipos[i][i] = Integer.parseInt(tipos[i]);
        }
    }

    public Set<Integer> obtenerConexionesExternas(int islaInicial) {
        int tipo = matrizTipos[islaInicial][islaInicial];
        Set<Integer> bloque = new HashSet<>();
        Set<Integer> conexionesExternas = new HashSet<>();
        boolean[] visitado = new boolean[matrizTipos.length];

        // Si la isla está destruida, no procesamos
        if (matrizDestruccion[islaInicial]) {
            return conexionesExternas;  // Vacio si isla destruida
        }

        explorarBloque(islaInicial, tipo, visitado, bloque);

        for (int isla : bloque) {
            for (int j = 0; j < matrizAdyacencia[isla].length; j++) {
                if (matrizAdyacencia[isla][j] == 1 && !bloque.contains(j) && !matrizDestruccion[j]) {
                    conexionesExternas.add(j);
                }
            }
        }

        return conexionesExternas;
    }

    private void explorarBloque(int isla, int tipo, boolean[] visitado, Set<Integer> bloque) {
        if (visitado[isla] || matrizTipos[isla][isla] != tipo || matrizDestruccion[isla]) {
            return;  // SI ISLA DESTRUDIA No se explora
        }

        visitado[isla] = true;
        bloque.add(isla);

        for (int j = 0; j < matrizAdyacencia[isla].length; j++) {
            if (matrizAdyacencia[isla][j] == 1 && !visitado[j] && !matrizDestruccion[j]) {
                explorarBloque(j, tipo, visitado, bloque);
            }
        }
    }








}

