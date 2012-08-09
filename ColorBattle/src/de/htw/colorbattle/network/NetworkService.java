package de.htw.colorbattle.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.badlogic.gdx.Gdx;

import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.gameobjects.Player;
import de.htw.colorbattle.utils.SerializeUtils;

public class NetworkService {
	
	private InetAddress mcGroup;
	private MulticastSocket mcSocket;
	private int mcPort;
	
	private static final int MAX_UDP_DATAGRAM_LEN = 1024;
	
	public NetworkService(String mcAddress, int mcPort) throws NetworkException{
        try {
        	this.mcPort = mcPort;
			this.mcSocket = new MulticastSocket(mcPort);
			this.mcGroup = InetAddress.getByName(mcAddress);
	        mcSocket.joinGroup(mcGroup);
	      //socket.setSoTimeout(1000); //throws timeout exception
	        executeService();
		} catch (Exception e) {
			Gdx.app.error("NetworkService", "Can't create NetworkService", e);
			throw new NetworkException("Can't create NetworkService: \n" + e.toString());
		} 
	}
	
	private void executeService() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					receive(); 
				}
			} 
		}).start(); 
	}
	
	private void receive(){
		byte[] buffer = new byte[MAX_UDP_DATAGRAM_LEN];
		DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
		try {
			mcSocket.receive(receivedPacket);
			Object obj = SerializeUtils.deserializeObject(receivedPacket.getData());
			
//			Gdx.app.debug("Receiving", "new package from " + receivedPacket.getAddress());
			Gdx.app.debug("Player Info", ((PlayerMsg)obj).toString());
			
			//TODO: handle obj (network architecture)
//			throw new RuntimeException("Methode not complete implemented");
		} catch (IOException e) {
			Gdx.app.error("NetworkService", "Can't receive package", e);
		}
	}
	
	public void send(Object obj) throws NetworkException{
		try {
			byte[] msg = SerializeUtils.serializeObject(obj);
			DatagramPacket data = new DatagramPacket(msg, 0, msg.length, mcGroup, mcPort);
			mcSocket.send(data);
		} catch (Exception e) {
			Gdx.app.error("NetworkService", "Can't send message", e);
			throw new NetworkException("Can't send message: \n" + e.toString());
		}
	}

}
