import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialInterface {
	
	private int baud;
	
	private static final char startCharacter = '&';
	private static final char stopCharacter = '$';
	
	private static InputStream inputStream;
	private static OutputStream outputStream;
	private SerialPort serialPort;
	private Thread writeThread;
	
	private static boolean connected;
	private SerialUartInterface uartInterface;
	
	private static SerialInterface serialInterface;
	
	public SerialInterface(int baud, SerialUartInterface serialUartInterface) {
		super();
		
		SerialInterface.connected = false;
		this.baud = baud;
		this.uartInterface = serialUartInterface;
	}
	
	public static void startSerialInterface(String port, int baud, SerialUartInterface sui) {
		// Start up the serial port communication
		try {
			serialInterface = new SerialInterface(baud, sui);
			serialInterface.connect(port);
		} catch(Exception e) {
			System.out.println("Serial error on start up: "+e.getMessage());
		}
	}
	
	public static void stopSerialInterface() {
		serialInterface.disconnect();
	}
	
	public static void writeData(byte[] data) {
		try {
			outputStream.write(data);
		} catch (Exception e) {
			System.out.println("Serial error in writeData: "+e.getMessage());
		}
	}
	
	private void connect(String portName) {
		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
			
			if(portIdentifier.isCurrentlyOwned()) {
				System.out.println("Serial error: port is currently in use");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
				System.out.println("Serial connect with baud: "+baud);
				if (commPort instanceof SerialPort) {
					serialPort = (SerialPort) commPort;
					serialPort.setSerialPortParams(
							baud, 
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					inputStream = serialPort.getInputStream();
					outputStream = serialPort.getOutputStream();
					
					writeThread = new Thread(new SerialWriter(outputStream));
					writeThread.start();
					
					serialPort.addEventListener(new SerialReader(inputStream, uartInterface));
					serialPort.notifyOnDataAvailable(true);
					connected = true;
					uartInterface.onConnectedListener();
				}
			}
		} catch (Exception e) {
			System.out.println("Serial error in connect: "+e.getMessage());
		}
	}
	
	private void disconnect() {
		System.out.println("Serial try disconnect");
		if (serialPort != null) {
			try {
				inputStream.close();
				outputStream.close();
				uartInterface.onDisconnectedListener();
			} catch (IOException e) {
				System.out.println("Serial error in disconnect: "+e.getMessage());
			}
			serialPort.removeEventListener();
			serialPort.close();
			connected = false;
		}
		System.out.println("Serial disconnected");
	}
	
	public static boolean isConnected() {
		return connected;
	}
	
	public static class SerialReader implements SerialPortEventListener {
		
		private InputStream input;
		private byte[] buffer = new byte[1024];
		private SerialUartInterface uartInterface;
		
		public SerialReader(InputStream input, SerialUartInterface uartInterface) {
			this.input = input;
			this.uartInterface = uartInterface;
		}

		@Override
		public void serialEvent(SerialPortEvent arg0) {
			System.out.println("Serial event");
			int data;
			try {
				int length = 0;
				while((data = input.read()) > -1) {
					if(data == stopCharacter) break;
					buffer[length++] = (byte)data;
					
					if(length > 200) break; // error
				}
				uartInterface.onDataReadyListener(new String(buffer, 0, length));
				System.out.println("Data: "+new String(buffer, 0, length));
			} catch (IOException e) {
				System.out.println("Serial error in serialEvent: "+e.getMessage());
				System.exit(-1);
			}
		}
	}
	
	public static class SerialWriter implements Runnable {
		
		private OutputStream output;
		
		public SerialWriter(OutputStream output) {
			this.output = output;
		}

		@Override
		public void run() {
			try {
				int c = 0;
				while((c = System.in.read()) > -1) {
					this.output.write(c);
				}
			} catch (IOException e) {
				System.out.println("Serial error in SerialWriter run: "+e.getMessage());
				System.exit(-1);
			}	
		}
	}
	
	public static ArrayList<String> listPorts() {
    	@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
    	ArrayList<String> portNames = new ArrayList<>();
    	while (portEnum.hasMoreElements()) {
    		CommPortIdentifier portIdentifier = portEnum.nextElement();
    		System.out.println(portIdentifier.getName() + " - " + getPortTypeName(portIdentifier.getPortType()));
    		if(portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
    			portNames.add(portIdentifier.getName());
    		}
    	}
    	return portNames;
    }
    
    private static String getPortTypeName(int portType) {
    	switch (portType) {
    	case CommPortIdentifier.PORT_I2C: return "I2C";
    	case CommPortIdentifier.PORT_PARALLEL: return "PARALLEL";
    	case CommPortIdentifier.PORT_RAW: return "RAW";
    	case CommPortIdentifier.PORT_RS485: return "RS485";
    	case CommPortIdentifier.PORT_SERIAL: return "SERIAL";
    	default: return "unknown";
    	}
    }
}
