import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ParticipantTerminal implements Runnable {

	private String hostname;
	private int portno;
	private int participantID;
	private String logFile;

	private String status = Constants.DEREGISTERED;

	static boolean isConnected = false;

	private Socket participantSocket;
	private OutputStream outputStream = null;
	private PrintWriter printWriter = null;
	private InputStream inputStream = null;
	private BufferedReader bufferedReader = null;
	private BufferedReader streamReader = null;

	private ServerSocket communicationServerSocket;
	private Socket communicationSocket;
	private int communicationPortno;
	private boolean booked = false;

	private ParticipantConnection connection;

	Thread communicationThread;

	public ParticipantTerminal(String hostname, int portno, int participantID, String logFile) {
		this.portno = portno;
		this.hostname = hostname;
		this.participantID = participantID;
		this.logFile = logFile;
		try {
			participantSocket = new Socket(hostname, portno);
			outputStream = participantSocket.getOutputStream();
			printWriter = new PrintWriter(outputStream, true);
			inputStream = participantSocket.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			streamReader = new BufferedReader(new InputStreamReader(inputStream));

		} catch (Exception e) {
			System.out.println("Error in connecting to " + hostname + " in termincal client.");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		try {
			String userInput = "";
			while (true) {
				System.out.print("Terminal for " + participantID + " >");
				userInput = bufferedReader.readLine();
				if (userInput.trim().length() == 0)
					continue;

				String words[] = userInput.split(" ");
				String command = words[0];
				switch (command.toLowerCase()) {
				case "register":
					if (words.length < 2) {
						System.out.println("Incorrect usage. \nCorrect usage: register <portno>");
						break;
					}

					// Communication socket messages : Socket

					communicationPortno = Integer.parseInt(words[1]);
					connection = new ParticipantConnection(
							InetAddress.getLocalHost().getHostName(), communicationPortno, logFile);
					communicationThread = new Thread(connection);
					communicationThread.start();

					printWriter.println(userInput + Constants.REGEX_SEPARATOR + this.participantID
							+ Constants.REGEX_SEPARATOR + InetAddress.getLocalHost().getHostName());

					// Registered
					System.out.println(streamReader.readLine());

					this.status = Constants.REGISTERED;
					isConnected = true;

					booked = true;

					break;
				
				case "deregister":
					
					if (this.status.equals(Constants.REGISTERED)) {
						printWriter.println(userInput);
						this.status = Constants.DEREGISTERED;
						isConnected = false;
						
					} else {
						System.out.println("This participant is not currently registered to the multicast group");
					}
					break;

				case "reconnect":
					if (words.length < 2) {
						System.out.println("Incorrect usage. \nCorrect usage: reconnect <portno>");
						break;
					}
					// re-spawn communicationThread or wait it's not required at
					// all?
					if (isConnected) {
						System.out.println("Participant " + this.participantID + " is already connected.");
						break;
					}
					if (!this.status.equals(Constants.REGISTERED)) {
						System.out.println("Participant not registered.");
						break;
					}
					
					communicationPortno = Integer.parseInt(words[1]);
					communicationThread = new Thread(new ParticipantConnection(
							InetAddress.getLocalHost().getHostName(), communicationPortno, logFile));

					communicationThread.start();

					printWriter.println(userInput);
					isConnected = true;
					break;

				case "disconnect":
					// terminate communicationThread or wait it's not required
					// at all?

					if (!isConnected) {
						System.out.println("Can not disconnect.");
						break;
					}
					
					printWriter.println(userInput);
					isConnected = false;
					
					break;

				case "msend":
					if (!this.status.equals(Constants.REGISTERED)) {
						System.out.println(
								"You can not send or receive messages without registering to the multicast group.");
						break;
					}

					if (!isConnected) {
						System.out.println("Connect to the host to send or receive messsages.");
						break;
					}

					if (words.length < 2) {
						System.out.println("Can not send blank message.\nUse msend [message to send]");
						break;
					}

					printWriter.println(userInput + Constants.REGEX_SEPARATOR + this.participantID);

					break;
				default:
					System.out.println("Command " + command + " not supported.");

				}
			}

		} catch (Exception e) {
			System.out.println("Exception in run() of ParticipantTerminal");
			e.printStackTrace();
		}

	}

}
