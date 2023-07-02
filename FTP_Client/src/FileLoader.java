import java.io.*;
import java.util.List;

public class FileLoader{

    public static String getStudentsJSON(){
        String studentsJSON = "";
        try{
            BufferedReader reader = new BufferedReader(new FileReader("Students.json"));
            String line;
            while ((line = reader.readLine()) != null){
                studentsJSON += line;
            }
        }catch (IOException e){
            System.out.println("Файла Students.json не существует");
        }
        return studentsJSON;
    }

    public static void changeFile(List<Student> students) throws Exception {

        File file = new File("Students.json");
        FileWriter writer = new FileWriter(file);

        students.forEach(student -> {
            try {
                writer.write(student.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
               });
        writer.close();
    }

    public static void createLocalFile(String json) throws IOException {
        File file = new File("Students.json");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(json);
        writer.close();
    }
}

