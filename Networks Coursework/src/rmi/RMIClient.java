package rmi;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import common.MessageInfo;

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

		String urlServer = new String("//" + args[1] + ':' + args[2] + '/'
				+ args[3]);
		int registryPort = Integer.parseInt(args[0]);
		int numMessages = Integer.parseInt(args[4]);

		client = new RMIClient(numMessages, null);

		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());

		try {
			System.out.println("Connecting to registry on host: " + args[1]
					+ " on port: " + registryPort);
			client.retrieveServer(registryPort, args[1], urlServer);
		} catch (RemoteException e) {
			System.out.println("Remote Exception: " + e.getMessage());
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			System.out.println("No server bound to port: " + e.getMessage());
		}
		int successCount = client.sendLoop("");
		System.out.println("Successful messages: " + successCount);

	}

	public RMIClient(int repeats, RMIServerInterface rmiServer) {
		this.repeats = repeats;
		this.rmiServer = rmiServer;
	}

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
