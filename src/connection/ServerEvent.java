package connection;

public interface ServerEvent {
    public void onReceiveText(String text);

    public void onReceiveFile(String filename);

    public void onLogin(String id, String name);


}
