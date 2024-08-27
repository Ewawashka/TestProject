public class Student {
    private String name;
    private int id;

    public Student() {}

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id + ", " +
                "\"name\": \"" + name + "\"" +
                "}";
    }
}
