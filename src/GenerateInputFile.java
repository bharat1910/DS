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
	public void run() throws NumberFormatException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("input.txt"));
		int processId = Integer.parseInt(br.readLine());
		
		for (int i=0; i<processId; i++) {
			BufferedWriter bw = new BufferedWriter(new FileWriter("input_file_" + i + ".txt"));
		
			for (int j=0; j<5; j++) {
				int prob = (int) (Math.random() * 15);
				
				if (i != 0 || (i == 0 && prob <14)) {
					int pId =  i;
					while (pId == i) {
						pId = (int) (Math.random() * processId);
					}
					int cost = (int) (Math.random() * 5);
					int quantity = (int) (Math.random() * 5);
					
					bw.write(pId + ":" + cost + "," + quantity + "\n");
				} else {
					bw.write("snapshot\n");
				}
			}
		
			bw.close();
		}
		
		
		br.close();
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException
	{
		GenerateInputFile main = new GenerateInputFile();
		main.run();
	}
}
