package connection;

public interface ServerEvent {
    public void onReceiveText(String sender, String text);

    public void onReceiveFile(String filename);

    public void onReceiveAnswer(String id, String name, String answer);

    public boolean onLogin(String id, String name);     // Needs auth, if id & name is pass, return true


}
