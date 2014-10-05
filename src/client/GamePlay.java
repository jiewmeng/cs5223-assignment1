package client;

import java.rmi.RemoteException;
import java.util.Random;

import remoteInterface.GameStatus;
import remoteInterface.IServer;
import remoteInterface.MoveDirection;

public class GamePlay implements Runnable {

	public int id;
	public GameStatus gameState;

	public GamePlay(int id, IServer iServer, GameStatus initGameStatus) {
		this.id = id;
		this.gameState = initGameStatus;
	}

	@Override
	public void run() {

		Random rand = new Random();

		System.out.println("Game initializing...");
		gameState.print();
		System.out.println("Game starts...");
		
		do {
			try {
				this.move();
				if (gameState == null) {
					// eg. when no backup server anymore
					break;
				}
				
				System.out.println("DEBUG: primary server " + gameState.primaryServer.getId());
				System.out.println("DEBUG: backup server " + gameState.backupServer.getId());
				
				System.out
						.println(gameState.players.get(this.id).totalTreasuresFound
								+ " treasures collected");
				gameState.print();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			// Give a random delay
			try {
				Thread.sleep(randInt(rand, 200, 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} while (gameState != null && gameState.numTreasuresLeft > 0);

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
}
