package connection;

public interface Event {
    public void onReceiveText(String text);

    public void onReceiveFile(String filename);

//    public void onReceiveCommand();

    public void onLogin(String name, String id);

//    public void onReceiveVideo();
}
