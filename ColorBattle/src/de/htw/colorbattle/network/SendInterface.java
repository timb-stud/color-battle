package de.htw.colorbattle.network;

import java.util.Observer;

import de.htw.colorbattle.exception.NetworkException;

public interface SendInterface {
	public void send(Object obj) throws NetworkException;
	public void addObserver(Observer observer);
}
