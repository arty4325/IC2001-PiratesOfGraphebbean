package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ServerScreenController pantallaServidor;
    ServerConnectionsThread connectionsThread;
    private ServerSocket servidor;
    private ArrayList<Socket> clientes; //Sockets para cada usuario / cliente
    private ArrayList<ThreadServidor> threadsServidor;
    private int conectados;


    public Server(ServerScreenController pantallaServidor){
        this.pantallaServidor = pantallaServidor;
        this.clientes = new ArrayList<Socket>();
        this.threadsServidor= new ArrayList<ThreadServidor>();
        this.conectados = 0;
    }

    public void run(){
        try {
            servidor = new ServerSocket(50000);
        } catch (IOException ex) {System.out.println("Error levantando el servidor \n");}

        pantallaServidor.write("Servidor levantado \n");
        pantallaServidor.write("Esperando usuarios \n");
        connectionsThread = new ServerConnectionsThread(this);
        connectionsThread.start();
        esperarConexiones(); //Se queda aquí hasta tener la cantidad necesaria de jugadores conectados y que pongan start o hagan toda la vara.

        //Cuando ya pasa aquí, es que ya todos presionaron start, entonces ya puede comenzar el juego


    }

    private void esperarConexiones(){ //esperar que todos inicien.
        while(conectados < 2){
            try {Thread.sleep(500);} catch (InterruptedException ex) {}
        } //esperar a que al menos hayan 2 conectados

        while(true) { //esperar que todos los conectados presionen start
            try {Thread.sleep(500);} catch (InterruptedException ex) {}
            boolean algunoNoHaPresionado = false;
            for (ThreadServidor threadServidor : threadsServidor) {
                if (!threadServidor.isStartPresionado()) {
                    algunoNoHaPresionado = true;
                }
            }
            if (!algunoNoHaPresionado) {
                connectionsThread.apagar();
                // Aqui es donde debo de jugar (Abrir la ventana inicial de gaming)
                comenzarPartida("START");
                break;
            }
        }
    }

    public void writeInConsole(String texto){
        pantallaServidor.write(texto);
    }

    public ServerSocket getServidor() {
        return servidor;
    }

    public void anadirAThreadsServidor(ThreadServidor ts) {
        threadsServidor.add(ts);
    }

    public int getConectados() {
        return conectados;
    }

    public void conectarPersona(){
        conectados +=1;
    }

    public void comenzarPartida(String mensaje) {
        for (ThreadServidor threadServidor : threadsServidor) {
            threadServidor.mensajeComenzar(mensaje);
        }
    }

}
