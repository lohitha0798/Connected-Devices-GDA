package programmingtheiot.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import programmingtheiot.common.ConfigConst;

/**
 * Convenience wrapper to store system state data, including location
 * information, action command, state data, and lists of:
 * <p>SystemPerformanceData
 * <p>SensorData
 */
public class SystemStateData extends BaseIotData implements Serializable
{
    // static
    private static final long serialVersionUID = 1L; // For Serializable

    // private variables
    private int command = ConfigConst.DEFAULT_COMMAND; // Action command
    private List<SystemPerformanceData> sysPerfDataList; // List of SystemPerformanceData
    private List<SensorData> sensorDataList; // List of SensorData

    // constructors
    public SystemStateData()
    {
        super();
        super.setName(ConfigConst.SYS_STATE_DATA); // Set the name for the superclass
        this.sysPerfDataList = new ArrayList<>();
        this.sensorDataList = new ArrayList<>();
    }

    // public methods
    public boolean addSystemPerformanceData(SystemPerformanceData sysPerfData)
    {
        if (sysPerfData != null)
        {
            return sysPerfDataList.add(sysPerfData);
        }
        return false; // Return false if the data is null
    }

    public boolean addSensorData(SensorData sensorData)
    {
        if (sensorData != null)
        {
            return sensorDataList.add(sensorData);
        }
        return false; // Return false if the data is null
    }

    public int getCommand()
    {
        return command;
    }

    public List<SystemPerformanceData> getSystemPerformanceDataList()
    {
        return sysPerfDataList;
    }

    public List<SensorData> getSensorDataList()
    {
        return sensorDataList;
    }

    public void setCommand(int actionCmd)
    {
        this.command = actionCmd;
    }

    /**
     * Returns a string representation of this instance. This will invoke the base class
     * {@link #toString()} method, then append the output from this call.
     * 
     * @return String The string representing this instance, returned in CSV 'key=value' format.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(super.toString());
        
        sb.append(',');
        sb.append(ConfigConst.COMMAND_PROP).append('=').append(this.getCommand()).append(',');
        sb.append(ConfigConst.SYSTEM_PERF_DATA_LIST_PROP).append('=').append(this.getSystemPerformanceDataList()).append(',');
        sb.append(ConfigConst.SENSOR_DATA_LIST_PROP).append('=').append(this.getSensorDataList());
        
        return sb.toString();
    }

    // protected methods
    /* (non-Javadoc)
     * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
     */
    @Override
    protected void handleUpdateData(BaseIotData data)
    {
        // You can implement any specific logic for updating this instance based on the provided data.
        // This method might be useful for synchronizing with incoming data updates.
    }
}
