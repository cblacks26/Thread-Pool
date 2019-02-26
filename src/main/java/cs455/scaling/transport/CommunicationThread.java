package cs455.scaling.transport;

import java.net.Socket;
import java.util.LinkedList;

public class CommunicationThread implements Runnable{

	private final LinkedList<byte[]> queue = new LinkedList<byte[]>();
	private Socket socket = null;
	private boolean running = false;
	
	public void sendData(byte[] data) {
		synchronized(queue) {
			queue.add(data);
		}
	}
	
	@Override
	public void run() {
		while(running) {
			while(!queue.isEmpty()) {
				synchronized(queue) {
					byte[] data = queue.remove(0);
				}
			}
		}
	}
	
}
