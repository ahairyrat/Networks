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
	 * The start of the client program. It parses the command line arguments and
	 * instantiates as well as runs the client class
	 * 
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
		client = new RMIClient(numMessages);

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
	 * Constructor for the RMIServer. It requires parameters for the number of
	 * messages sent
	 * 
	 * @param repeats
	 * @param rmiServer
	 */
	public RMIClient(int repeats) {
		this.repeats = repeats;
	}

	/**
	 * A method that loops through all messages and sends them. It requires the
	 * message data to be sent which can be of any class
	 * 
	 * @param message
	 * @return successfulSends
	 */
	private <T> int sendLoop(T message) {

		int failedSends = 0;
		MessageInfo info;

		// Loop through all messages, create the message info and send them
		for (int i = 0; i < this.repeats; i++) {

			// Create a new unique message info
			info = new MessageInfo(repeats, i + 1);

			try {
				// Send the message
				send(info, message);

			} catch (IOException e) {
				// If a message fails to send, count and try the next one
				failedSends++;
			}
		}

		// Return the number of messages that have successfully sent
		return (this.repeats - failedSends);
	}

	/**
	 * A method that sends a single message with the specified message info and
	 * message data to the server
	 * 
	 * @param info
	 * @param message
	 * @throws RemoteException
	 */
	private <T> void send(MessageInfo info, T message) throws RemoteException {

		// Invoke the method on the server referenced by the interface on the
		// registry, passing the info and message to it
		rmiServer.receiveMessage(info, message);
	}

	/**
	 * A method that retrieves the server reference from the registry on the
	 * port and host specidfied that has the URL gived
	 * 
	 * @param port
	 * @param host
	 * @param serverURL
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void retrieveServer(int port, String host, String serverURL)
			throws RemoteException, NotBoundException {

		// Locate the registry on the server machine
		Registry registry = LocateRegistry.getRegistry(host, port);

		// Retrieve the reference to the server class
		this.rmiServer = (RMIServerInterface) registry.lookup(serverURL);
	}

	// Private fields
	private int repeats;
	private RMIServerInterface rmiServer = null;

}
