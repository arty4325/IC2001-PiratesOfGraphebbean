package Cliente;

import Modelos.CasesEnCliente;
import Modelos.CasesEnThreadServidor;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private ClienteScreenController pantallaCliente;
    private MainGameController pantallaMain;
    private DataInputStream entradaDatos;
    private DataOutputStream salidaDatos;
    private ObjectInputStream entradaObjetos;
    private ObjectOutputStream salidaObjetos;
    private Socket socket; //Socket del cliente.
    private String nombreCliente;
    private boolean canStart = false;
    // Aqui voy a tener una lista de items que van a estar en pantalla
    private List<String> listaItems = new ArrayList<String>();


    public Cliente(ClienteScreenController pantallaCliente) {
        this.pantallaCliente = pantallaCliente;
    }

    public List<String> getListaItems() {
        return listaItems;
    }

    public void run() {
        listaItems.add("Tienda");
        listaItems.add("Energia");
        try {
            conectar();
        } catch (Exception ex) {System.out.println("Error conectando al servidor");}

        try {
            esperarStart();
        } catch (Exception ex) {System.out.println("Error conectando al servidor");}
        System.out.println("ya todos presionaron listo");
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

    private void juegoEmpieza() {
        CasesEnCliente evento = CasesEnCliente.NADA;
        while (true) {
            try {
                evento = (CasesEnCliente) entradaObjetos.readObject();
            } catch (Exception ex) {
                System.out.println("Error con entrada de evento en threadCliente");
            }
            switch (evento) {
                case RECIBIRMENSAJE:
                    try {
                        recibirMensaje();
                        break;
                    } catch (Exception ex) {
                        System.out.println("Error con caso recibirMensaje en ThreadCliente");
                    }
                case RECIBIRACCION:
                    try {
                        recibirAccion();
                        break;
                    } catch (Exception ex) {
                        System.out.println("Error con caso recibirAccion en ThreadCliente");
                    }
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

    private void recibirAccion() throws Exception{
        String mensaje = entradaDatos.readUTF();
        pantallaMain.getTxaAcciones().appendText(mensaje + "\n");
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
}
