import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ParticipantConnection implements Runnable {
	private String hostname;
	private int portno;
	private String logFile;
	private ServerSocket communicationServerSocket;
	private Socket communicationSocket;

	BufferedReader bufferedReader = null;
	InputStream inputStream = null;
	PrintWriter printWriter = null;

	public ParticipantConnection(String hostname, int portno, String logFile) {

		this.hostname = hostname;
		this.portno = portno;
		this.logFile = logFile;
		try {
			communicationServerSocket = new ServerSocket(this.portno);
		} catch (IOException e) {
			System.out.println("Exception in ParticipantConnection.. " + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		
		try {
			communicationSocket = this.communicationServerSocket.accept();
			communicationSocket.setReuseAddress(true);
			
			inputStream = communicationSocket.getInputStream();
			OutputStream outputStream = communicationSocket.getOutputStream();

			printWriter = new PrintWriter(outputStream, true);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String msg = "";
			
			Path p = Paths.get(logFile);
			File f = p.toFile();

			while (true) {
				msg = bufferedReader.readLine();
				if(msg.equals("gtg"))
					break;
				else
					Files.write(f.toPath(), (msg+"\n").getBytes(), StandardOpenOption.APPEND);
			}
			communicationSocket.close();
			communicationServerSocket.close();
			
		} catch (IOException e) {
			System.out.println("Exception in run() of ParticipantConnection.. " + e.getMessage());
			e.printStackTrace();
		}

	}

	public ServerSocket getCommunicationServerSocket() {
		return communicationServerSocket;
	}

	public void setCommunicationServerSocket(ServerSocket communicationServerSocket) {
		this.communicationServerSocket = communicationServerSocket;
	}

	public Socket getCommunicationSocket() {
		return communicationSocket;
	}

	public void setCommunicationSocket(Socket communicationSocket) {
		this.communicationSocket = communicationSocket;
	}

}
