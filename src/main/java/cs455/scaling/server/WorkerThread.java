package cs455.scaling.server;

import java.security.NoSuchAlgorithmException;

import cs455.scaling.util.Helper;

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
			if(currentTask == null) continue;
			else {
				synchronized(currentTask) {
					try {
						String hash = Helper.SHA1FromBytes(currentTask.getData());
						currentTask.setHash(hash);
						currentTask.sendHash();
						finishedTask();
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void setTask(Task t) {
		synchronized(currentTask) {
			currentTask = t;
		}
	}
	
	private void finishedTask() {
		currentTask = null;
		manager.addWorkerThread(this);
	}

}
