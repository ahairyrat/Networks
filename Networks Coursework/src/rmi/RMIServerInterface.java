package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import common.MessageInfo;

public interface RMIServerInterface extends Remote{
	public void receiveMessage(MessageInfo info) throws RemoteException;
}
