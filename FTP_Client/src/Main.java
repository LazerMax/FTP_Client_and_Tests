import java.io.File;
import java.util.Scanner;

public class Main {

   private static Scanner scanner = new Scanner(System.in);
   private static String host, userName, password;
   private static boolean activeMode = true;
   private static FTPConnect ftpConnect;
   private static Students students;

    //получение данных для подключения к FTP-серверу от пользователя
    private static void getFTPHostAndPort(){
        System.out.println("Введите ip-адрес FTP-сервера");
        host = scanner.nextLine();
        System.out.println("Введите логин для входа на FTP-сервер:");
        userName = scanner.nextLine();
        System.out.println("Введите пароль для входа на FTP-сервер");
        password = scanner.nextLine();
    }

    //выбор режима подключения к FTP-серверу
    private static void choiceFTPMode(){

        String startMenu = "\nВыберите режим подключения:\n"+
                "1. Активный режим\n"+
                "2. Пассивный режим";
        System.out.println(startMenu);

        int choice = 0;

        while (choice != 1 && choice != 2){
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e){
                System.out.println("Вы ввели не число! \n");
            }
            if(choice != 1 && choice != 2){
                System.out.println("Введите число 1 или 2");
            }
        }

        switch (choice){
            case 1: activeMode = true;
                break;
            case 2: activeMode = false;
        }
    }

    //выбор действия после подключения
    private static void choosingAnAction(boolean activeMode) throws Exception {

        File file = new File("Students.json");
        int choice = 0;
        String menu = "";
        while (!(file.exists())){
             menu =  "\nФайл Students.json не скачан\n"+
                     "\nВыберете цифру:\n"+
                     "1. Получить файл списка студентов с FTP-сервера";

            System.out.println(menu);

            while (choice != 1){
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e){
                    System.out.println("Вы ввели не число! \n");
                }
                if(choice != 1){
                    System.out.println("Введите число 1");
                }
            }
            ftpConnect.getFileFromFTP(activeMode);
        }

        try {
            students = new Students();
        }catch (Exception e){
            file.delete();
            ftpConnect.getFileFromFTP(activeMode);
            students = new Students();
        }

        menu = "\nВыберете цифру:"+
                "\n1. Получить файл списка студентов с FTP-сервера\n" +
                "2. Отправить файл списка студентов на FTP-сервер\n" +
                "3. Получить список студентов по имени\n"+
                "4. Получить информацию о студенте по id\n"+
                "5. Добавить студента\n"+
                "6. Удалить студента по id\n"+
                "7. Завершение работы\n";

        System.out.println(menu);


        choice = 0;

        while (choice < 1 || choice > 7){
            try {
                choice = Integer.parseInt(scanner.nextLine());
                System.out.println();
            } catch (NumberFormatException e){
                System.out.println("Вы ввели не число! \n");
            }
            if(choice < 1 || choice > 7){
                System.out.println("Введите число от 1 до 7");
            }
        }


        switch (choice){
            case 1: ftpConnect.getFileFromFTP(activeMode);
                break;
            case 2: ftpConnect.sendFileFromFTP(activeMode);
                break;
            case 3: students.getListByName();
                    System.out.println(students.result);
                break;
            case 4:
                System.out.println("Введите id студента:");
                int id = Integer.parseInt(scanner.nextLine());
                Student student = students.getStudentById(id);
                if (student == null){
                    System.out.println("Студента с таким id не найдено!");
                }else{
                    System.out.println(student);
                }
                break;
            case 5:
                System.out.println("Введите имя студента:");
                String name = scanner.nextLine();
                students.addStudent(name);
                break;
            case 6:
                System.out.println("Введите id студента");
                id = Integer.parseInt(scanner.nextLine());
                students.removeStudent(id);
                break;
            case 7: return;
        }
            choosingAnAction(activeMode);
    }

    public static void main(String[] args) throws Exception {

        getFTPHostAndPort();

        ftpConnect = new FTPConnect(host, userName, password);

        choiceFTPMode();

        choosingAnAction(activeMode);
    }
}

