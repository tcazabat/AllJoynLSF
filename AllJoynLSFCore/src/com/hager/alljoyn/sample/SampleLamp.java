package com.hager.alljoyn.sample;

import java.util.HashMap;

import org.alljoyn.bus.AboutObj;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Variant;
import org.allseen.LSF.LampDetails;
import org.allseen.LSF.LampFaultResult;
import org.allseen.LSF.LampParameters;
import org.allseen.LSF.LampService;
import org.allseen.LSF.LampState;

/**
 * A sample lamp exposed using Alljoyn
 * @author t.cazabat
 *
 */
public class SampleLamp implements BusObject, LampState, LampService, LampDetails, LampParameters  {
	
	private BusAttachment bus;
    private static final Mutable.ShortValue CONTACT_PORT = new Mutable.ShortValue((short) 42);
    private static final String BUS_NAME = "com.hager.alljoyn.hdm";
    private static final String DEVICE_PATH = "/org/allseen/LSF/Lamp";
    private static final String DEVICE_NAME = "SampleLamp";
    private static final String DEVICE_ID = "LAMP-ID";
    private SignalEmitter signalEmitter;
    private AboutObj aboutObj;
	
    public SampleLamp(){    	
    }
    
    public void registerDevice(){
    	System.out.println("Register device.");
        initializeBus();
        registerDeviceOnBus();
        announceDevice();
    }
    
    public void unregisterDevice(){
    	System.out.println("Unregister device.");
    	dispose();
    }
    
    /**
     * Create the bus and connect it.
     */
    private void initializeBus() {

        this.bus = new BusAttachment(BUS_NAME, BusAttachment.RemoteMessage.Receive);

        System.out.println("BusAttachment Connect");
        System.out.println("BusAttachment registerBusObject OK");

        this.bus.registerBusListener(new BusListener());

        System.out.println("BusAttachment registerBusListener OK");

        Status status = bus.connect();
        if (status != Status.OK) {
            throw new RuntimeException("error during bus connect, Status= " + status);
        }

        System.out.println("BusAttachment connect OK on port " + System.getProperty("org.alljoyn.bus.address"));

        SessionOpts sessionOpts = new SessionOpts();
        sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
        sessionOpts.isMultipoint = true;
        sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
        sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

        status = this.bus.bindSessionPort(CONTACT_PORT, sessionOpts, new SessionPortListener() {
            public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
                System.out.println("SessionPortListener.acceptSessionJoiner called");
                if (sessionPort == CONTACT_PORT.value) {
                    return true;
                } else {
                    return false;
                }
            }

            public void sessionJoined(short sessionPort, int id, String joiner) {
                System.out.println(String.format("SessionPortListener.sessionJoined(%d, %d, %s)", sessionPort, id, joiner));
            }
        });
        if (status != Status.OK) {
            throw new RuntimeException("error during bind session, Status= " + status);
        }

        System.out.println("BusAttachment.bindSessionPort successful");

        int flags = 0; //do not use any request name flags
        status = bus.requestName(BUS_NAME, flags);
        if (status != Status.OK) {
            throw new RuntimeException("error during request Name, Status= " + status);
        }
        System.out.println("BusAttachment.request 'com.hager.knxng.alljoyn' successful");

        status = bus.advertiseName(BUS_NAME, SessionOpts.TRANSPORT_ANY);
        if (status != Status.OK) {
            throw new RuntimeException("error during advertiseName, Status= " + status);
        }
        System.out.println("BusAttachment.advertiseName 'com.hager.knxng.alljoyn' successful");
    }
    
    /**
     * Release all resources
     */
    private void dispose() {
        try {
            if (aboutObj != null) {
                System.out.println("BusManagerImpl unannounce about.");
                this.aboutObj.unannounce();
            }
            System.out.println("BusManagerImpl cancel advertise name.");
            this.bus.cancelAdvertiseName(BUS_NAME, SessionOpts.TRANSPORT_ANY);
            System.out.println("BusManagerImpl release name.");
            this.bus.releaseName(BUS_NAME);
            System.out.println("BusManagerImpl disconnect the bus.");
            this.bus.disconnect();
        } catch (UnsatisfiedLinkError ex) {
            System.out.println("Error while unregistering device: " + ex.getMessage());
        }
    }

    /**
     * Announce device on the alljoyn bus
     */
    private void announceDevice() {
        System.out.println("BusManagerImpl announce device: " + DEVICE_NAME);
        if (this.bus.isConnected()) {

            if (aboutObj == null) {
                aboutObj = new AboutObj(this.bus, false);
            }

            Status status = aboutObj.announce(CONTACT_PORT.value, new AboutDevice());
            if (status != Status.OK) {
                System.out.println("Announce failed: " + status.toString());
                return;
            }
        }
    }

    /**
     * Register device as a bus Object.
     */
    private void registerDeviceOnBus() {
        System.out.println("BusManagerImpl register device on bus: " + DEVICE_NAME);

        Status status = this.bus.registerBusObject(this, DEVICE_PATH);

        if (status != Status.OK) {
            System.out.println("BusManagerImpl register device: failed with error: " + status);
        } else {
            System.out.println("BusManagerImpl register device: succeed.");
        }

        signalEmitter = new SignalEmitter(this, BusAttachment.SESSION_ID_ALL_HOSTED, SignalEmitter.GlobalBroadcast.On);
    }
    
    private boolean onOff = false;
    
    @Override
    public boolean getOnOff() {
    	return this.onOff;
    }

    @Override
    public void setOnOff(boolean onOff) {
    	System.out.println("The lamp state has changed : " + onOff);
    	
    	this.onOff = onOff;
    	fireStateChanged();
    }

    @Override
    public int getHue() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setHue(int hue) {
    }

    @Override
    public int getSaturation() {
        return 0;
    }

    @Override
    public void setSaturation(int saturation) {
    }

    @Override
    public int getColorTemp() {
        return 0;
    }

    @Override
    public void setColorTemp(int colorTemp) {
    }

    @Override
    public int getBrightness() {
        return 0;
    }

    @Override
    public void setBrightness(int brightness) {
    }

    @Override
    public int transitionLampState(long timestamp, HashMap<String, Variant> newState, int transitionPeriod) {

        if (newState.containsKey("Brightness")) {
            try {
                String s = newState.get("Brightness").getSignature();
                Integer res = newState.get("Brightness").getObject(Integer.class);
                this.setBrightness(50);
            } catch (BusException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return 1;
    }

    @Override
    public int applyPulseEffect(HashMap<String, Variant> fromState, HashMap<String, Variant> toState, int period, int duration,
            int numPulses, long timestamp) {
        return 1;
    }

    @Override
    public void lampStateChanged(String lampId) {
    }

    @Override
    public int getMake() {
        return 2;
    }

    @Override
    public int getModel() {
        return 1;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public int getLampType() {
        return 14;
    }

    @Override
    public int getLampBaseType() {
        return 7;
    }

    @Override
    public int getLampBeamAngle() {
        return 750;
    }

    @Override
    public boolean getDimmable() {
        return false;
    }

    @Override
    public boolean getColor() {
        return true;
    }

    @Override
    public boolean getVariableColorTemp() {
        return true;
    }

    @Override
    public boolean getHasEffects() {
        return true;
    }

    @Override
    public int getMinVoltage() {
        return 120;
    }

    @Override
    public int getMaxVoltage() {
        return 240;
    }

    @Override
    public int getWattage() {
        return 9;
    }

    @Override
    public int getIncandescentEquivalent() {
        return 75;
    }

    @Override
    public int getMaxLumens() {
        return 900;
    }

    @Override
    public int getMinTemperature() {
        return 2700;
    }

    @Override
    public int getMaxTemperature() {
        return 5000;
    }

    @Override
    public int getColorRenderingIndex() {
        return 90;
    }

    @Override
    public String getLampID() {
        return DEVICE_ID;
    }

    @Override
    public int getEnergy_Usage_Milliwatts() {
        return 0;
    }

    @Override
    public int getBrightness_Lumens() {
        return 0;
    }

    @Override
    public int getLampServiceVersion() {
        return 1;
    }

    // @Override
    public int[] getLampFaults() {
        return new int[0];
    }

    @Override
    public LampFaultResult clearLampFault(int lampFaultCode) {
        return new LampFaultResult();
    }
	
	private void fireStateChanged(){
		try {
			this.signalEmitter.getInterface(LampState.class).lampStateChanged(getLampID());
		} catch (BusException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getVersionParameter() throws BusException {
		return 1;
	}

	@Override
	public int getVersionDetails() throws BusException {
		return 1;
	}

	@Override
	public int getVersionService() throws BusException {
		return 1;
	}

	@Override
	public int getVersionState() throws BusException {
		return 1;
	}
}
