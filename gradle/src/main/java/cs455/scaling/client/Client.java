package cs455.scaling.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

	private Socket socket;
	private DataInputStream dis;
	private SenderThread sender;
	
	public static void main(String[] args) {
		if(args.length!=3) {
			System.out.println("Must include server-host server-port message-rate");
			System.exit(0);
		}
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		int messageRate = Integer.parseInt(args[2]);
		Client client = new Client();
		try {
			client.initialize(hostname, port, messageRate);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initialize(String hostname, int port, int messageRate) throws UnknownHostException, IOException {
		socket = new Socket(hostname,port);
		dis = new DataInputStream(socket.getInputStream());
		ClientStatistics stats = new ClientStatistics();
		Thread statsThread = new Thread(stats);
		statsThread.start();
		sender = SenderThread.createInstance(new DataOutputStream(socket.getOutputStream()),stats,messageRate);
		Thread senderThread = new Thread(sender);
		senderThread.start();
	}
	
	private void recieve() {
		while(true) {
			try {
				byte[] data = new byte[40];
				dis.readFully(data);
				sender.recievedHash(new String(data));
			} catch(SocketException se) {
				System.out.println("SError in Reciever Thread: "+se.getMessage());
				se.printStackTrace();
			} catch(IOException ioe) {
				System.out.println("IOError in Reciever Thread: "+ioe.getMessage());
				ioe.printStackTrace();
			} catch (Exception e) {
				System.out.println("Error in Reciever Thread: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
}
