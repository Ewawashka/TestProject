import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class StudentManager {
    private List<Student> students;

    public StudentManager() {
        this.students = new CopyOnWriteArrayList<>();
    }

    public List<Student> getStudents() {
        return students;
    }

    public void loadStudents(String jsonString) {
        students = parseJsonToList(jsonString);
    }

    public String saveStudents() {
        return studentsToJsonString();
    }

    public void addStudent(String name) {
        int newId = generateNewId();
        Student student = new Student(newId, name);
        students.add(student);
        System.out.println("Студент успешно добавлен: " + student);
    }

    public void deleteStudentById(int id) {
        students.removeIf(student -> student.getId() == id);
        System.out.println("Студент с id " + id + " удален.");
    }

    public void getStudentsByName(String name) {
        students.stream()
                .filter(student -> student.getName().equalsIgnoreCase(name))
                .sorted(Comparator.comparing(Student::getName))
                .forEach(System.out::println);
    }

    public void getStudentById(int id) {
        students.stream()
                .filter(student -> student.getId() == id)
                .findFirst()
                .ifPresent(System.out::println);
    }

    private int generateNewId() {
        int i = -1;
        for (Student student : students) {
            if (student.getId() > i) {
                i = student.getId();
            }
        }
        return ++i;
    }

    // Метод для парсинга JSON строки в список студентов
    private List<Student> parseJsonToList(String jsonString) {
        List<Student> students = new ArrayList<>();
        String studentsArray = jsonString.substring(jsonString.indexOf("[") + 1, jsonString.lastIndexOf("]"));
        String[] jsonObjects = studentsArray.split("(?<=\\}),\\s*(?=\\{)");
//        Map<String,String> map = getJsonObjects(studentsArray);
//        boolean isAdd = false;
//        for (Map.Entry<String,String> pair :
//            map.entrySet() ) {
//            Student student = new Student();
//            switch (pair.getKey()) {
//                case ("name"): {
//                    student.setName(pair.getValue());
//                    break;
//                }
//                case ("id"): {
//                    student.setId(Integer.parseInt(pair.getValue()));
//                    isAdd = true;
//                    break;
//                }
//            }
//            if (isAdd) {
//                students.add(student);
//            }
//        }
       for (String jsonObject : getJsonObjects(studentsArray)) {
           students.add(parseStudent(jsonObject));
        }

        return students;
    }

    private List<String> getJsonObjects(String jsonString) {
        List<String> result = new ArrayList<>();
        boolean isStart = false;
        StringBuilder builder = new StringBuilder();
        for (char c:
             jsonString.toCharArray()) {

            if(c == '{' && !isStart){
                isStart = true;
            }else {
                if (c == '}' && isStart) {
                    isStart = false;
                    if(builder.length() > 0){
                        result.add(builder.toString());
                        builder = new StringBuilder();
                    }
                }else {
                    if(isStart ){
                        builder.append(c);
                    }
                }
            }

        }
        return result;
    }

    // Метод для парсинга одного JSON объекта в объект Student
    private Student parseStudent(String jsonObject) {
        Student student = new Student();

        jsonObject = jsonObject.trim();
        if (jsonObject.startsWith("{")) {
            jsonObject = jsonObject.substring(1);
        }
        if (jsonObject.endsWith("}")) {
            jsonObject = jsonObject.substring(0, jsonObject.length() - 1);
        }

        String[] pairs = jsonObject.split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            String key = keyValue[0].trim().replaceAll("\"", "");
            String value = keyValue[1].trim().replaceAll("\"", "");

            switch (key) {
                case "name":
                    student.setName(value);
                    break;
                case "id":
                    student.setId(Integer.parseInt(value));
                    break;
            }
        }

        return student;
    }

    // Метод для преобразования списка студентов обратно в JSON строку
    public String studentsToJsonString() {
        StringBuilder json = new StringBuilder();
        json.append("{ \"students\": [");

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            json.append("{");
            json.append("\"id\": ").append(student.getId()).append(", ");
            json.append("\"name\": \"").append(student.getName()).append("\"");
            json.append("}");

            if (i < students.size() - 1) {
                json.append(", ");
            }
        }

        json.append("] }");

        return json.toString();
    }
}

