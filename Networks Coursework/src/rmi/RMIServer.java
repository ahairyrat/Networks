package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
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

		if (args.length < 2) {
			System.err.println("Arguments required: server name, server port");
			System.exit(-1);
		}

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		RMIServer server;
		try {
			server = new RMIServer();
			rebindServer(args[0], server, Integer.parseInt(args[1]));

			System.out.println(server.getClass().getName()
					+ " bound to registry");

		} catch (RemoteException e) {
			System.out.print(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.print(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void receiveMessage(MessageInfo info) throws RemoteException {

		if (this.totalMessages <= 0) {
			this.totalMessages = info.getTotalMessages();
			this.receivedMessages = new boolean[this.totalMessages];
		}

		this.receivedMessages[info.getMessageNum() - 1] = true;

		System.out.println("Recieved message: " + info.getMessageNum());

		if (this.receivedMessages[this.totalMessages - 1] == true)
			findMissingMessages();

	}

	protected static void rebindServer(String serverURL, RMIServer server,
			int port) throws RemoteException, MalformedURLException {

		LocateRegistry.createRegistry(port);
		System.out.println("Registry created for port " + port);

		String address = "rmi://localhost:" + port + "/"
				+ serverURL;

		Naming.rebind(address, server);
	}

	private void findMissingMessages() {
		for (int i = 0; i < this.totalMessages; i++)
			if (!this.receivedMessages[i])
				System.out.println("Missing message: " + (i + 1));
	}

	private int totalMessages = 0;
	private boolean[] receivedMessages;

}
