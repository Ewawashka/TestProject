import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class FtpStudentClient {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Ввод логина, пароля и IP-адреса FTP-сервера
       try {
           while (true) {
               try {


                   System.out.print("Введите логин: ");
                   String username = reader.readLine();
                   System.out.print("Введите пароль: ");
                   String password = reader.readLine();
               }catch (IOException e){
                   System.out.println(e.getMessage());
               }
               System.out.print("Введите IP-адрес FTP-сервера: ");
               String ipAddress = reader.readLine();
           }
           // Указание пути к файлу на сервере
           String filePath = "/path/to/json/file.json";

           // Создание клиента для работы с FTP
           FtpClient ftpClient = new FtpClient(username, password, ipAddress, filePath);

           // Загрузка данных с FTP
           String jsonString = ftpClient.loadFile();
           StudentManager studentManager = new StudentManager();
           studentManager.loadStudents(jsonString);

           // Меню
           while (true) {
               System.out.println("Меню:");
               System.out.println("1. Получение списка студентов по имени");
               System.out.println("2. Получение информации о студенте по id");
               System.out.println("3. Добавление студента");
               System.out.println("4. Удаление студента по id");
               System.out.println("5. Завершение работы");
               System.out.print("Выберите действие: ");

               String choice = reader.readLine();

               switch (choice) {
                   case "1":
                       System.out.print("Введите имя студента: ");
                       String name = reader.readLine();
                       studentManager.getStudentsByName(name);
                       break;
                   case "2":
                       System.out.print("Введите id студента: ");
                       int id = Integer.parseInt(reader.readLine());
                       studentManager.getStudentById(id);
                       break;
                   case "3":
                       System.out.print("Введите имя нового студента: ");
                       String newName = reader.readLine();
                       studentManager.addStudent(newName);
                       break;
                   case "4":
                       System.out.print("Введите id студента для удаления: ");
                       int deleteId = Integer.parseInt(reader.readLine());
                       studentManager.deleteStudentById(deleteId);
                       break;
                   case "5":
                       String updatedJson = studentManager.saveStudents();
                       ftpClient.saveFile(updatedJson);
                       System.out.println("Завершение работы.");
                       return;
                   default:
                       System.out.println("Неверный выбор. Попробуйте снова.");
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}

