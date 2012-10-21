package de.htw.colorbattle.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

import com.badlogic.gdx.Gdx;

import de.htw.colorbattle.exception.NetworkException;

public class TCPService extends Observable implements NetworkActionResolver {
	
	ServerSocket serverSocket;
	int port;
	String sendAddress;
	
	public TCPService(String sendAddress, int port) throws NetworkException{
		try{
			this.sendAddress = sendAddress;
			this.port = port;
			serverSocket = new ServerSocket(port);
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
        try{
           Socket connectionSocket = serverSocket.accept();
           ObjectInputStream ois=
              new ObjectInputStream(connectionSocket.getInputStream());
           Object obj = ois.readObject();
           Gdx.app.debug("Receiving", "new package received!");
		   setChanged();
		   notifyObservers(obj);
//		   connectionSocket.close();
//		   ois.close();
	    } catch (UnknownHostException e) {
	        e.printStackTrace();
	        Gdx.app.error("NetworkService", "Unknown host", e);
	    } catch (IOException e) {
	        e.printStackTrace();
	        Gdx.app.error("NetworkService", "Can't receive package", e);
	    } catch (ClassNotFoundException e) {
	    	 Gdx.app.error("NetworkService", "Unknown object", e);
			e.printStackTrace();
		} 
	}

	@Override
	public void send(Object obj) throws NetworkException{
	    try {
	    	Socket clientSocket = new Socket(sendAddress, port);
	    	OutputStream os = clientSocket.getOutputStream();  
	    	ObjectOutputStream oos = new ObjectOutputStream(os);  
	    	oos.writeObject(obj);  
	    	Gdx.app.debug("Sending", "send object!");
	    	oos.close();  
	    	os.close();  
	    	clientSocket.close(); 
	    } catch (UnknownHostException e) {
	        e.printStackTrace();
	        Gdx.app.error("NetworkService", "Unknown host", e);
	    } catch (IOException e) {
	        e.printStackTrace();
	        Gdx.app.error("NetworkService", "IOException", e);
	    } 
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
