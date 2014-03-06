import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Snapshot
{
	Node[] nodes;
	int processId;
	Map<Integer, Integer> markersFromOthers;
	boolean isStateRecorded;
	BufferedWriter br;
	Widget widget;

	public Snapshot(Node[] n, int pId, Widget w) throws IOException
	{
		nodes = n;
		processId = pId;
		br = new BufferedWriter(new FileWriter("process_" + processId + "_log.txt"));
		widget = w;
	}
	
	public void receiveMarker(int id) throws IOException
	{
		if (!isStateRecorded) {
			initiateSnapshot();
		}
		
		if (!markersFromOthers.containsKey(id)) {
			markersFromOthers.put(id, null);
		}
		
		// Snapshot ends
		if (markersFromOthers.keySet().size() == nodes.length - 1) {
			markersFromOthers = null;
			isStateRecorded = false;
		}
	}
	
	public void initiateSnapshot() throws IOException
	{
		markersFromOthers = new HashMap<Integer, Integer>();
		isStateRecorded = true;
		
		br.write("Widgets count : " + widget.cost + ", Widgets quantity : " + widget.quantity);
		
		for (int i=0; i<nodes.length; i++) {
			if (i == processId) {
				continue;
			}
			
	    	String hostName = nodes[i].ipAddress;
	    	Integer portNumber = nodes[i].portNumber;
			try {
		    	   Socket socket = new Socket(hostName, portNumber);
		    	   PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    	   out.println(processId + ":marker");
		    	   out.flush();
		    	   System.out.println("Marker sent to " + i);
		    	   socket.close();
			} catch(Exception e) {
				
			}
		}
	}
}
