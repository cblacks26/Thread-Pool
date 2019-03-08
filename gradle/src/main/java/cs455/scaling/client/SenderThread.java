package cs455.scaling.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;

import cs455.scaling.util.Helper;

public class SenderThread implements Runnable{

	private boolean running = false;
	private int messageRate = 0;
	private DataOutputStream dos;
	private HashMap<String,byte[]> sent = new HashMap<String,byte[]>();
	private ClientStatistics stats;
	
	private SenderThread(DataOutputStream dos, ClientStatistics stats, int messageRate) throws UnknownHostException, IOException {
		this.dos = dos;
		this.stats = stats;
		this.messageRate = messageRate;
	}

	public static SenderThread createInstance(DataOutputStream dos, ClientStatistics stats, int messageRate) throws UnknownHostException, IOException {
		SenderThread sender = new SenderThread(dos, stats, messageRate);
		return sender;
	}
	
	public void recievedHash(String hash) {
		stats.addMessagesRecieved(1);
		if(sent.containsKey(hash)) {
			sent.remove(hash);
		}
	}
	
	@Override
	public void run() {
		running = true;
		while(running) {
			Random rand = new Random();
			byte[] data = new byte[8000];
			rand.nextBytes(data);
			try {
				String hash = Helper.SHA1FromBytes(data);
				Thread.sleep(1000/messageRate);
				dos.write(data);
				sent.put(hash,data);
				stats.addMessagesSent(1);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
