import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Program {
    public static void main(String[] args){
                try {
                    ServerSocket severSocket = new ServerSocket(6000);

                    while (true) {
                        Socket socket = severSocket.accept();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String remoteAddress = socket.getRemoteSocketAddress().toString();
                                    System.out.println(remoteAddress);

                                    InputStream is = socket.getInputStream();
                                    OutputStream os = socket.getOutputStream();

                                    while (true) {
                                        byte[] data = new byte[1024];
                                        int len = is.read(data);
                                        if (len == -1) {
                                            System.out.println("연결이 종료되었습니다.");
                                            break;
                                        }
                                        os.write(data, 0, len);
                                    }
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
}
