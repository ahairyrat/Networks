package rmi;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

import common.MessageInfo;

public class RMIServer extends UnicastRemoteObject implements
		RMIServerInterface {

	public RMIServer() throws RemoteException {

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
			System.out.print(e.getMessage());
			// TODO Auto-generated catch block
			System.exit(-1);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			this.totalMessages = info.getTotalMessages();
			this.receivedMessages = new boolean[this.totalMessages];
		}

		this.receivedMessages[info.getMessageNum() - 1] = true;

		System.out.println("Recieved message: " + info.getMessageNum());

		if (this.receivedMessages[this.totalMessages - 1] == true) {
			reset();
			findMissingMessages();
		}

	}

	private void reset() {
		this.receivedMessages = null;
		this.totalMessages = -1;

	}

	/**
	 * @param registryPort
	 * @param serverURL
	 * @param server
	 * @param serverPort
	 * @throws RemoteException
	 * @throws UnknownHostException 
	 */
	protected static void rebindServer(int registryPort, String serverURL,
			RMIServer server, int serverPort) throws RemoteException, UnknownHostException {
		Registry registry = null;
		try {
			registry = LocateRegistry.createRegistry(registryPort);
			System.out.println("Registry created at port " + registryPort);
		} catch (ExportException e) {
			System.out.println("The registry already exists");
			registry = LocateRegistry.getRegistry(registryPort);
		}

		String address = "//" +Inet4Address.getLocalHost().getHostAddress()+':' + serverPort + '/' + serverURL;
		registry.rebind(address, server);
		System.out.println("Address is: " + address);
	}

	/**
	 * 
	 */
	private void findMissingMessages() {
		for (int i = 0; i < this.totalMessages; i++)
			if (!this.receivedMessages[i])
				System.out.println("Missing message: " + (i + 1));
	}

	private int totalMessages = -1;
	private boolean[] receivedMessages;

}
