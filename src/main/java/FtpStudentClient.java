import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FtpStudentClient {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        FtpHelper ftpHelper;
        // ���� ������, ������ � IP-������ FTP-�������
        try {
            ftpHelper = getFtpHelper(Properties.FTP_SERVER_DEFAULT_FILE_PATH, Properties.FTP_SERVER_IP, Properties.FTP_SERVER_PORT);
            ftpHelper.authorize();
            // ����
            while (true) {
                try {
                    System.out.println("����:");
                    System.out.println("1. ��������� ������ ��������� �� �����");
                    System.out.println("2. ��������� ���������� � �������� �� id");
                    System.out.println("3. ���������� ��������");
                    System.out.println("4. �������� �������� �� id");
                    System.out.println("b. �������� � ������� ����");
                    System.out.println("q. ���������� ������");
                    System.out.println("m. ������� ��������/��������� ������ � ftp");
                    System.out.print("�������� ��������: ");

                    String choice = reader.readLine();
                    boolean isActive = false;
                    StudentManager studentManager = new StudentManager();
                    switch (choice) {
                        case "1": {
                            System.out.print("������� ��� ��������: ");
                            String name = reader.readLine();
                            String serverResponse = ftpHelper.downloadFile(isActive);
                            studentManager.loadStudents(serverResponse);
                            studentManager.getStudentsByName(name);
                            break;
                        }
                        case "2": {
                            System.out.print("������� id ��������: ");
                            int id = Integer.parseInt(reader.readLine());
                            String serverResponse = ftpHelper.downloadFile(isActive);
                            studentManager.loadStudents(serverResponse);
                            studentManager.getStudentById(id);
                            break;
                        }
                        case "3": {
                            System.out.print("������� ��� ������ ��������: ");
                            String newName = reader.readLine();
                            String serverResponse = ftpHelper.downloadFile(isActive);
                            studentManager.loadStudents(serverResponse);
                            studentManager.addStudent(newName);
                            ftpHelper.uploadFile(isActive, studentManager.studentsToJsonString());
                            break;
                        }
                        case "4": {
                            System.out.print("������� id �������� ��� ��������: ");
                            int deleteId = Integer.parseInt(reader.readLine());
                            String serverResponse = ftpHelper.downloadFile(isActive);
                            studentManager.loadStudents(serverResponse);
                            studentManager.deleteStudentById(deleteId);
                            ftpHelper.uploadFile(isActive, studentManager.studentsToJsonString());
                            break;
                        }
                        case "m": {
                            boolean isRun = true;
                            while (isRun) {
                                System.out.println("������� ����� ������:" + (isActive ? "��������" : "���������") + " �������� y/n?");
                                switch (reader.readLine()) {
                                    case "y": {
                                        isActive = !isActive;
                                        isRun = false;
                                        break;
                                    }
                                    case "n": {
                                        isRun = false;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        case "q": {
                            System.out.println("���������� ������.");
                            return;
                        }
                        case "b": {
                            ftpHelper = getFtpHelper(ftpHelper.getFilePath(), ftpHelper.getServerIp(), ftpHelper.getServerPort());
                            ftpHelper.authorize();
                            break;
                        }
                        default: {
                            System.out.println("�������� �����. ���������� �����.");
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FtpHelper getFtpHelper(String filePath, String serverIp, int serverPort) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("������� �����: ");
        String username = reader.readLine();
        System.out.print("������� ������: ");
        String password = reader.readLine();
        boolean isStop = false;
        serverIp = askToChangeValue(serverIp, "IP-����� FTP-�������:", reader);
        serverPort = Integer.parseInt(askToChangeValue(String.valueOf(serverPort), "IP-port FTP-�������:", reader));
        filePath = askToChangeValue(filePath, "���� �� ����� ��  FTP-�������:", reader);
        return new FtpHelper(username, password, serverIp, serverPort, filePath);
    }

    private static String askToChangeValue(String value, String message, BufferedReader reader) throws IOException {
        boolean isStop = false;
        while (!isStop) {
            System.out.println(message + " " + value + " �������� y/n ?");
            switch (reader.readLine()) {
                case "y": {
                    System.out.print("������� " + message);
                    value = reader.readLine();
                    isStop = true;
                    break;
                }
                case "n": {
                    isStop = true;
                    break;
                }
            }
        }
        return value;
    }
}

