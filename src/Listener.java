import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class Listener implements Runnable {
	   // This is the entry point for the second thread.
	   
	Node []nodes;
	int id;
	TimeStamp timestamp;
	Snapshot snapshot;
	Widget widget;
	
	Listener(Node[] x, int y, TimeStamp t, Widget w){
		nodes = x;
		id = y;
		timestamp = t;
		widget = w;
	}
	
	public int parseLamport(String msg)
	{
		return Integer.parseInt(msg.split(":")[1]);
	}
	
	public int[] parseVector(String msg)
	{
		String ls = msg.split(":")[2];
		
		String[] lsArr = ls.split(",");
		int[] v = new int[lsArr.length];
		for (int i=0; i<lsArr.length; i++) {
			v[i] = Integer.parseInt(lsArr[i]);
		}
		
		return v;
	}
	
	public void run() {
		   
		   try {
				ServerSocket serverSocket = new ServerSocket(nodes[id].portNumber);
				//Socket clientSocket = serverSocket.accept();
				InputStream input = null;
				while (true) {
					Socket connection = serverSocket.accept();
					input = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		            String msg = reader.readLine();
		            
		            if (msg.split(":")[1].equals("marker")) {
		            	snapshot.receiveMarker(Integer.parseInt(msg.split(":")[0]));
		            	continue;
		            }
		            
		            widget.update(Integer.parseInt(msg.split(":")[0].split(",")[0]), Integer.parseInt(msg.split(":")[0].split(",")[1]));
		            
		            int l = parseLamport(msg);
		            int[] v = parseVector(msg);
		            timestamp.increment(l, v);
		            
					System.out.println("Message received - " + msg.split(":")[0] + ":" + timestamp.getLamport() + ":" + timestamp.getVector());
			    	System.out.println("Current Widget cost : " + widget.cost + ", Widget quantity : " + widget.quantity);
			    	System.out.println();
			    	   
					connection.close();
				}
				//System.out.println("Stopped Listening !!");
			} catch (Exception e) {
					System.out.println(e);
			}
	   }
	
	public void setSnapshotObj(Snapshot s)
	{
		snapshot = s;
	}
	
	
}