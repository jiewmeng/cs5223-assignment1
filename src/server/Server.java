package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import remoteInterface.Maze;

public class Server implements Maze {

	public Server() {
	}

	public String sayMaze() {
		return "Hello, Maze!";
	}

	public static void main(String args[]) {
		Maze stub = null;
		Registry registry = null;

		try {
			Server obj = new Server();
			stub = (Maze) UnicastRemoteObject.exportObject(obj, 0);
			registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			registry.bind("Maze", stub);

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
