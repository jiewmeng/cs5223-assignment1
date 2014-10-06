package client;

import java.rmi.RemoteException;
import java.util.Scanner;

import remoteInterface.GameStatus;
import remoteInterface.MoveDirection;
import remoteInterface.Player;

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

			// Give a random delay
			// try {
			// Thread.sleep(randInt(rand, 4000, 5000));
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }

		} while (this.gameState != null && this.gameState.backupServer != null
				&& this.gameState.numTreasuresLeft > 0);

		System.out.println("====== GAME ENDS ======");
		for (Player p : this.gameState.players) {
			System.out.println("Player #" + p.id + " : "
					+ p.totalTreasuresFound);
		}

		// Terminate gamePlay and client
		System.exit(0);

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
				this.gameState = this.gameState.backupServer.primaryFailed(
						this.id, moveDirection);
			} catch (RemoteException ee) {
				return;
			}
		}
	}

	public void setGameState(GameStatus gameState) {
		this.gameState = gameState;

		try {
			System.out.println("DEBUG: Updated client gameState. Primary #"
					+ this.gameState.primaryServer.getId() + ". Backup #"
					+ this.gameState.backupServer.getId());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
