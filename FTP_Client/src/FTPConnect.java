import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class FTPConnect {
    private String host;
    private int port = 21;
    private String username;
    private String password;
    private Socket socket;
    private ServerSocket serverSocket;
    private Socket dataSocket;
    private BufferedReader controlReader;
    private PrintWriter controlWriter;
    private String response;

    String fileName = "Students.json";

    public FTPConnect(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public void connectToFTPServer() throws IOException, InterruptedException {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 5000);
        } catch (Exception e){
            throw new SocketException("FTP-сервер не отвечает! Проверьте правильность IP-адреса");
        }
        controlReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        controlWriter = new PrintWriter((socket.getOutputStream()), true);

        controlWriter.println("USER " + username);
        response = controlReader.readLine();
        controlWriter.println("PASS " + password);

        while (!response.startsWith("230")) {
            if(response.startsWith("530")){
                throw new SocketException("Неккоректный логин или пароль");
            }
            response = controlReader.readLine();
        }
    }

    private void enterPassiveMode() {
        try {
            connectToFTPServer();
            controlWriter.println("PASV");
            response = controlReader.readLine();
            int openingParenthesis = response.indexOf("(");
            int closingParenthesis = response.indexOf(")", openingParenthesis + 1);
            if (closingParenthesis > 0) {
                String data = response.substring(openingParenthesis + 1, closingParenthesis);
                String[] parts = data.split(",");
                if (parts.length == 6) {
                    String ip = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
                    int portNumber = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);
                    dataSocket = new Socket(ip, portNumber);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void enterActiveMode() throws IOException, InterruptedException {
        connectToFTPServer();
        serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        String localAddress = socket.getLocalAddress().getHostAddress().replace('.', ',');
        String portString = String.format("%d,%d", port / 256, port % 256);
        String addressString = String.format("%s", localAddress);
        controlWriter.println(String.format("PORT %s,%s", addressString, portString));
        response = controlReader.readLine();
    }

    private void initCommandRETR() throws IOException{
        controlWriter.println("RETR " + fileName);
        response = controlReader.readLine();

        if (response.startsWith("550")) {
            throw new EOFException("Файл не найден на FTP-сервере");
        }
        response = controlReader.readLine();
        if (!response.startsWith("226")) {
                throw new IOException("\nFTP server not responding");
        }
    }

    public void getFileFromFTP(boolean activeMode) throws Exception {

        String json = "";
        String str;

        if (activeMode) {
            enterActiveMode();

            initCommandRETR();
            Socket accept = serverSocket.accept();

            BufferedReader reader2 = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            while ((str = reader2.readLine()) != null) {
                json += str;
            }

            accept.close();

        } else {
            enterPassiveMode();
            initCommandRETR();

            BufferedReader reader2 = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            while ((str = reader2.readLine()) != null) {
                json += str;
            }
            dataSocket.close();
        }

        if (json == ""){
            throw new Exception("Файл на FTP-сервере не найден, пуст или не соответствует требованиям формата");
        }
        FileLoader.createLocalFile(json);
    }

    public void deleteFileFromFTP() throws IOException {
        controlWriter.println("DELE " + fileName);
        response = controlReader.readLine();
        if (!response.startsWith("250")) {
            throw new IOException("\nFTP server not responding");
        }
    }

    private void initCommandStore() throws IOException {
        controlWriter.println("STOR " + fileName);
        response = controlReader.readLine();
    }

    public void sendFileFromFTP(boolean activeMode) throws IOException, InterruptedException {

        PrintWriter writer;
        File file = new File("Students.json");
        Scanner scanner = new Scanner(file);

        if (activeMode) {
            deleteFileFromFTP();
            enterActiveMode();
            initCommandStore();

            Socket accept = serverSocket.accept();
            writer = new PrintWriter((accept.getOutputStream()), true);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                writer.println(line);
            }
            accept.close();
            writer.close();
        } else {
            deleteFileFromFTP();
            enterPassiveMode();

            writer = new PrintWriter((dataSocket.getOutputStream()), true);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                writer.println(line);
            }

            initCommandStore();
            socket.close();
            writer.close();
        }
    }
}

