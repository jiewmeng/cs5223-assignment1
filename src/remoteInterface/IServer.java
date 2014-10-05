package remoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

import client.Client;

public interface IServer extends Remote {
	public int joinGame(Client client) throws RemoteException;
	public GameStatus move(int id, MoveDirection moveDirection) throws RemoteException;
	public boolean makeBackup(GameStatus gameState) throws RemoteException;
	public GameStatus primaryFailed(int id, MoveDirection moveDirection) throws RemoteException;
	public boolean updateState(GameStatus gameState) throws RemoteException;
}
