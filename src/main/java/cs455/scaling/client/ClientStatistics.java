package cs455.scaling.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientStatistics implements Runnable{

	private AtomicInteger messagesRecieved = new AtomicInteger(0);
	private AtomicInteger messagesSent = new AtomicInteger(0);
	
	public void addMessagesRecieved(int count) {
		messagesRecieved.getAndAdd(count);
	}
	
	public void addMessagesSent(int count) {
		messagesSent.getAndAdd(count);
	}
	
	private void resetMessages() {
		synchronized(this) {
			messagesRecieved.set(0);
			messagesSent.set(0);
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				TimeUnit.SECONDS.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int msgRec = messagesRecieved.get();
			int msgSent = messagesSent.get();
			System.out.println(System.currentTimeMillis()+"/t Total Sent Count: "+msgSent+"/t Total Recieved Count: "+msgRec);
			resetMessages();
		}
	}
	
}
