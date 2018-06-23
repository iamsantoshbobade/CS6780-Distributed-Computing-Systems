import java.io.PrintWriter;
import java.util.Calendar;


public class ParticipantBean {

	private int participantId;
	private String hostname;
	private int portno;
	private String status = Constants.DEREGISTERED;
	private boolean isConnected = false;
	private PrintWriter printWriter;
	private Calendar timeConnected;
	private Calendar timeDisconnected;
	private Message msgQueue[];
	private int tail;
	
	public ParticipantBean() {
		// TODO Auto-generated constructor stub
		tail = -1;
		msgQueue = new Message[200];
		for(int i = 0; i < 200; i++){
			msgQueue[i] = new Message();                      
		}
		
	}

	// Mention about connected time here

	public int getParticipantId() {
		return participantId;
	}

	public void setParticipantId(int participantId) {
		this.participantId = participantId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPortno() {
		return portno;
	}

	public void setPortno(int portno) {
		this.portno = portno;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public PrintWriter getPrintWriter() {
		return printWriter;
	}

	public void setPrintWriter(PrintWriter printWriter) {
		this.printWriter = printWriter;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public Calendar getTimeConnected() {
		return timeConnected;
	}

	public void setTimeConnected(Calendar timeConnected) {
		this.timeConnected = timeConnected;
	}

	public Calendar getTimeDisconnected() {
		return timeDisconnected;
	}

	public void setTimeDisconnected(Calendar timeDisconnected) {
		this.timeDisconnected = timeDisconnected;
	}

	public Message[] getMsgQueue() {
		return msgQueue;
	}

	public void setMsgQueue(Message[] msgQueue) {
		this.msgQueue = msgQueue;
	}

	public int getTail() {
		return tail;
	}

	public void setTail(int tail) {
		this.tail = tail;
	}

}
