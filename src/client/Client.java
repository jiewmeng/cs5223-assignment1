package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

import remoteInterface.GameStatus;
import remoteInterface.IClient;
import remoteInterface.IServer;
import server.Server;

public class Client implements IClient {

	public int id;
	public IServer iServer = null; // remote server
	private Server clientServer = new Server(); // client attached server
	public GameStatus gameState;

	public Client() throws RemoteException {
		UnicastRemoteObject.exportObject(this, 0);
	}

	public static void main(String args[]) throws RemoteException {

		Client client = new Client();

		try {
			client.createClient(null);
		} catch (RemoteException e) {
			client.createClientServer(args);
			try {
				client.createClient(null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

	}

	private void createClient(String host) throws RemoteException,
			NotBoundException {

		Registry registry = LocateRegistry.getRegistry(host);
		IServer stub = (IServer) registry.lookup("Maze");

		this.id = stub.joinGame(this);

		if (this.id == -1) {
			System.out
					.println("Server rejected client. Game has already started.");
		} else {
			this.iServer = stub;
			this.clientServer.setId(this.id);
			System.out.println("Client connected with id " + this.id);
		}
	}

	private void createClientServer(String args[]) {

		IServer stub = null;
		Registry registry = null;

		clientServer.initGridParam(args);
		try {
			try {
				stub = (IServer) UnicastRemoteObject.exportObject(clientServer, 0);
			} catch (ExportException e) {
				// likely here because its main server. Can skip exporting
				// itself
				stub = (IServer) UnicastRemoteObject.toStub(clientServer);
			}

			registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			registry.bind("Maze", stub);

			System.err.println("Server ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void startGame(GameStatus initGameStatus) throws RemoteException {
		this.gameState = initGameStatus;
		Thread t = new Thread(new GamePlay(this.id, this));
		t.start();
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public IServer getIClientServer() throws RemoteException {
		IServer stub = null;
		try {
			stub = (IServer) UnicastRemoteObject.exportObject(clientServer, 0);
		} catch (ExportException e) {
			stub = (IServer) UnicastRemoteObject.toStub(clientServer);
		}
		return stub;
	}

	@Override
	public void updateGameState(GameStatus gameState) throws RemoteException {
		this.gameState = gameState;
		System.out.println("DEBUG: Updated client gameState. Primary #"
				+ this.gameState.primaryServer.getId() + ". Backup #"
				+ this.gameState.backupServer.getId());
	}

}
