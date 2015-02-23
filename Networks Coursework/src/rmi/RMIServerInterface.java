package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import common.MessageInfo;

/**
 * The interface for an RMIServer class. It allows for teh passing of messages
 * to the implementation. It can also be bound to the registry to allow for
 * network communication
 * 
 * @author pe313
 *
 */
public interface RMIServerInterface extends Remote {
	public <T> void receiveMessage(MessageInfo info, T message)
			throws RemoteException;
}
