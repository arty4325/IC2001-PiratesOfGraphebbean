package Server;

import java.io.*;
import java.net.Socket;

public class ThreadServidor extends Thread{
    public Socket socket;
    private Server servidor;
    private DataInputStream entradaDatos;
    private DataOutputStream salidaDatos;
    private ObjectInputStream entradaObjetos;
    private ObjectOutputStream salidaObjetos;
    private String nombreCliente;
    private int numeroCliente; //numConexion
    private boolean startPresionado;

    ThreadServidor(Socket socket, Server servidor, int numeroCliente){
        this.socket = socket;
        this.servidor = servidor;
        this.numeroCliente = numeroCliente;
        this.nombreCliente = "";
        this.startPresionado = false;
        try {
            entradaDatos = new DataInputStream(socket.getInputStream());
            salidaDatos = new DataOutputStream(socket.getOutputStream());
            salidaObjetos = new ObjectOutputStream(socket.getOutputStream());
            entradaObjetos = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) { System.out.println("Error en entrada/salida de datos");}
    }

    public void run(){
        recibirNombreCliente();
        recibirSiYaInicio();
        //Aqui ya iria la funcion con todos los cases.
    }

    private void recibirNombreCliente(){
        try {
            nombreCliente = entradaDatos.readUTF();
            servidor.writeInConsole("El jugador " + numeroCliente + " se llama "+ nombreCliente); //Lo muestra en el servidor.
        } catch (IOException ex) {System.out.println("Error leyendo el nombre del cliente");}
    }

    private void recibirSiYaInicio(){
        try {
            startPresionado = entradaDatos.readBoolean();
            servidor.writeInConsole(nombreCliente + " ya presiono iniciar"); //Lo muestra en el servidor.
        } catch (IOException ex) {System.out.println("Error leyendo si ya presiono el botón");}
    }


    public boolean isStartPresionado() {
        return startPresionado;
    }
}
