package androidintroductie.ugent.be.steps;

import android.hardware.SensorManager;

public enum SampleRate
{
	// Do not change the order of these values!
	FASTEST(SensorManager.SENSOR_DELAY_FASTEST),
	GAME(SensorManager.SENSOR_DELAY_GAME),
	UI(SensorManager.SENSOR_DELAY_UI),
	NORMAL(SensorManager.SENSOR_DELAY_NORMAL);
	
	private int accuracy;
	
	private SampleRate(int accuracy)
	{
		this.accuracy = accuracy;		
	}
	
	public int getAccuracy()
	{
		return accuracy;
	}
}