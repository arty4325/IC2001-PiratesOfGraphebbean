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
    private ArrayList<Boolean> jugadoresPerdieron;
    private int conectados;
    private int turnoActual;
    private static Server singleton;

    private Server(ServerScreenController pantallaServidor){
        this.pantallaServidor = pantallaServidor;
        this.clientes = new ArrayList<Socket>();
        this.threadsServidor= new ArrayList<ThreadServidor>();
        this.conectados = 0;
        this.turnoActual = 1;
        this.jugadoresPerdieron = new ArrayList<Boolean>();
    }

    public static Server getInstance(ServerScreenController pantallaServidor) {
        if (singleton == null) {
            singleton = new Server(pantallaServidor);
        }

        return singleton;
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
        ponerContrincantes();

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

    private void ponerContrincantes(){
        for (ThreadServidor cliente : threadsServidor) {
            for (ThreadServidor enemigo : threadsServidor) {
                if(cliente != enemigo){
                    cliente.getContrincantes().add(enemigo);
                }
            }
            jugadoresPerdieron.add(false);
        }
    }

    public void siguienteTurno(){
        do{
            if(turnoActual == threadsServidor.size()){
                turnoActual = 1;
            } else {
                turnoActual++;
            }
        }while(jugadoresPerdieron.get(turnoActual-1));
        System.out.println("ES EL TURNO DE " + turnoActual);
    }
    public boolean seRendioId(int id){
        if(jugadoresPerdieron.get(id-1)){
            return false; //indicando que ya se había rendido o perdió antes
        }
        jugadoresPerdieron.set(id-1, true);
        return true;
    }
    public boolean revisarGane(){
        boolean gano = !jugadoresPerdieron.get(turnoActual-1); //es true si aún sigue vivo
        for (int i = 1; i <= jugadoresPerdieron.size(); i++) {
            if(i != turnoActual){
                gano = gano && jugadoresPerdieron.get(i-1);
            }
        }
        return gano;
    }
    public int getTurnoActual() {
        return turnoActual;
    }

}
