import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

interface OnReceived {
    void onReceive(int user, String packet);
}


class Reader extends Thread {
    private int user;
    private Socket socket;

    private OnReceived onReceived;

    Reader(int user, Socket socket) {
        this.user = user;
        this.socket = socket;
    }

    void setOnReceived(OnReceived onReceived) {
        this.onReceived = onReceived;
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();

            while (true) {
                byte[] buf = new byte[1024];
                int len = is.read(buf);
                if (len == -1) {
                    break;
                }

                String packet = new String(buf, 0, len);
                if (onReceived != null) {
                    onReceived.onReceive(user, packet);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class Server {
    private Socket[] users;
    private OnReceived onReceived = new OnReceived() {
        @Override
        public void onReceive(int user, String packet) {
            String[] packets = packet.split(":");
            String mode = packets[0];
            if(mode.equals("PUT")){
                int x= Integer.parseInt(packets[1]);
                int y= Integer.parseInt(packets[2]);
                putStone(user, x, y);
            }

        }

        private void putStone(int user, int x, int y) {
        }
    };

    void start() {
        users = new Socket[2];
        Reader[] readers = new Reader[2];

        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            for (int i = 0; i < 2; ++i) {
                Socket socket = serverSocket.accept();

                Reader reader = new Reader(i, socket);
                reader.setOnReceived(onReceived);
                reader.start();

                users[i] = socket;
                readers[i] = reader;
            }

            startGame();

            for (Reader reader : readers) {
                reader.join();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // START:{user_no}
    private void startGame() throws IOException {
        for (int user = 0; user < users.length; ++user) {
            Socket socket = users[user];
            OutputStream os = socket.getOutputStream();

            String packet = "START:" + user;
            os.write(packet.getBytes());
        }
    }
}

public class Program {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
