package Server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionsThread extends Thread{
    private boolean isRunning = true;
    private Server server;

    public ServerConnectionsThread(Server server){
        this.server = server;
    }


    public void run(){
        while (isRunning && server.getConectados() <4) {
            try {
                server.writeInConsole("Esperando cliente ... ");
                Socket socket = server.getServidor().accept();
                server.conectarPersona(); //suma 1 a conectados.
                ThreadServidor ts = new ThreadServidor(socket, server, server.getConectados());
                ts.start();
                server.anadirAThreadsServidor(ts);

                server.writeInConsole("Cliente conectado");

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

//    try {
//        clientes.add(servidor.accept()); //aqui se queda esperando a que alguien se meta y lo mete al arraylist de clientes
//    } catch (IOException ex) {System.out.println("Error conectando cliente");}
//
//            pantallaServidor.write("Cliente número " + (conectados+1) + " conectado");
//            threadsServidor.add(new ThreadServidor(clientes.get(conectados),this,++conectados));
//            threadsServidor.get(conectados-1).start();

    public void apagar(){
        isRunning = false;
        try {
            ServerSocket serverSocket = server.getServidor();
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error cerrando el serversocket"); //Quizá si se llama la función más de una vez o asi.
        }
    }

}
