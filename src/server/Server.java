package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import remoteInterface.Coordinates;
import remoteInterface.GameStatus;
import remoteInterface.IClient;
import remoteInterface.IServer;
import remoteInterface.MoveDirection;
import remoteInterface.Player;

public class Server implements IServer {

	// TODO: Configure these
	public static final int WAIT_FOR_PLAYERS_IN_SECONDS = 10;
	public static final int DEFAULT_GRID_SIZE = 3;
	public static final int DEFAULT_NUM_TREASURES = 4;

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
		this.serverGameStatus.numTreasuresLeft = this.serverGameStatus.numTreasures;
	}

	protected void startGame() throws RemoteException {

		// Init grid with treasures
		this.serverGameStatus.treasuresGrid = Util.initTreasuresGrid(
				this.serverGameStatus.gridSize,
				this.serverGameStatus.numTreasures);

		// Init player positions
		this.serverGameStatus.players = Util.initPlayers(
				this.serverGameStatus.gridSize, this.clients);

		// Init player grid
		this.serverGameStatus.playersGrid = Util.initPlayersGrid(
				this.serverGameStatus.gridSize, this.serverGameStatus.players);

		// Print game status
		this.serverGameStatus.print();

		// Assign treasures if they are at player's init position
		for (Player p : this.serverGameStatus.players) {
			this.move(p.id, MoveDirection.NO_MOVE);
		}

		this.isGameStarted = true;
		announceStartGame(this.serverGameStatus);
	}

	protected void announceStartGame(GameStatus initGameStatus) throws RemoteException {
		for (IClient client : this.clients) {
			client.startGame(initGameStatus);
		}
	}

	@Override
	public synchronized int joinGame(IClient client) throws RemoteException {
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
	public synchronized GameStatus move(int clientId, MoveDirection moveDirection)
			throws RemoteException {

		// Game has ended
		if (this.serverGameStatus.numTreasuresLeft <= 0) {
			return this.serverGameStatus;
		}
		
		/*
		 * Grid coordinates
		 * 	  y0 y1 y2 . . .
		 * x0
		 * x1
		 * x2
		 * .
		 * .
		 * .
		 */
		Player player = this.serverGameStatus.players.get(clientId);
		Coordinates oldCoord = player.coordinates;
		Coordinates newCoord = new Coordinates(oldCoord.x, oldCoord.y);
		switch (moveDirection) {
		case N:
			newCoord.x -= 1;
			break;
		case S:
			newCoord.x += 1;
			break;
		case E:
			newCoord.y += 1;
			break;
		case W:
			newCoord.y -= 1;
			break;
		case NO_MOVE:
			break;
		default:
			break;
		}

		if (newCoord.x < 0 || newCoord.x >= this.serverGameStatus.gridSize
				|| newCoord.y < 0
				|| newCoord.y >= this.serverGameStatus.gridSize) {
			// out of grid bounds
			newCoord = oldCoord;
		} else if (this.serverGameStatus.playersGrid[newCoord.x][newCoord.y] >= 0) {
			// new coord contains another player
			newCoord = oldCoord;
		}

		player.coordinates = newCoord;
		player.newTreasuresFound = this.serverGameStatus.treasuresGrid[newCoord.x][newCoord.y];
		player.totalTreasuresFound += player.newTreasuresFound;

		this.serverGameStatus.treasuresGrid[newCoord.x][newCoord.y] = 0;
		this.serverGameStatus.numTreasuresLeft -= player.newTreasuresFound;

		// update game status
		this.serverGameStatus.playersGrid[oldCoord.x][oldCoord.y] = -1;
		this.serverGameStatus.playersGrid[newCoord.x][newCoord.y] = clientId;
		player.coordinates = newCoord;
		
		System.out.print("Player " + clientId + " ");
		moveDirection.print();
		this.serverGameStatus.print();

		return this.serverGameStatus;
	}
}
