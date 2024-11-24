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
    private Object objeto;

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
            } catch (Exception ex) {System.out.println("Error con entrada de evento en ThreadServidor");}
            switch(evento){
                case MANDARMENSAJE:
                    try {
                        mandarMensaje();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso recibirMensaje en ThreadServidor");}
                case MANDARACCION:
                    try {
                        mandarAccion();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso recibirAccion en ThreadServidor");}
                case DEVOLVERNOMBRESOPONENTES:
                    try {
                        devolverNombresOponentes();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso devolverNombresOponentes en ThreadServidor");}
                case PROPONERVENTA:
                    try {
                        proponerVenta();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso proponerVenta en ThreadServidor");}
                case PONERENOBJETO:
                    try {
                        objeto = entradaObjetos.readObject();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso PONERENOBJETO en ThreadServidor");}
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

    private void mandarAccion() throws Exception{
        String mensaje = entradaDatos.readUTF();
        salidaObjetos.writeObject(CasesEnCliente.RECIBIRACCION);
        salidaDatos.writeUTF(mensaje);
        for(ThreadServidor contrincante : contrincantes){
            contrincante.salidaObjetos.writeObject((CasesEnCliente.RECIBIRACCION));
            contrincante.salidaDatos.writeUTF(mensaje);
        }
    }

    private void devolverNombresOponentes() throws Exception{
        ArrayList<String> nombres = new ArrayList<String>();
        for(ThreadServidor contrincante : contrincantes){
            nombres.add(contrincante.getNombreCliente());
        }
        salidaObjetos.writeObject(nombres);
    }

    private void proponerVenta() throws Exception{
        String selectedItem = entradaDatos.readUTF();
        String selectedPlayer = entradaDatos.readUTF();
        int precio = entradaDatos.readInt();
        ThreadServidor ts = getEnemigoConNombre(selectedPlayer);
        ts.getSalidaObjetos().writeObject(CasesEnCliente.RECIBIROFERTA);
        ts.getSalidaDatos().writeUTF(nombreCliente);
        ts.getSalidaDatos().writeUTF(selectedItem);
        ts.getSalidaDatos().writeInt(precio);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        Boolean acepto = (Boolean)ts.getObjeto();
        ts.setObjeto(null);

        if(acepto){
            salidaObjetos.writeObject(CasesEnCliente.OFERTAACEPTADA);
            salidaDatos.writeUTF(selectedItem);
            salidaDatos.writeInt(precio);
        }

    }











    //GETTERS Y SETTERS:

    public boolean isStartPresionado() {
        return startPresionado;
    }

    public ArrayList<ThreadServidor> getContrincantes() {
        return contrincantes;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public ThreadServidor getEnemigoConNombre(String nombre){
        for (ThreadServidor contrincante : contrincantes) {
            if(contrincante.getNombreCliente().equals(nombre)){
                return contrincante;
            }
        }
        return null;
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

    public Object getObjeto() {
        return objeto;
    }

    public void setObjeto(Object objeto) {
        this.objeto = objeto;
    }
}
