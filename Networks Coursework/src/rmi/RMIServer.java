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

	/**
	 * @throws RemoteException
	 */
	public RMIServer() throws RemoteException {
		this.receivedMessages = new ArrayList<Integer>();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 3) {
			System.err
					.println("Needs 3 arguments: Registry Port, Server Name, Server Port");
			System.exit(-1);
		}

		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());

		RMIServer server;
		int registryPort = Integer.parseInt(args[0]);
		int serverPort = Integer.parseInt(args[2]);
		try {
			server = new RMIServer();
			rebindServer(registryPort, args[1], server, serverPort);

			System.out.println(server.getClass().getName()
					+ " bound to registry");

		} catch (RemoteException e) {
			System.err.println("Error creating registry");
			System.exit(-1);
		} catch (UnknownHostException e) {
			System.err
					.println("The local machine does not have an IPV4 address.");
			System.exit(-1);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rmi.RMIServerInterface#receiveMessage(common.MessageInfo)
	 */
	@Override
	public <T> void receiveMessage(MessageInfo info, T message)
			throws RemoteException {

		if (this.totalMessages <= 0) {
			System.out.println("Recieving messages...");
			this.totalMessages = info.getTotalMessages();
		}
		this.totalMessages = info.getTotalMessages();

		this.receivedMessages.add(info.getMessageNum());

		if (info.getMessageNum() == this.totalMessages) {
			findMissingMessages();
			reset();
		}

	}

	/**
	 * 
	 */
	private void reset() {
		this.receivedMessages = null;
		this.totalMessages = -1;
		this.receivedMessages = new ArrayList<Integer>();
	}

	/**
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
			registry = LocateRegistry.createRegistry(registryPort);
			System.out.println("Registry created at port " + registryPort);
		} catch (ExportException e) {
			System.out.println("The registry already exists");
			registry = LocateRegistry.getRegistry(registryPort);
		}

		String address = "//" + Inet4Address.getLocalHost().getHostAddress()
				+ ':' + serverPort + '/' + serverURL;
		registry.rebind(address, server);
		System.out.println("Address is: " + address);
	}

	/**
	 * 
	 */
	private void findMissingMessages() {
		this.receivedMessages.sort(null);
		for (int i = 0; i < (this.receivedMessages.size() - 1); i++)
			for (int n = this.receivedMessages.get(i); (n + 1) < this.receivedMessages
					.get(i + 1); n++)
				System.out.println("Missing message: " + (n + 1));

		System.out.println("Recieved " + this.receivedMessages.size()
				+ " messages out of " + this.totalMessages + " : "
				+ ((this.receivedMessages.size() * 100) / this.totalMessages)
				+ "%");
	}

	private int totalMessages = -1;
	private List<Integer> receivedMessages;

}
