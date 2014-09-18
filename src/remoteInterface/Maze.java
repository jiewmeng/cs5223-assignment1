package remoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Maze extends Remote {
	public int joinGame(IClient client) throws RemoteException;
	public GameStatus move(MoveDirection moveDirection) throws RemoteException;
}
