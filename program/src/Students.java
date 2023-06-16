import java.util.*;
import java.util.stream.Collectors;

public class Students {

    private List<Student> students = new ArrayList<>();
    private static String studentsJSON;

    // Создание списка студентов
    public void getArrayOfStudents() throws Exception {

        studentsJSON = FileLoader.getStudentsJSON();
        studentsJSON = studentsJSON.replaceAll("[,{}]", "\n");
        studentsJSON = studentsJSON.replaceAll("\\s+", " ");
        studentsJSON = studentsJSON.replaceAll("\"", "");

        String [] strObjects = studentsJSON.split(" ");

        for (int i = 0; i < strObjects.length; ++i) {
            int id;
            String name;
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
        }
        Collections.sort(this.students);
        FileLoader.changeFile(this.students);
    }

    //получение списка студентов по имени
    public void getListByName (){

        System.out.println("Введите имя студента:");

        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        List <Student> studentsByName = this.students.stream().filter(item -> name.equals(item.getName()))
                .collect(Collectors.toList());

        if (studentsByName != null){
            Collections.sort(studentsByName);
            System.out.println();
            studentsByName.forEach(student -> System.out.println(student.toString()));
        }else{
            System.out.println("Студентов с таким именем не найдено!");
        }
    }


    //проверка наличия студентов с одинаковым id
    public void checkId(){
        for (int i = 0; i < this.students.size(); i++) {
            for (int j = i + 1; j < this.students.size(); j++) {
                if (this.students.get(i).getId() == (this.students.get(j).getId())) {
                    throw new Error("В списке есть студенты с одинаковым id!");
                }
            }
        }
    }

    //получение информации о студенте по id
    public void getStudentById (){

        System.out.println("Введите id студента:");
        Scanner scanner = new Scanner(System.in);

        int id = Integer.parseInt(scanner.nextLine());

        Student student = this.students.stream().filter(item -> id == item.getId()).findFirst().orElse(null);
        if (student != null){
            System.out.println();
            System.out.println(student);
        } else{
            System.out.println("Студента с таким id не найдено!");
        }
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
    public void addStudent () throws Exception {

        System.out.println("Введите имя студента:");

        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        this.students.add(new Student(generateId(), name));
        Collections.sort(this.students);
        FileLoader.changeFile(students);
    }

    //удаление студента по id
    public void removeStudent () throws Exception{
        System.out.println("Введите id студента");

        Scanner scanner = new Scanner(System.in);
        int id = Integer.parseInt(scanner.nextLine());

        this.students.remove(this.students.stream().filter(item -> id == item.getId()).findFirst().orElse(null));
        FileLoader.changeFile(students);
    }
}
