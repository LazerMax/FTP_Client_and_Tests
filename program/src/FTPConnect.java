import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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

    private void connectToFTPServer() throws IOException, InterruptedException {
        socket = new Socket(host, port);
        controlReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        controlWriter = new PrintWriter((socket.getOutputStream()), true);
        response = controlReader.readLine();
        if (!response.startsWith("220")) {
            throw new IOException("FTP server not responding");
        }
        controlWriter.println("USER " + username);
        response = controlReader.readLine();
        controlWriter.println("PASS " + password);
        while (!response.startsWith("230")) {
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

    private boolean initCommandRETR() throws IOException, InterruptedException {
        controlWriter.println("RETR " + fileName);
        response = controlReader.readLine();

        if (response.startsWith("550")) {
            return false;
        }
        response = controlReader.readLine();
        if (!response.startsWith("226")) {
                throw new IOException("\nFTP server not responding");
        }
        return true;
    }

    public void getFileFromFTP(boolean activeMode) throws Exception {

        String json = "";
        String str;

        if (activeMode) {
            enterActiveMode();

            if (!(initCommandRETR())) {
                return;
            }

            Socket accept = serverSocket.accept();

            BufferedReader reader2 = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            while ((str = reader2.readLine()) != null) {
                json += str;
            }

            accept.close();

        } else {
            enterPassiveMode();

            if (!(initCommandRETR())) {
                return;
            }

            BufferedReader reader2 = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            while ((str = reader2.readLine()) != null) {
                json += str;
            }
            dataSocket.close();
        }
        FileLoader.createLocalFile(json);
    }

    private void deleteFileFromFTP() throws IOException {
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
            enterActiveMode();
            deleteFileFromFTP();
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
            enterPassiveMode();

            deleteFileFromFTP();

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

