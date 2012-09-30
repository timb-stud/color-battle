package de.htw.colorbattle.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Observable;
import java.util.Observer;

import com.badlogic.gdx.Gdx;

import de.htw.colorbattle.exception.NetworkException;
import de.htw.colorbattle.utils.SerializeUtils;

/**
 * Service class for WiFi network actions
 */
public class NetworkService extends Observable implements NetworkActionResolver {
	
	private InetAddress mcGroup;
	private MulticastSocket mcSocket;
	private int mcPort;
	private static NetworkService netSvc;
	
	private static final int MAX_UDP_DATAGRAM_LEN = 2048;
	
	/**
	 * Constructor of the class. Opens a multicast socket to a specific address and port
	 * @param mcAddress	multicast address
	 * @param mcPort	multicast port
	 * @throws NetworkException	thrown on network errors
	 */
	private NetworkService(String mcAddress, int mcPort) throws NetworkException{
        try {
        	this.mcPort = mcPort;
			this.mcSocket = new MulticastSocket(mcPort);
			this.mcGroup = InetAddress.getByName(mcAddress);
	        mcSocket.joinGroup(mcGroup);
	        mcSocket.setLoopbackMode(true);
	      //socket.setSoTimeout(1000); //throws timeout exception
	        executeService();
		} catch (Exception e) {
			Gdx.app.error("NetworkService", "Can't create NetworkService", e);
			throw new NetworkException("Can't create NetworkService: \n" + e.toString());
		} 
	}
	
	/**
	 * singleton pattern - needed to create only one instance of network service
	 * @return NetworkService instance
	 */
	public static NetworkService getInstance(String mcAddress, int mcPort) throws NetworkException{
		if( netSvc == null)
			netSvc = new NetworkService(mcAddress, mcPort);
		
		if(mcPort != netSvc.mcPort )
			Gdx.app.debug("NetworkService instance", "HINT: NetworkService will only create once!");
		
		return netSvc;
	}
	
	/**
	 * creates a thread in which service wait to receive messages
	 */
	private void executeService() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					receive(); 
				}
			} 
		}).start(); 
	}
	
	/**
	 * method receives sent NetworkService messages
	 */
	private void receive(){
		byte[] buffer = new byte[MAX_UDP_DATAGRAM_LEN];
		DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
		try {
			mcSocket.receive(receivedPacket);
			Object obj = SerializeUtils.deserializeObject(receivedPacket.getData());
			//Gdx.app.debug("Receiving", "new package from " + receivedPacket.getAddress());
		    setChanged();
		    notifyObservers(obj);
		} catch (IOException e) {
			Gdx.app.error("NetworkService", "Can't receive package", e);
		}
	}
	
	/**
	 * Can send an object to the set multicast address/port
	 */
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

	@Override
	public synchronized void addObserver(Observer observer) {
		super.addObserver(observer);
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startServer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isServer() {
		// TODO Auto-generated method stub
		return false;
	}
}
