package androidintroductie.ugent.be.steps;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLogger {
    private static final String TAG = DataLogger.class.getName();

    private static final char FIELD_SEPARATOR = ',';

    private boolean printedTitleLine;

    private List<String> fields;
    private List<Object> currentData;

    private String logPrefix;

    private BufferedWriter writer;

    private File logDirectory;

    private String logFileName;

    private boolean logging;

    public DataLogger(File logDirectory, String logFileName) {
        this.logDirectory = logDirectory;
        this.logFileName = logFileName;

        printedTitleLine = false;
        fields = new ArrayList<String>();
        currentData = new ArrayList<Object>();

        clearLogFile();
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public void clearLogFile() {
        try {
            File logFile = new File(logDirectory, logFileName);
            writer = new BufferedWriter(new FileWriter(logFile, false));
        }
        catch(IOException e) {
            Log.e(TAG, "IOException when opening a writer to " + logFileName, e);
            e.printStackTrace(System.err);
        }
    }

    public void logData(String detector, String filterStep, Object value) {
        fields.add(detector + "_" + filterStep);
        currentData.add(value);
    }

    private void printTitleLine() throws IOException {
        StringBuffer buffer = new StringBuffer();

        for(String field : fields) {
			/*
			 * We replace spaces by underscores so that OpenOffice doesn't
			 * detect this as a new column by default.
			 */
            buffer.append(field.replace(' ', '_')).append(FIELD_SEPARATOR);
        }

        buffer.append('\n');
        writer.write(buffer.toString());
        writer.flush();
    }

    private void printDataLine() throws IOException {
        StringBuffer buffer = new StringBuffer();

        for(Object datum : currentData)
            buffer.append(datum).append(FIELD_SEPARATOR);

        buffer.append('\n');
        writer.write(buffer.toString());
        writer.flush();
    }

    public void flushLine() {
        if(logging) {
            try {
                if (!printedTitleLine) {
                    printTitleLine();
                    printedTitleLine = true;
                }
                printDataLine();
            }
            catch(IOException e){}
        }

        fields.clear();
        currentData.clear();
    }

    public void logSensorData(long timestamp, double x, double y, double z) {
        logData("raw", "prefix", logPrefix == null ? " " : logPrefix);
        logData("raw", "time", timestamp);
        logData("raw", "x", x);
        logData("raw", "y", y);
        logData("raw", "z", z);
    }
}