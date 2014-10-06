package client;

import java.rmi.RemoteException;
import java.util.Random;

import remoteInterface.GameStatus;
import remoteInterface.MoveDirection;
import remoteInterface.Player;

public class GamePlay implements Runnable {

	public int id;
	public GameStatus gameState;

	public GamePlay(int id, GameStatus initGameState) {
		this.id = id;
		this.gameState = initGameState;
	}

	@Override
	public void run() {

		Random rand = new Random();

		System.out.println("Game initializing...");
		this.gameState.print();
		System.out.println("Game starts...");
		
		do {
			try {
				this.move();
				if (this.gameState.backupServer == null) {
					// eg. when no backup server anymore
					break;
				}
				
				System.out.println("DEBUG: primary server " + this.gameState.primaryServer.getId() + ". " + (id == this.gameState.primaryServer.getId() ? "Me" : ""));
				System.out.println("DEBUG: backup server " + this.gameState.backupServer.getId() + ". " + (id == this.gameState.backupServer.getId() ? "Me" : ""));
				
				System.out
						.println(this.gameState.players.get(this.id).totalTreasuresFound
								+ " treasures collected");
				this.gameState.print();
			} catch (RemoteException e) {
//				e.printStackTrace();
			}

			// Give a random delay
			try {
				Thread.sleep(randInt(rand, 200, 3000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} while (this.gameState != null && this.gameState.backupServer != null && this.gameState.numTreasuresLeft > 0);

		System.out.println("====== GAME ENDS ======");
		for (Player p : this.gameState.players) {
			System.out.println("Player #" + p.id + " : " + p.totalTreasuresFound);
		}
		
	}

	private void move() throws RemoteException {
		MoveDirection moveDirection = MoveDirection.getRandDir();
		moveDirection.print();
		try {
			this.gameState = this.gameState.primaryServer.move(this.id, moveDirection);
		} catch (RemoteException e) {
			try {
				this.gameState = this.gameState.backupServer.primaryFailed(this.id, moveDirection);
			} catch (RemoteException ee) {
				return;
			}
		}
	}
	
	private static int randInt(Random rand, int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
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
