package rmi;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import common.MessageInfo;

/**
 * @author pe313
 *
 */
public class RMIClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		RMIClient client;
		// Check arguments for Server host and number of messages
		if (args.length < 5) {
			System.err
					.println("Needs 5 arguments: Registry Port, ServerHostName/IPAddress, port,"
							+ " Server Name, TotalMessageCount");
			System.exit(-1);
		}

		// Create the URL by which the server can be found in the registry
		String serverURL = new String("//" + args[1] + ':' + args[2] + '/'
				+ args[3]);

		// Parse the remaining arguments into the port for the registry and the
		// number of messages to be sent
		int registryPort = Integer.parseInt(args[0]);
		int numMessages = Integer.parseInt(args[4]);

		// If no security manager exists, create one
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());

		// Instantiate the client
		client = new RMIClient(numMessages, null);

		try {
			System.out.println("Connecting to registry on host: " + args[1]
					+ " on port: " + registryPort);

			// Retrieve the server reference from the registry
			client.retrieveServer(registryPort, args[1], serverURL);

		} catch (RemoteException e) {

			// If there is an error retrieving the server, the client does not
			// need to run
			System.out.println("Remote Exception: " + e.getMessage());
			System.exit(-1);

		} catch (NotBoundException e) {

			// If no server is bound, the client does not need to run anymore
			System.out.println("No server bound to port: " + e.getMessage());
			System.exit(-1);

		}

		// Send the messages with a data field that is empty and count the
		// number of successful sends
		int successCount = client.sendLoop(null);

		System.out.println("Successful send of " + successCount + " out of "
				+ numMessages + " objects");

	}

	/**
	 * @param repeats
	 * @param rmiServer
	 */
	public RMIClient(int repeats, RMIServerInterface rmiServer) {
		this.repeats = repeats;
		this.rmiServer = rmiServer;
	}

	/**
	 * @param message
	 * @return successfulSends
	 */
	private <T> int sendLoop(T message) {
		int failedSends = 0;
		MessageInfo info;
		for (int i = 0; i < this.repeats; i++) {
			info = new MessageInfo(repeats, i + 1);
			try {
				send(info, message);
				System.out.println("Sent message " + (i + 1) + " out of "
						+ this.repeats);
			} catch (IOException e) {
				failedSends++;
			}
		}
		return (this.repeats - failedSends);
	}

	/**
	 * @param info
	 * @param message
	 * @throws RemoteException
	 */
	private <T> void send(MessageInfo info, T message) throws RemoteException {
		rmiServer.receiveMessage(info, message);
	}

	private void retrieveServer(int port, String host, String urlServer)
			throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(host, port);
		this.rmiServer = (RMIServerInterface) registry.lookup(urlServer);
	}

	private int repeats;
	private RMIServerInterface rmiServer;

}
