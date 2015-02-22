package rmi;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import common.MessageInfo;

/**
 * @author pe313
 *
 */
public class RMIServer extends UnicastRemoteObject implements
		RMIServerInterface {

	private static final long serialVersionUID = 1L;

	/**
	 * The start of the server program. It parses the command line arguments and
	 * instantiates as well as runs the server class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		RMIServer server;

		// Check arguments for registry port, server name and server port
		if (args.length < 3) {
			System.err
					.println("Needs 3 arguments: Registry Port, Server Name, Server Port");
			System.exit(-1);
		}

		// If no security manager exists, create one
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());

		// Parse the command line arguments
		int registryPort = Integer.parseInt(args[0]);
		int serverPort = Integer.parseInt(args[2]);

		try {

			// Instantiate the server class
			server = new RMIServer();

			// Bind the server to the registry overwriting all existing
			// references
			rebindServer(registryPort, args[1], server, serverPort);

			System.out.println(server.getClass().getName()
					+ " bound to registry");

		} catch (RemoteException e) {
			// If there is a problem accessing the registry, close the server
			System.err.println("Error creating registry");
			System.exit(-1);
		} catch (UnknownHostException e) {
			// If the server cannot find the IP address of the localhost then
			// the registry cannot be stored
			System.err
					.println("The local machine does not have an IPV4 address.");
			System.exit(-1);
		}

	}

	/**
	 * Constructor for the RMIServer
	 * 
	 * @throws RemoteException
	 */
	public RMIServer() throws RemoteException {
		this.receivedMessages = new ArrayList<Integer>();
	}

	/**
	 * The implementation of the RMIServerInterface. It can be called by the
	 * client to pass a message to the server. It stores the messages until all
	 * have been received. Then it calculates the missing messages
	 * 
	 * @see rmi.RMIServerInterface#receiveMessage(common.MessageInfo)
	 */
	@Override
	public <T> void receiveMessage(MessageInfo info, T message)
			throws RemoteException {

		// If this is the first message to have been received, check how many
		// messages should arrive
		if (this.totalMessages <= 0) {
			System.out.println("Recieving messages...");
			this.totalMessages = info.getTotalMessages();
		}

		// Add the current message to the list of all received messages
		this.receivedMessages.add(info.getMessageNum());

		// If all messages have been received, find the missing message numbers
		// and close the server
		if (this.receivedMessages.size() == this.totalMessages) {
			this.findMissingMessages();
			System.exit(0);
		}

	}

	/**
	 * A method to bind the server to the registry on the specified port using
	 * the given arguments for the server URL
	 * 
	 * @param registryPort
	 * @param serverURL
	 * @param server
	 * @param serverPort
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	private static void rebindServer(int registryPort, String serverURL,
			RMIServer server, int serverPort) throws RemoteException,
			UnknownHostException {
		Registry registry = null;
		try {

			// Try to create the registry on the specified port
			registry = LocateRegistry.createRegistry(registryPort);
			System.out.println("Registry created at port " + registryPort);

		} catch (ExportException e) {

			// If the registry already exists get it
			System.out.println("The registry already exists");
			registry = LocateRegistry.getRegistry(registryPort);

		}
		// Create the URL by which the server can be found using the hosts IP
		// address
		String address = "//" + Inet4Address.getLocalHost().getHostAddress()
				+ ':' + serverPort + '/' + serverURL;

		// Bind the URL to the registry overwriting all previous objects
		registry.rebind(address, server);

		System.out.println("Address is: " + address);
	}

	/**
	 * A method to print out all missing message numbers and calculate the
	 * percentage of successful messages
	 */
	private void findMissingMessages() {

		// Sort the messages into ascending order
		this.receivedMessages.sort(null);

		// Loop through the list of received messages
		// For each message, print out all numbers between it and the next
		// message number as failed messages
		for (int i = 0; i < (this.receivedMessages.size() - 1); i++)
			for (int n = this.receivedMessages.get(i); (n + 1) < this.receivedMessages
					.get(i + 1); n++)
				System.out.println("Missing message: " + (n + 1));

		// Calculate the number and percentage of successful messages received
		System.out.println("Recieved " + this.receivedMessages.size()
				+ " messages out of " + this.totalMessages + " : "
				+ ((this.receivedMessages.size() * 100) / this.totalMessages)
				+ "%");
	}

	//Private fields
	private int totalMessages = -1;
	private List<Integer> receivedMessages;

}
