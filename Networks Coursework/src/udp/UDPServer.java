package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

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
		System.out.println("Server started");
		while (!close) {
			buf = new byte[256];
			pac = new DatagramPacket(buf, buf.length);
			try {
				recvSoc.receive(pac);

				processMessage(new String(pac.getData()));

			} catch (SocketTimeoutException e) {
				this.closeConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		this.receivedMessages.sort(null);
		for (int i = 0; i < (this.receivedMessages.size() - 1); i++)
			for (int n = this.receivedMessages.get(i); (n + 1) < this.receivedMessages
					.get(i + 1); n++)
				System.out.println("Missing message: " + (n + 1));

		System.out.println("Recieved " + this.receivedMessages.size()
				+ " out of " + this.totalMessages + " : "
				+ ((this.receivedMessages.size() * 100) / this.totalMessages)
				+ "%");
	}

	/**
	 * @param recvSoc
	 * @throws SocketException
	 */
	public UDPServer(int recvSoc) throws SocketException {
		this.recvSoc = new DatagramSocket(recvSoc);
		this.totalMessages = 0;
		this.receivedMessages = new ArrayList<Integer>();
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

		if (this.totalMessages <= 0)
			this.totalMessages = info.getTotalMessages();

		this.receivedMessages.add(info.getMessageNum());

		if (info.getMessageNum() == this.totalMessages)
			close = true;
	}

	private void closeConnection() {
		close = true;
	}

	private DatagramSocket recvSoc;
	private int totalMessages;
	private List<Integer> receivedMessages;
	private boolean close;

}
