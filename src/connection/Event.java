package connection;

public interface Event {
    public void onReceiveText(String text);

    public void OnReceiveFile();

//    public abstract void onReceiveCommand();

//    public abstract void onReceivedLogin();

//    public abstract void onReceiveVideo();
}
