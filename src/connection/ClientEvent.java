package connection;

public interface ClientEvent {
    public void onReceiveText(String sender, String text);

    public void onReceiveFile(String sender, String filename);

    public void onReceivedQuiz(String quiz);

//    public void onReceiveCommand();

//    public void onReceiveVideo();
}
