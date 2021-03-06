package cs455.scaling.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;

import cs455.scaling.util.Helper;

public class Task {

	private static ServerStatistics stats;
	private static Selector selector;
	private byte[] data;
	private int type;
	private SelectionKey key;

	public static void initialize(Selector select, ServerStatistics serverStats) {
		selector = select;
		stats = serverStats;
	}
	
	private Task(int type, SelectionKey key) {
		this.type = type;
		this.key = key;
	}
	
	public static Task createInstance(int type, SelectionKey key) {
		Task t = new Task(type, key);
		return t;
	}
	
	public void completeTask() throws IOException, NoSuchAlgorithmException {
		synchronized(key) {
			if(type==1) {
				key.attach(new Object());
				ByteBuffer buffer = ByteBuffer.allocate(8000);
				SocketChannel socket = (SocketChannel)key.channel();
				SocketAddress address = socket.getRemoteAddress();
				int bytesRead = socket.read(buffer);
				if(bytesRead<1) {
					key.attach(null);
					return;
				}
				this.data = buffer.array();
				stats.addMessagesRecieved(address);
				String hash = Helper.SHA1FromBytes(data);
				buffer = ByteBuffer.wrap(hash.getBytes());
				socket.write(buffer);
				buffer.clear();
				stats.addMessagesSent(address,1);
				key.attach(null);
			}else {
				acceptConnection();
			}
		}
	}
	
	private void acceptConnection() {
		try {
			ServerSocketChannel channel = (ServerSocketChannel) key.channel();
			SocketChannel socket = channel.accept();
			if(socket!=null) {
				socket.configureBlocking(false);
				//selector.wakeup();
				socket.register(selector, SelectionKey.OP_READ);
			}
		} catch (IOException e) {
			System.out.println("IOException in CommunicationThread accepting a socket: "+e.getStackTrace());
		}
	}
}
