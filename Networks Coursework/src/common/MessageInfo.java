package common;

import java.io.Serializable;

/**
 * Utility class that encapsulates the message information to be passed from
 * client to server. Information can be extracted or constructed as a String for
 * use by the UDP example.
 * 
 * @author pe313
 * 
 */
public class MessageInfo implements Serializable {

	public static final long serialVersionUID = 52L;

	/**
	 * @param total
	 * @param msgNum
	 */
	public MessageInfo(int total, int msgNum) {
		totalMessages = total;
		messageNum = msgNum;
	}

	/**
	 * @param msg
	 * @throws IllegalArgumentException
	 */
	public MessageInfo(String msg) throws IllegalArgumentException {
		String[] fields = msg.split(";");
		if (fields.length != 2)
			throw new IllegalArgumentException( // Changed exception type thrown
												// to prevent conflict with
												// NumberFormatException
					"MessageInfo: Invalid string for message construction: "
							+ msg);
		totalMessages = Integer.parseInt(fields[0]);
		messageNum = Integer.parseInt(fields[1]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new String(totalMessages + ";" + messageNum); // removed the new
																// line at the
																// end
	}

	public int getTotalMessages() {
		return totalMessages;
	}

	public int getMessageNum() {
		return messageNum;
	}

	private int totalMessages;
	private int messageNum;

}