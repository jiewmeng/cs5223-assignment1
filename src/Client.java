

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Client implements IClient {

	private static final String HOST = null;
	
	public int id;
	public IServer iServer = null; // remote server
	private Server clientServer = new Server(); // client attached server
	public GamePlay gamePlay;
	public Scanner in;

	public Client() throws RemoteException {
		this.in = new Scanner(System.in);
		UnicastRemoteObject.exportObject(this, 0);
	}

	public static void main(String args[]) throws RemoteException {

		Client client = new Client();

		try {
			client.createClient(HOST);
		} catch (RemoteException e) {
			client.createClientServer(args);
			try {
				client.createClient(HOST);
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
			this.gamePlay = new GamePlay(this.id, new GameStatus(), this.in);
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
	public void startGame(GameStatus initGameState) throws RemoteException {
		this.gamePlay.setGameState(initGameState);
		Thread t = new Thread(this.gamePlay);
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
		this.gamePlay.setGameState(gameState);
	}

}
