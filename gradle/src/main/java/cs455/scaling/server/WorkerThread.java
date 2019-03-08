package cs455.scaling.server;

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
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			System.out.println("Thread woken up");
			synchronized(currentTask) {
				currentTask.completeTask();
				finishedTask();
			}
		}
	}
	
	public void setTask(Task t) {
		currentTask = t;
	}
	
	private void finishedTask() {
		System.out.println("Finished task");
		currentTask = null;
		manager.addWorkerThread(this);
	}

}
