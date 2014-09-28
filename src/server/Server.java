package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import remoteInterface.GameStatus;
import remoteInterface.IServer;
import remoteInterface.MoveDirection;

public class Server extends AbstractServer {

	public static void main(String args[]) {
		IServer stub = null;
		Registry registry = null;

		try {
			Server server = new Server();
			stub = (IServer) UnicastRemoteObject.exportObject(server, 0);
			registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			registry.bind("Maze", stub);

			// Set N and M
			server.initGridParam(args);

			System.err.println("Server ready");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				registry.unbind("Maze");
				registry.bind("Maze", stub);
				System.err.println("Server ready");
			} catch (Exception ee) {
				System.err.println("Server exception: " + ee.toString());
				ee.printStackTrace();
			}
		}
	}

}
