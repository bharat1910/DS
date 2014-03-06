import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

public class Snapshot
{
	Node[] nodes;
	int processId;
	Set<Integer> markersFromOthers;
	Map<Integer, Queue<String>> incomingChannelByProcess;
	boolean isStateRecorded;
	BufferedWriter bw;
	Widget widget;

	public Snapshot(Node[] n, int pId, Widget w) throws IOException
	{
		nodes = n;
		processId = pId;
		bw = new BufferedWriter(new FileWriter("process_" + processId + "_log.txt"));
		widget = w;
	}
	
	public void receiveMarker(int id) throws IOException
	{
		if (!isStateRecorded) {
			initiateSnapshot();
		}
		
		markersFromOthers.add(id);
		
		// Snapshot ends
		if (markersFromOthers.size() == nodes.length - 1) {
			for (Entry<Integer, Queue<String>>  e : incomingChannelByProcess.entrySet()) {
				Queue<String> q = e.getValue();
				while (!q.isEmpty()) {
					bw.write(q.remove() + "\n");
				}
			}
			bw.write("\n");
			bw.flush();
			System.out.println();
			
			markersFromOthers = null;
			isStateRecorded = false;
			incomingChannelByProcess = null;
		}
	}
	
	public void initiateSnapshot() throws IOException
	{
		markersFromOthers = new HashSet<Integer>();
		isStateRecorded = true;
		incomingChannelByProcess = new HashMap<Integer, Queue<String>>();
		
		bw.write("Widgets Cost : " + widget.cost + ", Widgets Quantity : " + widget.quantity + "\n");
		bw.flush();
		
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
		    	   System.out.println("Marker sent to : " + i);
		    	   socket.close();
			} catch(Exception e) {
				
			}
		}
	}
	
	public void checkAndAddMessage(String s)
	{
		int fromProcess = Integer.parseInt(s.split(":")[0]);

		if (isStateRecorded && !markersFromOthers.contains(fromProcess)) {
			if(!incomingChannelByProcess.containsKey(fromProcess)) {
				incomingChannelByProcess.put(fromProcess, new LinkedList<String>());
			}
			
			incomingChannelByProcess.get(fromProcess).add(s);
		}
	}
}
