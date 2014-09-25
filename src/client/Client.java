package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import remoteInterface.GameStatus;
import remoteInterface.IClient;
import remoteInterface.IServer;
import remoteInterface.MoveDirection;

public class Client implements IClient {

	public int id;
	public IServer iServer = null;

	public Client() throws RemoteException {
		UnicastRemoteObject.exportObject(this, 0);
	}

	public static void main(String[] args) throws RemoteException {

		String host = (args.length < 1) ? null : args[0];
		Client client;
		try {
			client = new Client();
			Registry registry = LocateRegistry.getRegistry(host);
			IServer stub = (IServer) registry.lookup("Maze");

			client.id = stub.joinGame(client);
			if (client.id == -1) {
				System.out
						.println("Server rejected client. Game has already started.");
			} else {
				client.iServer = stub;
				System.out.println("Client connected with id " + client.id);
			}
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void startGame() throws RemoteException {
		
		Thread t = new Thread(new GamePlay(this.id, this.iServer));
		t.start();
	}

}
