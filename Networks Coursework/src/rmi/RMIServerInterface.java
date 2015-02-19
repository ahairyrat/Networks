package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import common.MessageInfo;

public interface RMIServerInterface extends Remote{
	public <T> void receiveMessage(MessageInfo info, T message) throws RemoteException;
}
