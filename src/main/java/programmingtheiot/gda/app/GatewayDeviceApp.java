
 
package programmingtheiot.gda.app;
 
import java.util.logging.Level;
import java.util.logging.Logger;
 
import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
 
public class GatewayDeviceApp {
    // Logger definition
    private static final Logger _Logger = Logger.getLogger(GatewayDeviceApp.class.getName());
    public static final long DEFAULT_TEST_RUNTIME = 600000L; // 10 min's
 
    // Instance of DeviceDataManager
    private DeviceDataManager dataMgr = null;
 
    // Constructor
    public GatewayDeviceApp(String[] args) {
        super();
        _Logger.info("Initializing GDA...");
        this.dataMgr = new DeviceDataManager(); // Initialize DeviceDataManager here
    }
 
    // Main method
    public static void main(String[] args) {
        GatewayDeviceApp gwApp = new GatewayDeviceApp(args);
        gwApp.startApp();
 
        boolean runForever = ConfigUtil.getInstance().getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_RUN_FOREVER_KEY);
        if (runForever) {
            try {
                while (true) {
                    Thread.sleep(2000L);
                }
            } catch (InterruptedException e) {
                // Ignore
            }
            gwApp.stopApp(0);
        } else {
            try {
                Thread.sleep(DEFAULT_TEST_RUNTIME);
            } catch (InterruptedException e) {
                // Ignore
            }
            gwApp.stopApp(0);
        }
    }
 
    // Public methods
    public void startApp() {
        _Logger.info("Starting GDA...");
        try {
            if (this.dataMgr != null) {
                this.dataMgr.startManager();
            }
            _Logger.info("GDA started successfully.");
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Failed to start GDA. Exiting.", e);
            stopApp(-1);
        }
    }
 
    public void stopApp(int code) {
        _Logger.info("Stopping GDA...");
        try {
            if (this.dataMgr != null) {
                this.dataMgr.stopManager();
            }
            _Logger.log(Level.INFO, "GDA stopped successfully with exit code {0}.", code);
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Failed to cleanly stop GDA. Exiting.", e);
        }
        System.exit(code);
    }
 // private methods
 	/**
 	 * Load the config file.
 	 * 
 	 * NOTE: This will be added later.
 	 * 
 	 * @param configFile The name of the config file to load.
 	 */
 	private void initConfig(String configFile)
 	{
 		_Logger.log(Level.INFO, "Attempting to load configuration: {0}", (configFile != null ? configFile : "Default."));
 		// TODO: Your code here
 	}
 	/**
 	 * Parse any arguments passed in on app startup.
 	 * <p>
 	 * This method should be written to check if any valid command line args are provided,
 	 * including the name of the config file. Once parsed, call {@link #initConfig(String)}
 	 * with the name of the config file, or null if the default should be used.
 	 * <p>
 	 * If any command line args conflict with the config file, the config file
 	 * in-memory content should be overridden with the command line argument(s).
 	 * 
 	 * @param args The non-null and non-empty args array.
 	 */
 	private void parseArgs(String[] args)
 	{
 		String configFile = null;
 		if (args != null) {
 			_Logger.log(Level.INFO, "Parsing {0} command line args.", args.length);
 			for (String arg : args) {
 				if (arg != null) {
 					arg = arg.trim();
 				}
 			}
 		} else {
 			_Logger.info("No command line args to parse.");
 		}
 		initConfig(configFile);
 	}
  
 }