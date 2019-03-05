package cs455.scaling.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Task {

	private byte[] data;
	private SelectionKey key;
	private String hash = null;

	private Task(byte[] data, SelectionKey key) {
		this.data = Arrays.copyOf(data, data.length);
		this.key = key;
	}
	
	public static Task createInstance(byte[] data, SelectionKey key) {
		Task t = new Task(data, key);
		return t;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public SelectionKey getKey() {
		return key;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	private String getHash() {
		return hash;
	}
	
	public void sendHash() {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.wrap(getHash().getBytes());
		try {
			channel.write(buffer);
			buffer.clear();
		} catch (IOException e) {
			System.out.println("IOException sending hash back to client: "+ e.getStackTrace());
		}
	}
}
