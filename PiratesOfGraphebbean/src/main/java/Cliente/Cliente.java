package Cliente;

import Modelos.CasesEnCliente;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

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


    public Cliente(ClienteScreenController pantallaCliente) {
        this.pantallaCliente = pantallaCliente;
    }

    public void run() {
        try {
            conectar();
        } catch (Exception ex) {System.out.println("Error conectando al servidor");}

        try {
            esperarStart();
        } catch (Exception ex) {System.out.println("Error conectando al servidor");}

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
        System.out.println("test2");
    }

    public void mandarNombreAServer(String nombre) {
        try {
            this.nombreCliente = nombre;
            salidaDatos.writeUTF(nombre);
        } catch (Exception ex) {
            System.out.println("Error mandando el nombre");
        }
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
                }
            } catch (Exception ex) {
                System.out.println("Error recibiendo mensaje del servidor");
                ex.printStackTrace();
            }
    }

    private void juegoEmpieza(){
        CasesEnCliente evento = CasesEnCliente.NADA;
        while(true){
            try {
                evento = (CasesEnCliente) entradaObjetos.readObject();
            } catch (Exception ex) {System.out.println("Error con entrada de evento en threadCliente");}
            switch(evento){
                case RECIBIRMENSAJE:
                    try {
                        recibirMensaje();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso recibirMensaje en ThreadCliente");}
                case RECIBIRACCION:
                    try {
                        recibirAccion();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso recibirAccion en ThreadCliente");}
            }
        }
    }

    private void recibirMensaje() throws Exception{
        String mensaje = entradaDatos.readUTF();
        pantallaMain.getTxaChat().appendText(mensaje + "\n");
    }

    private void recibirAccion() throws Exception{
        String mensaje = entradaDatos.readUTF();
        //TODO
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
