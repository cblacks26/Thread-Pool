package cs455.scaling.server;

import java.io.IOException;

public class Server {

	private CommunicationThread communication;
	private ThreadPoolManager manager;
	
	public static void main(String[] args) {
		if(args.length!=4) {
			System.out.println("Must speciy portnum thread-pool-size batch-size batch-time");
		}
		int port = Integer.parseInt(args[0]);
		int poolSize = Integer.parseInt(args[1]);
		int batchSize = Integer.parseInt(args[2]);
		int batchTime = Integer.parseInt(args[3]);
		Server server = new Server();
		server.initialize(port, poolSize, batchSize, batchTime);
		server.getThreadPoolManager().managePool();
	}
	
	public void initialize(int port, int poolSize, int batchSize, int batchTime) {
		try {
			communication = CommunicationThread.createInstance(this,port);
			Thread t2 = new Thread(communication);
			t2.start();
		} catch (IOException e) {
			System.out.println("IOEcpetion creating new CommunicationThread: "+e.getStackTrace());
		}
		manager = ThreadPoolManager.createInstance(poolSize);
	}
	
	public CommunicationThread getCommunicationThread() {
		return communication;
	}
	
	public ThreadPoolManager getThreadPoolManager() {
		return manager;
	}
	
}
