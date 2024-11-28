package Cliente;

import Cliente.Grafo.MapaDelMar;
import Modelos.CasesEnThreadServidor;
import Modelos.Random;
import Modelos.TiposAtaque;
import Modelos.Utilities;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MainGameController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private String userName;
    private Cliente cliente;
    private MapaDelMar mapaDelMar;
    private boolean comodinListo;

    private StoreController storeActual;

    static final HashMap<String, List<Integer>> hashPosItems = new HashMap<String, List<Integer>>();

    static final HashMap<List<Integer>, List<List<Integer>>> hashAllCoords = new HashMap<List<Integer>, List<List<Integer>>>();

    @FXML private Label lblComodinTimer;

    @FXML private Button btnEscudo;

    @FXML private Button btnKraken;

    @FXML private Label lblMinaTimer;

    @FXML private Label lblEscudoEnemigo;

    @FXML private AnchorPane anchorPane;

    @FXML private Button btnSend;

    @FXML private GridPane PantallaJugador;

    @FXML private GridPane PantallaEnemigo;

    @FXML private TextArea txaChat;

    @FXML private TextField txfMensaje;

    @FXML private TextArea txaAcciones;

    @FXML private ComboBox<String> itemComboBox;

    @FXML private ComboBox<String> conectorComboBox;

    @FXML private ComboBox<String> placeItemComboBox;

    @FXML private Spinner<Integer> coordX;

    @FXML private Spinner<Integer> coordY;

    @FXML private ComboBox<String> cbxVerEnemy;

    @FXML private ComboBox<String> cbxKraken;

    @FXML private TextField coordX1;

    @FXML private TextField coordY1;

    private List<ImageView> enemyImageViews = new ArrayList<>();

    @FXML private Spinner<Integer> sbxC_X;      @FXML private Spinner<Integer> sbxC_Y;

    @FXML private Spinner<Integer> sbxCM_X;      @FXML private Spinner<Integer> sbxCM_Y;

    @FXML private Spinner<Integer> sbxBomb1_X;      @FXML private Spinner<Integer> sbxBomb1_Y;
    @FXML private Spinner<Integer> sbxBomb2_X;      @FXML private Spinner<Integer> sbxBomb2_Y;
    @FXML private Spinner<Integer> sbxBomb3_X;      @FXML private Spinner<Integer> sbxBomb3_Y;

    @FXML private Spinner<Integer> sbxCBR1_X;   @FXML private Spinner<Integer> sbxCBR1_Y;
    @FXML private Spinner<Integer> sbxCBR2_X;   @FXML private Spinner<Integer> sbxCBR2_Y;
    @FXML private Spinner<Integer> sbxCBR3_X;   @FXML private Spinner<Integer> sbxCBR3_Y;
    @FXML private Spinner<Integer> sbxCBR4_X;   @FXML private Spinner<Integer> sbxCBR4_Y;
    @FXML private Spinner<Integer> sbxCBR5_X;   @FXML private Spinner<Integer> sbxCBR5_Y;
    @FXML private Spinner<Integer> sbxCBR6_X;   @FXML private Spinner<Integer> sbxCBR6_Y;
    @FXML private Spinner<Integer> sbxCBR7_X;   @FXML private Spinner<Integer> sbxCBR7_Y;
    @FXML private Spinner<Integer> sbxCBR8_X;   @FXML private Spinner<Integer> sbxCBR8_Y;
    @FXML private Spinner<Integer> sbxCBR9_X;   @FXML private Spinner<Integer> sbxCBR9_Y;
    @FXML private Spinner<Integer> sbxCBR10_X;  @FXML private Spinner<Integer> sbxCBR10_Y;



    private List<int[][]> coordenadas = new ArrayList<>();
    private List<List<Integer>> coordenadasConector = new ArrayList<>();
    //private List<String> itemsInScreen = new ArrayList<>();
    private Spinner<Integer>[] spinnersC;
    private Spinner<Integer>[] spinnersCM;
    private Spinner<Integer>[][] spinnersBomb;
    private Spinner<Integer>[][] spinnersCBR;


    private static int getNumberFromString(String item) {
        System.out.println(item);
        int number = switch (item) {
            case "Energia" -> 1;
            case "Mina" -> 2;
            case "Templo" -> 3;
            case "Tienda" -> 4;
            case "Conector" -> 5;
            case "Tornado" -> 7;
            default -> {
                System.out.println("Ítem no reconocido: " + item);
                yield -1;
            }
        };
        return number;
    }

    private static String getStringFromNumber(int number) {

        String val = switch (number) {
            case 1 -> "Energia";
            case 2 -> "Mina";
            case 3 -> "Templo";
            case 4 -> "Tienda";
            case 5 -> "Conector";
            case 7 -> "Tornado";
            default -> {
                System.out.println("Ítem no reconocido: " + number);
                yield "";
            }
        };
        return val;
    }

    public static void colocarEnHash(String text, List<Integer> pos){
        hashPosItems.put(text, pos);
    }

    public static void colocarTodasCoordsHash(int x, int y, int val){
        List<Integer> key = new ArrayList<>();
        key.add(x);
        key.add(y);
        // Ahora aqui es en donde yo voy a comenzar a usar el hash para guardar las coordenadas;
        List<List<Integer>> allKeys = new ArrayList<>();
        // Ahora si aqui vienen los casos
        // FALTA PONER LOS CASOS QUE AUN NO ESTAN
        if(val == 1){
            List<Integer> firstCoord = new ArrayList<>();
            List<Integer> secondCoord = new ArrayList<>();
            List<Integer> thirdCoord = new ArrayList<>();
            List<Integer> fourthCoord = new ArrayList<>();
            firstCoord.add(x);
            firstCoord.add(y);

            secondCoord.add(x + 1);
            secondCoord.add(y);

            thirdCoord.add(x);
            thirdCoord.add(y + 1);

            fourthCoord.add(x + 1);
            fourthCoord.add(y + 1);

            allKeys.add(firstCoord);
            allKeys.add(secondCoord);
            allKeys.add(thirdCoord);
            allKeys.add(fourthCoord);
            hashAllCoords.put(key, allKeys);
        } else if (val == 2 || val == 4){
            List<Integer> firstCoord = new ArrayList<>();
            List<Integer> secondCoord = new ArrayList<>();

            firstCoord.add(x);
            firstCoord.add(y);

            secondCoord.add(x + 1);
            secondCoord.add(y);

            allKeys.add(firstCoord);
            allKeys.add(secondCoord);
            hashAllCoords.put(key, allKeys);
        }

    }


    public static List<List<Integer>> obtenerCoordsPorLlave(int x, int y) {
        List<Integer> key = new ArrayList<>();
        key.add(x);
        key.add(y);
        if (hashAllCoords.containsKey(key)) {
            return hashAllCoords.get(key);
        } else {
            return null;
        }
    }



    public void loadAccordion(List<String> items) {
        for(int i = 0; i < items.size(); i++) {
            itemComboBox.getItems().add(items.get(i));
        }
        items.clear();
    }

    public void loadDataComboBox() {
        itemComboBox.getItems().clear();
        for(int i = 0; i < cliente.getListaItems().size(); i++) {
            itemComboBox.getItems().add(cliente.getListaItems().get(i));
        }
    }

    public void setUserData(Cliente _cliente){
        // Como esto se corre cuando se inicializa la aplicacion, aqui vamos a poner los items principales de la pantalla
        this.userName = _cliente.getNombreCliente();
        this.cliente = _cliente;
        agruparSpinnersAtaque();
        loadDataComboBox();
        loadEnemigosCbx();
        setSpinners();
        mapaDelMar = new MapaDelMar(PantallaJugador, 20);
        mapaDelMar.inicializarGrid();
        new Thread(() -> cronoMina()).start();
        new Thread(() -> cronoComodin()).start();
    }

    private void loadEnemigosCbx(){
        for (String oponente : cliente.getNombresOponentes()) {
            cbxVerEnemy.getItems().add(oponente);
            cbxKraken.getItems().add(oponente);
        }
    }

    private void setSpinners(){
        //sbxPrecioComponente.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 3000, 100, 100));
        coordX.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 19, 1, 1));
        coordY.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 19, 1, 1));
        for (Spinner<Integer> spinner : spinnersC) {
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 19, 0, 1));
        }

        for (Spinner<Integer> spinner : spinnersCM) {
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 19, 0, 1));
        }

        for (Spinner<Integer>[] row : spinnersBomb) {
            for (Spinner<Integer> spinner : row) {
                spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 18, 0, 1));
            }
        }

        for (Spinner<Integer>[] row : spinnersCBR) {
            for (Spinner<Integer> spinner : row) {
                spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 19, 0, 1));
            }
        }
    }

    private void agruparSpinnersAtaque(){
        spinnersC = new Spinner[] { sbxC_X, sbxC_Y };
        spinnersCM = new Spinner[] { sbxCM_X, sbxCM_Y };

        spinnersBomb = new Spinner[][] {
                { sbxBomb1_X, sbxBomb1_Y },
                { sbxBomb2_X, sbxBomb2_Y },
                { sbxBomb3_X, sbxBomb3_Y }
        };

        spinnersCBR = new Spinner[][] {
                { sbxCBR1_X, sbxCBR1_Y },
                { sbxCBR2_X, sbxCBR2_Y },
                { sbxCBR3_X, sbxCBR3_Y },
                { sbxCBR4_X, sbxCBR4_Y },
                { sbxCBR5_X, sbxCBR5_Y },
                { sbxCBR6_X, sbxCBR6_Y },
                { sbxCBR7_X, sbxCBR7_Y },
                { sbxCBR8_X, sbxCBR8_Y },
                { sbxCBR9_X, sbxCBR9_Y },
                { sbxCBR10_X, sbxCBR10_Y }
        };
    }

    private void dibujarLinea(AnchorPane anchorPane, double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStrokeWidth(2);
        line.setStroke(javafx.scene.paint.Color.BLUE);
        anchorPane.getChildren().add(line);
    }

    private void drawLine(AnchorPane anchorPane, int x1, int y1, int x2, int y2) {
        int StartX = 252 + x1*24;
        int StartY = 100 + y1*24;
        int EndX = 252 + x2*24;
        int EndY = 100 + y2*24;
        Line line = new Line(StartX, StartY, EndX, EndY);
        line.setStrokeWidth(2);
        line.setStroke(javafx.scene.paint.Color.BLUE);
        anchorPane.getChildren().add(line);
    }


    private void placeImage(AnchorPane anchorPane, int x, int y, String item) {
        Platform.runLater(() -> {
            int posX = 242 + x * 24;
            int posY = 90 + y * 24;
            String imagePath = "/Images/" + item + ".jpg";

            // Carga la imagen desde la ruta proporcionada
            System.out.println(String.valueOf(getClass().getResource(imagePath)));
            Image image = new Image(String.valueOf(getClass().getResource(imagePath)));

            // Crea un ImageView para mostrar la imagen
            ImageView imageView = new ImageView(image);

            // Ajusta las dimensiones del ImageView (opcional, según tus necesidades)
            if(item.equals("Conector") || item.equals("Tornado") || item.equals("Destruccion")) {
                imageView.setFitWidth(24);
                imageView.setFitHeight(24);
            } else if (item.equals("Tienda")) {
                imageView.setFitWidth(24);
                imageView.setFitHeight(48);
            } else if (item.equals("Energia")) {
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
            }

            // Posiciona el ImageView en el AnchorPane
            imageView.setLayoutX(posX);
            imageView.setLayoutY(posY);

            // Añade el ImageView al AnchorPane
            anchorPane.getChildren().add(imageView);
        });
    }

    private void placeEnemyImage(AnchorPane anchorPane, int x, int y, String item) {
        Platform.runLater(() -> {
            int posX = 245 + (x + 22) * 24;
            int posY = 90 + y * 24;
            String imagePath = "/Images/" + item + ".jpg";

            // Carga la imagen desde la ruta proporcionada
            System.out.println(String.valueOf(getClass().getResource(imagePath)));
            Image image = new Image(String.valueOf(getClass().getResource(imagePath)));

            // Crea un ImageView para mostrar la imagen
            ImageView imageView = new ImageView(image);

            // Ajusta las dimensiones del ImageView según tus necesidades
            if(item.equals("Conector") || item.equals("Tornado") || item.equals("Destruccion")) {
                imageView.setFitWidth(24);
                imageView.setFitHeight(24);
            } else if (item.equals("Tienda")) {
                imageView.setFitWidth(24);
                imageView.setFitHeight(48);
            } else if (item.equals("Energia")) {
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
            }

            // Posiciona el ImageView en el AnchorPane
            imageView.setLayoutX(posX);
            imageView.setLayoutY(posY);

            // Añade el ImageView al AnchorPane
            anchorPane.getChildren().add(imageView);

            // Añade el ImageView a la lista para referencia futura
            enemyImageViews.add(imageView);
        });
    }

    private void removeAllEnemyImages(AnchorPane anchorPane) {
        Platform.runLater(() -> {
            anchorPane.getChildren().removeAll(enemyImageViews);
            enemyImageViews.clear(); // Limpia la lista después de eliminar
        });
    }



    public void recibeGrafoEnemigo(String grafo) {
        // Dibujar en pantalla
        removeAllEnemyImages(anchorPane);
        System.out.println(grafo);
        String[] partes = grafo.split("t=&");
        // Tengo el grafo ahora tengo que procesarlo
        List<List<Integer>> listaAdyacencia = new ArrayList<>();
        int[][] matrizTipos;
        int[][] matrizTiposCopia;
        boolean[][] matrizDestruccion;


        System.out.println(partes[0]);
        System.out.println(partes[1]);
        System.out.println(partes[2]);

        listaAdyacencia = mapaDelMar.deserializeListaAdyacencia(partes[0]);
        matrizTipos = mapaDelMar.deserializeMatrix(partes[1]);
        matrizTiposCopia =  mapaDelMar.deserializeMatrix(partes[1]);
        matrizDestruccion = mapaDelMar.deserializeBooleanMatrix(partes[2]);

        System.out.println(listaAdyacencia);
        //System.out.println(matrizTipos.toString());
        System.out.println(Arrays.deepToString(matrizTipos));
        System.out.println(Arrays.deepToString(matrizDestruccion));

        /**
        for(int i = 0; i < listaAdyacencia.size(); i++) {
            System.out.println(matrizTipos[listaAdyacencia.get(i).get(1)][listaAdyacencia.get(i).get(0)]);
            System.out.println(matrizTipos[listaAdyacencia.get(i).get(3)][listaAdyacencia.get(i).get(2)]);
        }

        for(int i = 0; i < listaAdyacencia.size(); i++) {
            System.out.println(matrizTipos[listaAdyacencia.get(i).get(0)][listaAdyacencia.get(i).get(1)]);
            System.out.println(matrizTipos[listaAdyacencia.get(i).get(2)][listaAdyacencia.get(i).get(3)]);
        }
         */
        List<List<Integer>> conexionesFuente = new ArrayList<>();
        conexionesFuente = mapaDelMar.obtenerConexFuente(listaAdyacencia, matrizDestruccion, matrizTipos);
        System.out.println("ADY" + listaAdyacencia);
        System.out.println(conexionesFuente);
        // Tengo que dibujar en todos los items de matrizEliminados sex
        for(int i = 0; i < matrizDestruccion.length; i++){
            for(int j = 0; j < matrizDestruccion[i].length; j++){
                if(matrizDestruccion[i][j]){
                    System.out.println(i + " " + j + " ESTA DESTRUIU ");
                    placeEnemyImage(anchorPane, j, i, "Destruccion");
                }
            }
        }
        for(int i = 0; i < conexionesFuente.size(); i++) {
            System.out.println(conexionesFuente.get(i));

            if(matrizTipos[conexionesFuente.get(i).get(1)][conexionesFuente.get(i).get(0)] == 2
            || matrizTipos[conexionesFuente.get(i).get(1)][conexionesFuente.get(i).get(0)] == 3 ||
                    matrizTipos[conexionesFuente.get(i).get(1)][conexionesFuente.get(i).get(0)] == 4
            ){
                placeEnemyImage(anchorPane, conexionesFuente.get(i).get(0), conexionesFuente.get(i).get(1), getStringFromNumber(matrizTipos[conexionesFuente.get(i).get(1)][conexionesFuente.get(i).get(0)]));
                matrizTiposCopia[conexionesFuente.get(i).get(1) + 1][conexionesFuente.get(i).get(0)] = 0;
            }
            matrizTiposCopia[conexionesFuente.get(i).get(1)][conexionesFuente.get(i).get(0)] = 0;
        }
        mapaDelMar.inicializarGridEnemgio(matrizTiposCopia,  PantallaEnemigo);
        // Lo que no esta conectado a la fuente tengo que mostrarlo :)


    }

    public void recibeEscudoEnemigo(int escudo){
        lblEscudoEnemigo.setText(escudo + "");
    }

    @FXML
    protected void onBtnVerEnemyClick() {
        String nombreEnemigo = cbxVerEnemy.getValue();
        if(nombreEnemigo==null){return;}
        try {
            cliente.getSalidaObjetos().writeObject(CasesEnThreadServidor.CONSEGUIRGRAFOENEMIGO);
            cliente.getSalidaDatos().writeUTF(nombreEnemigo);
        } catch (Exception e) {System.out.println("Error consiguiendo grafo enemigo");}
    }

    public void testLineDraw() {
        String selectedConnector = conectorComboBox.getValue();
        String selectedItem = placeItemComboBox.getValue();
        if (selectedConnector != null) {
            String[] coordinates = selectedConnector.split(",");
            try {
                int x = Integer.parseInt(coordinates[0].trim());
                int y = Integer.parseInt(coordinates[1].trim());
                System.out.println("X: " + x + ", Y: " + y);
                // conectorComboBox.getItems().remove(selectedConnector);
                // Ahora hay que pasarle las del item :)
                System.out.println(selectedItem);
                List<Integer> listaCoordenadasItem  = hashPosItems.get(selectedItem);
                drawLine(anchorPane, x, y, listaCoordenadasItem.get(0), listaCoordenadasItem.get(1));
                mapaDelMar.conectarIslas(x, y, listaCoordenadasItem.get(0), listaCoordenadasItem.get(1)); // Por ahora solo conecto el item papa
            } catch (NumberFormatException e) {
                System.out.println("Error: formato inválido en selectedConnector.");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Error: selectedConnector no tiene el formato esperado.");
            }
        } else {
            System.out.println("No hay conector seleccionado.");
        }
    }


    public void backFromStore(Cliente _cliente, MapaDelMar _mapaDelMar){
        this.cliente = _cliente;
        loadDataComboBox();
        this.mapaDelMar = _mapaDelMar;
        for(int i = 0; i < mapaDelMar.getMatrizTipos().length; i++){
            System.out.println(Arrays.toString(mapaDelMar.getMatrizTipos()[i]));
        }
        mapaDelMar.inicializarGrid();
        mapaDelMar.recrearGrid(PantallaJugador);
    }

    public void updateGUIDespuesDeOfertaOCompra(){
        if(storeActual != null){
            Platform.runLater(() -> storeActual.actualizarComponentesCbx());
            Platform.runLater(() -> storeActual.actualizarDinero());
            Platform.runLater(() -> storeActual.actualizarAcero());
        } else {
            Platform.runLater(() ->loadDataComboBox());
        }
    }

    @FXML
    protected void onBtnSendClick(){
        String mensaje = cliente.getNombreCliente() + ": " + txfMensaje.getText();
        cliente.mandarMensaje(mensaje);
        txfMensaje.clear();
    }

    @FXML protected void btnGoStore() {
        if(!mapaDelMar.getItemsInScreen().contains("Tienda")){return;}

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cliente/StoreScreen.fxml"));
                Parent root = loader.load();
                StoreController controller = loader.getController();
                storeActual = controller;
                controller.setGameController(cliente, mapaDelMar);
                Stage popupStage = new Stage();
                popupStage.setTitle("Store");
                popupStage.initModality(Modality.WINDOW_MODAL);
                popupStage.initOwner(MainCliente.getPrimaryStage());
                Scene scene = new Scene(root);
                popupStage.setScene(scene);

                popupStage.setOnHidden(event -> {
                    System.out.println("El pop-up ha sido cerrado.");
                    itemComboBox.getItems().clear();
                    loadDataComboBox();
                    storeActual = null;
                });
                popupStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @FXML
    protected void btnProcessData(ActionEvent event){
        String selectedItem = itemComboBox.getValue();
        int selectedInt = getNumberFromString(selectedItem);
        /**
        int coordXInt = Integer.parseInt(coordX.getText());
        int coordYInt = Integer.parseInt(coordY.getText());
         */
        int coordXInt = coordX.getValue();
        int coordYInt = coordY.getValue();
        // PROCESA
        // 1. Tengo que ver si lo puedo colocar donde se quiere:
        // Este verificador debe de tomar todos los bloques del evento en cuenta
        if(mapaDelMar.estaDisponibleItem(coordXInt, coordYInt, selectedInt)) {

            mapaDelMar.asignarTipoIsla(coordYInt, coordXInt, selectedInt); // En este item se agrega la logica para cubrir todos los bloques
            // TENGO QUE VER COMO LLEVO UN REGISTRO DE ESTO
            mapaDelMar.inicializarGrid();
            itemComboBox.getItems().remove(selectedItem);
            cliente.getListaItems().remove(selectedItem);
            // Yo deberia de tomar esto, y guardarlo en algo que me permita saber que item esta en que coordenada?

            if (Objects.equals(selectedItem, "Conector")) {
                List<Integer> coordenadasConect = new ArrayList<>();
                coordenadasConect.add(coordXInt);
                coordenadasConect.add(coordYInt);
                String itemComboBox = coordX.getValue().toString() + "," + coordY.getValue().toString();
                coordenadasConector.add(coordenadasConect);
                conectorComboBox.getItems().add(itemComboBox);
                // Ahora, debo de registrar en el grafo que existe una liga
                placeImage(anchorPane, coordXInt, coordYInt, "Conector");
            } else {
                mapaDelMar.getItemsInScreen().add(selectedItem);
                // El item es algo que puede ser conectado
                // Hay que hacer el hash que guarda la informacion de conexcion
                placeItemComboBox.getItems().add(selectedItem);
                List<Integer> coordItem = new ArrayList<>();
                coordItem.add(coordXInt);
                coordItem.add(coordYInt);
                colocarEnHash(selectedItem, coordItem);
                // Voy a hacer otro hash que sea, coordInciialx, coordinicialy, (todo el resto de coordenadas)
                colocarTodasCoordsHash(coordXInt, coordYInt, selectedInt); // Ya aqui deberian de estar todas las coordenadas de cada item
                placeImage(anchorPane, coordXInt, coordYInt, selectedItem);
                // Ahora tengo que asignar todos los cuadros en mapaDelMar
            }
        } else {
            // Debe de haber un laben en pantalla en donde le muestro el error
            System.out.println("Hay algun bloque ocupado.");
        }
    }

    /**
     * La funcion de ataque devuelva un enum
     * ¿Que casos?
     * -> Que ataco a una fuente de energia (Me la da) (En cualquier casilla)
     * -> Que ataque a un remolino (Se debe de ejecutar la funcion que envia ataques)
     * -> No le di a nada (Tengo que dejar marcado que el me disparo ahi)
     * -> Si le di a algo (No puede estar dentro de lo especificado) (Se debe de tramitar bien el item como eliminado en el grafo)
     * -> y eso
     */

    private void cronoMina(){
        while(true){
            int[] sec = {0};
            int[] min = {1};
            while(sec[0] != 0 || min[0] != 0){
                sec[0]--;
                if(sec[0]<0){
                    min[0]--;
                    sec[0] = 59;
                }
                Platform.runLater(() -> lblMinaTimer.setText(Utilities.formatearEnTimer(min[0]) + ":" + Utilities.formatearEnTimer(sec[0])));
                try {Thread.sleep(1000);} catch (InterruptedException ignore) {}
            }
            generarAcero();
        }
    }

    private void generarAcero(){
        int minas = Collections.frequency(mapaDelMar.getItemsInScreen(),"Mina");
        cliente.subirAcero(minas*100);
    }

    private void cronoComodin(){
        while(true){
            int[] sec = {0};
            int[] min = {5};
            Platform.runLater(()-> lblComodinTimer.setText("N/A"));
            while(!mapaDelMar.getItemsInScreen().contains("Templo")){
                try {Thread.sleep(1000);} catch (InterruptedException ignore) {}
            } //si no tiene templo, entonces a esperar

            while(!comodinListo){
                if(!mapaDelMar.getItemsInScreen().contains("Templo")){break;}
                sec[0]--;
                if(sec[0]==0 && min[0] ==0){
                    comodinListo = true;
                    Platform.runLater(()-> lblComodinTimer.setText("Comodín Listo"));
                    comodinListo();
                    continue;
                } else if(sec[0]<0 && min[0] > 0){
                    min[0]--;
                    sec[0] = 59;
                }
                Platform.runLater(()-> lblComodinTimer.setText(Utilities.formatearEnTimer(min[0]) + ":" + Utilities.formatearEnTimer(sec[0])));
                try {Thread.sleep(1000);} catch (InterruptedException ignore) {}
            }
            while(comodinListo){
                if(!mapaDelMar.getItemsInScreen().contains("Templo")){
                    btnEscudo.setDisable(true);
                    btnKraken.setDisable(true);
                    break;
                }
                try {Thread.sleep(1000);} catch (InterruptedException ignore) {}
            }
        }
    }



    private void comodinListo(){
        if(Random.randomBoolean()){
            btnEscudo.setDisable(false);
        } else {
            btnKraken.setDisable(false);
        }
    }

    @FXML
    protected void onBtnEscudoClick(){
        btnEscudo.setDisable(false);
        cliente.setEscudo(Random.randomInt(2,5));
    }

    private boolean validacionesAtaques(){
        if(cliente.getTurnoActual() != cliente.getIdCliente()) {
            Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "No es tu turno").showAndWait());
            return true;
        }
        if(!cliente.isJugando()){
            Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "Ya perdiste, no puedes jugar").showAndWait());
            return true;
        }
        if(!cliente.getListaItems().isEmpty()){
            Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "No puedes atacar hasta poner todos los objetos en tu inventario").showAndWait());
            return true;
        }
        return false;
    }

    @FXML
    protected void onBtnKrakenClick(){
        if(validacionesAtaques()){return;}

        String nombreEnemigo = cbxKraken.getValue();
        if(nombreEnemigo==null){return;}

        btnKraken.setDisable(false);
        try {
            cliente.getSalidaObjetos().writeObject(CasesEnThreadServidor.ATACARKRAKEN);
            cliente.getSalidaDatos().writeUTF(nombreEnemigo);
        } catch (IOException e) {System.out.println("Error atacando con Kraken");}
    }

    @FXML
    protected void onBtnCClick(){
        if(validacionesAtaques()){return;}

        String nombreEnemigo = cbxVerEnemy.getValue();
        if(nombreEnemigo==null){return;}
        if(cliente.getCanon() <= 0){return;}
        cliente.usarCanon();

        try {
            int[] coords = new int[2];
            for (int i = 0; i < coords.length; i++) {
                coords[i] = spinnersC[i].getValue();
            }
            cliente.getSalidaObjetos().writeObject(CasesEnThreadServidor.ATACARCANON);
            cliente.getSalidaDatos().writeUTF(nombreEnemigo);
            cliente.getSalidaObjetos().writeObject(coords);
        } catch (Exception e) {System.out.println("Error atacando con cañon");}
    }

    @FXML
    protected void onBtnCMClick(){
        if(validacionesAtaques()){return;}

        String nombreEnemigo = cbxVerEnemy.getValue();
        if(nombreEnemigo==null){return;}
        if(cliente.getCanonMult() <= 0){return;}
        cliente.usarCanonMult();

        try {
            int[] coords = new int[2];
            for (int i = 0; i < coords.length; i++) {
                coords[i] = spinnersCM[i].getValue();
            }
            cliente.getSalidaObjetos().writeObject(CasesEnThreadServidor.ATACARCANONMULT);
            cliente.getSalidaDatos().writeUTF(nombreEnemigo);
            cliente.getSalidaObjetos().writeObject(coords);
        } catch (Exception e) {System.out.println("Error atacando con cañon múltiple");}
    }

    @FXML
    protected void onBtnBombClick(){
        if(validacionesAtaques()){return;}

        String nombreEnemigo = cbxVerEnemy.getValue();
        if(nombreEnemigo==null){return;}
        if(cliente.getBomba() <= 0){return;}
        cliente.usarBomba();

        try {
            int[][] coords = new int[3][2];
            for (int i = 0; i < coords.length; i++) {
                for (int j = 0; j < 2; j++) {
                    coords[i][j] = spinnersBomb[i][j].getValue();
                }
            }
            cliente.getSalidaObjetos().writeObject(CasesEnThreadServidor.ATACARBOMBA);
            cliente.getSalidaDatos().writeUTF(nombreEnemigo);
            cliente.getSalidaObjetos().writeObject(coords);
        } catch (Exception e) {System.out.println("Error atacando con bomba");}
    }

    @FXML
    protected void onBtnCBRClick(){
        if(validacionesAtaques()){return;}

        String nombreEnemigo = cbxVerEnemy.getValue();
        if(nombreEnemigo==null){return;}
        if(cliente.getCanonBR() <= 0){return;}
        cliente.usarCanonBR();

        try {
            int[][] coords = new int[10][2];
            for (int i = 0; i < coords.length; i++) {
                for (int j = 0; j < 2; j++) {
                    coords[i][j] = spinnersCBR[i][j].getValue();
                }
            }
            cliente.getSalidaObjetos().writeObject(CasesEnThreadServidor.ATACARCBR);
            cliente.getSalidaDatos().writeUTF(nombreEnemigo);
            cliente.getSalidaObjetos().writeObject(coords);
        } catch (Exception e) {System.out.println("Error atacando con Cañón de Barba Roja");}
    }

    public TiposAtaque atacarIsla(int x, int y){
        // Nunca se debe de retornar null
        //return CasesEnCliente.NADA; // El caso de nothing
        System.out.println("Ataque " + x + " " + y);
        // Lo primero que vamos a hacer es poner el ataque en matriz destruccion
        mapaDelMar.getMatrizDestruccion()[y][x] = true; // Esta isla ya fue atacada :(
        System.out.println("Mta" + mapaDelMar.getMatrizTipos()[y][x]);
        if(mapaDelMar.getMatrizTipos()[y][x] == 1){
            // reviso al rededor
            // arriba, abajo izquierda, derecha
            if(mapaDelMar.getMatrizTipos()[y + 1][x] == 1) {
                mapaDelMar.getMatrizDestruccion()[y + 1][x] = true;
            }
            if(mapaDelMar.getMatrizTipos()[y - 1][x] == 1) {
                mapaDelMar.getMatrizDestruccion()[y - 1][x] = true;
            }if(mapaDelMar.getMatrizTipos()[y][x + 1] == 1) {
                mapaDelMar.getMatrizDestruccion()[y][x + 1] = true;
            }if(mapaDelMar.getMatrizTipos()[y][x - 1] == 1) {
                mapaDelMar.getMatrizDestruccion()[y][x - 1] = true;
            }
            // Diag arriba, diag abajo, diag izquierda, diag derecha
            if(mapaDelMar.getMatrizTipos()[y + 1][x + 1] == 1) {
                mapaDelMar.getMatrizDestruccion()[y + 1][x + 1]= true;
            }if(mapaDelMar.getMatrizTipos()[y + 1][x - 1] == 1) {
                mapaDelMar.getMatrizDestruccion()[y + 1][x - 1] = true;
            }if(mapaDelMar.getMatrizTipos()[y - 1][x - 1] == 1) {
                mapaDelMar.getMatrizDestruccion()[y - 1][x - 1] = true;
            }if(mapaDelMar.getMatrizTipos()[y - 1][x + 1] == 1) {
                mapaDelMar.getMatrizDestruccion()[y - 1][x + 1] = true;
            }
        }
        // Ahora tengo que ver que esta en esa coordenada en la martiz, y en base a eso tomar una desicion :)
        System.out.println("Ataque " + mapaDelMar.getMatrizTipos()[x][y]);
        System.out.println("ITINSC" + mapaDelMar.getItemsInScreen());
        if(mapaDelMar.getMatrizTipos()[y][x] != 0){
            // Primer caso IMPORTANTE, cuando le doy a una fuente de energia :(
            // Tengo que ver cuales son el resto de coordenadas de esa fuente para destruirlas tambien
            if(mapaDelMar.getMatrizTipos()[y][x] == 1){
                // Tengo que darle al enemigo una fuente de energia y destuir la mia
                // DESTRUCCION DE MI FUENTE DE ENERGIA
                mapaDelMar.getItemsInScreen().remove("Energia");
                // Tengo que indicar que esta fuente de energia no esta, para que muestre lo que ahora es disconexo :P
                return TiposAtaque.FUENTEDEENERGIA;
            } else if (mapaDelMar.getMatrizTipos()[y][x] == 4){
                mapaDelMar.getItemsInScreen().remove("Tienda");
                return TiposAtaque.HIT;
            } else if (mapaDelMar.getMatrizTipos()[y][x] == 7){
                mapaDelMar.getItemsInScreen().remove("Tornado");
                return TiposAtaque.REMOLINO;
            }
            placeImage(anchorPane, x, y, "Destruccion");
        }
        // Lo ultimo es darle feedback al usuario de lo que paso :)
        placeImage(anchorPane, x, y, "Destruccion");
        return TiposAtaque.MISS;
    }

    public TextArea getTxaChat() {
        return txaChat;
    }

    public TextArea getTxaAcciones(){
        return txaAcciones;
    }

    public MapaDelMar getMapaDelMar() {
        return mapaDelMar;
    }
}
