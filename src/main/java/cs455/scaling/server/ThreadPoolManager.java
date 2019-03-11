package cs455.scaling.server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import cs455.scaling.client.Client;

public class ThreadPoolManager{

	private final LinkedList<WorkerThread> workers = new LinkedList<>();
	private final LinkedList<LinkedList<Task>> tasks = new LinkedList<>();
	private int batchSize; 
	private int batchTime;
	private long batch;
	private boolean running;
	private boolean distribute = false;
	
	private ThreadPoolManager(int batchsize, int batchtime) {
		running = false;
		this.batchSize = batchsize;
		this.batchTime = batchtime;
	}
	
	public static ThreadPoolManager createInstance(int numThreads, int batchsize, int batchtime) {
		ThreadPoolManager manager = new ThreadPoolManager(batchsize,batchtime);
		manager.createThreads(numThreads);
		return manager;
	}
	
	public synchronized void addTask(Task t) {
		LinkedList<Task> queue;
		int size = tasks.size();
		// create new batch queue
		if(size<1) {
			queue = new LinkedList<Task>();
			queue.add(t);
			tasks.add(queue);
		}else {
			queue = tasks.get(size-1);
			// Batch size has been met create new batch queue
			if(queue.size()>=batchSize) {
				LinkedList<Task> newQueue =  new LinkedList<Task>();
				newQueue.add(t);
				tasks.add(newQueue);
				distribute = true;
			// Add task to existing batch queue
			}else {
				queue.add(t);
			}
		}
	}

	public void addWorkerThread(WorkerThread wt) {
		workers.add(wt);
	}
	
	private void createThreads(int numThreads) {
		for(int i = 0; i  < numThreads; i++) {
			WorkerThread worker = WorkerThread.createInstance(this);
			Thread t = new Thread(worker);
			t.start();
			addWorkerThread(worker);
		}
	}
	
	private void useWorkerThread() {
		if(tasks.size()<1) return;
		// has a queue in tasks
		LinkedList<Task> queue = tasks.getFirst();
		if(!workers.isEmpty()) {
			WorkerThread worker = workers.removeFirst();
			worker.setTask(queue);
			synchronized(worker) {
				worker.notify();
			}
			tasks.removeFirst();
			System.out.println("Used worker thread\tpool size: "+workers.size());
			distribute = false;
			batch = TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		}
	}
	
	public void managePool() {
		running = true;
		batch = TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		while(running) {
			synchronized(this) {
				// batch time has not been met yet
				if(TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)-batch<batchTime) {
					// batch size and time has not been met
					if(!distribute) continue;
					// batch size met, try to use worker thread
					useWorkerThread();
				// batch time has been met
				}else {
					useWorkerThread();
				}
			}
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Server server = new Server();
		server.initialize(5001, 10, 1, 1);
		Client client = new Client();
		client.initialize("localhost", 5001, 1);
		server.getThreadPoolManager().managePool();
	}
}