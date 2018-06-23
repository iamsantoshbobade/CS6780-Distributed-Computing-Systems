package code;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSide {

	public static String PWD = "";
	public static String serverOutput = "";
	public static int PORT = 0;
	

	public static void startServer(int port) throws IOException{
		ServerSocket ssocketObj = null;
		Socket socket = null;
		BufferedReader br = null;
		InputStream ips = null;
		PrintWriter pw = null;
		try {
			ssocketObj = new ServerSocket(port);
			socket = ssocketObj.accept();
			ips = socket.getInputStream();

			OutputStream ops = socket.getOutputStream();
			pw = new PrintWriter(ops, true);
			br = new BufferedReader(new InputStreamReader(ips));
			String clientData = "";
			PWD = null;
			if (PWD == null)
				PWD = System.getProperty("user.dir");
			while (true) {
				clientData = br.readLine();

				String command = interpret(clientData);

				switch (command) {
				case "get":
					String str = executeGet(PWD+"/"+clientData.split(" ")[1], socket, pw);
					//pw.println(str);
					System.out.println("print in get case "+str);
					break;
				case "put":
					// Need to think about it
					break;

				case "mkdir":
					String words[] = clientData.trim().split(" ");

					if (words.length > 1) {
						File file = new File(PWD + "/" + words[1].trim());
						boolean fileExists = file.exists();
						if (!fileExists) {
							file.mkdir();
							pw.println("MKDIR COMMAND IGNORE");
						} else {
							pw.println("File: " + words[1].trim() + " already exists.");
						}
					} else {
						pw.println("mkdir: missing operand. \nCorrect usage: mkdir <directory-name>");
					}

					break;

				case "cd":
					words = clientData.trim().split(" ");
					if (words.length > 1) {
						if (words[1].trim().equals("..")) {
							int indexOfLastSlash = PWD.lastIndexOf('/');
							if (indexOfLastSlash != -1) {
								String temp = PWD.substring(0, indexOfLastSlash);
								PWD = temp;
								// pw.println(PWD);
								pw.println("CD COMMAND IGNORE");
								// pw.println();
							}

						} else {

							File f = new File(words[1].trim());
							if (f.exists() && f.isDirectory() && f.isAbsolute()) {
								PWD = words[1].trim();
								// pw.println(PWD);
								pw.println("CD COMMAND IGNORE");
							} else {
								File file = new File(PWD + "/" + words[1].trim());
								boolean fileExists = file.exists();
								boolean isFileAdirectory = file.isDirectory();
								if (fileExists) {
									if (isFileAdirectory) {
										PWD = PWD + "/" + words[1].trim();
										pw.println("CD COMMAND IGNORE");
									} else {
										pw.println("cd: " + words[1] + ": Not a directory");
									}
								} else {
									pw.println("cd: " + words[1] + ": No such file or directory");
								}
							}

						}
					} else {
						pw.println("cd: missing operand. \nOnly cd .. and cd <directory-name> are supported.");
					}

					break;
				case "delete":

					words = clientData.trim().split(" ");
					if (words.length > 1) {
						File file = new File(PWD + "/" + words[1].trim());
						boolean fileExists = file.exists();
						boolean isFileAdirectory = file.isDirectory();
						if (fileExists && !isFileAdirectory) {
							file.delete();
							pw.println("DELETE COMMAND IGNORE");
						} else if (isFileAdirectory) {
							pw.println(words[1] + " is a directory.\nCan not delete directory using delete.");
						}
					} else {
						pw.println("delete: missing operand. \nCorrect usage: delete <filename>");
					}
					break;
				case "ls":

					File file = new File(PWD);
					String directoryList[] = file.list();
					String temp = "";
					for (String directory : directoryList) {
						temp = temp + directory + "\t";
					}
					pw.println(temp);

					break;

				case "pwd":
					pw.println((InetAddress.getLocalHost().getHostName().toString() + PWD));
					break;
				case "quit":
					
					pw.println("QUIT COMMAND IGNORE");
					
					break;
				default:
					pw.println("Invalid command.");
					break;

				}

				/*
				 * String words[] = clientData.trim().split(" ");
				 * 
				 * if (words[0].trim().equals("cd")) {
				 * 
				 * if (words.length > 1) { if (words[1].trim().equals("..")) {
				 * int indexOfLastSlash = PWD.lastIndexOf('/'); if
				 * (indexOfLastSlash != -1) { String temp = PWD.substring(0,
				 * indexOfLastSlash); PWD = temp; // pw.println(PWD);
				 * pw.println("CD COMMAND IGNORE"); // pw.println(); }
				 * 
				 * } else {
				 * 
				 * File f = new File(words[1].trim()); if (f.exists() &&
				 * f.isDirectory() && f.isAbsolute()) { PWD = words[1].trim();
				 * // pw.println(PWD); pw.println("CD COMMAND IGNORE"); } else {
				 * File file = new File(PWD + "/" + words[1].trim()); boolean
				 * fileExists = file.exists(); boolean isFileAdirectory =
				 * file.isDirectory(); if (fileExists) { if (isFileAdirectory) {
				 * PWD = PWD + "/" + words[1].trim(); pw.println(
				 * "CD COMMAND IGNORE"); } else { pw.println("cd: " + words[1] +
				 * ": Not a directory"); } } else { pw.println("cd: " + words[1]
				 * + ": No such file or directory"); } }
				 * 
				 * } } else { pw.println(
				 * "cd: missing operand. \nOnly cd .. and cd <directory-name> are supported."
				 * ); }
				 * 
				 * } else if (words[0].trim().equals("pwd")) {
				 * 
				 * // pw.println("PWD request");
				 * 
				 * System.out.println("Curr Directory: " +
				 * (InetAddress.getLocalHost().getHostName() .toString() +
				 * PWD));
				 * 
				 * pw.println((InetAddress.getLocalHost().getHostName().toString
				 * () + PWD)); } else if (words[0].trim().equals("ls")) { File
				 * file = new File(PWD); boolean fileExists = file.exists(); if
				 * (fileExists) { // NOT REQUIRED String directoryList[] =
				 * file.list(); String temp = ""; for (String directory :
				 * directoryList) { temp = temp + directory + "\t"; } //
				 * if(temp.length() > 1){ pw.println(temp); // } }
				 * 
				 * } else if (words[0].trim().equals("delete")) {
				 * 
				 * if (words.length > 1) { File file = new File(PWD + "/" +
				 * words[1].trim()); boolean fileExists = file.exists(); boolean
				 * isFileAdirectory = file.isDirectory(); if (fileExists &&
				 * !isFileAdirectory) { file.delete(); pw.println(
				 * "DELETE COMMAND IGNORE"); } else if (isFileAdirectory) {
				 * pw.println(words[1] +
				 * " is a directory.\nCan not delete directory using delete.");
				 * } } else { pw.println(
				 * "delete: missing operand. \nCorrect usage: delete <filename>"
				 * ); }
				 * 
				 * } else if (words[0].trim().equals("mkdir")) {
				 * 
				 * if (words.length > 1) { File file = new File(PWD + "/" +
				 * words[1].trim()); boolean fileExists = file.exists(); if
				 * (!fileExists) { file.mkdir(); pw.println(
				 * "MKDIR COMMAND IGNORE"); } else { pw.println("File: " +
				 * words[1].trim() + " already exists."); } } else { pw.println(
				 * "mkdir: missing operand. \nCorrect usage: mkdir <directory-name>"
				 * ); }
				 * 
				 * } else if (words[0].trim().equals("quit")) { pw.println(
				 * "QUIT COMMAND IGNORE"); ssocketObj.close(); }
				 * 
				 * 
				 * String currentDir = System.getProperty("user.dir");
				 * System.setProperty("user.dir", currentDir + "/src123");
				 * 
				 * Path path = Paths.get(""); currentDir =
				 * path.toAbsolutePath().toString();
				 * 
				 * 
				 * currentDir = System.getProperty("user.dir");
				 * 
				 * System.out.println("Dir::::::"+currentDir);
				 * 
				 * 
				 * // Path path = Paths.get("Test.txt");
				 */
			}

		} catch (IOException e) {
			pw.println("Exception occured in StartServer method: " + e.getMessage());
		} finally {
			br.close();
			ips.close();
			socket.close();
			ssocketObj.close();
			startServer(PORT);
			PWD = System.getProperty("user.dir");

		}
	}

	private static String executeGet(String path, Socket socket, PrintWriter pw) throws IOException {
		OutputStream outStr = socket.getOutputStream();
		File file = new File(path);
		int i = 0;
		boolean fileExists = file.exists();
		if(fileExists){
			System.out.println("at least file exists   ");
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			byte fileChars[] = new byte[(int)(file.length())];
			//bufferedInputStream.read(fileChars, 0, fileChars.length);
			int b = 0;
			int count = 0;
			//outStr.write(fileChars, 0, fileChars.length);
			/*while ((b = fileInputStream.read()) > -1) {
				//outStr.write(b);
				pw.println(b);
				//System.out.println(b);
				//System.out.println(file.getName());
			}*/
			
			while(( b = bufferedInputStream.read()) >= 0){
				System.out.println("Server writing "+b);
				outStr.write(b);
				//System.out.print(b+":");
				count++;
			}
			outStr.flush();
			fileInputStream.close();
			System.out.println(":"+b+ "\nread concluded in server:"+count);
		}else{
			return "get: "+file.getName()+": No such file or directory";
		}
		return "GET COMMAND IGNORE";
		
		
	}

	private static String interpret(String clientData) {
		return clientData.split(" ")[0];
	}

	public static void main(String args[]) throws IOException{
		PWD = null;
		PORT = 1234;
		startServer(1234);
	}
}