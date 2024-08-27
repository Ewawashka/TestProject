import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Date;

public class FtpHelper {
    private final String serverIp;
    private final int serverPort;
    private final String user;
    private final String password;
    private final String filePath;
    private PrintWriter out;
    private Socket socket;

    public FtpHelper(String user, String password, String filePath) throws IOException {
        this(user, password, Properties.FTP_SERVER_IP, Properties.FTP_SERVER_PORT, filePath);
    }

    public FtpHelper(String user, String password, String serverIp, int serverPort, String filePath) throws IOException {
        this.serverIp = serverIp;
        this.user = user;
        this.password = password;
        this.filePath = filePath;
        this.serverPort = serverPort;
        open();
    }

    private static void sendCommand(PrintWriter out, String command) {
        if (command != null && !command.isEmpty()) {
            out.println(command);
            out.flush();
        }
    }


    public String downloadFilePassiveMode() throws IOException {
        changeDirectory();
        Socket dataSocket = getSocketInPassiveMode();
        sendRETR();
        return readFile(dataSocket);

    }

    public String downloadFileActiveMode() throws IOException {
        changeDirectory();
        ServerSocket dataServerSocket = getServerSocketInActiveMode();
        sendRETR();
        Socket dataSocket = dataServerSocket.accept();
        String result = readFile(dataSocket);
        dataServerSocket.close();
        return result;
    }


    private String getPortCommandString(int port) throws UnknownHostException {
        byte[] ipAddress;
        if (serverIp.equals(Properties.LOCALHOST)) {
            String[] splitedIp = serverIp.split("\\.");
            ipAddress = new byte[splitedIp.length];
            for (int i = 0; i < splitedIp.length; i++) {
                ipAddress[i] = Byte.parseByte(splitedIp[i]);
            }
        } else {
            InetAddress address = InetAddress.getLocalHost();
            ipAddress = address.getAddress();
        }

        // Преобразуем порт в два байта в сетевом порядке
        byte[] portBytes = ByteBuffer.allocate(2).putShort((short) port).array();

        // Формируем строку команды PORT
        return String.format("PORT %d,%d,%d,%d,%d,%d",
                ipAddress[0] & 0xff,
                ipAddress[1] & 0xff,
                ipAddress[2] & 0xff,
                ipAddress[3] & 0xff,
                portBytes[0] & 0xff,
                portBytes[1] & 0xff);
    }

    private String readFile(Socket dataSocket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[Properties.BUFFER_SIZE];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }

    private void changeDirectory() {
        String directory = filePath.substring(0, filePath.lastIndexOf("/"));
        if (!directory.isEmpty()) {
            sendCommand(out, "CWD " + directory);
        }
    }

    private void sendPort(int dataPort) throws IOException {
        String command = getPortCommandString(dataPort);
        sendCommand(out, command);
        checkResponse(command, "200");
    }

    private void sendRETR() throws IOException {
        String command = "RETR " + filePath.substring(filePath.lastIndexOf("/") + 1);
        sendCommand(out, command);
        checkResponse(command, "150");
    }

    private void sendSTOR() throws IOException {
        String command = "STOR " + filePath.substring(filePath.lastIndexOf("/") + 1);
        sendCommand(out, command);
        checkResponse(command, "150");
    }

    private void checkResponse(String command, String expectedResponseCode) throws IOException {
        String response = getResponse(expectedResponseCode);
        if (!response.startsWith(expectedResponseCode)) {
            throw new IOException("Не получили ожидаемый код ответа, после выполнения комманды:\" " + command + "\"\n" +
                    "Полученный ответ: " + response);
        }
    }

    public FtpHelper authorize() {
        sendCommand(out, "USER " + user);
        sendCommand(out, "PASS " + password);
        return this;
    }

    public FtpHelper close() throws IOException {
        out.close();
        socket.close();
        return this;
    }

    public FtpHelper open() throws IOException {
        this.socket = new Socket(serverIp, serverPort);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        return this;
    }

    private String getResponse(String code) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String answer = in.readLine();
        long finishWaitTime = new Date().getTime() + Properties.RESPONSE_WAIT_SEC * 1000;
        while (!answer.startsWith(code) && new Date().getTime() < finishWaitTime) {
            answer = in.readLine();
        }
        return answer;
    }

    private ServerSocket getServerSocketInActiveMode() throws IOException {
        ServerSocket dataServerSocket = new ServerSocket(0);
        dataServerSocket.setSoTimeout(Properties.ACTIVE_MODE_CONNECTION_WAIT_SEC * 1000);
        int dataPort = dataServerSocket.getLocalPort();
        sendPort(dataPort);
        return dataServerSocket;
    }

    private Socket getSocketInPassiveMode() throws IOException {
        sendCommand(out, "PASV");
        String response = getResponse("227");
        String[] parts = response.substring(response.indexOf('(') + 1, response.indexOf(')')).split(",");
        int port1 = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);
        return new Socket(serverIp, port1);
    }

    public FtpHelper uploadFileInPassiveMode(String fileData) throws IOException {
        Socket socketPassive = getSocketInPassiveMode();
        changeDirectory();
        sendSTOR();
        uploadFile(socketPassive, fileData);
        return this;
    }

    public FtpHelper uploadFileInActiveMode(String fileData) throws IOException {
        ServerSocket dataServerSocket = getServerSocketInActiveMode();
        sendSTOR();
        Socket dataSocket = dataServerSocket.accept();
        uploadFile(dataSocket, fileData);
        dataServerSocket.close();
        return this;
    }

    private void uploadFile(Socket socket, String fileData) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(fileData);
        writer.close();
        socket.close();
        // Проверка завершения передачи
        String response = getResponse("226");
        if (!response.startsWith("226")) {
            throw new IOException("File transfer failed");
        }
    }
}
