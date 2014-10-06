package client;

import java.rmi.RemoteException;
import java.util.Random;

import remoteInterface.MoveDirection;
import remoteInterface.Player;

public class GamePlay implements Runnable {

	public int id;
	public Client client;

	public GamePlay(int id, Client client) {
		this.id = id;
		this.client = client;
	}

	@Override
	public void run() {

		Random rand = new Random();

		System.out.println("Game initializing...");
		client.gameState.print();
		System.out.println("Game starts...");
		
		do {
			try {
				this.move();
				if (client.gameState == null) {
					// eg. when no backup server anymore
					break;
				}
				
				System.out.println("DEBUG: primary server " + client.gameState.primaryServer.getId() + ". " + (id == client.gameState.primaryServer.getId() ? "Me" : ""));
				System.out.println("DEBUG: backup server " + client.gameState.backupServer.getId() + ". " + (id == client.gameState.backupServer.getId() ? "Me" : ""));
				
				System.out
						.println(client.gameState.players.get(this.id).totalTreasuresFound
								+ " treasures collected");
				client.gameState.print();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			// Give a random delay
			try {
				Thread.sleep(randInt(rand, 200, 3000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} while (client.gameState != null && client.gameState.numTreasuresLeft > 0);

		System.out.println("====== GAME ENDS ======");
		for (Player p : client.gameState.players) {
			System.out.println("Player #" + p.id + " : " + p.totalTreasuresFound);
		}
		
		
	}

	private void move() throws RemoteException {
		MoveDirection moveDirection = MoveDirection.getRandDir();
		moveDirection.print();
		try {
			this.client.gameState = this.client.gameState.primaryServer.move(this.id, moveDirection);
		} catch (RemoteException e) {
			try {
				this.client.gameState = this.client.gameState.backupServer.primaryFailed(this.id, moveDirection);
			} catch (RemoteException ee) {
				this.client.gameState = null;
				return;
			}
		}
	}
	
	private static int randInt(Random rand, int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}
}
