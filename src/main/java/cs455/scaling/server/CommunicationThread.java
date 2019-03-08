package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class CommunicationThread implements Runnable{

	private boolean running = false;
	private Server server;
	private Selector selector;
	private ServerSocketChannel serverSocket;
	
	private CommunicationThread(Server server, int port) throws IOException {
		this.server = server;
		selector = Selector.open();
		serverSocket = ServerSocketChannel.open();
		serverSocket.bind(new InetSocketAddress(port));
		serverSocket.configureBlocking(false);
		serverSocket.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	public static CommunicationThread createInstance(Server server, int port) throws IOException {
		CommunicationThread ct = new CommunicationThread(server,port);
		Task.initialize(ct.selector, server.getServerStats());
		return ct;
	}
	
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
				it.remove();
				synchronized(key) {
					if(!key.isValid()) {
						continue;
					}
					if(key.isAcceptable()&&key.attachment()==null) {
						server.getThreadPoolManager().addTask(Task.createInstance(2,key));
					}
					
					if(key.isReadable()&&key.attachment()==null) {
						server.getThreadPoolManager().addTask(Task.createInstance(1,key));
					}
				}
			}
		}
	}
	
}
