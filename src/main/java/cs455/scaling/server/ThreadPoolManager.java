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
			System.out.println("Tasks: "+tasks.size());
		}
	}

	public void addWorkerThread(WorkerThread wt) {
		synchronized(workers) {
			workers.add(wt);
			System.out.println("Workers: "+workers.size());
		}
	}
	
	private void createThreads(int numThreads) {
		for(int i = 0; i  < numThreads; i++) {
			WorkerThread worker = WorkerThread.createInstance(this);
			Thread t = new Thread(worker);
			t.start();
			addWorkerThread(worker);
		}
	}
	
	public void managePool() {
		running = true;
		while(running) {
			if(!tasks.isEmpty()) {
				synchronized(workers) {
					if(!workers.isEmpty()) {
						System.out.println("Designating worker thread to task");
						WorkerThread worker = workers.removeFirst();
						worker.setTask(tasks.removeFirst());
						worker.notify();
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
