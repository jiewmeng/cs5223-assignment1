import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Server implements IServer {
	// TODO: Configure these
	public static final int WAIT_FOR_PLAYERS_IN_SECONDS = 20;
	public static final int DEFAULT_GRID_SIZE = 5;
	public static final int DEFAULT_NUM_TREASURES = 10;
	public static final int PING_TIMER_IN_SECONDS = 10; // ping between primary
														// / backup servers

	private int id;
	protected int nextId;
	protected Vector<IClient> clients;
	protected GameStatus serverGameStatus;
	protected boolean primaryFailed = false;
	protected boolean isBackup = false;
	protected Timer pingTimer;

	public Server() {
		this.nextId = 0;
		this.clients = new Vector<IClient>();
		this.serverGameStatus = new GameStatus();
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	protected void initGridParam(String[] args) {
		if (args.length >= 2) {
			this.serverGameStatus.gridSize = Integer.parseInt(args[0]);
			this.serverGameStatus.numTreasures = Integer.parseInt(args[1]);
		} else {
			this.serverGameStatus.gridSize = Server.DEFAULT_GRID_SIZE;
			this.serverGameStatus.numTreasures = Server.DEFAULT_NUM_TREASURES;
		}
		this.serverGameStatus.numTreasuresLeft = this.serverGameStatus.numTreasures;
	}

	/**
	 * Init game state, start game
	 */
	private void startGame() throws RemoteException {

		System.out.println("Start game");

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
		this.serverGameStatus.primaryServer = this;
		this.serverGameStatus.print();

		// choose a backup server
		this.chooseBackup();

		// Assign treasures if they are at player's init position
		for (Player p : this.serverGameStatus.players) {
			this.move(p.id, MoveDirection.NO_MOVE);
		}

		this.serverGameStatus.isGameStarted = true;
		announceStartGame(this.serverGameStatus);
	}

	/**
	 * Chooses a backup server. Excludes self and previously non-responsive
	 * nodes
	 * 
	 * @return true if successfully choose a backup server
	 * @throws RemoteException
	 */
	private void chooseBackup() throws RemoteException {
		System.out.println("Choosing backup. Clients length: "
				+ this.clients.size());
		boolean success = false;
		IServer candidateServer;
		for (int i = 0; i < this.clients.size(); i++) {

			try {
				candidateServer = this.clients.get(i).getIClientServer();
			} catch (RemoteException e) {
				// client/server dead, try others
				continue;
			}

			// some validation
			if (i == this.id || candidateServer == null) {
				// candidate cannot be self or null
				// (indicating a previously non-responsive server)
				System.out.println("Choosing backup ... skipping #" + i);
				continue;
			}

			try {
				success = candidateServer.makeBackup(serverGameStatus,
						this.clients);
				if (!success)
					continue;
				serverGameStatus.backupServer = candidateServer;
				System.out.println("Choosing backup ... choose " + i
						+ " as backup server.");

				// broadcast to inform clients of new backup
				for (IClient p : this.clients) {
					try {
						p.updateGameState(serverGameStatus);
					} catch (RemoteException ee) {
						// perhaps client is down, do nothing
					}
				}

				// init ping backup
				this.initPingBackup();

				return;
			} catch (RemoteException e) {
				System.err.println("Choosing backup ... failed to make #" + i
						+ " a backup server");
			}
		}

		synchronized (this) {
			System.out
					.println("Choosing backup ... failed to choose a backup server");

			// Only client of itself left, inform to terminate
			serverGameStatus.backupServer = null;
			this.clients.get(this.id).updateGameState(serverGameStatus);
		}

	}

	private void announceStartGame(GameStatus initGameStatus)
			throws RemoteException {
		for (IClient client : this.clients) {
			client.startGame(initGameStatus);
		}
	}

	@Override
	public synchronized int joinGame(IClient iClient) throws RemoteException {
		// once game is started, no new connections are accepted
		if (this.serverGameStatus.isGameStarted) {
			System.out.println("Server rejected client. Game started.");
			return -1;
		}

		// if is first client connecting, initialize timer that starts game
		// after 20s
		if (nextId == 0) {
			System.out
					.println("Server received first connection. Starting timer of "
							+ WAIT_FOR_PLAYERS_IN_SECONDS + "s");

			// add self to game too
			// this.clients.add(0, (IClient)this);
			// nextId++;
			// System.out.println("Server joined game with id 0");

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
		this.clients.add(index, iClient);
		System.out.println("Server accepted client " + index);
		nextId++;
		return index;
	}

	@Override
	public synchronized GameStatus move(int clientId,
			MoveDirection moveDirection) throws RemoteException {

		// Game has ended
		if (this.serverGameStatus.numTreasuresLeft <= 0) {
			return this.serverGameStatus;
		}

		/*
		 * Grid coordinates y0 y1 y2 . . . x0 x1 x2 . . .
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

		if (this.id == this.serverGameStatus.primaryServer.getId()) {

			// broadcast end game status
			if (this.serverGameStatus.numTreasuresLeft <= 0) {
				for (int i = this.clients.size() - 1; i >= 0; i--) {
					// broadcast in reverse order so that self is the last one
					// to kill
					try {
						this.clients.get(i).updateGameState(serverGameStatus);
					} catch (RemoteException ee) {
						// perhaps client is down, do nothing
					}
				}

			} else {
				// update backup
				try {
					if (this.serverGameStatus.backupServer == null) {
						return this.serverGameStatus;
					}
					this.serverGameStatus.backupServer.move(clientId,
							moveDirection);
				} catch (RemoteException e) {
					// backup server failed ... choose a new one
					this.chooseBackup();
				}
			}
		}

		return this.serverGameStatus;
	}

	/**
	 * Promotes a normal client to a backup server Only allowed to call once
	 * when making backup server
	 * 
	 * @return true to acknowledge
	 */
	@Override
	public boolean makeBackup(GameStatus gameState, Vector<IClient> clients)
			throws RemoteException {

		if (!this.isBackup) {
			System.out.println("I am now backup server.");
			this.isBackup = true;
			this.serverGameStatus = gameState;
			this.clients = clients;
			this.initPingPrimary();
			return true;
		}
		return false;
	}

	/**
	 * Backup server receives this from clients notifying it clients cannot
	 * connect to primary server
	 * 
	 * @return GameStatus
	 */
	@Override
	public synchronized GameStatus primaryFailed(int clientId,
			MoveDirection moveDirection) throws RemoteException {
		
		try {
			// verify
			this.serverGameStatus.primaryServer.getId();
			
		} catch (RemoteException e) {
			// truly failed
			this.primaryFailed();
			// process move
			return this.move(clientId, moveDirection);
		}
		
		// primary still alive
		return this.serverGameStatus.primaryServer.move(clientId, moveDirection);
	}

	private void initPingPrimary() {
		pingTimer = new Timer();
		pingTimer.scheduleAtFixedRate(new PingPrimaryServerTimer(
				this.serverGameStatus.primaryServer),
				PING_TIMER_IN_SECONDS * 1000, PING_TIMER_IN_SECONDS * 1000);
	}

	private void initPingBackup() {
		if (pingTimer != null) {
			pingTimer.cancel();
		}
		pingTimer = new Timer();
		pingTimer.scheduleAtFixedRate(new PingBackupServerTimer(
				this.serverGameStatus.backupServer),
				PING_TIMER_IN_SECONDS * 1000, PING_TIMER_IN_SECONDS * 1000);
	}

	private class PingPrimaryServerTimer extends TimerTask {

		protected IServer primary;

		public PingPrimaryServerTimer(IServer primary) {
			this.primary = primary;
		}

		@Override
		public void run() {
			try {
				System.out.println("Pinging primary server ... ");
				this.primary.getId();
			} catch (RemoteException e) {
				try {
					System.out
							.println("Primary server non-responsive ... make myself primary");
					primaryFailed();
				} catch (RemoteException e1) {
					System.err
							.println("Primary server non-responsive ... failed to make myself primary");
					e1.printStackTrace();
				}
			}
		}

	}

	private class PingBackupServerTimer extends TimerTask {

		protected IServer backup;

		public PingBackupServerTimer(IServer backup) {
			this.backup = backup;
		}

		@Override
		public void run() {
			try {
				System.out.println("Pinging backup server ... ");
				this.backup.getId();
			} catch (RemoteException e) {
				try {
					System.out
							.println("Backup server non-responsive ... choosing a new backup");
					chooseBackup();
				} catch (RemoteException e1) {
					System.err
							.println("Failed to choose a new backup after old backup failed");
				}
			}
		}

	}

	private void primaryFailed() throws RemoteException {

		// first time receiving notification
		if (!primaryFailed && isBackup) {
			this.serverGameStatus.primaryServer = this;

			// stop pinging old primary server
			this.pingTimer.cancel();

			// choose a new backup
			this.chooseBackup();

			primaryFailed = true;
			System.out.println("I am now primary server");
		}

	}

}
