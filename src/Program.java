import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

interface OnReceived {
    void onReceive(int user, String packet);
}

class Reader extends Thread{
    private int user;
    private Socket socket;

    private OnReceived onReceived;

    Reader(int user, Socket socket){
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


class Server{
    private Socket[] users;
    private OnReceived onReceived = new OnReceived() {
        @Override
        public void onReceive(int user, String packet) {
            System.out.println(user + "-" + packet);
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

    private void startGame() throws IOException {
        for(int i=0; i<2 ; i++){
            Socket socket = users[i];
            OutputStream os = socket.getOutputStream();

            String packet = "START : " + i;
            os.write(packet.getBytes());
        }
    }
}

public class Program {

    HashMap area;
    public static void main(String args[]) {
        Server server = new Server();
        server.start();
    }

    /*Program() {
        clients = new HashMap(); // (좌표, 돌색)
        Collections.synchronizedMap(clients);
    }

    public void start() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(7777);
            System.out.println("서버가 시작되었습니다.");
            while (true) {
                socket = serverSocket.accept();
                if (clients.size() == 1) {
                    u1 = new User("B", socket);
                } else if (clients.size() == 2) {
                    u2 = new User("W", socket);
                } else {
                    break;
                }

                System.out.println("[" + socket.getInetAddress() + ":"
                        + socket.getPort() + "]" + "에서 접속하였습니다.");
                ServerReceiver thread = new ServerReceiver(socket);
                thread.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // start()


    void sendToAll(String msg) {
        Iterator it = clients.keySet().iterator();
        while (it.hasNext()) {
            try {
                DataOutputStream out = (DataOutputStream) clients.get(it.next());
                out.writeUTF(msg);
            } catch (IOException e) {
            }
        } // while
    } // sendToAll

    void sendOne(String msg, User user) {
        OutputStream out =
    }



    class ServerReceiver extends Thread {

        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        ServerReceiver(Socket socket) {
            this.socket = socket;
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
            }
        }


        public void run() {
            String name = "";
            try {
                name = in.readUTF();
                sendToAll("#" + name + "님이 들어오셨습니다.");
                clients.put(name, out);
                System.out.println("현재 서버접속자 수는 "
                        + clients.size() + "입니다.");
                while (in != null) {
                    sendToAll(in.readUTF());
                }
            } catch (IOException e) {
                // ignore
            } finally {
                sendToAll("#" + name + "님이 나가셨습니다.");
                clients.remove(name);
                System.out.println("[" + socket.getInetAddress() + ":"
                        + socket.getPort() + "]"
                        + "에서 접속을 종료하였습니다.");
                System.out.println("현재 서버접속자 수는 "
                        + clients.size() + "입니다.");
            } // try
        } // run
    } // ReceiverThread*/
}