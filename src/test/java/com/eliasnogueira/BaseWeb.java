/*
 * MIT License
 *
 * Copyright (c) 2018 Elias Nogueira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.eliasnogueira ;

import com.eliasnogueira.driver.DriverManager;
import com.eliasnogueira.driver.TargetFactory;
import com.eliasnogueira.report.AllureManager;
//import com.eliasnogueira.test.utilities.UtilityTest;
import com.eliasnogueira.test.utilities.UtilityTest; 

import org.aeonbits.owner.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.TestNGUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

// import jdk.internal.net.http.common.Log;

import static com.eliasnogueira.config.ConfigurationManager.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

@Listeners({TestListener.class})
public abstract class BaseWeb {

    @BeforeSuite
    public void beforeSuite() {
        AllureManager.setAllureEnvironmentInformation();
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void preCondition(@Optional("chrome") String browser) {
        if(browser.equals("chrome") && !configuration().quit_browser() && configuration().same_browser()){
            RemoteWebDriver driver = null;
            try {
                SessionId sessionId = configuration().sessionId();
                URL driverUrl = configuration().driverUrl();
                driver = UtilityTest.createDriverFromSession(sessionId, driverUrl);
                DriverManager.setDriver(driver);
                DriverManager.getDriver().get(configuration().url());
                return;
            } catch (Exception e) {
                e.printStackTrace();
                driver =  (RemoteWebDriver)new TargetFactory().createInstance(browser);
                DriverManager.setDriver(driver);
                DriverManager.getDriver().get(configuration().url());
                UtilityTest.saveDriverInfo(driver);
                return;
            }
        }
        WebDriver driver =  new TargetFactory().createInstance(browser);
        DriverManager.setDriver(driver);
        DriverManager.getDriver().get(configuration().url());
    }

    @AfterMethod(alwaysRun = true)
    public void postCondition() {
        
        boolean quit_browser = configuration().quit_browser();
        if(quit_browser){
            DriverManager.quit();
        }
    }
}
