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
	private DataOutputStream dos;
	private HashMap<String,byte[]> sent = new HashMap<String,byte[]>();
	private ClientStatistics stats;
	
	private SenderThread(DataOutputStream dos, ClientStatistics stats) throws UnknownHostException, IOException {
		this.dos = dos;
		this.stats = stats;
	}

	public static SenderThread createInstance(DataOutputStream dos, ClientStatistics stats) throws UnknownHostException, IOException {
		SenderThread sender = new SenderThread(dos, stats);
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
			byte[] data = new byte[8192];
			rand.nextBytes(data);
			try {
				String hash = Helper.SHA1FromBytes(data);
				dos.write(data);
				sent.put(hash,data);
				stats.addMessagesSent(1);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
