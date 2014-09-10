package remoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Maze extends Remote {
	String sayMaze() throws RemoteException;
}
