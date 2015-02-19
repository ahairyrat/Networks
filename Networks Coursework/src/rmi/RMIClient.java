package rmi;

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

		RMIServerInterface rmiServer = null;

		// Check arguments for Server host and number of messages
		if (args.length < 2) {
			System.err
					.println("Needs 2 arguments: ServerHostName/IPAddress, TotalMessageCount");
			System.exit(-1);
		}

		String urlServer = new String("//" + args[0] + "/RMIServer");
		int numMessages = Integer.parseInt(args[1]);

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			Registry registry = LocateRegistry.getRegistry();
			rmiServer = (RMIServerInterface) registry.lookup(urlServer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getClass().getName());
			e.printStackTrace();
			System.out.println("Remote Exception: " + e.getMessage());
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			System.out.println("No server bound to port: " + e.getMessage());
		}
		int failedCount = 0;
		try {
			for (int i = 0; i < numMessages; i++)
				rmiServer.receiveMessage(new MessageInfo(numMessages, i + 1));
		} catch (RemoteException e) {
			failedCount++;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Successful messages: "+ (numMessages-failedCount));

	}

}
