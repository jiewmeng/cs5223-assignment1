package remoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {
	public int joinGame(IClient client) throws RemoteException;
	public GameStatus move(int id, MoveDirection moveDirection) throws RemoteException;
}
