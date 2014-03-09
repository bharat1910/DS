import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * This module is used to randomly generate a sequence of events 
 * for different processes. Files like input_file_0.txt, input_file_1.txt 
 * are generated for each set of processes.
 * Note that this module takes care of the snapshot message is generated with 
 * a particular probability for the initiator node. 
 */

public class GenerateInputFile
{
	static int numberSnapshots;
	
	public void run() throws NumberFormatException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("input.txt"));
		int processId = Integer.parseInt(br.readLine());
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("input_file_0.txt"));
		int count = 0;
		while (count < numberSnapshots) {
			int prob = (int) (Math.random() * 10);
			if (prob < 9) {
				int pId =  0;
				while (pId == 0) {
					pId = (int) (Math.random() * processId);
				}
				int cost = (int) (Math.random() * 5);
				int quantity = (int) (Math.random() * 5);
				bw.write(pId + ":" + cost + "," + quantity + "\n");
			} else {
				bw.write("snapshot\n");
				count++;
			}
		}
		bw.close();
		
		for (int i=1; i<processId; i++) {
			bw = new BufferedWriter(new FileWriter("input_file_" + i + ".txt"));
			for (int j=0; j<10; j++) {
				int pId =  i;
				while (pId == i) {
					pId = (int) (Math.random() * processId);
				}
				int cost = (int) (Math.random() * 5);
				int quantity = (int) (Math.random() * 5);
				
				bw.write(pId + ":" + cost + "," + quantity + "\n");
			}
			bw.close();
		}
		
		br.close();
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException
	{
		GenerateInputFile main = new GenerateInputFile();
		numberSnapshots = Integer.parseInt(args[0]);
		main.run();
	}
}
