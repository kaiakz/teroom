package connection;

public class MsgCode {
    final static int LOGIN = 0;     // Client only
    final static int TEXT = 1;
    final static int FILE = 2;
    final static int QUIZ = 3;      // Server only
    final static int ANSWER = 4;     // Client only
    final static int SCREEN = 5;    // Server 2 Client
}
