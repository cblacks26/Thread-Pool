package cs455.scaling.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	private Socket socket;
	private DataInputStream dis;
	
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
		SenderThread sender = SenderThread.createInstance(new DataOutputStream(socket.getOutputStream()),stats);
		Thread senderThread = new Thread(sender);
		senderThread.start();
	}
	
}
