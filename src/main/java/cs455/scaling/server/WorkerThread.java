package cs455.scaling.server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class WorkerThread implements Runnable{

	private boolean running = false;
	private Task currentTask = null;
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
					currentTask.completeTask();
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
	
	public synchronized void setTask(Task t) {
		currentTask = t;
	}
	
	private void finishedTask() {
		currentTask = null;
		manager.addWorkerThread(this);
	}

}
