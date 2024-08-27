import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class FtpStudentClient {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // ���� ������, ������ � IP-������ FTP-�������
       try {
           while (true) {
               try {


                   System.out.print("������� �����: ");
                   String username = reader.readLine();
                   System.out.print("������� ������: ");
                   String password = reader.readLine();
               }catch (IOException e){
                   System.out.println(e.getMessage());
               }
               System.out.print("������� IP-����� FTP-�������: ");
               String ipAddress = reader.readLine();
           }
           // �������� ���� � ����� �� �������
           String filePath = "/path/to/json/file.json";

           // �������� ������� ��� ������ � FTP
           FtpClient ftpClient = new FtpClient(username, password, ipAddress, filePath);

           // �������� ������ � FTP
           String jsonString = ftpClient.loadFile();
           StudentManager studentManager = new StudentManager();
           studentManager.loadStudents(jsonString);

           // ����
           while (true) {
               System.out.println("����:");
               System.out.println("1. ��������� ������ ��������� �� �����");
               System.out.println("2. ��������� ���������� � �������� �� id");
               System.out.println("3. ���������� ��������");
               System.out.println("4. �������� �������� �� id");
               System.out.println("5. ���������� ������");
               System.out.print("�������� ��������: ");

               String choice = reader.readLine();

               switch (choice) {
                   case "1":
                       System.out.print("������� ��� ��������: ");
                       String name = reader.readLine();
                       studentManager.getStudentsByName(name);
                       break;
                   case "2":
                       System.out.print("������� id ��������: ");
                       int id = Integer.parseInt(reader.readLine());
                       studentManager.getStudentById(id);
                       break;
                   case "3":
                       System.out.print("������� ��� ������ ��������: ");
                       String newName = reader.readLine();
                       studentManager.addStudent(newName);
                       break;
                   case "4":
                       System.out.print("������� id �������� ��� ��������: ");
                       int deleteId = Integer.parseInt(reader.readLine());
                       studentManager.deleteStudentById(deleteId);
                       break;
                   case "5":
                       String updatedJson = studentManager.saveStudents();
                       ftpClient.saveFile(updatedJson);
                       System.out.println("���������� ������.");
                       return;
                   default:
                       System.out.println("�������� �����. ���������� �����.");
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}

