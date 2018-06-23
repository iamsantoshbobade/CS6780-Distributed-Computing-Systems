package code;

import java.io.BufferedOutputStream;
/**
 * @author Pranjay Patil
 * 		   Santosh Bobade
 * @version 1.0.0
 */
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSide {
	/**
	 * This method establishes a connection with the remote server.
	 * 
	 * @param port
	 *            - The port of the server machine
	 * @throws UnknownHostException
	 * @throws IOException
	 * 
	 */
	public static void startClient(String address, int port) throws UnknownHostException, IOException {
		Socket socket = null;
		BufferedOutputStream bufferedOutputStream = null;

		try {

			int max_int = Integer.MAX_VALUE;
			socket = new Socket(address, port);
			OutputStream ops = socket.getOutputStream();
			// System.out.println(socket.getInetAddress().getLocalHost().getHostName());
			PrintWriter pw = new PrintWriter(ops, true);
			InputStream ips = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(ips));
			String userInp = "";
			while (!userInp.equalsIgnoreCase("quit")) {
				System.out.print("myftp>");
				userInp = br.readLine();
				if (userInp.length() == 0) {
					// System.out.println("hi");
					continue;
				}

				// if(userInp.trim().length()>=1){
				pw.println(userInp);
				// System.out.println("****"++"*****");
				String serverStream = null;
				String temp = "";
				// System.out.println("****"+serverStream+"*****");
				if (userInp != null && userInp.startsWith("get")) {
					/*
					 * while (serverStream!=null) {
					 * System.out.println(serverStream); serverStream =
					 * streamReader.readLine();
					 * 
					 * }
					 */

					// continue;

					// ACTUAL CODE STARTS HERE:
					/*
					 * while((serverStream = streamReader.readLine()) != null){
					 * //System.out.println("Line:"+serverStream); temp = temp +
					 * serverStream+"\n"; //System.out.println("String format:"
					 * +temp); }
					 */
					System.out.println("Exited read loop..String format:" + temp);
					byte fileChars[] = new byte[60606060];
					InputStream inputStream = socket.getInputStream();
					String fileName = userInp.substring(4, userInp.length());
					System.out.println("In client:**" + fileName + "**");

					FileOutputStream fileOutputStream = new FileOutputStream(
							System.getProperty("user.dir") + "/" + fileName);
					bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					System.out.println("reached");
					// pw.println();
					// pw.println();
					int b = 0;// inputStream.read(fileChars, 0, fileChars.length
								// - 100);
					System.out.println("ServerStream in client:" + serverStream);// +
																					// "\n
																					// Bytes
																					// Read:"
																					// +
																					// bytesRead);
					int current = b;
					/*
					 * do {
					 * 
					 * //bytesRead = inputStream.read(fileChars, current,
					 * fileChars.length - current); bytesRead =
					 * inputStream.read();
					 * 
					 * //bufferedOutputStream.write(fileChars, 0, current);
					 * System.out.println("client writing " + bytesRead); if
					 * (bytesRead >= 0) { current = current + bytesRead; }
					 * //System.out.println("end of while..about to go"); }
					 * while (bytesRead >= 0);
					 */

					int index = 0;

					while ((b = inputStream.read()) != -1) {

						fileChars[index++] = (byte) b;
						System.out.println("client writing " + b + ":::::::index:" + index);
						if (index == 20) {
							System.out.println("EOF");
						}
					}

					System.out.println("before write");
					bufferedOutputStream.write(fileChars, 0, current);
					System.out.println("after write: " + current);

				}

				serverStream = streamReader.readLine();
				if (serverStream != null && !serverStream.endsWith("IGNORE"))
					System.out.println(serverStream);

			}
			// socket.wait();

		} catch (Exception e) {
			System.out.println("Exception occured: " + e.getMessage());
			socket.close();
			bufferedOutputStream.flush();
			bufferedOutputStream.close();
		} finally {
			//System.out.println("Finally bloack");
			// socket.close();
			// bufferedOutputStream.flush();
			// /bufferedOutputStream.close();

		}
	}

	public static void main(String args[]) throws UnknownHostException, IOException {
		startClient(args[0], Integer.parseInt(args[1]));// 1234);
	}
}