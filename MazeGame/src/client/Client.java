package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import remoteInterface.Maze;

public class Client {
	
	private Client() {
	}

	public static void main(String[] args) {

		String host = (args.length < 1) ? null : args[0];
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			Maze stub = (Maze) registry.lookup("Maze");
			String response = stub.sayMaze();
			System.out.println("response: " + response);
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
