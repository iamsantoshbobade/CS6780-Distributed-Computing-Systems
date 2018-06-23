import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;

public class ParticipantMain {

	public static void main(String[] args) {

		try {
			String filename = args[0];
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			int participantID = Integer.parseInt(reader.readLine());
			String logFile = reader.readLine();
			String hostname = reader.readLine();
			int portno = Integer.parseInt(reader.readLine());

			ParticipantTerminal terminal = new ParticipantTerminal(hostname, portno, participantID,logFile);
			Thread terminalThread = new Thread(terminal);
			terminalThread.start();
			reader.close();
		} catch (Exception e) {
			System.out.println("Exception in CoordinatorMain: " + e.getMessage());
			e.printStackTrace();
		}

	}

}
