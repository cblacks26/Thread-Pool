package cs455.scaling.server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

public class WorkerThread implements Runnable{

	private boolean running = false;
	private LinkedList<Task> queue = null;
	private ThreadPoolManager manager;
	
	private WorkerThread(ThreadPoolManager tpm) {
		manager = tpm;
	}
	
	public static WorkerThread createInstance(ThreadPoolManager tpm) {
		WorkerThread worker = new WorkerThread(tpm);
		return worker;
	}
	
	@Override
	public void run() {
		running = true;
		while(running) {
			synchronized(this) {
				try {
					wait();
					Task current;
					int sent = 0;
					while(!queue.isEmpty()) {
						 current = queue.removeFirst();
						 current.completeTask();
					}
					finishedTask();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized void setTask(LinkedList<Task> work) {
		queue = work;
	}
	
	private void finishedTask() {
		queue = null;
		manager.addWorkerThread(this);
	}

}
