package Cliente.Grafo;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapaDelMar {
    private GridPane gridPane;
    private int[][] matrizAdyacencia;
    private int[][] matrizTipos;
    private boolean[][] matrizDestruccion;

    public int[][] getMatrizTipos() {
        return matrizTipos;
    }

    public MapaDelMar(GridPane gridPane, int numIslas) {
        /**
         * Se crea una matriz vacia
         */
        this.gridPane = gridPane;
        matrizAdyacencia = new int[numIslas][numIslas];
        matrizTipos = new int[numIslas][numIslas];
        matrizDestruccion = new boolean[numIslas][numIslas];

        for (int i = 0; i < numIslas; i++) {
            for (int j = 0; j < numIslas; j++) {
                matrizTipos[i][j] = TipoIsla.VACIA;
                matrizDestruccion[i][j] = false;
            }
        }
    }



    // Método para inicializar el GridPane con los elementos de la matriz de tipos
    public void limpiarCasillas() {
        for (int row = 0; row < matrizTipos.length; row++) {
            for (int col = 0; col < matrizTipos[row].length; col++) {
                for (Node node : gridPane.getChildren()) {
                    if (GridPane.getRowIndex(node) != null && GridPane.getColumnIndex(node) != null) {
                        int rowIndex = GridPane.getRowIndex(node);
                        int colIndex = GridPane.getColumnIndex(node);
                        if (rowIndex == row && colIndex == col && node instanceof Label) {
                            ((Label) node).setText("");
                        }
                    }
                }
            }
        }
    }


    public void inicializarGrid() {
        limpiarCasillas();
        for (int row = 0; row < matrizTipos.length; row++) {
            for (int col = 0; col < matrizTipos[row].length; col++) {
                Label label = new Label(String.valueOf(matrizTipos[row][col]));
                gridPane.add(label, col, row);
            }
        }
    }

    public void recrearGrid(GridPane pantallaJugador) {
        this.gridPane = pantallaJugador;
        //limpiarCasillas();
        for (int row = 0; row < matrizTipos.length; row++) {
            for (int col = 0; col < matrizTipos[row].length; col++) {
                System.out.print(matrizTipos[row][col] + " ");
                Label label = new Label(String.valueOf(matrizTipos[row][col]));
                gridPane.add(label, col, row);
            }
        }
    }



    public void conectarIslas(int isla1x, int isla1y, int isla2x, int isla2y) {
        /**
         * Se hace una estructura que permite hacer la coneccion entre dos islas
         */
        matrizAdyacencia[isla1x][isla1y] = 1;
        matrizAdyacencia[isla2x][isla2y] = 1;
    }

    /**
     * TODO
     * Permitir que se pueda agregar, destruir y restaurar cualquier coordenada de isla
     */
    public void asignarTipoIsla(int coordx, int coordy, int tipo) {
        matrizTipos[coordx][coordy] = tipo;
    }

    public void destruirIsla(int coordx, int coordy) {
        matrizDestruccion[coordx][coordy] = true;
    }

    public void restaurarIsla(int coordx, int coordy) {
        matrizDestruccion[coordx][coordy] = false;
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
                case TipoIsla.CONECTOR -> System.out.println("Conector");
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

    public Set<Integer> obtenerConexionesExternas(int coordx, int coordy) {
        int tipo = matrizTipos[coordx][coordy];
        Set<String> bloque = new HashSet<>();
        Set<Integer> conexionesExternas = new HashSet<>();
        boolean[][] visitado = new boolean[matrizTipos.length][matrizTipos.length];
        if (matrizDestruccion[coordx][coordy]) {
            return conexionesExternas;
        }
        explorarBloque(coordx, coordy, tipo, visitado, bloque);
        for (String coordenada : bloque) {
            String[] partes = coordenada.split(",");
            int x = Integer.parseInt(partes[0]);
            int y = Integer.parseInt(partes[1]);
            for (int j = 0; j < matrizAdyacencia[x].length; j++) {
                if (matrizAdyacencia[x][j] == 1 && !bloque.contains(j + "," + y) && !matrizDestruccion[j][y]) {
                    conexionesExternas.add(j);
                }
            }
        }
        return conexionesExternas;
    }

    private void explorarBloque(int coordx, int coordy, int tipo, boolean[][] visitado, Set<String> bloque) {
        if (visitado[coordx][coordy] || matrizTipos[coordx][coordy] != tipo || matrizDestruccion[coordx][coordy]) {
            return;
        }
        visitado[coordx][coordy] = true;
        bloque.add(coordx + "," + coordy);
        for (int i = 0; i < matrizAdyacencia.length; i++) {
            if (matrizAdyacencia[coordx][i] == 1 && !visitado[i][coordy] && !matrizDestruccion[i][coordy]) {
                explorarBloque(i, coordy, tipo, visitado, bloque);
            }
        }
    }


}

