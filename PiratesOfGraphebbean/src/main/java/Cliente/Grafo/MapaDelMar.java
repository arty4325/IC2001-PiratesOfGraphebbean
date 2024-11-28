package Cliente.Grafo;

import Modelos.CasesEnCliente;
import Modelos.TiposAtaque;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import javax.sound.midi.Soundbank;
import java.util.*;


public class MapaDelMar {
    private GridPane gridPane;
    private int[][] matrizAdyacencia;
    private List<List<Integer>> listaAdyacencia = new ArrayList<>();
    private int[][] matrizTipos;
    private static boolean[][] matrizDestruccion;
    private List<String> itemsInScreen = new ArrayList<>();

    public List<String> getItemsInScreen() {
        return itemsInScreen;
    }
    public int[][] getMatrizTipos() {
        return matrizTipos;
    }
    public static boolean[][] getMatrizDestruccion() {
        return matrizDestruccion;
    }
    public int[][] getMatrizAdyacencia() {
        return matrizAdyacencia;
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

    public boolean estaDisponible(int x, int y){
        if(x < 20 && y < 20) {
            if (matrizTipos[x][y] == 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean estaDisponibleItem(int x, int y, int val){
        // Aqui tiene que estar disponible todos los cuadros en donde se quiere colocar el item
        // fuente de energia 2x2
        // fabricas 1x2 o 2x1 (Evaluar luego)
        // templo 1x2 0 2x1
        // Mercado 1x2 o 2x1
        // conector 1x1
        // PARA EFECTOS DE PRUEBAS POR AHORA TEMPLO NO ESTA ROTADO
        if(val == 1){
            boolean ret = (estaDisponible(x, y) && estaDisponible(x + 1, y) && estaDisponible(x , y + 1) && estaDisponible(x + 1, y + 1));
            return ret;
        } else if(val == 2){
            // Por ahora lo vamos a tomar como la pos horizontal (Pronto incluimos la rotacion)
            boolean ret = (estaDisponible(x, y) && estaDisponible(x + 1, y));
            return ret;
        } else if (val == 4){
            boolean ret = (estaDisponible(x, y) && estaDisponible(x + 1, y));
            return ret;
        } else if(val == 5 || val == 7){
            boolean ret = estaDisponible(x, y);
            return ret;
        }
        return false;

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

    public void limpiarGridPane(GridPane gridPane, int rows, int cols) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
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

    public void inicializarGridEnemgio(int[][] matriz,GridPane gridPane) {
        limpiarGridPane(gridPane, 20, 20);
        for (int row = 0; row < matriz.length; row++) {
            for (int col = 0; col < matriz[row].length; col++) {
                Label label = new Label(String.valueOf(matriz[row][col]));
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
        List<Integer> conection = new ArrayList<>();
        conection.add(isla1x);
        conection.add(isla1y);
        conection.add(isla2x);
        conection.add(isla2y);
        listaAdyacencia.add(conection);

    }

    /**
     * TODO
     * Permitir que se pueda agregar, destruir y restaurar cualquier coordenada de isla
     */
    public void asignarTipoIsla(int coordx, int coordy, int tipo) {
        if(tipo == 1){
            matrizTipos[coordx][coordy] = tipo;
            matrizTipos[coordx + 1][coordy] = tipo;
            matrizTipos[coordx][coordy + 1] = tipo;
            matrizTipos[coordx + 1][coordy + 1] = tipo;
        } else if(tipo == 2){
            matrizTipos[coordx][coordy] = tipo;
            matrizTipos[coordx + 1][coordy] = tipo;
        } else if(tipo == 4){
            matrizTipos[coordx][coordy] = tipo;
            matrizTipos[coordx + 1][coordy] = tipo;
        } else if(tipo == 5 || tipo == 7){
            matrizTipos[coordx][coordy] = tipo;
        }
    }

    public void destruirIsla(int coordx, int coordy) {
        matrizDestruccion[coordx][coordy] = true;
    }

    public void restaurarIsla(int coordx, int coordy) {
        matrizDestruccion[coordx][coordy] = false;
    }

    public TiposAtaque atacarIsla(int x, int y){
        // Nunca se debe de retornar null
        //return CasesEnCliente.NADA; // El caso de nothing
        System.out.println("Ataque " + x + " " + y);
        // Lo primero que vamos a hacer es poner el ataque en matriz destruccion
        matrizDestruccion[y][x] = true; // Esta isla ya fue atacada :(
        if(matrizTipos[y][x] == 1){
            // reviso al rededor
            // arriba, abajo izquierda, derecha
            if(matrizTipos[y + 1][x] == 1) {
                matrizDestruccion[y + 1][x] = true;
            }
            if(matrizTipos[y - 1][x] == 1) {
                matrizDestruccion[y - 1][x] = true;
            }if(matrizTipos[y][x + 1] == 1) {
                matrizDestruccion[y][x + 1] = true;
            }if(matrizTipos[y][x - 1] == 1) {
                matrizDestruccion[y][x - 1] = true;
            }
            // Diag arriba, diag abajo, diag izquierda, diag derecha
            if(matrizTipos[y + 1][x + 1] == 1) {
                matrizDestruccion[y + 1][x + 1]= true;
            }if(matrizTipos[y + 1][x - 1] == 1) {
                matrizDestruccion[y + 1][x - 1] = true;
            }if(matrizTipos[y - 1][x - 1] == 1) {
                matrizDestruccion[y - 1][x - 1] = true;
            }if(matrizTipos[y - 1][x + 1] == 1) {
                matrizDestruccion[y - 1][x + 1] = true;
            }
        }
        // Ahora tengo que ver que esta en esa coordenada en la martiz, y en base a eso tomar una desicion :)
        System.out.println("Ataque " + matrizTipos[x][y]);
        System.out.println("ITINSC" + itemsInScreen);
        if(matrizTipos[y][x] != 0){
            // Primer caso IMPORTANTE, cuando le doy a una fuente de energia :(
            // Tengo que ver cuales son el resto de coordenadas de esa fuente para destruirlas tambien
            if(matrizTipos[y][x] == 1){
                // Tengo que darle al enemigo una fuente de energia y destuir la mia
                // DESTRUCCION DE MI FUENTE DE ENERGIA
                itemsInScreen.remove("Energia");
                // Tengo que indicar que esta fuente de energia no esta, para que muestre lo que ahora es disconexo :P
                return TiposAtaque.FUENTEDEENERGIA;
            } else if (matrizTipos[y][x] == 4){
                itemsInScreen.remove("Tienda");
                return TiposAtaque.HIT;
            }
        }

        // Lo ultimo es darle feedback al usuario de lo que paso :)
        return TiposAtaque.MISS;
    }

    // [x,y]
    public int[] conseguirElementoRandom(){
        List<int[]> coordenadasValidas = new ArrayList<>();
        for (int i = 0; i < matrizAdyacencia.length; i++) {
            for (int j = 0; j < matrizAdyacencia[i].length; j++) {
                if (matrizAdyacencia[i][j] != 0 && matrizDestruccion[i][j]) {
                    coordenadasValidas.add(new int[]{i, j});
                }
            }
        }
        if (coordenadasValidas.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int indiceAleatorio = random.nextInt(coordenadasValidas.size());

        return coordenadasValidas.get(indiceAleatorio);
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

    // Serialización
    public String serializar() {
        StringBuilder sb = new StringBuilder();

        // Serializar listaAdyacencia
        sb.append(serializeListaAdyacencia()).append("t=&");

        // Serializar matrizTipos
        sb.append(serializeMatrix(matrizTipos)).append("t=&");

        // Serializar matrizDestruccion
        sb.append(serializeBooleanMatrix(matrizDestruccion));

        return sb.toString();
    }

    // Deserialización: Reconstruimos las estructuras desde el string
    public void deserializar(String data) {
        String[] parts = data.split("\\|");
        this.listaAdyacencia = deserializeListaAdyacencia(parts[0]);
        this.matrizTipos = deserializeMatrix(parts[1]);
        this.matrizDestruccion = deserializeBooleanMatrix(parts[2]);
        // Perfecto, ya tengo la informacion

    }

    private String serializeListaAdyacencia() {
        StringBuilder sb = new StringBuilder();
        for (List<Integer> connection : listaAdyacencia) {
            for (int val : connection) {
                sb.append(val).append(",");
            }
            sb.deleteCharAt(sb.length() - 1); // Eliminar última coma
            sb.append(";");
        }
        sb.deleteCharAt(sb.length() - 1); // Eliminar último punto y coma
        return sb.toString();
    }

    public List<List<Integer>> deserializeListaAdyacencia(String data) {
        List<List<Integer>> lista = new ArrayList<>();
        String[] connections = data.split(";");
        for (String connection : connections) {
            String[] values = connection.split(",");
            List<Integer> conectionList = new ArrayList<>();
            for (String value : values) {
                conectionList.add(Integer.parseInt(value));
            }
            lista.add(conectionList);
        }
        return lista;
    }

    private String serializeMatrix(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : matrix) {
            for (int val : row) {
                sb.append(val).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(";");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public int[][] deserializeMatrix(String data) {
        final int rows = 20;
        final int cols = 20;
        String[] rowStrings = data.split(";");
        int[][] matrix = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            if (i < rowStrings.length) {
                String[] values = rowStrings[i].split(",");

                for (int j = 0; j < cols; j++) {
                    if (j < values.length) {
                        matrix[i][j] = Integer.parseInt(values[j].trim());
                    } else {
                        matrix[i][j] = 0;
                    }
                }
            } else {
                for (int j = 0; j < cols; j++) {
                    matrix[i][j] = 0;
                }
            }
        }

        return matrix;
    }


    private String serializeBooleanMatrix(boolean[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (boolean[] row : matrix) {
            for (boolean val : row) {
                sb.append(val ? "1" : "0").append(",");
            }
            sb.deleteCharAt(sb.length() - 1); // Eliminar última coma
            sb.append(";");
        }
        sb.deleteCharAt(sb.length() - 1); // Eliminar último punto y coma
        return sb.toString();
    }

    public boolean[][] deserializeBooleanMatrix(String data) {
        String[] rows = data.split(";");
        boolean[][] matrix = new boolean[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] values = rows[i].split(",");
            matrix[i] = new boolean[values.length];
            for (int j = 0; j < values.length; j++) {
                matrix[i][j] = values[j].equals("1");
            }
        }
        return matrix;
    }

    // Método para obtener todas las conexiones entre dos coordenadas específicas
    public List<List<Integer>> obtenerConexiones(int x1, int y1, int x2, int y2) {
        List<List<Integer>> conexiones = new ArrayList<>();
        for (List<Integer> conection : listaAdyacencia) {
            int isla1x = conection.get(0);
            int isla1y = conection.get(1);
            int isla2x = conection.get(2);
            int isla2y = conection.get(3);

            // Comprobamos si la conexión corresponde a las coordenadas (x1, y1) y (x2, y2)
            if ((isla1x == x1 && isla1y == y1 && isla2x == x2 && isla2y == y2) ||
                    (isla1x == x2 && isla1y == y2 && isla2x == x1 && isla2y == y1)) {
                conexiones.add(conection);
            }
        }
        return conexiones;
    }

    public static List<List<Integer>> obtenerConexionesFuente( List<List<Integer>> listaAdyacencia, int[][] matrizTipos) {
        List<List<Integer>> coordenadasConexas = new ArrayList<>();
        for (List<Integer> conexion : listaAdyacencia) {
            int x1 = conexion.get(0);
            int y1 = conexion.get(1);
            int x2 = conexion.get(2);
            int y2 = conexion.get(3);
            if (matrizTipos[y1][x1] == 1 || matrizTipos[y2][x2] == 1) {
                coordenadasConexas.add(List.of(x1, y1));
                coordenadasConexas.add(List.of(x2, y2));
            }
        }

        return coordenadasConexas;
    }

    // Método principal que encuentra todos los nodos conexos a un nodo con valor 1
    public static List<List<Integer>> obtenerConexFuente(List<List<Integer>> listaAdyacencia, boolean[][] matrizDest,int[][] matrizTipos) {
        List<List<Integer>> coordenadasConexas = new ArrayList<>();
        Set<String> visitados = new HashSet<>();
        Map<String, List<String>> grafo = construirGrafo(listaAdyacencia);
        for (int y = 0; y < matrizTipos.length; y++) {
            for (int x = 0; x < matrizTipos[0].length; x++) {
                if (matrizTipos[y][x] == 1 && !matrizDest[y][x]) {
                    String nodoInicial = x + "," + y;
                    if (!visitados.contains(nodoInicial)) {
                        buscarConexiones(nodoInicial, grafo, visitados, coordenadasConexas);
                    }
                }
            }
        }

        return coordenadasConexas;
    }

    // Método para realizar la búsqueda en profundidad (DFS)
    private static void buscarConexiones(String nodo, Map<String, List<String>> grafo, Set<String> visitados, List<List<Integer>> resultado) {
        if (visitados.contains(nodo)) return;
        visitados.add(nodo);
        String[] partes = nodo.split(",");
        int x = Integer.parseInt(partes[0]);
        int y = Integer.parseInt(partes[1]);
        resultado.add(List.of(x, y));
        if (grafo.containsKey(nodo)) {
            for (String vecino : grafo.get(nodo)) {
                buscarConexiones(vecino, grafo, visitados, resultado); // Llamada recursiva
            }
        }
    }
    private static Map<String, List<String>> construirGrafo(List<List<Integer>> listaAdyacencia) {
        Map<String, List<String>> grafo = new HashMap<>();

        for (List<Integer> conexion : listaAdyacencia) {
            String nodo1 = conexion.get(0) + "," + conexion.get(1); // Coordenada 1
            String nodo2 = conexion.get(2) + "," + conexion.get(3); // Coordenada 2
            grafo.computeIfAbsent(nodo1, k -> new ArrayList<>()).add(nodo2);
            grafo.computeIfAbsent(nodo2, k -> new ArrayList<>()).add(nodo1);
        }

        return grafo;
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

