

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface IServer extends Remote {
	public int joinGame(IClient iClient) throws RemoteException;
	public GameStatus move(int id, MoveDirection moveDirection) throws RemoteException;
	public boolean makeBackup(GameStatus gameState, Vector<IClient> clients) throws RemoteException;
	public GameStatus primaryFailed(int id, MoveDirection moveDirection) throws RemoteException;
	public int getId() throws RemoteException;
}
