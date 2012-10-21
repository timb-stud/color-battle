package de.htw.colorbattle.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

import com.badlogic.gdx.Gdx;

import de.htw.colorbattle.exception.NetworkException;

public class TCPService extends Observable implements NetworkActionResolver {
	
	ServerSocket socket;
	
	public TCPService(int port) throws NetworkException{
		try{
			socket = new ServerSocket(port);
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
           Socket connectionSocket = socket.accept();
           ObjectInputStream ois=
              new ObjectInputStream(connectionSocket.getInputStream());
           Object obj = ois.readObject();
		   setChanged();
		   notifyObservers(obj);
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
			  String sentence;
			  String modifiedSentence;
			  BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
			  Socket clientSocket = new Socket("localhost", 6789);
			  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			  BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			  sentence = inFromUser.readLine();
			  outToServer.writeBytes(sentence + '\n');
			  modifiedSentence = inFromServer.readLine();
			  System.out.println("FROM SERVER: " + modifiedSentence);
			  clientSocket.close();
	    } catch (UnknownHostException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
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
