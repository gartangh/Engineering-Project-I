package be.ugent.csl.StepCounter;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/*
 * @author Andy Georges
 * @author Christophe Foket
 */

public class AccellMeterService extends Service implements SensorEventListener  {
	
	private static final String TAG = AccellMeterService.class.getName();
	/* 
	 * Communication with the Activity that is using this Service.
	 */
    private LocalBinder accellBinder = new LocalBinder();
	/*
	 * Sensor objects
	 */
	private SensorManager mSensorManager;
	private Sensor mAccellSensor;
	/* 
	 * Global data that is used to make sure the service is actually up
	 * and running before using it.  
	 */
	private boolean started = false;
	private boolean registered = false;
	
	@Override 
	public void onCreate() {
		super.onCreate();
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mAccellSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
    	synchronized(this)
    	{
    		if(started)
    			return START_STICKY;

    		mSensorManager.registerListener(this, mAccellSensor, Util.get().getRate().getAccuracy());
        	started = true;
    	}
    	return START_STICKY;
    }
        
    @Override 
    public IBinder onBind(Intent intent)
    {
		return accellBinder;
    }
    
    public class LocalBinder extends Binder
    {
    	AccellMeterService getService()
    	{
    		return AccellMeterService.this;
    	}
    }
    
    @Override
    public void onDestroy() {
    	mSensorManager.unregisterListener(this, mAccellSensor);    	
    	super.onDestroy();
    }
    
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void onSensorChanged(SensorEvent event) {
    	if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			Util.get().gotSensorData(event.timestamp, event.values[0], event.values[1], event.values[2]);
    	}
	}

    public void setAccuracy(int accuracy) {
    	/* 
    	 * Unregister first, otherwise the listener keeps receiving 
    	 * data at the highest set rate.
    	 */
    	synchronized(this) {
    		if(registered) {
    			mSensorManager.unregisterListener(this, mAccellSensor);
    			registered = false;
    		}
    	   	
    		if(mSensorManager.registerListener(this, mAccellSensor, accuracy)) {
    	   		Log.i(TAG, "Sensor accuracy set to " + accuracy);
    	   		registered = true;
    	   	}
    	}
    }
}