import com.applitools.eyes.*;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class ApplitoolsExample {

    RemoteWebDriver driver;
    Eyes eyes;
    EyesRunner runner;
    static BatchInfo batch;
    Map<String, String> params;
    
    @BeforeTest
    void beforeTest(ITestContext context){
        params = context.getCurrentXmlTest().getAllParameters();
    
        batch =  new BatchInfo(getParam("BatchName"));

        if(getParam("BatchID") != null && !getParam("BatchID").equals("")) {
            batch.setId(getParam("BatchID"));
        }

        try {
            createDriver();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void createDriver() throws MalformedURLException {
        
        System.out.println("Creating driver: " + params.get("targetEnvironment"));

        if(isVisualGridRunner()){
            runner = new VisualGridRunner(new RunnerOptions().testConcurrency(Integer.parseInt(getParam("VisualGridConcurrentTests"))));
        } else {
            runner = new ClassicRunner();
        }
        
        Logger logger = new Logger();
        logger.setLogHandler(new StdoutLogHandler(true));
        
        eyes = new Eyes(runner);
        eyes.setConfiguration(new EyesConfiguration(params, batch).getConfiguration());

        
        if(getConfigBool("EnableEyesLogger")){
            eyes.setLogHandler(new FileLogger("target/logging/" + params.get("targetEnvironment") + ".log",false,true));
        }


        eyes.addProperty("Runner ID", runner.toString().split("@")[1]);
        batch.addProperty("Runner ID", runner.toString().split("@")[1]);

        if(isVisualGridRunner()){
            eyes.addProperty("Runner", "Ultrafast Grid");
            eyes.addProperty("Capture Viewport Size", getParam("LocalViewport"));

            Integer configSize = eyes.getConfiguration().getBrowsersInfo().size();
            batch.addProperty("UFG Configurations", configSize.toString());
            batch.addProperty("Runner Concurrency", getParam("VisualGridConcurrentTests"));

        } else {
            eyes.addProperty("Runner", "Classic");
        }

        System.out.println("Environment: local");
        System.setProperty("webdriver.chrome.driver", getParam("ChromeDriverLocation"));
        System.setProperty("webdriver.gecko.driver", getParam("FirefoxDriverLocation"));
        System.setProperty("webdriver.ie.driver", getParam("IEDriverLocation"));
        System.setProperty("webdriver.edge.driver",getParam("EdgeDriverLocation"));
                
        switch (getParam("LocalBrowser")){
            case "IE":
                InternetExplorerOptions iOptions = new InternetExplorerOptions();
                driver = new InternetExplorerDriver(iOptions);
                break;

            case "chrome":
                ChromeOptions cOptions = new ChromeOptions();
                if(getConfigBool("UseHeadless")){
                     cOptions.addArguments("--headless");
                }

                if(getConfigBool("UseCustomUserAgent")){
                    cOptions.addArguments("--user-agent=\"" + params.get("CustomUserAgent") + "\"");
                }

                if(getConfigBool("UseMobileViewportForCapture")){
                    Map<String, String> mobileEmulation = new HashMap<>();
                    mobileEmulation.put("deviceName", "iPhone X");
                    cOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
                }

                driver = new ChromeDriver(cOptions);
                break;

            case "firefox":
                FirefoxOptions fOptions = new FirefoxOptions();

                if(getConfigBool("UseHeadless")){
                    fOptions.addArguments("--headless");
                    fOptions.setHeadless(true);
                }

                driver = new FirefoxDriver(fOptions);
                break;

            case "edge":
                EdgeOptions eOptions = new EdgeOptions();
                driver = new EdgeDriver(eOptions);
        }


        if(getConfigBool("UseEyes")){
            eyes.open(driver,
                    getParam("AppName"),
                    getParam("TestName"),
                    translateViewport(getParam("LocalViewport")));
        }
    }

    @Test
    public void BrowserTester() throws InterruptedException {
        driver.get(getParam("URL"));

        lazyLoadPage(driver, 400, 2000);

        if(getConfigBool("UseEyes")) {
            eyes.checkWindow();
       }
    }

    private static void lazyLoadPage(WebDriver driver, int scrollAmount, int pause) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Long height = (Long) js.executeScript("return document.body.scrollHeight;");
        for (int i = 0; i < height / scrollAmount; i++) {
            js.executeScript("window.scrollBy(0," + scrollAmount + ")");
            Thread.sleep(pause);
        }

        js.executeScript("window.scrollTo(0, 0);");
        Thread.sleep(pause);
    }
    
    @AfterMethod(alwaysRun = true)
    void afterMethod(ITestContext context){

        if(eyes.getIsOpen()){
            try{
                eyes.closeAsync();
                TestResultsSummary trs = runner.getAllTestResults(false);

                System.out.println(trs.getAllResults()[0].getTestResults().getUrl());
                System.out.println(trs);

            } catch (DiffsFoundException dfe) {
                System.out.println(dfe.getMessage());
            }
        }

        driver.close();
        try{driver.quit();}
        catch (NoSuchSessionException ex) {}
        
    }
    
    Boolean isVisualGridRunner(){
        return getConfigBool("UseVisualGrid");
    }
    
    Boolean getConfigBool(String configName){
        return Boolean.parseBoolean(getParam(configName));
    }

    String getParam(String paramName) {
        if(System.getProperty(paramName) == null){
            return params.get(paramName);
        } else {
            return System.getProperty(paramName);
        }
    }

    RectangleSize translateViewport(String viewportString){
        String[] vpSize = viewportString.split("x");
        return new RectangleSize(Integer.parseInt(vpSize[0]), Integer.parseInt(vpSize[1]));
    }

    Integer translateViewport(String viewportString, String dimension){
        String[] vpSize = viewportString.split("x");
        Integer size = 0;

        if(dimension.equals("width")){
            size = Integer.parseInt(vpSize[0]);
        }

        if(dimension.equals("height")){
            size = Integer.parseInt(vpSize[1]);
        }

        return size;
    }
}

