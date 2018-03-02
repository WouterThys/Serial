package classes;

import java.util.Calendar;

public class Message {
	
	private static final char startCharacter = '&';
	private static final String deviceName = "Computer";
	private static final String messageCharacter = "[M]";
	private static final String registerCharacter = "[R]";
	private static final String ackCharacter = "[A]";
	private static final char stopCharacter = '$';
	
	public static final int MES_UNKNOWN = -1;
	public static final int MES_MESSAGE = 0;
	public static final int MES_ACK = 1;
	public static final int MES_REGISTER = 2;
	
	private Calendar time;
	private String sender;
	private String command;
	private String message;
	
	private int messageType;
	
	public Message(String inputMsg) {
		time = Calendar.getInstance();
		convert(inputMsg);
	}
	
	public Message(String command, String message) {
		this.time = Calendar.getInstance();
		this.sender = deviceName;
		this.command = command;
		this.message = message;
	}
	
	public String construct(int id) {
		
		System.out.println("Construct for id: "+id);
		
		String sendTxt = "";
		if (id > 9) id = 0;
		sendTxt = startCharacter + messageCharacter + ":" + sender + ":" + command + ":" + message + ":" + String.valueOf(id)+stopCharacter;
		return sendTxt;
	}
	
	private void convert(String inputMsg) {
		
		System.out.println("Convert: "+inputMsg);
		
		sender = "?";
		command = "?";
		message = "UNKNOWN";
		//messageType = MES_UNKNOWN;
		
		if (inputMsg.isEmpty() || inputMsg == null) return;
		
		if (inputMsg.charAt(0) != startCharacter) return;
		
		try {
		// Remove first (start)character
		inputMsg = inputMsg.substring(1);
		
		int length = inputMsg.length();
		
		// Check message type
		String type = inputMsg.substring(0,3);
		switch(type) {
		case messageCharacter: messageType = MES_MESSAGE; break;
		case ackCharacter: messageType = MES_ACK; break;
		case registerCharacter: messageType = MES_REGISTER; break;
		default: messageType = MES_UNKNOWN; return;
		}
		
		// Remove type
		inputMsg = inputMsg.substring(3, length);
		
		switch(messageType) {
		case MES_MESSAGE:
			String[] m = inputMsg.split("\\:");
			this.sender = m[0];
			this.command = m[1];
			this.message = m[2];
			break;
			
		case MES_ACK:
			this.message = inputMsg;
			break;
			
		case MES_REGISTER:
			break;
		}
		} catch(Exception e) {
			System.out.println("Message convertion error: "+e.getMessage());
		}
		
	}

	public Calendar getTime() {
		return time;
	}

	public void setTime(Calendar time) {
		this.time = time;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMessageType() {
		return messageType;
	}
}
