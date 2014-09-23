package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import remoteInterface.GameStatus;
import remoteInterface.IClient;
import remoteInterface.IServer;
import remoteInterface.MoveDirection;

public class Server implements IServer {

	public static final int WAIT_FOR_PLAYERS_IN_SECONDS = 20;
	public static final int DEFAULT_GRID_SIZE = 5;
	public static final int DEFAULT_NUM_TREASURES = 10;

	protected int nextId;
	protected Vector<IClient> clients;
	protected boolean isGameStarted;
	protected GameStatus serverGameStatus;

	public Server() {
		this.nextId = 0;
		this.clients = new Vector<IClient>();
		this.isGameStarted = false;
		this.serverGameStatus = new GameStatus();
	}

	public static void main(String args[]) {
		IServer stub = null;
		Registry registry = null;
		
		try {
			Server server = new Server();
			stub = (IServer) UnicastRemoteObject.exportObject(server, 0);
			registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			registry.bind("Maze", stub);

			// Set N and M
			server.initGridParam(args);

			System.err.println("Server ready");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				registry.unbind("Maze");
				registry.bind("Maze", stub);
				System.err.println("Server ready");
			} catch (Exception ee) {
				System.err.println("Server exception: " + ee.toString());
				ee.printStackTrace();
			}
		}
	}

	private void initGridParam(String[] args) {
		if (args.length >= 2) {
			this.serverGameStatus.gridSize = Integer.parseInt(args[0]);
			this.serverGameStatus.numTreasures = Integer.parseInt(args[1]);
		} else {
			this.serverGameStatus.gridSize = Server.DEFAULT_GRID_SIZE;
			this.serverGameStatus.numTreasures = Server.DEFAULT_NUM_TREASURES;
		}
	}

	protected void startGame() throws RemoteException {

		// Init grid with treasures
		this.serverGameStatus.treasures = Util.initRandomGrid(
				this.serverGameStatus.gridSize,
				this.serverGameStatus.numTreasures);

		// Print treasures
		Util.printTreasureInfo(this.serverGameStatus.gridSize,
				this.serverGameStatus.treasures);

		// Init player positions
		this.serverGameStatus.players = Util.initPlayers(
				this.serverGameStatus.gridSize, this.clients);

		// Print players
		Util.printPlayerInfo(this.serverGameStatus.gridSize,
				this.serverGameStatus.players);

		// TODO Assign treasures if they are at player's init position

		this.isGameStarted = true;
		announceStartGame();
	}

	protected void announceStartGame() throws RemoteException {
		for (IClient client : this.clients) {
			client.startGame();
		}
	}

	@Override
	public int joinGame(IClient client) throws RemoteException {
		// once game is started, no new connections are accepted
		if (this.isGameStarted) {
			System.out.println("Server rejected client. Game started.");
			return -1;
		}

		// if is first client connecting, initialize timer that starts game
		// after 20s
		if (nextId == 0) {
			System.out
					.println("Server received first connection. Starting timer of "
							+ WAIT_FOR_PLAYERS_IN_SECONDS + "s");

			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						startGame();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}

			}, WAIT_FOR_PLAYERS_IN_SECONDS * 1000);
		}

		int index = nextId;
		this.clients.add(index, client);
		System.out.println("Server accepted client " + index);
		nextId++;
		return index;
	}

	@Override
	public GameStatus move(int clientId, MoveDirection moveDirection)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}
