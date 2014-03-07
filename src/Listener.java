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
	Integer snapshotId;
	
	Listener(Node[] x, int y, TimeStamp t, Widget w, Snapshot s, Integer snapId) {
		nodes = x;
		id = y;
		timestamp = t;
		widget = w;
		snapshot = s;
		snapshotId = snapId;
	}
	
	public int parseLamport(String msg)
	{
		return Integer.parseInt(msg.split(":")[2]);
	}
	
	public int[] parseVector(String msg)
	{
		String ls = msg.split(":")[3];
		
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
					//.sleep(1000);
					//Thread.sleep(1000);
					Socket connection = serverSocket.accept();
					input = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		            String msg = reader.readLine();
		            
		            if (msg.split(":")[1].contains("marker")) {
		            	System.out.println("Message - " + msg);
		            	System.out.println("Recived marker from : " + msg.split(":")[0]);
		            	int snapId = Integer.parseInt(msg.split(":")[1].split("-")[1]);
		            	snapshot.receiveMarker(Integer.parseInt(msg.split(":")[0]), snapId);
		            	System.out.println("DONE");
		            	continue;
		            }
		            
		            widget.getState();
		            widget.update(Integer.parseInt(msg.split(":")[1].split(",")[0]), Integer.parseInt(msg.split(":")[1].split(",")[1]));
		            
		            int l = parseLamport(msg);
		            int[] v = parseVector(msg);
		            timestamp.increment(l, v);
		            String[] tck = msg.split(":");
		            
					System.out.println("Message received from - " + msg.split(":")[0] + ":" + msg.split(":")[1] + ":" + timestamp.getLamport() + ":" + timestamp.getVector() + ":" + tck[tck.length-1]);
					int[] temp = widget.getState();
					System.out.println("Current Widget cost : " + temp[0] + ", Widget quantity : " + temp[1]);
					widget.releaseLock();
			    	System.out.println();
			    	
			    	if (snapshot != null) {
				    	snapshot.checkAndAddMessage(msg.split(":")[0] + ":" + msg.split(":")[1] + ":" + timestamp.getLamport() + ":" + timestamp.getVector(), snapshotId);
			    	}
			    	
					connection.close();
				}
				//System.out.println("Stopped Listening !!");
			} catch (Exception e) {
					System.out.println(e);
			}
	   }
		
	   public void putToSleep() throws Exception{
		   Thread.sleep(1000);
	   }
	
}