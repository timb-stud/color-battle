package de.htw.colorbattle.network;

import java.util.Observer;

import de.htw.colorbattle.exception.NetworkException;

public interface NetworkActionResolver {
	public void send(Object obj)  throws NetworkException;;
	public void connect();
	public void startServer();
	public boolean isServer();
	
	public void addObserver(Observer observer);
}
