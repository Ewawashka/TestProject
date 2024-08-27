import java.io.*;
import java.net.URL;

public class FtpClient {
    private String username;
    private String password;
    private String ipAddress;
    private String filePath;

    public FtpClient(String username, String password, String ipAddress, String filePath) {
        this.username = username;
        this.password = password;
        this.ipAddress = ipAddress;
        this.filePath = filePath;
    }

    public String loadFile() {
        String ftpUrl = String.format("ftp://%s:%s@%s%s", username, password, ipAddress, filePath);
        StringBuilder jsonString = new StringBuilder();

        try {
            URL url = new URL(ftpUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString.toString();
    }

    public void saveFile(String data) {
        String ftpUrl = String.format("ftp://%s:%s@%s%s", username, password, ipAddress, filePath);

        try {
            URL url = new URL(ftpUrl);
            OutputStream outputStream = url.openConnection().getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            writer.write(data);
            writer.flush();
            writer.close();

            System.out.println("—писок студентов успешно сохранен на FTP-сервере.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
