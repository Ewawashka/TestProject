import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FtpStudentClient {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        FtpHelper ftpHelper;
        // Ввод логина, пароля и IP-адреса FTP-сервера
        try {
            ftpHelper = getFtpHelper(Properties.FTP_SERVER_DEFAULT_FILE_PATH, Properties.FTP_SERVER_IP, Properties.FTP_SERVER_PORT);
            ftpHelper.authorize();
            // Меню
            while (true) {
                try {
                    System.out.println("Меню:");
                    System.out.println("1. Получение списка студентов по имени");
                    System.out.println("2. Получение информации о студенте по id");
                    System.out.println("3. Добавление студента");
                    System.out.println("4. Удаление студента по id");
                    System.out.println("b. Вернутся в прошлое меню");
                    System.out.println("q. Завершение работы");
                    System.out.println("m. Выбрать активный/пассивный работы с ftp");
                    System.out.print("Выберите действие: ");

                    String choice = reader.readLine();
                    boolean isActive = false;
                    StudentManager studentManager = new StudentManager();
                    switch (choice) {
                        case "1": {
                            System.out.print("Введите имя студента: ");
                            String name = reader.readLine();
                            String serverResponse = ftpHelper.downloadFile(isActive);
                            studentManager.loadStudents(serverResponse);
                            studentManager.getStudentsByName(name);
                            break;
                        }
                        case "2": {
                            System.out.print("Введите id студента: ");
                            int id = Integer.parseInt(reader.readLine());
                            String serverResponse = ftpHelper.downloadFile(isActive);
                            studentManager.loadStudents(serverResponse);
                            studentManager.getStudentById(id);
                            break;
                        }
                        case "3": {
                            System.out.print("Введите имя нового студента: ");
                            String newName = reader.readLine();
                            String serverResponse = ftpHelper.downloadFile(isActive);
                            studentManager.loadStudents(serverResponse);
                            studentManager.addStudent(newName);
                            ftpHelper.uploadFile(isActive, studentManager.studentsToJsonString());
                            break;
                        }
                        case "4": {
                            System.out.print("Введите id студента для удаления: ");
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
                                System.out.println("Текущий режим работы:" + (isActive ? "Активный" : "Пассивный") + " изменить y/n?");
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
                            System.out.println("Завершение работы.");
                            return;
                        }
                        case "b": {
                            ftpHelper = getFtpHelper(ftpHelper.getFilePath(), ftpHelper.getServerIp(), ftpHelper.getServerPort());
                            ftpHelper.authorize();
                            break;
                        }
                        default: {
                            System.out.println("Неверный выбор. Попробуйте снова.");
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
        System.out.print("Введите логин: ");
        String username = reader.readLine();
        System.out.print("Введите пароль: ");
        String password = reader.readLine();
        boolean isStop = false;
        serverIp = askToChangeValue(serverIp, "IP-адрес FTP-сервера:", reader);
        serverPort = Integer.parseInt(askToChangeValue(String.valueOf(serverPort), "IP-port FTP-сервера:", reader));
        filePath = askToChangeValue(filePath, "Путь до файла на  FTP-сервере:", reader);
        return new FtpHelper(username, password, serverIp, serverPort, filePath);
    }

    private static String askToChangeValue(String value, String message, BufferedReader reader) throws IOException {
        boolean isStop = false;
        while (!isStop) {
            System.out.println(message + " " + value + " изменить y/n ?");
            switch (reader.readLine()) {
                case "y": {
                    System.out.print("Введите " + message);
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

