package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class Server{
	public static void main(String[] args) {
        ServerSocket connSocket=null;
        try{
            connSocket = new ServerSocket(5059);
        }
        catch (Exception e2){
            System.out.println("Erreur de connexion");
        }
            Profile.inintaliseProfiles();
            while (true) {
                Socket commSocket = null;
                try {
                    commSocket = connSocket.accept();
                    System.out.println("Nouvelle connection : " + commSocket);
                    DataInputStream dis = new DataInputStream(commSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(commSocket.getOutputStream());
                    Thread t = new GestionClient(commSocket, dis, dos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
	}
}
