package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import common.MessageInfo;

/**
 * @author pe313
 * 
 */
public class UDPServer {

	/**
	 * @throws SocketException
	 */
	public void run() throws SocketException {
		byte buf[];
		DatagramPacket pac;
		try {
			recvSoc.setSoTimeout(30000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
		}
		while (!close) {
			buf = new byte[256];
			pac = new DatagramPacket(buf, buf.length);
			try {
				recvSoc.receive(pac);

				processMessage(new String(pac.getData()));

			} catch (SocketTimeoutException e) {
				System.err.println("The socket timed out");
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		for (int i = 0; i < this.receivedMessages.length; i++)
			if (!this.receivedMessages[i])
				System.out.println("Missing message: " + (i + 1));
	}

	/**
	 * @param recvSoc
	 * @throws SocketException
	 */
	public UDPServer(int recvSoc) throws SocketException {
		this.recvSoc = new DatagramSocket(recvSoc);
		this.totalMessages = 0;
		this.receivedMessages = null;
		this.close = false;

		this.recvSoc.setReuseAddress(true);
	}

	/**
	 * @param recvSoc
	 * @throws SocketException
	 */
	public void openSocket(int recvSoc) throws SocketException {
		if (!this.recvSoc.isClosed())
			this.recvSoc.close();
		this.recvSoc = new DatagramSocket(recvSoc);
	}

	/**
	 * 
	 */
	public void closeSocket() {
		this.recvSoc.close();
	}

	/**
	 * @param args
	 */
	public static void main(String args[]) {
		int recievePort;
		// Get the parameters from command line
		if (args.length < 1) {
			System.err.println("Arguments required: recieve port");
			System.exit(-1);
		}

		recievePort = Integer.parseInt(args[0]);
		UDPServer server = null;
		try {
			server = new UDPServer(recievePort);
			server.run();
		} catch (SocketException e) {
			System.err.println("Error creating socket " + e.getMessage());
			System.exit(-1);
		} finally {
			server.closeSocket();
		}
	}

	/**
	 * @param data
	 */
	private void processMessage(String data) {
		String[] fields = data.split(" ", 2);
		MessageInfo info = new MessageInfo(fields[0]);

		if (this.totalMessages <= 0) {
			this.totalMessages = info.getTotalMessages();
			this.receivedMessages = new boolean[this.totalMessages];
		}

		this.receivedMessages[info.getMessageNum() - 1] = true;
		System.out.println("Recieved message: " + info.getMessageNum());
		if (this.receivedMessages[this.totalMessages - 1] == true)
			close = true;
	}

	private DatagramSocket recvSoc;
	private int totalMessages;
	private boolean[] receivedMessages;
	private boolean close;

}
