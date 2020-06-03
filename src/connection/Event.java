package connection;

public interface Event {
    public abstract void onReceiveText(String text);

    public abstract void OnReceiveFile();

//    public abstract void onReceiveCommand();

//    public abstract void onReceivedLogin();

//    public abstract void onReceiveVideo();
}
