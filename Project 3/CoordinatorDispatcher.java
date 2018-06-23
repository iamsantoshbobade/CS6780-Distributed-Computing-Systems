import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class CoordinatorDispatcher implements Runnable {

	private int portno, threshold;
	ServerSocket dispatcherServerSocket;
	Socket dispatcherSocket;

	private HashMap<Integer, ParticipantBean> participantList;
	

	private static int COUNT = 1;

	public CoordinatorDispatcher(int portno, int threshold) {
		this.portno = portno;
		this.threshold = threshold;
		this.participantList = new HashMap<Integer, ParticipantBean>();
		try {
			dispatcherServerSocket = new ServerSocket(this.portno);
		} catch (IOException e) {
			System.out.println("Exception occured in creating dispatcher socket: " + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		
		try {
			while (true) {
				dispatcherSocket = dispatcherServerSocket.accept();
				Thread coordinatorThread = new Thread(new CoordinatorWorkerThread(dispatcherSocket,participantList,threshold));
				coordinatorThread.start();
			}

		} catch (Exception e) {
			System.out.println("Exception in establishing connection " + e.getMessage());
			e.printStackTrace();
		}

	}

}
