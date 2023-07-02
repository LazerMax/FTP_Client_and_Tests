import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestsOfStudentsClass {

    public static Students students;

    @BeforeClass
    public static void callGenerateStudentsJSON() throws Exception {
    TestsOfFTPConnectClass.generateStudentsJSON();
}
    @BeforeMethod
    public static void updateTheList() throws Exception {
        students = new Students();
    }

    @Test(description = "check get list of students by name")
    public void testGetListOfStudentsByName(){
        students.getListByName();
        String expectedResult =
                "Arkady\n" +
            "Ivan\n" +
            "Maksim\n";
        Assert.assertEquals(expectedResult, students.result);
    }

    @Test(description = "check get information about student by id")
    public void testGetInformationAboutStudentById(){
        String expectedResult = new Student(1, "Arkady").toString();
        Assert.assertEquals(expectedResult, students.getStudentById(1).toString());
    }

    @Test(description = "check add student", dependsOnMethods = "testGetListOfStudentsByName")
    public void testAddStudent() throws Exception {
        students.addStudent("Komar");
        students.getListByName();
        Assert.assertTrue(students.result.contains("Komar"));
    }

    @Test(description = "check remove student", dependsOnMethods = "testGetListOfStudentsByName")
    public void testRemoveStudent() throws Exception {
        students.removeStudent(1);
        students.getListByName();
        System.out.println(students.result);
        Assert.assertFalse(students.result.contains("Arkady"));
    }
}
