package androidintroductie.ugent.be.steps;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.util.HashMap;

public class Util {
    /*
     * TAG serves as a tag in the log files so we can easily see who is
     * responsible for a given entry in the log files.
     */
    public final static String TAG = Util.class.getName();

    /*
     * Fixed name of the trace file.
     */
    private static final String TRACE_FILE_NAME = "accelDataLog.csv";

    /*
     * Singleton instance of this class.
     */
    private static Util instance = new Util();

    private DataLogger logger;

    /*
     * Step detectors. Each of these will be called from the trace()
     * method to store and process the latest data item.
     */
    private HashMap<String, StepDetector> detectors;

    private StepDetector currentDetector;

    private AccellMeterService accellMeterService;

    private SampleRate sampleRate = SampleRate.GAME;

    private long lastEventTime;

    /*
     * This class acts as a singleton, this means that there can only
     * be a single instance (i.e., object) of the class present inside
     * a JVM execution. In other words, there is no public constructor
     * available for users to call upon. One can simply ask the class
     * for the instance that has been created at class initialization
     * time, through the get method.
     */
    private Util() {
        logger = new DataLogger(Environment.getExternalStorageDirectory(), TRACE_FILE_NAME);
        detectors = new HashMap<String, StepDetector>();
        addDetector(new DummyStepDetector());
        addDetector(new ComplexStepDetector());
        addDetector(new DerivedStepDetector());
    }

    public static Util get() {
        return instance;
    }

    public StepDetector getCurrentStepDetector()
    {
        return currentDetector;
    }

    public void setStepDetector(String s) {
        StepDetector d = detectors.get(s);
        if(d != null)
            currentDetector = d;
    }

    private void addDetector(StepDetector detector) {
        detectors.put(detector.getName(), detector);
        detector.setDataLogger(getLogger());
    }

    public DataLogger getLogger() {
        return logger;
    }

    public void setRate(SampleRate sampleRate) {
        this.sampleRate = sampleRate;

        if(accellMeterService != null)
            accellMeterService.setAccuracy(sampleRate.getAccuracy());
    }

    public SampleRate getRate() {
        return sampleRate;
    }

    public void setService(AccellMeterService s) {
        accellMeterService = s;
        accellMeterService.setAccuracy(sampleRate.getAccuracy());
    }

    public void gotSensorData(long timestamp, float x, float y, float z) {
        getLogger().logSensorData(timestamp, x, y, z);

        for(StepDetector s : detectors.values())
            s.addData(timestamp, x, y, z);

        if (accellMeterService == null) {
            return;
        }

        if(timestamp - lastEventTime > 500000000) { // 0.5 s in nanos
            Intent intent = new Intent("be.ugent.csl.StepCounterIntent");
            accellMeterService.sendBroadcast(intent);
            lastEventTime = timestamp;

        }
        getLogger().flushLine();
    }
}
