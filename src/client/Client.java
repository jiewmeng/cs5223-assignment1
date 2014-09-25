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

	public int id; // public for simplicity
	public IServer iServer = null;

	private Client() throws RemoteException {
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
		
		GameStatus gameStatus;

		System.out.println("Game starts...");
		do {
			gameStatus = this.move();
			gameStatus.print();
			// TODO Give a random delay 
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} while (gameStatus.numTreasuresLeft > 0);
		
	}

	private GameStatus move() throws RemoteException {
		MoveDirection moveDirection = MoveDirection.getRandDir();
		moveDirection.print();
		return this.iServer.move(this.id, moveDirection);
	}

}
