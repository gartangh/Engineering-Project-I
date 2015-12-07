package androidintroductie.ugent.be.steps;

/**
 * Created by student on 19/11/15.
 */
public class DummyStepDetector implements StepDetector {
    private int steps = 0;
    private DataLogger logger = null;
    private double waarde1 = 0;
    private double waarde2 = 0;
    private double waarde3 = 0;
    private double stapGrenswaarde = 0.20;
    private double[] waarden = new double[75];
    private int teller = 0;

    public void addData(long timestamp, double xAccell, double yAccell, double zAccell) {

        waarde3 = Math.sqrt((Math.pow(xAccell, 2) + Math.pow(yAccell, 2) + Math.pow(zAccell, 2)));
        double lopendGem = waarde2 - waarde1;
        waarden[teller] = lopendGem;
        teller++;
        if (teller == waarden.length) {
            controleer(waarden);
            teller = 0;
        }

        logger.logData(getName(), "stappen", steps);
        waarde1 = waarde2;
        waarde2 = waarde3;
    }

    private int controleer(double[] waarden) {
        double max = 0;
        double min = 0;
        for (double i : waarden) {
            if (i > max) {
                max = i;
            }
            if (i < min){
                min = i;
            }
        }

        if (max >= stapGrenswaarde||min <= -1 * stapGrenswaarde) {
            return steps++;
        } else {
            return steps;
        }
    }

    public void setDataLogger(DataLogger logger) {
        this.logger = logger;
    }

    public int getSteps() {
        return steps;
    }

    public int getRun() {
        return 0;
    }

    public int getToday() {return 0;}

    public int getTotal() {
        return 0;
    }

    public String getName() {
        return "dummyStepDetector";
    }
}

