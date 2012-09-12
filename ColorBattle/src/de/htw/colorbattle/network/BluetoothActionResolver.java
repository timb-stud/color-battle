package de.htw.colorbattle.network;

public interface BluetoothActionResolver {
	public void send(Object obj);
	public void connect();
	public void startServer();
}
