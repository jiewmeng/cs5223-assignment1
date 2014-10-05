package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

import client.Client;
import remoteInterface.IServer;

/**
 * For part 2, since all clients implements AbstractServer, 
 * this class represents the "main starting" server 
 */
public class Server extends AbstractServer {

	public static void main(String args[]) {
		IServer stub = null;
		Registry registry = null;

		try {
			Client server = new Client();
			try {
				stub = (IServer) UnicastRemoteObject.exportObject(server, 0);
			} catch (ExportException e) {
				// likely here because its main server. Can skip exporting itself
				stub = (IServer) UnicastRemoteObject.toStub(server);
			}
			server.iServer = stub;
			
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
