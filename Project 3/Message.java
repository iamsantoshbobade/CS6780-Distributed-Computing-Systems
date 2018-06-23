import java.util.Calendar;

public class Message {
	
	private String message;
	private Calendar receivedTime;
	private boolean sent;
	
	public Message() {
		message = "";
		sent = false;
	}
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Calendar getReceivedTime() {
		return receivedTime;
	}
	public void setReceivedTime(Calendar receivedTime) {
		this.receivedTime = receivedTime;
	}
	public boolean isSent() {
		return sent;
	}
	public void setSent(boolean sent) {
		this.sent = sent;
	}
	
}
