package remoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {
	public int joinGame(IPlayer client) throws RemoteException;
	public GameStatus move(int id, MoveDirection moveDirection) throws RemoteException;
	public boolean makeBackup(GameStatus gameState) throws RemoteException;
	public GameStatus primaryFailed(int id, MoveDirection moveDirection) throws RemoteException;
	public boolean updateState(GameStatus gameState) throws RemoteException;
}
