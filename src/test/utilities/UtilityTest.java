package com.eliasnogueira.test.utilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.remote.codec.w3c.W3CHttpCommandCodec;
import org.openqa.selenium.remote.codec.w3c.W3CHttpResponseCodec;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import java.io.File;


public class UtilityTest {
     public static RemoteWebDriver createDriverFromSession(final SessionId sessionId, URL command_executor){
        CommandExecutor executor = new HttpCommandExecutor(command_executor) {

            @Override
            public Response execute(Command command) throws IOException {
                Response response = null;
                if (command.getName() == "newSession") {
                    response = new Response();
                    response.setSessionId(sessionId.toString());
                    response.setStatus(0);
                    response.setValue(Collections.<String, String>emptyMap());

                    try {
                        Field commandCodec = null;
                        commandCodec = this.getClass().getSuperclass().getDeclaredField("commandCodec");
                        commandCodec.setAccessible(true);
                        commandCodec.set(this, new W3CHttpCommandCodec());

                        Field responseCodec = null;
                        responseCodec = this.getClass().getSuperclass().getDeclaredField("responseCodec");
                        responseCodec.setAccessible(true);
                        responseCodec.set(this, new W3CHttpResponseCodec());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                } else {
                    response = super.execute(command);
                }
                return response;
            }
        };
        return new RemoteWebDriver(executor, new DesiredCapabilities());
    }

    public static void createDriverAndSaveDriverInfo(){

        System.setProperty("webdriver.chrome.driver", "C:/Users/LeaderGroupUser/.cache/selenium/chromedriver/win32/89.0.4389.23/chromedriver.exe");

        ChromeDriver driver = new ChromeDriver();
        HttpCommandExecutor executor = (HttpCommandExecutor) driver.getCommandExecutor();
        URL driver_url = executor.getAddressOfRemoteServer();
        SessionId session_id = driver.getSessionId();
        try {
            PrintWriter writer = new PrintWriter("src/test/resources/driver-info.properties", "UTF-8");
            writer.println("session_id = " + session_id);
            writer.println("driver_url = " + driver_url);
            writer.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean driverInfoExists(){
        File f = new File("src/test/resources/driver-info.properties"); 
        return f.exists();
    }

    // public static URL readURL(){
    //     File f = new File("driver-info.txt"); 
    //     // f.read
    //     return f.exists();
    // }
    // public static SessionId readSessionId(){
    //     File f = new File("driver-info.txt"); 
    //     return f.exists();
    // }


    public static void saveDriverInfo(RemoteWebDriver driver){

        HttpCommandExecutor executor = (HttpCommandExecutor) driver.getCommandExecutor();
        URL driver_url = executor.getAddressOfRemoteServer();
        SessionId session_id = driver.getSessionId();
        try {
            PrintWriter writer = new PrintWriter("src/test/resources/driver-info.properties", "UTF-8");
            writer.flush();
            writer.println("session_id = " + session_id);
            writer.println("driver_url = " + driver_url);
            writer.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String [] args) {
        createDriverAndSaveDriverInfo();

        // try {
        //     SessionId session_id = new SessionId("8411bc566b53fe1d7e9fcf28f14f1fbe");
        //     URL driver_url = new URL("http://localhost:26202");
        //     RemoteWebDriver driver2 = createDriverFromSession(session_id, url);
        //     driver2.get("http://www.google.com");
        //     // WebElement elem = driver2.findElement(By.xpath("//li[@role][@class='active']"));
        //     System.out.println("---------------------------------------------");
        //     System.out.println("---------------------------------------------");
        //     // System.out.println("**"+elem.getText()+"**");
        //     System.out.println("---------------------------------------------");
        //     System.out.println("---------------------------------------------");
        //     // driver2.switchTo().alert().accept();

        // } catch (MalformedURLException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
    }
}
