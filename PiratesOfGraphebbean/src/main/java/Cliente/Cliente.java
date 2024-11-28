package Cliente;

import Modelos.CasesEnCliente;
import Modelos.CasesEnThreadServidor;
import Modelos.TiposAtaque;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Cliente {
    private ClienteScreenController pantallaCliente;
    private MainGameController pantallaMain;
    private DataInputStream entradaDatos;
    private DataOutputStream salidaDatos;
    private ObjectInputStream entradaObjetos;
    private ObjectOutputStream salidaObjetos;
    private Socket socket; //Socket del cliente.
    private String nombreCliente;
    private int idCliente;
    private boolean canStart = false;
    private ArrayList<String> nombresOponentes;
    // Aqui voy a tener una lista de items que van a estar en pantalla
    private List<String> listaItems = new ArrayList<String>();
    private int acero;
    private int dinero;
    private int canon;
    private int canonMult;
    private int bomba;
    private int canonBR;
    private int turnoActual;
    private boolean jugando;
    private int escudo;



    public Cliente(ClienteScreenController pantallaCliente) {
        this.pantallaCliente = pantallaCliente;
        dinero = 1000000;
        acero = 1000000; //tesitng values
        canon = 0;
        canonMult = 0;
        bomba = 0;
        canonBR = 0;
        jugando = true;
        turnoActual = 1;
    }

    public List<String> getListaItems() {
        return listaItems;
    }

    public void run() {
        listaItems.add("Tienda");
        listaItems.add("Energia");
        listaItems.add("Tornado");
        try {
            conectar();
            idCliente = entradaDatos.readInt();
        } catch (Exception ex) {System.out.println("Error conectando al servidor");}

        try {
            esperarStart();
        } catch (Exception ex) {System.out.println("Error esperando a start");}
        System.out.println(idCliente + " " + nombreCliente);
        System.out.println("ya todos presionaron listo");

        try{
            conseguirNombresOponentes();
        } catch (Exception ex) {System.out.println("Error consiguiendo nombre de oponentes");}

        new Thread(() -> {
            juegoEmpieza();
        }).start();
    }

    public void conectar() throws Exception {
        socket = new Socket("localhost", 50000);
        entradaDatos = new DataInputStream(socket.getInputStream());
        salidaDatos = new DataOutputStream(socket.getOutputStream());
        salidaObjetos = new ObjectOutputStream(socket.getOutputStream());
        entradaObjetos = new ObjectInputStream(socket.getInputStream());
    }

    public boolean mandarNombreAServer(String nombre) {
        if(nombre.trim().equals("")){
            return false;
        }
        try {
            this.nombreCliente = nombre;
            salidaDatos.writeUTF(nombre);
        } catch (Exception ex) {
            System.out.println("Error mandando el nombre");
        }
        return true;
    }

    public void mandarIniciarAServer() {
        try {
            salidaDatos.writeBoolean(true);
        } catch (Exception ex) {
            System.out.println("Error mandando que se presionó el botón iniciar");
        }
    }

    // Algunas cosas que tienen que llegar del cliente van a venir por aqui:
    private void esperarStart() {
            try {
                String mensaje = entradaDatos.readUTF();
                System.out.println(mensaje);
                if (mensaje.equals("START")) {

                    // aqui tengo que decirle a la aplicacion que tiene que moverse al siguiente stage
                    canStart = true;
                    System.out.println("test pantalla");
                    pantallaCliente.moveMain();
                }
            } catch (Exception ex) {
                System.out.println("Error recibiendo mensaje del servidor");
                ex.printStackTrace();
            }
    }

    private void conseguirNombresOponentes() throws Exception {
        salidaObjetos.writeObject(CasesEnThreadServidor.DEVOLVERNOMBRESOPONENTES);
        nombresOponentes = (ArrayList<String>)entradaObjetos.readObject();
    }

    private void juegoEmpieza() {
        CasesEnCliente evento = CasesEnCliente.NADA;
        while (true) {
            try {
                evento = (CasesEnCliente) entradaObjetos.readObject();
            } catch (Exception ex) {
                System.out.println("Error con entrada de evento en threadCliente");
            }
            switch (evento) {
                case SIGUIENTETURNO:
                    try {
                        turnoActual = entradaDatos.readInt();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso SIGUIENTETURNO");}
                case RECIBIRMENSAJE:
                    try {
                        recibirMensaje();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso recibirMensaje en Cliente");}
                case PONERENBITACORA:
                    try {
                        ponerEnBitacora();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso ponerEnBitacora en Cliente");}
                case RECIBIROFERTA:
                    try {
                        recibirOferta();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso recibirOferta en Cliente");}
                case OFERTAACEPTADA:
                    try {
                        ofertaAceptada();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso ofertaAceptada en Cliente");}
                case DEVOLVERGRAFO:
                    try {
                        salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
                        salidaObjetos.writeObject(pantallaMain.getMapaDelMar().serializar());
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso devolverGrafo en Cliente" + ex);}
                case SETGRAFOENEMIGO:
                    try {
                        setGrafoEnemigo();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso setGrafoEnemigo en Cliente");}
                case SETESCUDOENEMIGO:
                    try {
                        setEscudoEnemigo();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso setEscudoEnemigo en Cliente");}
                case RECIBIROFERTAACERO:
                    try {
                        recibirOfertaAcero();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso recibirOfertaAcero en Cliente");}
                case OFERTAACEPTADAACERO:
                    try {
                        ofertaAceptadaAcero();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso ofertaAceptadaAcero en Cliente");}
                case SERATACADO:
                    try {
                        serAtacado();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso serAtacado en Cliente");ex.printStackTrace();}
                case SERATACADOPORREMOLINO:
                    try {
                        serAtacadoPorRemolino();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso serAtacado en Cliente");ex.printStackTrace();}
                case CONSEGUIRFUENTE:
                    try {
                        listaItems.add("Energia");
                        pantallaMain.loadDataComboBox();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso serAtacado en Cliente");}
                case ALGUIENGANO:
                    try {
                        alguienGano();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso alguienGano en Cliente");}
                case YOGANE:
                    try {
                        yoGane();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso yoGane en Cliente");}
                case DEVOLVERESCUDO:
                    try {
                        salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
                        salidaObjetos.writeObject(escudo);
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso devolverEscudo en Cliente");}
                case BAJARESCUDO:
                    try {
                        escudo--;
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso bajarEscudo en Cliente");}
                case DEVOLVERCOORDSCOMP:
                    try {
                        devolverCoordsComp();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso devolverCoordsComp en Cliente");}
            }
        }
    }

    public void mandarMensaje(String mensaje){
        try {
            salidaObjetos.writeObject(CasesEnThreadServidor.MANDARMENSAJE);
            salidaDatos.writeUTF(mensaje);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void recibirMensaje() throws Exception{
        String mensaje = entradaDatos.readUTF();
        pantallaMain.getTxaChat().appendText(mensaje + "\n");
    }

    private void ponerEnBitacora() throws Exception{
        String mensaje = entradaDatos.readUTF();
        pantallaMain.getTxaAcciones().appendText(mensaje + "\n");
    }

    private void recibirOferta() throws Exception{
        String jugadorProponiendo = entradaDatos.readUTF();
        String itemPropuesto = entradaDatos.readUTF();
        int precio = entradaDatos.readInt();
        System.out.println("llega aqui");

//        ButtonType yesButton = new ButtonType("Sí");
//        ButtonType noButton = new ButtonType("No");
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, jugadorProponiendo + " te propone " + itemPropuesto + " por el precio de " + precio + ". Aceptas?", yesButton, noButton);
//        alert.setTitle("Confirmación");
//        alert.getButtonTypes().setAll(yesButton, noButton);
//        Optional<ButtonType> result = alert.showAndWait();
//
//        if (result.isPresent() && result.get() == yesButton) {
//            System.out.println("lo quiso");
//            if(tengoDineroSuficiente(precio)){
//                salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
//                salidaObjetos.writeObject(true);
//                bajarDinero(precio);
//                listaItems.add(itemPropuesto);
//                System.out.println(nombreCliente + " " +  dinero);
//            } else{
//                salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
//                salidaObjetos.writeObject(false);
//            }
//        } else {
//            System.out.println("no lo quiso");
//            salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
//            salidaObjetos.writeObject(false);
//        }



        Platform.runLater(() -> {
            try {
                ButtonType yesButton = new ButtonType("Sí");
                ButtonType noButton = new ButtonType("No");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, jugadorProponiendo + " te propone " + itemPropuesto + " por el precio de " + precio + ". ¿Aceptas?", yesButton, noButton);
                alert.setTitle("Confirmación");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == yesButton) {
                    System.out.println("lo quiso");
                    if(tengoDineroSuficiente(precio)){
                        salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
                        salidaObjetos.writeObject(true);
                        bajarDinero(precio);
                        listaItems.add(itemPropuesto);
                        System.out.println(nombreCliente + " " +  dinero);
                        pantallaMain.updateGUIDespuesDeOfertaOCompra();
                    } else{
                        salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
                        salidaObjetos.writeObject(false);
                    }
                } else {
                    System.out.println("no lo quiso");
                    salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
                    salidaObjetos.writeObject(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void ofertaAceptada() throws Exception{
        String item = entradaDatos.readUTF();
        listaItems.remove(item);
        subirDinero(entradaDatos.readInt());
        System.out.println(nombreCliente + " " +  dinero);
        pantallaMain.updateGUIDespuesDeOfertaOCompra();
    }

    private void setGrafoEnemigo() throws Exception{
        String grafoEnemigo = entradaDatos.readUTF();
        Platform.runLater(() -> pantallaMain.recibeGrafoEnemigo(grafoEnemigo));
    }

    private void setEscudoEnemigo() throws Exception{
        int escudo = entradaDatos.readInt();
        Platform.runLater(() -> pantallaMain.recibeEscudoEnemigo(escudo));
    }

    private void recibirOfertaAcero() throws Exception{
        String jugadorProponiendo = entradaDatos.readUTF();
        int cantidadPropuesta = entradaDatos.readInt();
        int precio = entradaDatos.readInt();

        Platform.runLater(() -> {
            try {
                ButtonType yesButton = new ButtonType("Sí");
                ButtonType noButton = new ButtonType("No");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, jugadorProponiendo + " te propone " + cantidadPropuesta + " de acero por el precio de " + precio + ". ¿Aceptas?", yesButton, noButton);
                alert.setTitle("Confirmación");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == yesButton) {
                    System.out.println("lo quiso");
                    if(tengoDineroSuficiente(precio)){
                        salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
                        salidaObjetos.writeObject(true);
                        bajarDinero(precio);
                        subirAcero(cantidadPropuesta);
                        pantallaMain.updateGUIDespuesDeOfertaOCompra();
                    } else{
                        salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
                        salidaObjetos.writeObject(false);
                    }
                } else {
                    System.out.println("no lo quiso");
                    salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
                    salidaObjetos.writeObject(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void ofertaAceptadaAcero() throws Exception{
        int cant = entradaDatos.readInt();
        bajarAcero(cant);
        subirDinero(entradaDatos.readInt());
        pantallaMain.updateGUIDespuesDeOfertaOCompra();
    }

    private void serAtacado() throws Exception{
        System.out.println("Test 1");
        int[] coords = (int[])entradaObjetos.readObject();
        // Estas son las coordenadas en las que me atacan
        // Aqui es donde tengo que hacer cositas lindas
        TiposAtaque tipoAtaqueRetornar = pantallaMain.atacarIsla(coords[0],coords[1]); // Aqui es en donde llego yo y le mando el ataque a mi compa
        System.out.println("Test 2 " + tipoAtaqueRetornar);
        if(pantallaMain.getMapaDelMar().perdi()){ //TODO: CAMBIAR EL FALSE POR CONDICIONAL QUE REVISE SI YA PERDÍ
            jugando = false;
            salidaObjetos.writeObject(CasesEnThreadServidor.PERDER);
        }
        System.out.println("Test 3");
        salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
        salidaObjetos.writeObject(tipoAtaqueRetornar);
        System.out.println("Test 4");
    }

    private void serAtacadoPorRemolino() throws Exception{
        System.out.println("Test 1");
        int[] coords = (int[])entradaObjetos.readObject();
        // Estas son las coordenadas en las que me atacan
        // Aqui es donde tengo que hacer cositas lindas
        TiposAtaque tipoAtaqueRetornar = pantallaMain.atacarIsla(coords[0],coords[1]); // Aqui es en donde llego yo y le mando el ataque a mi compa
        System.out.println("Test 2 " + tipoAtaqueRetornar);
        if(pantallaMain.getMapaDelMar().perdi()){ //TODO: CAMBIAR EL FALSE POR CONDICIONAL QUE REVISE SI YA PERDÍ
            jugando = false;
            salidaObjetos.writeObject(CasesEnThreadServidor.PERDER);
        }
        System.out.println("Test 3");
        salidaObjetos.writeObject(tipoAtaqueRetornar);
        System.out.println("Test 4");
    }

    private void alguienGano() throws Exception{
        int gano = entradaDatos.readInt();
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "Ganó el jugador " + gano).showAndWait());
    }

    private void yoGane() throws Exception{
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "¡Tu ganaste!").showAndWait());
    }

    private void devolverCoordsComp() throws Exception{
        int[] coords = pantallaMain.getMapaDelMar().conseguirElementoRandom();
        salidaObjetos.writeObject(CasesEnThreadServidor.PONERENOBJETO);
        salidaObjetos.writeObject(coords);
    }

    public boolean tengoDineroSuficiente(int precio){
        return dinero - precio >= 0; //si la resta da más o igual que 0, puede comprar.
    }

    public void bajarDinero(int precio){
        dinero -= precio;
    }

    public void subirDinero(int precio){
        dinero += precio;
    }

    public boolean tengoAceroSuficiente(int cantidad){
        return acero - cantidad >= 0; //si la resta da más o igual que 0, puede comprar.
    }

    public void bajarAcero(int cantidad){
        acero -= cantidad;
    }

    public void subirAcero(int cantidad){
        acero += cantidad;
    }









    public String getNombreCliente() {
        return nombreCliente;
    }

    public boolean getCanStart() {
        return canStart;
    }

    public void setGameController(MainGameController pantallaMain) {
        this.pantallaMain = pantallaMain;
    }

    public ArrayList<String> getNombresOponentes() {
        return nombresOponentes;
    }

    public int getDinero() {
        return dinero;
    }

    public DataInputStream getEntradaDatos() {
        return entradaDatos;
    }

    public DataOutputStream getSalidaDatos() {
        return salidaDatos;
    }

    public ObjectInputStream getEntradaObjetos() {
        return entradaObjetos;
    }

    public ObjectOutputStream getSalidaObjetos() {
        return salidaObjetos;
    }

    public int getAcero() {
        return acero;
    }

    public void comprarCanon(){
        canon++;
    }

    public void usarCanon(){
        canon--;
    }

    public void comprarCanonMult() {
        canonMult++;
    }

    public void usarCanonMult() {
        canonMult--;
    }

    public void comprarBomba() {
        bomba++;
    }

    public void usarBomba() {
        bomba--;
    }

    public void comprarCanonBR() {
        canonBR++;
    }

    public void usarCanonBR() {
        canonBR--;
    }

    public int getCanon(){
        return canon;
    }

    public int getCanonMult() {
        return canonMult;
    }

    public int getBomba() {
        return bomba;
    }

    public int getCanonBR() {
        return canonBR;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    public boolean isJugando() {
        return jugando;
    }

    public void setEscudo(int escudo) {
        this.escudo = escudo;
    }
}
