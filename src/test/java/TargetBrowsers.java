import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.IosDeviceName;

public class TargetBrowsers {

    public BrowserType[] getBrowserList() {
        return browserList;
    }

    public IosDeviceName[] getIosDeviceNames() {
        return IosDeviceNames;
    }

    public DeviceName[] getAndroidDeviceNames(){
        return AndroidDeviceNames;
    }

    public String[] getViewports(){
        return Viewports;
    }

    BrowserType[] browserList = {
            BrowserType.CHROME,
            BrowserType.CHROME_ONE_VERSION_BACK,
            BrowserType.CHROME_TWO_VERSIONS_BACK,
            BrowserType.FIREFOX,
            BrowserType.FIREFOX_ONE_VERSION_BACK,
            BrowserType.FIREFOX_TWO_VERSIONS_BACK,
            BrowserType.EDGE_LEGACY,
            BrowserType.EDGE_CHROMIUM,
            BrowserType.EDGE_CHROMIUM_ONE_VERSION_BACK,
            BrowserType.SAFARI,
            BrowserType.SAFARI_ONE_VERSION_BACK,
            BrowserType.SAFARI_TWO_VERSIONS_BACK,
            BrowserType.SAFARI_EARLY_ACCESS,
            BrowserType.IE_11
    };

    String[] Viewports = {

            "1920x1080",
			"1366x768",
            "1440x900",
            "1536x864"
    };

    IosDeviceName[] IosDeviceNames = {
            IosDeviceName.iPhone_11_Pro,
            IosDeviceName.iPhone_11_Pro_Max,
            IosDeviceName.iPhone_11,
            IosDeviceName.iPhone_X,
            IosDeviceName.iPhone_8,
            IosDeviceName.iPad_Pro_3,
            IosDeviceName.iPad_7,
            IosDeviceName.iPad_Air_2,
            IosDeviceName.iPhone_12_Pro_Max,
            IosDeviceName.iPhone_12_Pro,
            IosDeviceName.iPhone_12,
            IosDeviceName.iPhone_12_mini
    };

    DeviceName[] AndroidDeviceNames = {
            DeviceName.Pixel_4,
            DeviceName.Pixel_4_XL,
            DeviceName.Galaxy_Note_10,
            DeviceName.Galaxy_Note_10_Plus,
            DeviceName.Galaxy_Note_8,
            DeviceName.Galaxy_Note_9,
            DeviceName.Galaxy_S8,
            DeviceName.Galaxy_S9,
            DeviceName.Galaxy_S10,
            DeviceName.Galaxy_S9_Plus,
            DeviceName.Galaxy_S10_Plus
    };
}
