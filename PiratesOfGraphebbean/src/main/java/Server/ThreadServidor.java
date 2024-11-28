package Server;

import Modelos.CasesEnCliente;
import Modelos.CasesEnThreadServidor;
import Modelos.Random;
import Modelos.TiposAtaque;

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
        mandarIdCliente();
        recibirNombreCliente();
        recibirSiYaInicio();
        juegoEmpieza();
    }

    private void mandarIdCliente(){
        try{
            salidaDatos.writeInt(numeroCliente);
        } catch (IOException ex) {System.out.println("Error mandando id del cliente");}
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
                case CONSEGUIRGRAFOENEMIGO:
                    try {
                        conseguirGrafoEnemigo();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso CONSEGUIRGRAFOENEMIGO en ThreadServidor");}
                case PROPONERVENTAACERO:
                    try {
                        proponerVentaAcero();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso PROPONERVENTAACERO en ThreadServidor");}
                case ATACARCANON:
                    try {
                        atacarCanon();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso ATACARCANON en ThreadServidor");}
                case ATACARCANONMULT:
                    try {
                        atacarCanonMult();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso ATACARCANONMULT en ThreadServidor");}
                case ATACARBOMBA:
                    try {
                        atacarBomba();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso ATACARBOMBA en ThreadServidor");}
                case ATACARCBR:
                    try {
                        atacarCanonBR();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso ATACARCBR en ThreadServidor");}
                case ATACARKRAKEN:
                    try {
                        atacarKraken();
                        break;
                    } catch (Exception ex) {System.out.println("Error con caso ATACARKRAKEN en ThreadServidor");}
                case PERDER:
                    servidor.seRendioId(numeroCliente);
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


    private void conseguirGrafoEnemigo() throws Exception{
        String selectedPlayer = entradaDatos.readUTF();
        ThreadServidor ts = getEnemigoConNombre(selectedPlayer);
        ts.getSalidaObjetos().writeObject(CasesEnCliente.DEVOLVERGRAFO);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        String grafoEnemigo = (String)ts.getObjeto();
        ts.setObjeto(null);

        ts.getSalidaObjetos().writeObject(CasesEnCliente.DEVOLVERESCUDO);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        int escudo = (int)ts.getObjeto();
        ts.setObjeto(null);

        salidaObjetos.writeObject(CasesEnCliente.SETGRAFOENEMIGO);
        salidaDatos.writeUTF(grafoEnemigo);

        salidaObjetos.writeObject(CasesEnCliente.SETESCUDOENEMIGO);
        salidaDatos.writeInt(escudo);
    }

    private void proponerVentaAcero() throws Exception{
        String selectedPlayer = entradaDatos.readUTF();
        int precio = entradaDatos.readInt();
        int cant = entradaDatos.readInt();
        ThreadServidor ts = getEnemigoConNombre(selectedPlayer);
        ts.getSalidaObjetos().writeObject(CasesEnCliente.RECIBIROFERTAACERO);
        ts.getSalidaDatos().writeUTF(nombreCliente);
        ts.getSalidaDatos().writeInt(precio);
        ts.getSalidaDatos().writeInt(cant);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        Boolean acepto = (Boolean)ts.getObjeto();
        ts.setObjeto(null);

        if(acepto){
            salidaObjetos.writeObject(CasesEnCliente.OFERTAACEPTADAACERO);
            salidaDatos.writeInt(cant);
            salidaDatos.writeInt(precio);
        }
    }

    private void atacarCanon() throws Exception{
        String nombreEnemigo = entradaDatos.readUTF();
        int[] coords = (int[])entradaObjetos.readObject();
        ThreadServidor ts = getEnemigoConNombre(nombreEnemigo);
        //revisar si tiene escudo.
        ts.getSalidaObjetos().writeObject(CasesEnCliente.DEVOLVERESCUDO);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        int escudo = (Integer)ts.getObjeto();
        ts.setObjeto(null);
        if(escudo > 0){
            System.out.println("Tiene escudo, exactamente " + escudo);
            salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
            salidaDatos.writeUTF("Cañón: Disparaste a " + nombreEnemigo + " pero tenía un escudo activo");
            ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
            ts.getSalidaDatos().writeUTF("Te disparó un Cañón pero aún tenías escudo");
            ts.getSalidaObjetos().writeObject(CasesEnCliente.BAJARESCUDO);
            siguienteTurno();
            return;
        }
        //en otros ataques, ponerlo como un for de cada coords
        salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
        salidaDatos.writeUTF("Cañón: Disparaste a" + nombreEnemigo + "en (" + coords[0] + "," + coords[1] + ")");
        ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
        ts.getSalidaDatos().writeUTF("Te disparó un Cañón en (" + coords[0] + "," + coords[1] + ")");
        ts.getSalidaObjetos().writeObject(CasesEnCliente.SERATACADO);
        ts.getSalidaObjetos().writeObject(coords);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        TiposAtaque tipoAtaque = (TiposAtaque)ts.getObjeto();
        ts.setObjeto(null);
        switch(tipoAtaque){
            case FUENTEDEENERGIA:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque le dió a una fuente de energía, consigues una fuente de energía.");
                salidaObjetos.writeObject(CasesEnCliente.CONSEGUIRFUENTE);
                break;
            case REMOLINO:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque cayó en un remolino, se te devolverán 3 disparos en lugares random.");
                ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                ts.getSalidaDatos().writeUTF("El disparo enemigo cayó en tu remolino, se le devolverán 3 disparos al enemigo");
                int[][] coordsRemolino = {{Random.randomInt(0,19),Random.randomInt(0,19)}, {Random.randomInt(0,19),Random.randomInt(0,19)},{Random.randomInt(0,19),Random.randomInt(0,19)}};
                for (int[] cds : coordsRemolino) {
                    salidaObjetos.writeObject(CasesEnCliente.SERATACADO);
                    salidaObjetos.writeObject(cds);
                    while(ts.getObjeto() == null){
                        sleep(500);
                    }
                    ts.setObjeto(null);
                    salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                    salidaDatos.writeUTF("Una bala del remolino cayó en (" + cds[0] + "," + cds[1] + ")");
                    ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                    ts.getSalidaDatos().writeUTF("Una bala del remolino cayó en (" + cds[0] + "," + cds[1] + ")");
                }
                break;
            case HIT:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque le dió a un componente en el campo de tu enemigo, bien hecho!");
                break;
            case MISS:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque no le dió a nada...");
                break;
        }
        if(servidor.revisarGane()){
            for (ThreadServidor contrincante : contrincantes) {
                contrincante.getSalidaObjetos().writeObject(CasesEnCliente.ALGUIENGANO);
                contrincante.getSalidaDatos().writeInt(servidor.getTurnoActual());
            }
            salidaObjetos.writeObject(CasesEnCliente.YOGANE);
            return;
        }
        siguienteTurno();
    }

    private void atacarCanonMult() throws Exception{
        String nombreEnemigo = entradaDatos.readUTF();
        int[] coords = (int[])entradaObjetos.readObject();
        ThreadServidor ts = getEnemigoConNombre(nombreEnemigo);

        ts.getSalidaObjetos().writeObject(CasesEnCliente.DEVOLVERESCUDO);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        int escudo = (Integer)ts.getObjeto();
        ts.setObjeto(null);
        if(escudo > 0){
            System.out.println("Tiene escudo, exactamente " + escudo);
            salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
            salidaDatos.writeUTF("Cañón Múltiple: Disparaste a " + nombreEnemigo + " pero tenía un escudo activo");
            ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
            ts.getSalidaDatos().writeUTF("Te disparó un Cañón Multiple pero aún tenías escudo");
            ts.getSalidaObjetos().writeObject(CasesEnCliente.BAJARESCUDO);
            siguienteTurno();
            return;
        }
        //en otros ataques, ponerlo como un for de cada coords
        salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
        salidaDatos.writeUTF("Cañón Multiple: Disparaste a" + nombreEnemigo + "en (" + coords[0] + "," + coords[1] + ")");
        ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
        ts.getSalidaDatos().writeUTF("Te disparó un Cañón multiple en (" + coords[0] + "," + coords[1] + ")");
        ts.getSalidaObjetos().writeObject(CasesEnCliente.SERATACADO);
        ts.getSalidaObjetos().writeObject(coords);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        TiposAtaque tipoAtaque = (TiposAtaque)ts.getObjeto();
        ts.setObjeto(null);
        switch(tipoAtaque){
            case FUENTEDEENERGIA:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque le dió a una fuente de energía, consigues una fuente de energía. Además, por el tipo de ataque, se dispararán 4 tiros más al azar.");
                salidaObjetos.writeObject(CasesEnCliente.CONSEGUIRFUENTE);
                ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                ts.getSalidaDatos().writeUTF("El ataque de cañón multiple enemigo acertó, disparará 4 tiros más");
                for (int i = 0; i < 4; i++) {
                    int[] cds = {Random.randomInt(0,19),Random.randomInt(0,19)};
                    ts.getSalidaObjetos().writeObject(CasesEnCliente.SERATACADO);
                    ts.getSalidaObjetos().writeObject(cds);
                    while(ts.getObjeto() == null){
                        sleep(500);
                    }
                    ts.setObjeto(null);
                    salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                    salidaDatos.writeUTF("Una bala del Cañón múltiple cayó en (" + cds[0] + "," + cds[1] + ")");
                    ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                    ts.getSalidaDatos().writeUTF("Una bala del Cañón múltiple cayó en (" + cds[0] + "," + cds[1] + ")");
                }
                break;
            case REMOLINO:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque cayó en un remolino, se te devolverán 3 disparos en lugares random.");
                ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                ts.getSalidaDatos().writeUTF("El disparo enemigo cayó en tu remolino, se le devolverán 3 disparos al enemigo");
                int[][] coordsRemolino = {{Random.randomInt(0,19),Random.randomInt(0,19)}, {Random.randomInt(0,19),Random.randomInt(0,19)},{Random.randomInt(0,19),Random.randomInt(0,19)}};
                System.out.println("Balas que devuelve remolino: " + coordsRemolino.length);
                for (int[] cds : coordsRemolino) {
                    System.out.println("BALA" + cds[0] + "," + cds[1]);
                    salidaObjetos.writeObject(CasesEnCliente.SERATACADO);
                    salidaObjetos.writeObject(cds);
                    while(ts.getObjeto() == null){
                        sleep(500);
                    }
                    ts.setObjeto(null);
                    salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                    salidaDatos.writeUTF("Una bala del remolino cayó en (" + cds[0] + "," + cds[1] + ")");
                    ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                    ts.getSalidaDatos().writeUTF("Una bala del remolino cayó en (" + cds[0] + "," + cds[1] + ")");
                }
                break;
            case HIT:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque le dió a un componente en el campo de tu enemigo, por el tipo de ataque, se dispararán 4 tiros más al azar.");
                ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                ts.getSalidaDatos().writeUTF("El ataque de cañón multiple enemigo acertó, disparará 4 tiros más");
                for (int i = 0; i < 4; i++) {
                    int[] cds = {Random.randomInt(0,19),Random.randomInt(0,19)};
                    ts.getSalidaObjetos().writeObject(CasesEnCliente.SERATACADO);
                    ts.getSalidaObjetos().writeObject(cds);
                    while(ts.getObjeto() == null){
                        sleep(500);
                    }
                    ts.setObjeto(null);
                    salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                    salidaDatos.writeUTF("Una bala del Cañón múltiple cayó en (" + cds[0] + "," + cds[1] + ")");
                    ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                    ts.getSalidaDatos().writeUTF("Una bala del Cañón múltiple cayó en (" + cds[0] + "," + cds[1] + ")");
                }
                break;
            case MISS:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque no le dió a nada...");
                break;
        }
        if(servidor.revisarGane()){
            for (ThreadServidor contrincante : contrincantes) {
                contrincante.getSalidaObjetos().writeObject(CasesEnCliente.ALGUIENGANO);
                contrincante.getSalidaDatos().writeInt(servidor.getTurnoActual());
            }
            salidaObjetos.writeObject(CasesEnCliente.YOGANE);
            return;
        }
        siguienteTurno();
    }

    private void atacarBomba() throws Exception{
        String nombreEnemigo = entradaDatos.readUTF();
        int[][] fullCoords = (int[][])entradaObjetos.readObject();
        ThreadServidor ts = getEnemigoConNombre(nombreEnemigo);
        //en otros ataques, ponerlo como un for de cada coords
        for (int[] coords : fullCoords) {
            ts.getSalidaObjetos().writeObject(CasesEnCliente.DEVOLVERESCUDO);
            while(ts.getObjeto() == null){
                sleep(500);
            }
            int escudo = (Integer)ts.getObjeto();
            ts.setObjeto(null);
            if(escudo > 0){
                System.out.println("Tiene escudo, exactamente " + escudo);
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Bomba: Disparaste a " + nombreEnemigo + " pero tenía un escudo activo");
                ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                ts.getSalidaDatos().writeUTF("Te disparó una Bomba pero aún tenías escudo");
                ts.getSalidaObjetos().writeObject(CasesEnCliente.BAJARESCUDO);
                siguienteTurno();
                continue;
            }
            for (int i = 0; i < 2; i++) {
                if(i ==1){
                    coords[Random.randomInt(0,1)]++;
                }
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Bomba: Disparaste a" + nombreEnemigo + "en (" + coords[0] + "," + coords[1] + ")");
                ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                ts.getSalidaDatos().writeUTF("Te disparó una bomba en (" + coords[0] + "," + coords[1] + ")");
                ts.getSalidaObjetos().writeObject(CasesEnCliente.SERATACADO);
                ts.getSalidaObjetos().writeObject(coords);
                while(ts.getObjeto() == null){
                    sleep(500);
                }
                TiposAtaque tipoAtaque = (TiposAtaque)ts.getObjeto();
                ts.setObjeto(null);
                switch(tipoAtaque){
                    case FUENTEDEENERGIA:
                        salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                        salidaDatos.writeUTF("Tu ataque le dió a una fuente de energía, consigues una fuente de energía.");
                        salidaObjetos.writeObject(CasesEnCliente.CONSEGUIRFUENTE);
                        break;
                    case REMOLINO:
                        salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                        salidaDatos.writeUTF("Tu ataque cayó en un remolino, se te devolverán 3 disparos en lugares random.");
                        ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                        ts.getSalidaDatos().writeUTF("El disparo enemigo cayó en tu remolino, se le devolverán 3 disparos al enemigo");
                        int[][] coordsRemolino = {{Random.randomInt(0,19),Random.randomInt(0,19)}, {Random.randomInt(0,19),Random.randomInt(0,19)},{Random.randomInt(0,19),Random.randomInt(0,19)}};
                        for (int[] cds : coordsRemolino) {
                            salidaObjetos.writeObject(CasesEnCliente.SERATACADO);
                            salidaObjetos.writeObject(cds);
                            while(ts.getObjeto() == null){
                                sleep(500);
                            }
                            ts.setObjeto(null);
                            salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                            salidaDatos.writeUTF("Una bala del remolino cayó en (" + cds[0] + "," + cds[1] + ")");
                            ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                            ts.getSalidaDatos().writeUTF("Una bala del remolino cayó en (" + cds[0] + "," + cds[1] + ")");
                        }
                        break;
                    case HIT:
                        salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                        salidaDatos.writeUTF("Tu ataque le dió a un componente en el campo de tu enemigo, bien hecho!");
                        break;
                    case MISS:
                        salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                        salidaDatos.writeUTF("Tu ataque no le dió a nada...");
                        break;
                }
            }
        }
        if(servidor.revisarGane()){
            for (ThreadServidor contrincante : contrincantes) {
                contrincante.getSalidaObjetos().writeObject(CasesEnCliente.ALGUIENGANO);
                contrincante.getSalidaDatos().writeInt(servidor.getTurnoActual());
            }
            salidaObjetos.writeObject(CasesEnCliente.YOGANE);
            return;
        }
        siguienteTurno();
    }

    private void atacarCanonBR() throws Exception{
        String nombreEnemigo = entradaDatos.readUTF();
        int[][] fullCoords = (int[][])entradaObjetos.readObject();
        ThreadServidor ts = getEnemigoConNombre(nombreEnemigo);
        //en otros ataques, ponerlo como un for de cada coords
        for (int[] coords : fullCoords) {

            ts.getSalidaObjetos().writeObject(CasesEnCliente.DEVOLVERESCUDO);
            while(ts.getObjeto() == null){
                sleep(500);
            }
            int escudo = (Integer)ts.getObjeto();
            ts.setObjeto(null);
            if(escudo > 0){
                System.out.println("Tiene escudo, exactamente " + escudo);
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Cañón de Barba Roja: Disparaste a " + nombreEnemigo + " pero tenía un escudo activo");
                ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                ts.getSalidaDatos().writeUTF("Te disparó un Cañón de Barba Roja pero aún tenías escudo");
                ts.getSalidaObjetos().writeObject(CasesEnCliente.BAJARESCUDO);
                siguienteTurno();
                continue;
            }

            salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
            salidaDatos.writeUTF("Cañón de Barba Roja: Disparaste a" + nombreEnemigo + "en (" + coords[0] + "," + coords[1] + ")");
            ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
            ts.getSalidaDatos().writeUTF("Te disparó un Cañón de Barba Roja en (" + coords[0] + "," + coords[1] + ")");
            ts.getSalidaObjetos().writeObject(CasesEnCliente.SERATACADO);
            ts.getSalidaObjetos().writeObject(coords);
            while (ts.getObjeto() == null) {
                sleep(500);
            }
            TiposAtaque tipoAtaque = (TiposAtaque) ts.getObjeto();
            ts.setObjeto(null);
            switch (tipoAtaque) {
                case FUENTEDEENERGIA:
                    salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                    salidaDatos.writeUTF("Tu ataque le dió a una fuente de energía, consigues una fuente de energía.");
                    salidaObjetos.writeObject(CasesEnCliente.CONSEGUIRFUENTE);
                    break;
                case REMOLINO:
                    salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                    salidaDatos.writeUTF("Tu ataque cayó en un remolino, se te devolverán 3 disparos en lugares random.");
                    ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                    ts.getSalidaDatos().writeUTF("El disparo enemigo cayó en tu remolino, se le devolverán 3 disparos al enemigo");
                    int[][] coordsRemolino = {{Random.randomInt(0, 19), Random.randomInt(0, 19)}, {Random.randomInt(0, 19), Random.randomInt(0, 19)}, {Random.randomInt(0, 19), Random.randomInt(0, 19)}};
                    for (int[] cds : coordsRemolino) {
                        salidaObjetos.writeObject(CasesEnCliente.SERATACADO);
                        salidaObjetos.writeObject(cds);
                        while (ts.getObjeto() == null) {
                            sleep(500);
                        }
                        ts.setObjeto(null);
                        salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                        salidaDatos.writeUTF("Una bala del remolino cayó en (" + cds[0] + "," + cds[1] + ")");
                        ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                        ts.getSalidaDatos().writeUTF("Una bala del remolino cayó en (" + cds[0] + "," + cds[1] + ")");
                    }
                    break;
                case HIT:
                    salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                    salidaDatos.writeUTF("Tu ataque le dió a un componente en el campo de tu enemigo, bien hecho!");
                    break;
                case MISS:
                    salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                    salidaDatos.writeUTF("Tu ataque no le dió a nada...");
                    break;
            }
        }
        if(servidor.revisarGane()){
            for (ThreadServidor contrincante : contrincantes) {
                contrincante.getSalidaObjetos().writeObject(CasesEnCliente.ALGUIENGANO);
                contrincante.getSalidaDatos().writeInt(servidor.getTurnoActual());
            }
            salidaObjetos.writeObject(CasesEnCliente.YOGANE);
            return;
        }
        siguienteTurno();
    }

    private void atacarKraken() throws Exception{
        String nombreEnemigo = entradaDatos.readUTF();
        ThreadServidor ts = getEnemigoConNombre(nombreEnemigo);
        ts.getSalidaObjetos().writeObject(CasesEnCliente.DEVOLVERCOORDSCOMP);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        int[] coords = (int[])ts.getObjeto();
        ts.setObjeto(null);

        if(coords == null){
            salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
            salidaDatos.writeUTF("Kraken: Atacaste a " + nombreEnemigo + " pero no tenía nada");
            ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
            ts.getSalidaDatos().writeUTF("Te atacó un Kraken pero no tenías nada");
            siguienteTurno();
            return;
        }
        //revisar si tiene escudo.
        ts.getSalidaObjetos().writeObject(CasesEnCliente.DEVOLVERESCUDO);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        int escudo = (Integer)ts.getObjeto();
        ts.setObjeto(null);
        if(escudo > 0){
            System.out.println("Tiene escudo, exactamente " + escudo);
            salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
            salidaDatos.writeUTF("Kraken: Atacaste a " + nombreEnemigo + " pero tenía un escudo activo");
            ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
            ts.getSalidaDatos().writeUTF("Te atacó un Kraken pero aún tenías escudo");
            ts.getSalidaObjetos().writeObject(CasesEnCliente.BAJARESCUDO);
            siguienteTurno();
            return;
        }
        //en otros ataques, ponerlo como un for de cada coords
        salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
        salidaDatos.writeUTF("Kraken: atacaste a" + nombreEnemigo + "en (" + coords[0] + "," + coords[1] + ")");
        ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
        ts.getSalidaDatos().writeUTF("Te atacó un Kraken en (" + coords[0] + "," + coords[1] + ")");
        ts.getSalidaObjetos().writeObject(CasesEnCliente.SERATACADO);
        ts.getSalidaObjetos().writeObject(coords);
        while(ts.getObjeto() == null){
            sleep(500);
        }
        TiposAtaque tipoAtaque = (TiposAtaque)ts.getObjeto();
        ts.setObjeto(null);
        switch(tipoAtaque){
            case FUENTEDEENERGIA:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque le dió a una fuente de energía, consigues una fuente de energía.");
                salidaObjetos.writeObject(CasesEnCliente.CONSEGUIRFUENTE);
                break;
            case REMOLINO:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque le dió a un remolino, se te devolverán 3 disparos en lugares random.");
                ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                ts.getSalidaDatos().writeUTF("El ataque enemigo cayó en tu remolino, se le devolverán 3 disparos al enemigo");
                int[][] coordsRemolino = {{Random.randomInt(0,19),Random.randomInt(0,19)}, {Random.randomInt(0,19),Random.randomInt(0,19)},{Random.randomInt(0,19),Random.randomInt(0,19)}};
                for (int[] cds : coordsRemolino) {
                    salidaObjetos.writeObject(CasesEnCliente.SERATACADO);
                    salidaObjetos.writeObject(cds);
                    while(ts.getObjeto() == null){
                        sleep(500);
                    }
                    ts.setObjeto(null);
                    salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                    salidaDatos.writeUTF("Una bala del remolino cayó en (" + cds[0] + "," + cds[1] + ")");
                    ts.getSalidaObjetos().writeObject(CasesEnCliente.PONERENBITACORA);
                    ts.getSalidaDatos().writeUTF("Una bala del remolino cayó en (" + cds[0] + "," + cds[1] + ")");
                }
                break;
            case HIT:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque le dió a un componente en el campo de tu enemigo, bien hecho!");
                break;
            case MISS:
                salidaObjetos.writeObject(CasesEnCliente.PONERENBITACORA);
                salidaDatos.writeUTF("Tu ataque no le dió a nada...");
                break;
        }
        if(servidor.revisarGane()){
            for (ThreadServidor contrincante : contrincantes) {
                contrincante.getSalidaObjetos().writeObject(CasesEnCliente.ALGUIENGANO);
                contrincante.getSalidaDatos().writeInt(servidor.getTurnoActual());
            }
            salidaObjetos.writeObject(CasesEnCliente.YOGANE);
            return;
        }
        siguienteTurno();
    }

    private void siguienteTurno() throws Exception{
        servidor.siguienteTurno();
        salidaObjetos.writeObject(CasesEnCliente.SIGUIENTETURNO);
        salidaDatos.writeInt(servidor.getTurnoActual());
        for (ThreadServidor contrincante : contrincantes) {
            contrincante.getSalidaObjetos().writeObject(CasesEnCliente.SIGUIENTETURNO);
            contrincante.getSalidaDatos().writeInt(servidor.getTurnoActual());
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
