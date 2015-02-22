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
	 * The start of the server program. It parses the command line arguments and
	 * instantiates as well as runs the server class
	 * 
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
			// Instantiate and run the server
			server = new UDPServer(recievePort);
			server.run();
		} catch (IOException e) {
			// If an IOException occurs, the server cannot run and should
			// exit
			System.err.println("Error creating or running server "
					+ e.getMessage());
			System.exit(-1);
		} finally {
			// Always close the socket, in case of an unexpected closure
			server.closeSocket();
		}
	}

	/**
	 * Constructor for the UDPServer. It requires a parameter for the value of
	 * the socket to be opened
	 * 
	 * @param recvSoc
	 * @throws SocketException
	 */
	public UDPServer(int recvSoc) throws SocketException {
		this.recvSoc = new DatagramSocket(recvSoc);
		this.receivedMessages = new ArrayList<Integer>();
	}

	/**
	 * A method to run the server, it waits for 30 seconds after each input and
	 * after this timeout is reached, lists all messages that are missing and
	 * the percentage of messages that have been received
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException {
		byte buf[];
		DatagramPacket pac;

		// Set the timeout of the socket to 30s
		recvSoc.setSoTimeout(30000);

		System.out.println("Server started");

		while (!exitServer) {

			// Create the structure for the packet to be received
			buf = new byte[256];
			pac = new DatagramPacket(buf, buf.length);

			// Receive and process each message
			try {
				recvSoc.receive(pac);

				processMessage(new String(pac.getData()));

			} catch (SocketTimeoutException e) {
				// If the timeout has occurred no input has been received for 30
				// seconds and it can be assumed that no further inputs will
				// arrive
				this.closeServer();
			}
		}
		// Access the list to find missing messages
		findMissingMessages();
	}

	/**
	 * A method the externally close the server socket in case of unexpected
	 * exit
	 */
	protected void closeSocket() {
		this.recvSoc.close();
	}

	/**
	 * A method to close the exit the server during the run procedure
	 */
	protected void closeServer() {
		this.exitServer = true;
	}

	/**
	 * A method to extract the message info from the message and
	 * 
	 * @param data
	 */
	private void processMessage(String data) {

		// Separate the message info and actual message
		String[] fields = data.split(" ", 2);
		MessageInfo info = new MessageInfo(fields[0]);

		// If this is the first message to have been received, check how many
		// messages should arrive
		if (this.totalMessages <= 0) {
			System.out.println("Recieving messages...");
			this.totalMessages = info.getTotalMessages();
		}

		// Add the currently received message to the list of all received
		// messages
		this.receivedMessages.add(info.getMessageNum());

		// If all messages have been receive the server can close
		if (this.receivedMessages.size() == this.totalMessages)
			this.closeServer();
	}

	/**
	 * A method to print out all missing message numbers and calculate the
	 * percentage of successful messages
	 */
	private void findMissingMessages() {

		// Sort the messages into ascending order
		this.receivedMessages.sort(null);

		// Loop through the list of received messages
		// For each message, print out all numbers between it and the next
		// message number as failed messages
		for (int i = 0; i < (this.receivedMessages.size() - 1); i++)
			for (int n = this.receivedMessages.get(i); (n + 1) < this.receivedMessages
					.get(i + 1); n++)
				System.out.println("Missing message: " + (n + 1));

		// Calculate the number and percentage of successful messages received
		System.out.println("Recieved " + this.receivedMessages.size()
				+ " messages out of " + this.totalMessages + " : "
				+ ((this.receivedMessages.size() * 100) / this.totalMessages)
				+ "%");
	}

	// Private fields for the server class
	private DatagramSocket recvSoc;
	private int totalMessages = -1;
	private List<Integer> receivedMessages;
	private boolean exitServer = false;

}
