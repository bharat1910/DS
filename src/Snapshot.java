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
	Map<Integer,Set<Integer>> markersFromOthers;
	Map<Integer,Map<Integer, Queue<String>>> incomingChannelByProcess;
	boolean []isStateRecorded; 
	BufferedWriter bw;
	Widget widget;

	public Snapshot(Node[] n, int pId, Widget w) throws IOException
	{
		nodes = n;
		processId = pId;
		bw = new BufferedWriter(new FileWriter("process_" + processId + "_log.txt"));
		widget = w;
		isStateRecorded = new boolean[n.length];
		markersFromOthers = new HashMap<Integer,Set<Integer>>();
		incomingChannelByProcess = new HashMap<Integer,Map<Integer, Queue<String>>>();
	}
	
	public void receiveMarker(int id, int snapId) throws IOException
	{
		if (!isStateRecorded[snapId]) {
			initiateSnapshot(snapId);
		}
		
		markersFromOthers.get(snapId).add(id);
		
		// Snapshot ends
		if (markersFromOthers.get(snapId).size() == nodes.length - 1) {
			for (Entry<Integer, Queue<String>>  e : incomingChannelByProcess.get(snapId).entrySet()) {
				Queue<String> q = e.getValue();
				while (!q.isEmpty()) {
					bw.write(q.remove() + "\n");
				}
			}
			bw.write("\n");
			bw.flush();
			System.out.println();
			
			//markersFromOthers = null;
			//isStateRecorded[snapId] = false;
			//incomingChannelByProcess = null;
		}
	}
	
	public void initiateSnapshot(Integer snapshotId) throws IOException
	{
		Set<Integer>markersFromOthersForThisSnapshot = new HashSet<Integer>();	
		markersFromOthers.put(snapshotId,markersFromOthersForThisSnapshot);
		isStateRecorded[snapshotId] = true;
		Map<Integer, Queue<String>>incomingChannelByProcessForThisSnapshot = new HashMap<Integer, Queue<String>>();
		incomingChannelByProcess.put(snapshotId, incomingChannelByProcessForThisSnapshot);
		
		int[] temp = widget.getState();
		bw.write("snapshot id " + snapshotId + " Widgets Cost : " + temp[0] + ", Widgets Quantity : " + temp[1] + "\n");
		bw.flush();
		widget.releaseLock();
		
		for (int i=0; i<nodes.length; i++) {
			if (i == processId) {
				continue;
			}
			
	    	String hostName = nodes[i].ipAddress;
	    	Integer portNumber = nodes[i].portNumber;
	    	while(!sendMessage(hostName, portNumber,i, snapshotId));
			
		}
	}
	
	private boolean sendMessage(String hostName, Integer portNumber,int i, int snapshotId){
		
		try {
	    	   Socket socket = new Socket(hostName, portNumber);
	    	   PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    	   out.println(processId + ":marker-" +snapshotId);
	    	   out.flush();
	    	   System.out.println("Marker sent to : " + i);
	    	   socket.close();
	    	   return true;
		} catch(Exception e) {
			//System.out.println(e);
			return false;
		}
		
	}
	public void checkAndAddMessage(String s, Integer snapshotId)
	{
		int fromProcess = Integer.parseInt(s.split(":")[0]);
		
		if (isStateRecorded[snapshotId] && !markersFromOthers.get(snapshotId).contains(fromProcess)) {
			if(!incomingChannelByProcess.get(snapshotId).containsKey(fromProcess)) {
				incomingChannelByProcess.get(snapshotId).put(fromProcess, new LinkedList<String>());
			}
			
			incomingChannelByProcess.get(snapshotId).get(fromProcess).add(s);
		}
	}
}
