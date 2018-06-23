import java.io.BufferedReader;
import java.io.FileReader;

public class CoordinatorMain {

	public static void main(String[] args) {
		try {
			String filename = args[0];
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			int portno = Integer.parseInt(reader.readLine());
			int threshold = Integer.parseInt(reader.readLine());
			CoordinatorDispatcher dispatcher = new CoordinatorDispatcher(portno, threshold);
			Thread mainDispatcherThread = new Thread(dispatcher);
			mainDispatcherThread.start();
			reader.close();
		} catch (Exception e) {
			System.out.println("Exception in CoordinatorMain: " + e.getMessage());
			e.printStackTrace();
		}

	}

}
