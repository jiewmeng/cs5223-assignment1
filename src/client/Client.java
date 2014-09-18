package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import remoteInterface.IClient;
import remoteInterface.Maze;

public class Client implements IClient {
	
	public int id; // public for simplicity
	
	private Client() throws RemoteException {
		UnicastRemoteObject.exportObject(this, 0);
	}

	public static void main(String[] args) throws RemoteException {

		String host = (args.length < 1) ? null : args[0];
		Client client;
		try {
			client = new Client();
			Registry registry = LocateRegistry.getRegistry(host);
			Maze stub = (Maze) registry.lookup("Maze");
			
			client.id = stub.joinGame(client);
			System.out.println("Client connected with id " + client.id);
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void startGame() {
		System.out.println("should start game ... call moves ...");
	}
}
