package connection;

public interface ServerEvent {
    public void onReceiveText(String sender, String text);

    public void onReceiveFile(String filename);

    public boolean onLogin(String id, String name);     // Needs auth, if id & name is pass, return true


}
