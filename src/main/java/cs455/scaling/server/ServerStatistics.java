package cs455.scaling.server;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ServerStatistics implements Runnable{

	private static final HashMap<SelectionKey, ConnectionStatistics> conns = new HashMap<SelectionKey, ConnectionStatistics>();
	
	public void addMessagesRecieved(SelectionKey key, int count) {
		conns.get(key).addMessagesRecieved(count);
	}
	
	public void addMessagesSent(SelectionKey key, int count) {
		conns.get(key).addMessagesSent(count);
	}
	
	public void addConnections(SelectionKey key) {
		synchronized(conns) {
			conns.put(key, new ConnectionStatistics());
		}
	}
	
	private void resetMessages() {
		synchronized(this) {
			conns.forEach((k,v) -> {
				v.resetMessages();
			});
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
			double msgRec = 0.0;
			double msgSent = 0.0;
			double connsNumber = conns.size();
			ArrayList<Double> throughs = new ArrayList<Double>();
			for(ConnectionStatistics conStat: conns.values()) {
				double sent = 0.0;
				double rec = 0.0;
				synchronized(conStat) {
					rec = conStat.getMessagesRecieved();
					sent = conStat.getMessagesSent();
				}
				conStat.resetMessages();
				msgRec += rec;
				msgSent += sent;
				double threw = sent/rec;
				throughs.add(threw);
			}
			
			double through = msgSent/msgRec;
			double throughPerClient = through/connsNumber;
			double std = 0.0;
			for(double threw:throughs) {
				std += (through-threw)*(through-threw);
			}
			std /= connsNumber;
			String out = System.currentTimeMillis()+"\t Server Throughput: ";
			out+=through+" messages/s \t Active Client Connections: ";
			out+=conns+"\t Mean Per-Client Throughput: "+throughPerClient;
			out+=" messages/s Std. Dev. Of Per-Client Throughput:";
			out+=std+" messages/s";
			// need per connection info to compute this
			System.out.println(out);
			resetMessages();
		}
	}
}
