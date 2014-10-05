package remoteInterface;

import java.rmi.RemoteException;

/**
 * Since in part 2, all nodes are clients and potential servers
 */
public interface IPlayer extends IServer, IClient {
	public int getId() throws RemoteException;
	//public void updateGameState(GameStatus state) throws RemoteException;
}
