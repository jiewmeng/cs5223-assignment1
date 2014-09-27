package client;

import java.rmi.RemoteException;
import java.util.Random;

import remoteInterface.GameStatus;
import remoteInterface.IServer;
import remoteInterface.MoveDirection;

public class GamePlay implements Runnable {

	public int id;
	public IServer iServer = null;
	public GameStatus initGameStatus;

	public GamePlay(int id, IServer iServer, GameStatus initGameStatus) {
		this.id = id;
		this.iServer = iServer;
		this.initGameStatus = initGameStatus;
	}

	@Override
	public void run() {

		GameStatus gameStatus = null;
		Random rand = new Random();

		System.out.println("Game initializing...");
		initGameStatus.print();
		System.out.println("Game starts...");
		
		do {
			try {
				gameStatus = this.move();
				System.out
						.println(gameStatus.players.get(this.id).totalTreasuresFound
								+ " treasures collected");
				gameStatus.print();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			// Give a random delay
			try {
				Thread.sleep(randInt(rand, 1, 300));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} while (gameStatus != null && gameStatus.numTreasuresLeft > 0);

	}

	private GameStatus move() throws RemoteException {
		MoveDirection moveDirection = MoveDirection.getRandDir();
		moveDirection.print();
		return this.iServer.move(this.id, moveDirection);
	}
	
	private static int randInt(Random rand, int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}
}
