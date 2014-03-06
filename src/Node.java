import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class Node{
    String ipAddress;
    Integer portNumber;
}

class Listen implements Runnable {
       // This is the entry point for the second thread.
       
    Node []nodes;
    int id;
    
    Listen(Node[] x, int y){
        nodes = x;
        id = y;
    }
    public void run() {
           
           try {
                ServerSocket serverSocket = new ServerSocket(nodes[id].portNumber);
                //Socket clientSocket = serverSocket.accept();
                InputStream input = null;
                String inputLine;
                while (true) {
                    Socket connection = serverSocket.accept();
                    input = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String msg = reader.readLine();
                    System.out.println("Message received - " + msg);
                    connection.close();
                }
                //System.out.println("Stopped Listening !!");
            } catch (Exception e) {
                    System.out.println(e);
            }
       }
}