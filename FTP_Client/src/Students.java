import java.util.*;
import java.util.stream.Collectors;

public class Students {

    private List<Student> students = new ArrayList<>();
    private static String studentsJSON;
    public String result = "";

    // Создание списка студентов
    public Students() throws Exception {

        studentsJSON = FileLoader.getStudentsJSON();
        studentsJSON = studentsJSON.replaceAll("[,{}]", "\n");
        studentsJSON = studentsJSON.replaceAll("\\s+", " ");
        studentsJSON = studentsJSON.replaceAll("\"", "");

        String [] strObjects = studentsJSON.split(" ");

        for (int i = 0; i < strObjects.length; ++i) {
            int id;
            String name;
            try {
                if (strObjects[i].contains("id:")) {
                    id = Integer.parseInt(strObjects[i + 1]);
                    for (int j = i + 2; j < strObjects.length; ++j) {
                        if (strObjects[j].contains("name:")) {
                            name = strObjects[j+1];
                            students.add(new Student(id, name));
                            break;
                        }
                    }
                }
            }catch (Exception e){
                throw new Exception("Файл students.json не соответствует требованиям формата");
            }
        }
        if(students.isEmpty()){
            throw new Exception("Файл students.json пуст");
        }
        Collections.sort(this.students);
        FileLoader.changeFile(this.students);
    }

    //получение списка студентов по имени
    public void getListByName (){
        students.forEach(student -> result += student.getName()+"\n");
    }

    //получение информации о студенте по id
    public Student getStudentById (int id){
        Student student = this.students.stream().filter(item -> id == item.getId()).findFirst().orElse(null);
        return student;
    }

    //создание уникального id
    public int generateId (){
        Random random = new Random();
        int id = -1;
        boolean tmp = true;

        while (tmp){
            tmp = false;
            id = random.nextInt(Integer.MAX_VALUE);
            for (Student student: this.students){
                if(student.getId() == id){
                    tmp = true;
                }
            }
        }
        return id;
    }

    //добавление студента в список
    public void addStudent (String name) throws Exception {
        this.students.add(new Student(generateId(), name));
        Collections.sort(this.students);
        FileLoader.changeFile(students);
    }

    //удаление студента по id
    public void removeStudent (int id) throws Exception{
        this.students.remove(this.students.stream().filter(item -> id == item.getId()).findFirst().orElse(null));
        FileLoader.changeFile(students);
    }

}
