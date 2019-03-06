package cs455.scaling.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;

import cs455.scaling.util.Helper;

public class Task {

	private static ServerSocketChannel serverSocket;
	private static Selector selector;
	private byte[] data;
	private int type;
	private SelectionKey key;

	public static void initialize(ServerSocketChannel socket, Selector select) {
		serverSocket = socket;
		selector = select;
	}
	
	private Task(int type, SelectionKey key) {
		this.type = type;
		this.key = key;
	}
	
	public static Task createInstance(int type, SelectionKey key) {
		key.attach(new Object());
		Task t = new Task(type, key);
		return t;
	}
	
	public void completeTask() {
		if(type==1) {
			ByteBuffer buffer = ByteBuffer.allocate(8000);
			SocketChannel socket = (SocketChannel)key.channel();
			try {
				socket.read(buffer);
				this.data = buffer.array();
				computeAndSend();
			} catch (IOException e) {
				System.out.println("IOException read from socket in CommunicationThread: "+e.getStackTrace());
			}
			key.attach(null);
		}else {
			acceptConnection();
		}
	}
	
	private void computeAndSend() {
		try {
			String hash = Helper.SHA1FromBytes(data);
			sendHash(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private void sendHash(String hash) {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.wrap(hash.getBytes());
		try {
			channel.write(buffer);
			buffer.clear();
		} catch (IOException e) {
			System.out.println("IOException sending hash back to client: "+ e.getStackTrace());
		}
		key.attach(null);
	}
	
	private void acceptConnection() {
		try {
			SocketChannel socket = serverSocket.accept();
			socket.configureBlocking(false);
			socket.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			System.out.println("IOException in CommunicationThread accepting a socket: "+e.getStackTrace());
		}
	}
}
