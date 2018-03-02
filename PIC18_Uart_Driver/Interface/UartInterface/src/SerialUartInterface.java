
public interface SerialUartInterface {
	void onConnectedListener();
	void onDisconnectedListener();
	void onDataReadyListener(String input);
	void onWriteSuccesListener();
	void onWriteFailedListener(String error);
}
