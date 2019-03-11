package cs455.scaling.server;

import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionStatistics {
	
	private AtomicInteger messagesSent = new AtomicInteger(0);

	
	public void addMessagesSent(int count) {
		messagesSent.getAndAdd(count);
	}
	
	public int getMessagesSent() {
		return messagesSent.get();
	}
	
	public void resetMessages() {
		synchronized(this) {
			messagesSent.set(0);
		}
	}
}
