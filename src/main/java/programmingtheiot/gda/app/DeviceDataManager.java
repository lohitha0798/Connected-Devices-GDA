package programmingtheiot.gda.app;
import java.util.logging.Level;
import java.util.logging.Logger;
import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IActuatorDataListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.SystemStateData;
import programmingtheiot.gda.connection.CloudClientConnector;
import programmingtheiot.gda.connection.CoapServerGateway;
import programmingtheiot.gda.connection.IPersistenceClient;
import programmingtheiot.gda.connection.IPubSubClient;
import programmingtheiot.gda.connection.MqttClientConnector;
import programmingtheiot.gda.connection.RedisPersistenceAdapter;
import programmingtheiot.gda.system.SystemPerformanceManager;

public class DeviceDataManager implements IDataMessageListener {
    // Logger definition
    private static final Logger _Logger = Logger.getLogger(DeviceDataManager.class.getName());
    // Configuration flags
    private boolean enableMqttClient;
    private boolean enableCoapServer;
    private boolean enableCloudClient;
    private boolean enablePersistenceClient;
    private boolean enableSystemPerf;
    // Connections and managers
    private IPubSubClient mqttClient;
    private CloudClientConnector cloudClient;
    private IPersistenceClient persistenceClient;
    private CoapServerGateway coapServer;
    private SystemPerformanceManager sysPerfMgr;
    // Constructor
    public DeviceDataManager() {
        super();
        _Logger.info("Initializing DeviceDataManager...");
        loadConfiguration();
        initManager();
    }
    // Load configuration
    private void loadConfiguration() {
        ConfigUtil configUtil = ConfigUtil.getInstance();
        this.enableMqttClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_MQTT_CLIENT_KEY);
        this.enableCoapServer = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_COAP_SERVER_KEY);
        this.enableCloudClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_CLOUD_CLIENT_KEY);
        this.enablePersistenceClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_PERSISTENCE_CLIENT_KEY);
        this.enableSystemPerf = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_SYSTEM_PERF_KEY);
    }
    // Initialize components
    private void initManager() {
    	ConfigUtil configUtil = ConfigUtil.getInstance();
    	
    	this.enableSystemPerf =
    		configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE,  ConfigConst.ENABLE_SYSTEM_PERF_KEY);
    	
    	if (this.enableSystemPerf) {
    		this.sysPerfMgr = new SystemPerformanceManager();
    		this.sysPerfMgr.setDataMessageListener(this);
    	}
    	
    	// NOTE: This is new - creating the MQTT client connector instance
    	if (this.enableMqttClient) {
    		this.mqttClient = new MqttClientConnector();
    		
    		// NOTE: The next line isn't technically needed until Lab Module 10
    		this.mqttClient.setDataMessageListener(this);
    	}
    	
    	if (this.enableCoapServer) {
    		// TODO: implement this in Lab Module 8
    	}
    	
    	if (this.enableCloudClient) {
    		// TODO: implement this in Lab Module 10
    	}
    	
    	if (this.enablePersistenceClient) {
    		// TODO: implement this as an optional exercise in Lab Module 5
    	}
    }
    // Start the manager
    public void startManager() {
        _Logger.info("Starting DeviceDataManager...");
        if (this.sysPerfMgr != null) {
            this.sysPerfMgr.startManager();
        }
        if (this.coapServer != null) {
            this.coapServer.startServer();
        }
        if (this.mqttClient != null) {
    		if (this.mqttClient.connectClient()) {
    			_Logger.info("Successfully connected MQTT client to broker.");
    			
    			// add necessary subscriptions
    			
    			// TODO: read this from the configuration file
    			int qos = ConfigConst.DEFAULT_QOS;
    			
    			// TODO: check the return value for each and take appropriate action
    			
    			// IMPORTANT NOTE: The 'subscribeToTopic()' method calls shown
    			// below will be moved to MqttClientConnector.connectComplete()
    			// in Lab Module 10. For now, they can remain here.
    			this.mqttClient.subscribeToTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, qos);
    			this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, qos);
    			this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, qos);
    			this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, qos);
    		} else {
    			_Logger.severe("Failed to connect MQTT client to broker.");
    			
    			// TODO: take appropriate action
    		}
    	}
    	
    	if (this.sysPerfMgr != null) {
    		this.sysPerfMgr.startManager();
    	}
    }
    // Stop the manager
    public void stopManager() {
        _Logger.info("Stopping DeviceDataManager...");
        if (this.sysPerfMgr != null) {
    		this.sysPerfMgr.stopManager();
    	}
    	
    	if (this.mqttClient != null) {
    		// add necessary un-subscribes
    		
    		// TODO: check the return value for each and take appropriate action
    		
    		// NOTE: The unsubscribeFromTopic() method calls below should match with
    		// the subscribeToTopic() method calls from startManager(). Also, the
    		// unsubscribe logic below can be moved to MqttClientConnector's
    		// disconnectClient() call PRIOR to actually disconnecting from
    		// the MQTT broker.
    		this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE);
    		this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE);
    		this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE);
    		this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE);
    		
    		if (this.mqttClient.disconnectClient()) {
    			_Logger.info("Successfully disconnected MQTT client from broker.");
    		} else {
    			_Logger.severe("Failed to disconnect MQTT client from broker.");
    			
    			// TODO: take appropriate action
    		}
    	}
        if (this.coapServer != null) {
            this.coapServer.stopServer();
        }
    }
    // Implement IDataMessageListener methods
    @Override
    public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data) {
        if (data != null) {
            _Logger.info("Handling actuator response: " + data.getName());
            if (data.hasError()) {
                _Logger.warning("Error flag set for ActuatorData instance.");
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg) {
        if (msg != null) {
            _Logger.info("Handling incoming generic message: " + msg);
            return true;
        }
        return false;
    }
    @Override
    public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data) {
        if (data != null) {
            _Logger.info("Handling sensor message: " + data.getName());
            if (data.hasError()) {
                _Logger.warning("Error flag set for SensorData instance.");
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data) {
        if (data != null) {
            _Logger.info("Handling system performance message: " + data.getName());
            if (data.hasError()) {
                _Logger.warning("Error flag set for SystemPerformanceData instance.");
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean handleActuatorCommandRequest(ResourceNameEnum resourceName, ActuatorData data) {
        return false; // TODO: Implement if needed
    }
    @Override
    public void setActuatorDataListener(String name, IActuatorDataListener listener) {
        // TODO: Implement if needed
    }
}