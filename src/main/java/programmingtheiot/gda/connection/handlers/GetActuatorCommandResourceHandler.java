/**
 * 
 */
package programmingtheiot.gda.connection.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import programmingtheiot.common.IActuatorDataListener;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;

public class GetActuatorCommandResourceHandler extends CoapResource
    implements IActuatorDataListener
{
    // Static logging infrastructure
    private static final Logger _Logger = Logger.getLogger(GetActuatorCommandResourceHandler.class.getName());

    // Actuator data to hold the command
    private ActuatorData actuatorData = null;

    // Constructor
    public GetActuatorCommandResourceHandler(String resourceName) {
        super(resourceName);
        // Set the resource to be observable (this will allow clients to observe changes)
        super.setObservable(true);
    }

    // Implementing the method from IActuatorDataListener interface
    @Override
    public boolean onActuatorDataUpdate(ActuatorData data) {
        if (data != null && this.actuatorData != null) {
            // Update the actuator data and notify observers
            this.actuatorData.updateData(data);
            super.changed();  // Notify clients observing the resource
            _Logger.fine("Actuator data updated for URI: " + super.getURI() + ": Data value = " + this.actuatorData.getValue());
            return true;
        }
        return false;
    }
    @Override
    public void handleDELETE(CoapExchange context)
    {
    }
    @Override
    public void handlePOST(CoapExchange context)
    {
    }
    @Override
    public void handlePUT(CoapExchange context)
    {
    } 
    // Override handleGET to respond to GET requests
    @Override
    public void handleGET(CoapExchange context) {
        // Accept the request
        context.accept();

        // Log the GET request
        _Logger.info("Received GET request for Actuator Command resource.");

        // Convert the actuator data to JSON (using DataUtil utility class)
        String jsonData = DataUtil.getInstance().actuatorDataToJson(this.actuatorData);

        // Send the response with the actuator data as JSON
        context.respond(ResponseCode.CONTENT, jsonData);
    }
}

