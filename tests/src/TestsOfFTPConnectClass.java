import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;

public class TestsOfFTPConnectClass {

    private FTPConnect ftpConnect;
    private File file = new File("Students.json");

    public static void generateStudentsJSON() throws IOException {
        String json = "{\n" +
                "  \"students\": [\n" +
                "    {\n" +
                "      \"id\": 3,\n" +
                "      \"name\": \"Maksim\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 2,\n" +
                "      \"name\": \"Ivan\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"name\": \"Arkady\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        FileLoader.createLocalFile(json);
    }

    @Test(description = "check connectToFTP", priority = 1)
    public void positiveTestConnectToFTP(){
        ftpConnect = new FTPConnect("127.0.0.1", "root", "aaaaaa");
        Assert.assertTrue(true);
    }

    @Test(description = "check Exception FTP Server not response", expectedExceptions = {SocketException.class}, expectedExceptionsMessageRegExp = "FTP-сервер не отвечает! Проверьте правильность IP-адреса", priority = 2)
    public void testExceptionFTPNotResponse() throws IOException, InterruptedException {
        ftpConnect = new FTPConnect("1.1.1.1", "root", "aaaaaa");
        ftpConnect.connectToFTPServer();
    }

    @Test(description = "check Exception FTP incorrect login", expectedExceptions = {SocketException.class}, expectedExceptionsMessageRegExp = "Неккоректный логин или пароль", priority = 2)
    public void testExceptionFTPIncorrectLogin() throws IOException, InterruptedException {
        ftpConnect = new FTPConnect("127.0.0.1", "Test", "aaaaaa");
        ftpConnect.connectToFTPServer();
    }

    @Test(description = "check Exception FTP incorrect password", expectedExceptions = {SocketException.class}, expectedExceptionsMessageRegExp = "Неккоректный логин или пароль", priority = 2)
    public void testExceptionFTPIncorrectPassword() throws IOException, InterruptedException {
        ftpConnect = new FTPConnect("127.0.0.1", "root", "Test");
        ftpConnect.connectToFTPServer();
    }

    @Test(description = "check active mode  positive test get file", priority = 1, dependsOnMethods = "testSendFileFromFTPActiveMode")
    public void testGetFileFromFTPActiveMode(){
        ftpConnect = new FTPConnect("127.0.0.1", "root", "aaaaaa");
        if(file.exists()){
            file.delete();
        }
        try {
            ftpConnect.getFileFromFTP(true);
        }catch (Exception e){
            System.out.println(e);
        }
        Assert.assertTrue(file.exists());
    }

    @Test(description = "check passive mode positive test get file", priority = 1, dependsOnMethods = "testSendFileFromFTPPassiveMode")
    public void testGetFileFromFTPPassiveMode(){
        ftpConnect = new FTPConnect("127.0.0.1", "root", "aaaaaa");
        if(file.exists()){
            file.delete();
        }
        try {
            ftpConnect.getFileFromFTP(false);
        }catch (Exception e){
            System.out.println(e);
        }
        Assert.assertTrue(file.exists());
    }

    @Test(description = "check active mode test send file", priority = 1)
    public void testSendFileFromFTPPassiveMode() throws IOException, InterruptedException {
        generateStudentsJSON();
        ftpConnect = new FTPConnect("127.0.0.1", "root", "aaaaaa");
        ftpConnect.connectToFTPServer();
        ftpConnect.deleteFileFromFTP();
        ftpConnect.sendFileFromFTP(true);
        Assert.assertTrue(true);
    }

    @Test(description = "check passive mode test send file", priority = 1)
    public void testSendFileFromFTPActiveMode() throws IOException, InterruptedException {
        generateStudentsJSON();
        ftpConnect = new FTPConnect("127.0.0.1", "root", "aaaaaa");
        ftpConnect.connectToFTPServer();
        ftpConnect.deleteFileFromFTP();
        ftpConnect.sendFileFromFTP(false);
        Assert.assertTrue(true);
    }

    @Test(description = "check Exception file was not found on the FTP", expectedExceptions = {Exception.class}, expectedExceptionsMessageRegExp = "Файл не найден на FTP-сервере", priority = 2)
    public void testExceptionFTPWithoutFile() throws Exception {
        ftpConnect = new FTPConnect("127.0.0.1", "root", "aaaaaa");
        if(file.exists()){
            file.delete();
        }
        ftpConnect.connectToFTPServer();
        ftpConnect.deleteFileFromFTP();
        ftpConnect.getFileFromFTP(true);
    }
}
