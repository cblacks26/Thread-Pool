package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class CommunicationThread implements Runnable{

	private boolean running = false;
	private Selector selector;
	private ServerSocketChannel serverSocket;
	private Server server;
	
	private CommunicationThread(Server server, int port) throws IOException {
		this.server = server;
		selector = Selector.open();
		serverSocket = ServerSocketChannel.open();
		serverSocket.bind(new InetSocketAddress("localhost", port));
		serverSocket.configureBlocking(false);
		serverSocket.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	public static CommunicationThread createInstance(Server server, int port) throws IOException {
		CommunicationThread ct = new CommunicationThread(server,port);
		return ct;
	}
	
	private void read(SelectionKey key) {
		ByteBuffer buffer = ByteBuffer.allocate(8000);
		
		SocketChannel socket = (SocketChannel) key.channel();
		int bytesRead;
		try {
			bytesRead = socket.read(buffer);
			server.getThreadPoolManager().addTask(Task.createInstance(buffer.array(),key));
		} catch (IOException e) {
			System.out.println("IOException read from socket in CommunicationThread: "+e.getStackTrace());
		}
		buffer.clear();
	}
	
	private void register() {
		try {
			SocketChannel socket = serverSocket.accept();
			socket.configureBlocking(false);
			socket.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			System.out.println("IOException in CommunicationThread accepting a socket: "+e.getStackTrace());
		}
	}
	
	// Have to check if need to synchronize connections as well
	@Override
	public void run() {
		running = true;
		while(running) {
			try {
				selector.select();
			} catch (IOException e) {
				System.out.println("IOException in CommunicationThread calling select(): "+e.getStackTrace());
			}
				
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> it = keys.iterator();
			while(it.hasNext()) {
				SelectionKey key = it.next();
				if(!key.isValid()) {
					
				}
				
				if(key.isAcceptable()) {
					register();
				}
				
				if(key.isReadable()) {
					read(key);
				}
				it.remove();
			}
		}
	}
	
}
