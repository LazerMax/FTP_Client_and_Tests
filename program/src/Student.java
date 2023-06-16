public class Student implements Comparable <Student> {
    private int id;
    private String name;

    public Student (int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }


    // метод сортировки студентов по алфавиту
    public int compareTo(Student other) {
        return this.name.compareTo(other.getName());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append(System.lineSeparator())
                .append("\"id\": ")
                .append("\""+getId()+"\"")
                .append(",")
                .append(System.lineSeparator())
                .append("\"name\": ")
                .append("\""+getName()+"\"")
                .append(System.lineSeparator())
                .append("}")
                .append(System.lineSeparator());
        return sb.toString();
    }
}
