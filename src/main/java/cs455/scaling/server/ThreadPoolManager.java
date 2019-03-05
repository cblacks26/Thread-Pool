package cs455.scaling.server;

import java.util.LinkedList;

public class ThreadPoolManager{

	private final LinkedList<WorkerThread> workers = new LinkedList<>();
	private final LinkedList<Task> tasks = new LinkedList<>();
	private boolean running;
	
	private ThreadPoolManager() {
		running = false;
	}
	
	public static ThreadPoolManager createInstance(int numThreads) {
		ThreadPoolManager manager = new ThreadPoolManager();
		manager.createThreads(numThreads);
		return manager;
	}
	
	public void addTask(Task t) {
		synchronized(tasks) {
			tasks.add(t);
		}
	}

	public void addWorkerThread(WorkerThread wt) {
		synchronized(workers) {
			workers.add(wt);
		}
	}
	
	private void createThreads(int numThreads) {
		for(int i = 0; i  < numThreads; i++) {
			WorkerThread worker = WorkerThread.createInstance(this);
			Thread t = new Thread(worker);
			t.start();
			workers.add(worker);
		}
	}
	
	public void managePool() {
		running = true;
		while(running) {
			synchronized(tasks) {
				if(!tasks.isEmpty()) {
					synchronized(workers) {
						if(!workers.isEmpty()) {
							workers.removeFirst().setTask(tasks.removeFirst());
							// commit a worker thread for a class
						}else {
							continue;
						}
					}
				}else {
					continue;
				}
			}
		}
	}
	
}
