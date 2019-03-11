package cs455.scaling.server;

import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionStatistics {

	private AtomicInteger messagesRecieved = new AtomicInteger(0);
	private AtomicInteger messagesSent = new AtomicInteger(0);
	
	public void addMessagesRecieved(int count) {
		messagesRecieved.getAndAdd(count);
	}
	
	public void addMessagesSent(int count) {
		messagesSent.getAndAdd(count);
	}
	
	public int getMessagesRecieved() {
		return messagesRecieved.get();
	}
	
	public int getMessagesSent() {
		return messagesSent.get();
	}
	
	public void resetMessages() {
		synchronized(this) {
			messagesRecieved.set(0);
			messagesSent.set(0);
		}
	}
}
