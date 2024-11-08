package Cliente;

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

    public Cliente(ClienteScreenController pantallaCliente){
        this.pantallaCliente = pantallaCliente;
    }

    public void run(){
        try {
          conectar();
        } catch (Exception ex) {System.out.println("Error conectando al servidor");}
    }

    public void conectar() throws Exception{
        socket = new Socket("localhost", 50000);
        entradaDatos = new DataInputStream(socket.getInputStream());
        salidaDatos = new DataOutputStream(socket.getOutputStream());
        salidaObjetos = new ObjectOutputStream(socket.getOutputStream());
        entradaObjetos = new ObjectInputStream(socket.getInputStream());
    }

    public void mandarNombreAServer(String nombre){
        try {
            this.nombreCliente = nombre;
            salidaDatos.writeUTF(nombre);
        } catch (Exception ex) {System.out.println("Error mandando el nombre");}
    }

    public void mandarIniciarAServer(){
        try{
            salidaDatos.writeBoolean(true);
        } catch (Exception ex) {System.out.println("Error mandando que se presiono el boton iniciar");}
    }
}


