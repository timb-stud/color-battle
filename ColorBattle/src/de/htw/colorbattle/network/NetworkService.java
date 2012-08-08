package de.htw.colorbattle.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.badlogic.gdx.Gdx;

import de.htw.colorbattle.exception.NetworkException;

public class NetworkService {
	
	private InetAddress mcGroup;
	private MulticastSocket mcSocket;
	private int mcPort;
	
	public NetworkService(String mcAddress, int mcPort) throws NetworkException{
        try {
			mcSocket = new MulticastSocket(mcPort);
			mcGroup = InetAddress.getByName(mcAddress);
		} catch (Exception e) {
			Gdx.app.error("NetworkService", "Can't create NetworkService", e);
			throw new NetworkException("Can't create NetworkService: \n" + e.toString());
		} 
	}

}
