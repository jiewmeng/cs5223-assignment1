

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote {
	public void startGame(GameStatus initGameStatus) throws RemoteException;
	public int getId() throws RemoteException;
	public IServer getIClientServer() throws RemoteException;
	public void updateGameState(GameStatus gameState) throws RemoteException;
}
