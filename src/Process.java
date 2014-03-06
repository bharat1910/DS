import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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



public class Process
{
	private static Node []nodes;
	private static final String INPUT_FILE = "input.txt";
	private static Integer totalNodes;
	private static Integer processId;
	
	
	
	private static void readFile(){
		
		 try {
	            // FileReader reads text files in the default encoding.
	            FileReader fileReader = new FileReader(INPUT_FILE);

	            // Always wrap FileReader in BufferedReader.
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	            
	            String line;
	            line = bufferedReader.readLine();
	            totalNodes = Integer.parseInt(line);
	            line = bufferedReader.readLine();
	            nodes = new Node[totalNodes];
	            int i = 0;
	            while((line = bufferedReader.readLine()) != null) {
	                Node n = new Node();
	            	String[] tockens = line.split(" ");
	            	n.ipAddress = tockens[0];
	            	n.portNumber = Integer.parseInt(tockens[1]);
	            	nodes[i] = n;
	            	i++;
	            }	

	            // Always close files.
	            bufferedReader.close();			
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Unable to open file '" + 
	                INPUT_FILE + "'");				
	        }
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException
	{
		String input;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		processId = Integer.parseInt(args[0]);
		
		readFile();
		
		// Create a new, second thread
		Listen l = new Listen(nodes, processId);
		Thread t;
	    t = new Thread(l);
	    System.out.println("Child thread: " + t);
	    t.start(); // Start the thread
	    
	    
	    while((input = br.readLine()) != null){
	    		
	    	String tokens[] = input.split(":");
	    	Integer nodeId = Integer.parseInt(tokens[0]);
	    	String hostName = nodes[nodeId].ipAddress;
	    	Integer portNumber = nodes[nodeId].portNumber;
	    	String message = tokens[1];
	    	
	    	try {
	    	   Socket socket = new Socket(hostName, portNumber);
	    	   PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    	   out.println(message);
	    	   out.flush();
	    	   System.out.println("Message sent " + message);
	    	   socket.close();
	    	} catch (Exception e) {
	    		System.out.println(e.toString());
	    	}	
	    }
	}
}