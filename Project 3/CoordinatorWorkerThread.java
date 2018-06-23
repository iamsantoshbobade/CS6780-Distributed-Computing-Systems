import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CoordinatorWorkerThread implements Runnable {

	Socket socket = null;
	Socket communicationSocket = null;
	BufferedReader bufferedReader = null;
	InputStream inputStream = null;
	PrintWriter printWriter = null;
	private int participantId, threshold;
	private HashMap<Integer, ParticipantBean> participantList;

	private OutputStream communicationOutputStream = null;
	private PrintWriter communicationPrintWriter = null;
	private InputStream communicationInputStream = null;
	private BufferedReader communicationStreamReader = null;
	String hostname = "";

	boolean loop = true;
	String receivedString;

	public CoordinatorWorkerThread(Socket socket, HashMap<Integer, ParticipantBean> participantList, int threshold) {
		this.socket = socket;
		this.participantList = participantList;
		this.receivedString = "";
		this.threshold = threshold;

	}

	@Override
	public void run() {
		
		ParticipantBean bean = new ParticipantBean();
		try {
			inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			printWriter = new PrintWriter(outputStream, true);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String clientData = "";

			while (true) {
				String command = "";
				clientData = bufferedReader.readLine();
				if (clientData != null) {
					if (clientData.startsWith("register"))
						command = "register";
					else if (clientData.startsWith("reconnect"))
						command = "reconnect";
					else if (clientData.startsWith("disconnect")) {
						command = "disconnect";
					} else if (clientData.startsWith("deregister")) {
						command = "deregister";
					} else if (clientData.startsWith("msend")) {
						command = "msend";
					}
				}

				switch (command) {

				case "register":
					
					receivedString = clientData.split(" ")[1];
					int portno = Integer.parseInt(receivedString.split(Constants.REGEX_SEPARATOR)[0]);
					this.participantId = Integer.parseInt(receivedString.split(Constants.REGEX_SEPARATOR)[1]);

					boolean participantExists = this.participantList.containsKey(this.participantId);
					if (!participantExists) {
						hostname = receivedString.split(Constants.REGEX_SEPARATOR)[2];
						
						bean.setHostname(hostname);
						bean.setPortno(portno);
						bean.setParticipantId(this.participantId);
						bean.setStatus(Constants.REGISTERED);
						bean.setConnected(true);
						bean.setTimeConnected(Calendar.getInstance());

						printWriter.println("Successfully Registered " + participantId);

						communicationSocket = new Socket(hostname, portno);
						communicationOutputStream = communicationSocket.getOutputStream();
						communicationPrintWriter = new PrintWriter(communicationOutputStream, true);
						communicationInputStream = communicationSocket.getInputStream();
						communicationStreamReader = new BufferedReader(new InputStreamReader(communicationInputStream));
						bean.setPrintWriter(communicationPrintWriter);

						participantList.put(this.participantId, bean);
					} else {
						printWriter.println("Participant " + this.participantId + " already registered.");
					}

					break;

				case "disconnect":
					loop = false;
					
					ParticipantBean disconnectBean = new ParticipantBean();
					disconnectBean = this.participantList.get(this.participantId);
					disconnectBean.setStatus(Constants.DISCONNECTED);
					disconnectBean.setConnected(false);
					disconnectBean.setTimeDisconnected(Calendar.getInstance());
					
					//communicationOutputStream = 
					//communicationPrintWriter = new PrintWriter(communicationOutputStream, true);
					
					communicationPrintWriter = disconnectBean.getPrintWriter();
					communicationPrintWriter.println("gtg");
					communicationOutputStream.close();
					
					break;
					
				case "deregister":
					loop = false;

					ParticipantBean derigsterBean = new ParticipantBean();
					derigsterBean = (ParticipantBean) this.participantList.get(this.participantId);
					derigsterBean.setStatus(Constants.DEREGISTERED);// Is it
																	// required??
					derigsterBean.setConnected(false);
					derigsterBean.setTimeDisconnected(Calendar.getInstance());
					this.participantList.remove(this.participantId);
					
					//communicationOutputStream = communicationSocket.getOutputStream();
					//communicationPrintWriter = new PrintWriter(communicationOutputStream, true);
					communicationPrintWriter = derigsterBean.getPrintWriter();
					communicationPrintWriter.println("gtg");
					communicationOutputStream.close();
					
					break;

				case "msend":
					receivedString = clientData.split(Constants.REGEX_SEPARATOR)[0];
					this.participantId = Integer.parseInt(clientData.split(Constants.REGEX_SEPARATOR)[1]);
					
					Iterator it = this.participantList.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						ParticipantBean iteratorParticipantBean = (ParticipantBean) pair.getValue();

						if (iteratorParticipantBean.isConnected()) {
							iteratorParticipantBean.getPrintWriter().println(receivedString.substring(6));
						} else {
							Message msgQueuq[] = iteratorParticipantBean.getMsgQueue();
							iteratorParticipantBean.setTail(1 + iteratorParticipantBean.getTail());

							Message message = new Message();
							message.setMessage(receivedString.substring(6));
							message.setReceivedTime(Calendar.getInstance());
							message.setSent(false);

							msgQueuq[iteratorParticipantBean.getTail()] = message;
						}

					}
					
					break;

				case "reconnect":
					
					receivedString = clientData.split(" ")[1];
					int nPortno = Integer.parseInt(receivedString.split(Constants.REGEX_SEPARATOR)[0]);
					Socket newCommunicationSocket = new Socket(hostname, nPortno);
					
					communicationOutputStream = newCommunicationSocket.getOutputStream();
					communicationPrintWriter = new PrintWriter(communicationOutputStream, true);
					communicationInputStream = newCommunicationSocket.getInputStream();
					communicationStreamReader = new BufferedReader(new InputStreamReader(communicationInputStream));
								
					bean.setPrintWriter(communicationPrintWriter);
					
					ParticipantBean reconnectBean = new ParticipantBean();
					reconnectBean = (ParticipantBean) this.participantList.get(this.participantId);
					reconnectBean.setStatus(Constants.CONNECTED);
					reconnectBean.setConnected(true);
					reconnectBean.setTimeConnected(Calendar.getInstance());
					
					PrintWriter reconnectBeanPrintWriter = reconnectBean.getPrintWriter();
					
					Message reconnectMsgQueuq[] = reconnectBean.getMsgQueue();
					Calendar currentTime = Calendar.getInstance();
					currentTime.add(Calendar.SECOND, threshold * (-1));
					
					for (int i = 0; i <= reconnectBean.getTail(); i++) {
						Message msgToWrite = reconnectMsgQueuq[i];
						if (msgToWrite.getReceivedTime().after(currentTime)) {
							reconnectBeanPrintWriter.println(msgToWrite.getMessage());
						}

					}
					break;
				default:
					System.out.println("Command " + command + " received.");

				}
			}

		} catch (Exception e) {
			System.out.println("Exception occured in run() of Coordinator Worker Thread.. " + e.getMessage());
			e.printStackTrace();

		}

	}

}
