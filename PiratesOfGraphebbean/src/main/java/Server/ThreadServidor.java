package Server;

import Modelos.CasesEnCliente;
import Modelos.CasesEnThreadServidor;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
    private ArrayList<ThreadServidor> contrincantes;

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
        this.contrincantes = new ArrayList<ThreadServidor>();
    }

    public void run(){
        recibirNombreCliente();
        recibirSiYaInicio();
        juegoEmpieza();
    }

    private void recibirNombreCliente(){
        try {
            nombreCliente = entradaDatos.readUTF();
            servidor.writeInConsole("El jugador " + numeroCliente + " se llama "+ nombreCliente + "\n"); //Lo muestra en el servidor.
        } catch (IOException ex) {System.out.println("Error leyendo el nombre del cliente");}
    }

    private void recibirSiYaInicio(){
        try {
            startPresionado = entradaDatos.readBoolean();
            servidor.writeInConsole(nombreCliente + " ya está listo" + "\n"); //Lo muestra en el servidor.
        } catch (IOException ex) {System.out.println("Error leyendo si ya presiono el botón");}
    }

    public void mensajeComenzar(String mensaje) {
        try {
            salidaDatos.writeUTF(mensaje);
            salidaDatos.flush(); // Asegura que el mensaje se envíe de inmediato
        } catch (IOException ex) {
            System.out.println("Error enviando mensaje al cliente " + nombreCliente);
        }
    }

    private void juegoEmpieza(){
        CasesEnThreadServidor evento = CasesEnThreadServidor.NADA;
        while(true){
            try {
                evento = (CasesEnThreadServidor) entradaObjetos.readObject();
            } catch (Exception ex) {System.out.println("Error con entrada de evento en threadCliente");}
            switch(evento){
                case MANDARMENSAJE:
                    try {
                        mandarMensaje();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso recibirMensaje en ThreadCliente");}
                case MANDARACCION:
                    try {

                        break;
                    } catch (Exception ex) {System.out.println("Error con caso recibirAccion en ThreadCliente");}
            }
        }
    }

    private void mandarMensaje() throws Exception{
        String mensaje = entradaDatos.readUTF();
        salidaObjetos.writeObject(CasesEnCliente.RECIBIRMENSAJE);
        salidaDatos.writeUTF(mensaje);
        for(ThreadServidor contrincante : contrincantes){
            contrincante.salidaObjetos.writeObject((CasesEnCliente.RECIBIRMENSAJE));
            contrincante.salidaDatos.writeUTF(mensaje);
        }
    }

    //GETTERS Y SETTERS:

    public boolean isStartPresionado() {
        return startPresionado;
    }

    public ArrayList<ThreadServidor> getContrincantes() {
        return contrincantes;
    }
}
