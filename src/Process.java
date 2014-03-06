import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Format of the messages -
 * 
 * Process needs to be given 3 parameters (space separated) - id 'initial cost' 'initial quantity' 
 * 
 * Marker initiation : any random message without ":" will start a snapshot
 * Marker send : 'process id of the process sending it' : "marker"
 * 
 * Widgets sent (console, file) : to process : cost, quantity
 * Widgets sent (over the channel) : from process : cost, quantity : lamport timestamp : vector timestamp (separated by commas)
 */
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
	            nodes = new Node[totalNodes];
	            
	            int i = 0;
	            while((line = bufferedReader.readLine()) != null) {
	                Node n = new Node();
	            	String[] tokens = line.split(" ");
	            	n.ipAddress = tokens[0];
	            	n.portNumber = Integer.parseInt(tokens[1]);
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
	
	public void initiateSnapshot()
	{
		
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException
	{
		String input;
		processId = Integer.parseInt(args[0]);
		BufferedReader br = new BufferedReader(new FileReader("input_file_" + processId + ".txt"));
		
		Widget widget = new Widget(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		
		readFile();
		
		Snapshot snapshot = new Snapshot(nodes, processId, widget);
		
		TimeStamp timestamp = new TimeStamp(nodes.length, processId);
		
		// Create a new, second thread
		Listener l = new Listener(nodes, processId, timestamp, widget, snapshot);
		Thread t;
	    t = new Thread(l);
	    System.out.println("Child thread: " + t);
	    t.start(); // Start the thread
	    
	    while((input = br.readLine()) != null){
	    		
	    	String tokens[] = input.split(":");
	    	
	    	// No ':' in the message is indicative of a snapshot initiation step,
	    	// because there is no receiver as such.
	    	if (tokens.length == 1) {
	    		snapshot.initiateSnapshot();
	    		continue;
	    	}
	    	
	    	Integer nodeId = Integer.parseInt(tokens[0]);
	    	String hostName = nodes[nodeId].ipAddress;
	    	Integer portNumber = nodes[nodeId].portNumber;
	    	String message = processId + ":" + tokens[1] + ":";
	    	
	    	//increment timestamps and append to the message
	    	timestamp.increment(-1, null);
	    	message += timestamp.lamport + ":";
	    	for (int i : timestamp.vector) {
	    		message += i + ",";
	    	}
	    	message = message.substring(0, message.length() - 1);
	    	
	    	widget.update(-1 * Integer.parseInt(tokens[1].split(",")[0]), -1 * Integer.parseInt(tokens[1].split(",")[1]));
	    	
	    	while(!sendMessage(hostName, portNumber, message, widget));
	    }
	    
	    br.close();
	}

	private static boolean sendMessage(String hostName, Integer portNumber,	String message, Widget widget)
	{
    	try {
	    	   Socket socket = new Socket(hostName, portNumber);
	    	   PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    	   out.println(message);
	    	   out.flush();
	    	   System.out.println("Message sent " + message);
	    	   System.out.println("Current Widget cost : " + widget.cost + ", Widget quantity : " + widget.quantity);
	    	   System.out.println();
	    	   socket.close();
	    	   return true;
	    	} catch (Exception e) {
	    		return false;
	    	}	
	}
}