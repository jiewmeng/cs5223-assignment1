package client;

import java.rmi.RemoteException;

import remoteInterface.GameStatus;
import remoteInterface.IServer;
import remoteInterface.MoveDirection;

public class GamePlay implements Runnable{

	public int id;
	public IServer iServer = null;

	public GamePlay(int id, IServer iServer) {
		this.id = id;
		this.iServer = iServer;
	}

	@Override
	public void run() {

		GameStatus gameStatus = null;

		System.out.println("Game starts...");
		do {
			try {
				gameStatus = this.move();
				gameStatus.print();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			// TODO Give a random delay 
			try {
				Thread.sleep(300);
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
}
