package cs455.scaling.server;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerStatistics implements Runnable{

	private AtomicInteger messagesRecieved = new AtomicInteger(0);
	private AtomicInteger connections = new AtomicInteger(0);
	private AtomicInteger messagesSent = new AtomicInteger(0);
	
	public void addMessagesRecieved(int count) {
		messagesRecieved.getAndAdd(count);
	}
	
	public void addMessagesSent(int count) {
		messagesSent.getAndAdd(count);
	}
	
	public void addConnections(int count) {
		connections.getAndAdd(count);
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
			double msgRec = messagesRecieved.doubleValue();
			double msgSent = messagesSent.doubleValue();
			double conns = connections.doubleValue();
			double through = msgSent/msgRec;
			double throughPerClient = through/conns;
			double std = 0.0;
			String out = System.currentTimeMillis()+"/t Server Throughput: ";
			out+=through+" messages/s /t Active Client Connections: ";
			out+=conns+"/t Mean Per-Client Throughput: "+throughPerClient;
			out+=" messages/s Std. Dev. Of Per-Client Throughput:";
			out+=std+" messages/s";
			// need per connection info to compute this
			System.out.println(out);
		}
	}
}
