package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import common.MessageInfo;

/**
 * @author pe313
 * 
 */
public class UDPClient {

	/**
	 * The start of the client program. It parses the command line arguments and
	 * instantiates as well as runs the client class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		InetAddress serverAddr = null;
		int destPort;
		int repeats;
		int successSends = 0;

		// Check arguments for Server, port and number of messages
		if (args.length < 3) {
			System.err.println("Arguments required: server name/IP, recv port"
					+ ", message count");
			System.exit(-1);
		}

		try {

			// Get the server's IP address from the command line argument
			serverAddr = InetAddress.getByName(args[0]);

		} catch (UnknownHostException e) {
			// If the server cannot be found, the client can exit
			System.err.println("Bad server address in UDPClient, " + args[0]
					+ " caused an unknown host exception " + e.getMessage());
			System.exit(-1);
		}
		// Parse the rest of the command line arguments
		destPort = Integer.parseInt(args[1]);
		repeats = Integer.parseInt(args[2]);

		// Instantiate a new client using the parameters
		UDPClient client = new UDPClient(serverAddr, destPort, repeats);

		try {

			// Send all messages with no message data and count the number of
			// successful sends
			successSends = client.sendLoop(null);

		} catch (SocketException e) {

			// If the client cannot send, the client should exit
			System.err
					.println("Error sending messages, caused an socket exception "
							+ e.getMessage());
			System.exit(-1);
		}

		System.out.println("Successful send of " + successSends + " out of "
				+ repeats + " packets");

	}

	/**
	 * Constructor for the UDPClient. It requires a parameter for the server
	 * address, server port and number of messages to send
	 * 
	 * @param serverAddr
	 * @param destPort
	 * @param repeats
	 */
	public UDPClient(InetAddress serverAddr, int destPort, int repeats) {
		this.serverAddr = serverAddr;
		this.destPort = destPort;
		this.repeats = repeats;
	}

	/**
	 * A method that loops through all messages and sends them. It requires the
	 * message data to be sent which can be of any class
	 * 
	 * @param message
	 * @return successfulSends
	 * @throws SocketException
	 */
	private <T> int sendLoop(T message) throws SocketException {
		int failedSends = 0;
		MessageInfo info;

		// Open a new socket to send packets through
		DatagramSocket socket = new DatagramSocket();

		// Loop through all messages, create the message info and send them
		for (int i = 0; i < this.repeats; i++) {
			
			// Create a new unique message info
			info = new MessageInfo(repeats, i + 1);

			try {
				// Send the message through the previously created socket
				send(info, message, socket);
			} catch (IOException e) {
				// If a message fails to send, count and try the next one
				failedSends++;
			}
		}
		// Close the socket after all messages have been sent
		socket.close();

		//Return the number of messages that have successfully sent
		return (this.repeats - failedSends);
	}

	/**
	 * A method that sends a single message with the specified message info and
	 * message data through a supplied socket
	 * 
	 * @param info
	 * @param message
	 * @throws IOException
	 */
	private <T> void send(MessageInfo info, T message, DatagramSocket socket)
			throws IOException {

		// Convert a ll of the input data to a byte array
		byte data[] = (info.toString() + " " + message.toString()).getBytes();
		// Create the packet to be sent
		DatagramPacket packet = new DatagramPacket(data, data.length,
				this.serverAddr, this.destPort);
		// Send the created packet
		socket.send(packet);
	}

	//Private fields
	private InetAddress serverAddr;
	private int destPort;
	private int repeats;

}
