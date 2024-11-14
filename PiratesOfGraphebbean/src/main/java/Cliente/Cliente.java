package Cliente;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente {
    private ClienteScreenController pantallaCliente;
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
            escucharMensajes(new ActionEvent());
        } catch (Exception ex) {
            System.out.println("Error conectando al servidor");
        }
    }

    public void conectar() throws Exception {
        socket = new Socket("localhost", 50000);
        entradaDatos = new DataInputStream(socket.getInputStream());
        salidaDatos = new DataOutputStream(socket.getOutputStream());
        salidaObjetos = new ObjectOutputStream(socket.getOutputStream());
        entradaObjetos = new ObjectInputStream(socket.getInputStream());
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
    private void escucharMensajes(ActionEvent event) {
        new Thread(() -> {
            try {
                while (true) {
                    String mensaje = entradaDatos.readUTF();
                    System.out.println(mensaje);
                    if(mensaje.equals("START")){
                        // aqui tengo que decirle a la aplicacion que tiene que moverse al siguiente stage
                        canStart = true;
                    }
                }
            } catch (Exception ex) {
                System.out.println("Error recibiendo mensaje del servidor");
                ex.printStackTrace();
            }
        }).start();
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public boolean getCanStart() {
        return canStart;
    }
}
