package rmi;

import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		RMIServerInterface RMIServer = null;

		// Check arguments for Server host and number of messages
		if (args.length < 2) {
			System.err
					.println("Needs 2 arguments: ServerHostName/IPAddress, TotalMessageCount");
			System.exit(-1);
		}

		String urlServer = new String("rmi://" + args[0] + "/RMIServer");
		int numMessages = Integer.parseInt(args[1]);

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		try {
			Registry registry = LocateRegistry.getRegistry();
			RMIServer = (RMIServerInterface)registry.lookup(urlServer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Remote Exception: " + e.getMessage());
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			System.out.println("No server bound to port: " + e.getMessage());
		}

		// TO-DO: Attempt to send messages the specified number of

	}

}
