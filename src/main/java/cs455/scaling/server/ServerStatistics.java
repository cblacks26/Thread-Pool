package cs455.scaling.server;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ServerStatistics implements Runnable{

	private static final HashMap<SocketAddress, ConnectionStatistics> conns = new HashMap<SocketAddress, ConnectionStatistics>();
	
	public void addMessagesRecieved(SocketAddress address) {
		synchronized(conns) {
			if(!conns.containsKey(address)) {
				ConnectionStatistics con = new ConnectionStatistics();
				conns.put(address, con);
			}
		}
	}
	
	public void addMessagesSent(SocketAddress address, int count){
		synchronized(conns) {
			conns.get(address).addMessagesSent(count);
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
			double msgSent = 0.0;
			double connsNumber = conns.size();
			ArrayList<Double> throughs = new ArrayList<Double>();
			for(ConnectionStatistics conStat: conns.values()) {
				double sent = 0.0;
				synchronized(conStat) {
					sent += conStat.getMessagesSent();
				}
				conStat.resetMessages();
				msgSent += sent;
				double threw = sent/20;
				throughs.add(threw);
			}
			
			double through = msgSent/20;
			double throughPerClient = through/connsNumber;
			double std = 0.0;
			for(double threw:throughs) {
				std += Math.pow(threw-through, 2);
			}
			std /= connsNumber;
			std = Math.sqrt(std);
			String out = System.currentTimeMillis()+"\t Server Throughput: ";
			out+=through+" messages/s \t Active Client Connections: ";
			out+=connsNumber+"\t Mean Per-Client Throughput: "+throughPerClient;
			out+=" messages/s Std. Dev. Of Per-Client Throughput:";
			out+=std+" messages/s";
			System.out.println(out);
			resetMessages();
		}
	}
}
