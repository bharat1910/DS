import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateInputFile
{
	public void run() throws NumberFormatException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("input.txt"));
		int processId = Integer.parseInt(br.readLine());
		
		for (int i=0; i<processId; i++) {
			BufferedWriter bw = new BufferedWriter(new FileWriter("input_file_" + i + ".txt"));
		
			for (int j=0; j<20; j++) {
				int prob = (int) (Math.random() * 10);
				
				if (prob <9) {
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
