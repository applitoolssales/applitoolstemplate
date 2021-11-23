import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.visualgrid.model.*;

import java.util.Map;

public class EyesConfiguration {
    Configuration conf;
    Map<String, String> params;
    BatchInfo batch;

    public EyesConfiguration (Map<String, String> params, BatchInfo batch) {
        conf = new Configuration();
        setParams(params);
        setBatch(batch);
        buildConfig();
    }

    private void setParams(Map<String, String> params) {
        this.params = params;
    }

    private void setBatch(BatchInfo batch) {
        this.batch = batch;
    }

    private void buildConfig(){

        Utility util = new Utility(params);

        conf
                .setTestName(util.getParam("TestName"))
                .setAppName(util.getParam("AppName"))
                .setApiKey(util.getParam("APIKey"))
                .setBatch(batch)
                .setHideScrollbars(true)
                .setForceFullPageScreenshot(true)
                .setStitchMode(StitchMode.CSS)
                .setMatchLevel(MatchLevel.STRICT)
                .setWaitBeforeScreenshots(Integer.parseInt(util.getParam("WaitBeforeScreenshots")))
                .setSendDom(true)
                .setUseDom(true);

        if (util.isVisualGridRunner()) {
            conf
                    .setDisableBrowserFetching(util.getConfigBool("DisableBrowserFetching"))
                    .setVisualGridOptions(new VisualGridOption("chromeHeadless", util.getConfigBool("UFGHeadlessChrome")))
                    .setLayoutBreakpoints(util.getConfigBool("UseLayoutBreakpoints"))
                    .setVisualGridOptions(new VisualGridOption("polyfillAdoptedStyleSheets", util.getConfigBool("PolyfillAdoptedStylesheets")));

            if(util.getConfigBool("UseIEv2")){
                conf.setVisualGridOptions(new VisualGridOption("ieV2", "true"));
            }

            if(util.getConfigBool("RunOnDesktop")){
                for (BrowserType b : new TargetBrowsers().getBrowserList()) {
                    for (String viewport : new TargetBrowsers().getViewports()) {
                        conf.addBrowser(util.translateViewport(viewport, "width"), util.translateViewport(viewport, "height"), b);
                    }
                }
            }

            if (util.getConfigBool("RunOnMobile")) {
                for (DeviceName d : new TargetBrowsers().getAndroidDeviceNames()) {
                    if (util.getConfigBool("MobilePortrait")) {
                        conf.addDeviceEmulation(d, ScreenOrientation.PORTRAIT);
                    }
                    if (util.getConfigBool("MobileLandscape")) {
                        conf.addDeviceEmulation(d, ScreenOrientation.LANDSCAPE);
                    }
                }

                for (IosDeviceName device : new TargetBrowsers().getIosDeviceNames()) {
                    if (util.getConfigBool("MobilePortrait")) {
                        conf.addBrowser(new IosDeviceInfo(device, ScreenOrientation.PORTRAIT));
                    }
                    if (util.getConfigBool("MobileLandscape")) {
                        conf.addBrowser(new IosDeviceInfo(device, ScreenOrientation.LANDSCAPE));
                    }
                }
            }

        }

    }

    public Configuration getConfiguration(){
        return conf;
    }
}
