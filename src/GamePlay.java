

import java.rmi.RemoteException;
import java.util.Scanner;

public class GamePlay implements Runnable {

	public int id;
	public GameStatus gameState;
	protected Scanner in;

	public GamePlay(int id, GameStatus initGameState, Scanner in) {
		this.id = id;
		this.gameState = initGameState;
		this.in = in;
	}

	@Override
	public void run() {
		System.out.println("Game initializing...");
		this.gameState.print();
		System.out.println("Game starts...");

		do {
			try {
				synchronized (this) {
					this.move();
					if (this.gameState.backupServer == null) {
						// eg. when no backup server anymore
						break;
					}

					System.out
							.println("DEBUG: primary server "
									+ this.gameState.primaryServer.getId()
									+ ". "
									+ (id == this.gameState.primaryServer
											.getId() ? "Me" : ""));
					System.out.println("DEBUG: backup server "
							+ this.gameState.backupServer.getId()
							+ ". "
							+ (id == this.gameState.backupServer.getId() ? "Me"
									: ""));

					System.out
							.println(this.gameState.players.get(this.id).totalTreasuresFound
									+ " treasures collected");
					this.gameState.print();
				}
			} catch (RemoteException e) {
				// e.printStackTrace();
			}

		} while (this.gameState != null && this.gameState.backupServer != null
				&& this.gameState.numTreasuresLeft > 0);

	}

	private void move() throws RemoteException {
		MoveDirection moveDirection;
		// moveDirection = MoveDirection.getRandDir();

		System.out.println("Enter move direction (N, S, E, W, NoMove): ");
		String dirStr = in.nextLine();
		if (dirStr.equalsIgnoreCase("N")) {
			moveDirection = MoveDirection.N;
		} else if (dirStr.equalsIgnoreCase("S")) {
			moveDirection = MoveDirection.S;
		} else if (dirStr.equalsIgnoreCase("E")) {
			moveDirection = MoveDirection.E;
		} else if (dirStr.equalsIgnoreCase("W")) {
			moveDirection = MoveDirection.W;
		} else if (dirStr.equalsIgnoreCase("NoMove")) {
			moveDirection = MoveDirection.NO_MOVE;
		} else {
			System.out.println("Invalid move direction. ");
			return;
		}

		moveDirection.print();
		try {
			this.gameState = this.gameState.primaryServer.move(this.id,
					moveDirection);
		} catch (RemoteException e) {
			try {
				if (this.gameState.backupServer == null) {
					return;
				}
				this.gameState = this.gameState.backupServer.primaryFailed(
						this.id, moveDirection);
			} catch (RemoteException ee) {
				return;
			}
		}
	}

	public void setGameState(GameStatus gameState) {
		this.gameState = gameState;
		
		if (gameState != null && (gameState.numTreasuresLeft <= 0 || gameState.backupServer == null)) {
			System.out.println("====== GAME ENDS ======");
			for (Player p : this.gameState.players) {
				System.out.println("Player #" + p.id + " : "
						+ p.totalTreasuresFound);
			}
			// Terminate
			System.exit(0);
		}

		try {
			System.out.println("DEBUG: Updated client gameState. Primary #"
					+ this.gameState.primaryServer.getId() + ". Backup #"
					+ this.gameState.backupServer.getId());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
