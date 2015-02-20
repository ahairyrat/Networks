package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import common.MessageInfo;

/**
 * @author pe313
 * 
 */
public class UDPClient {

	/**
	 * @param args
	 * @author pe313
	 */
	public static void main(String[] args) {
		InetAddress serverAddr = null;
		int destPort;
		int repeats;
		int successSends;
		if (args.length < 3) {
			System.err.println("Arguments required: server name/IP, recv port"
					+ ", message count");
			System.exit(-1);
		}

		try {
			serverAddr = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.err.println("Bad server address in UDPClient, " + args[0]
					+ " caused an unknown host exception " + e);
			System.exit(-1);
		}
		destPort = Integer.parseInt(args[1]);
		repeats = Integer.parseInt(args[2]);

		UDPClient client = new UDPClient(serverAddr, destPort, repeats);

		successSends = client.sendLoop("");

		System.out.println("Successful send of " + successSends + " out of "
				+ repeats + " packets");

	}

	/**
	 * @param serverAddr
	 * @param destPort
	 * @param repeats
	 * @author pe313
	 */
	public UDPClient(InetAddress serverAddr, int destPort, int repeats) {
		this.serverAddr = serverAddr;
		this.destPort = destPort;
		this.repeats = repeats;
	}

	/**
	 * @param message
	 * @return successfulSends
	 * @author pe313
	 */
	private <T> int sendLoop(T message) {
		int failedSends = 0;
		MessageInfo info;
		for (int i = 0; i < this.repeats; i++) {
			info = new MessageInfo(repeats, i + 1);
			try {
				send(info, message);
				System.out.println("Sent message " + (i + 1) + " out of "
						+ this.repeats);
				Thread.sleep(20);
			} catch (IOException e) {
				failedSends++;
			} catch (InterruptedException e) {
				// TO-DO Add exception handler
			}
		}
		return (this.repeats - failedSends);
	}

	/**
	 * @param info
	 * @param message
	 * @throws IOException
	 * @author pe313
	 */
	private <T> void send(MessageInfo info, T message) throws IOException {
		byte data[] = (info.toString() + " " + message.toString()).getBytes();

		DatagramPacket packet = new DatagramPacket(data, data.length,
				this.serverAddr, this.destPort);
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);
		socket.close();
	}

	private InetAddress serverAddr;
	private int destPort;
	private int repeats;

}
