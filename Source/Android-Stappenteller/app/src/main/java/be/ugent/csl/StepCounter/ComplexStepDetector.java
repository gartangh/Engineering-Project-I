package be.ugent.csl.StepCounter;

/**
 * Created by Gebruiker on 26/11/2015.
 */
public class ComplexStepDetector implements  StepDetector {

    private int steps = 0;
    private int run = 0;
    private DataLogger logger = null;
    private double waarde1 = 0;
    private double waarde2 = 0;
    private double waarde3 = 0;
    private double waarde4 = 0;
    private double waarde5;
    private double stapGrenswaarde = 2.15;
    private double loopGrenswaarde = 6;
    private double[] waarden = new double[30];
    private int teller = 0;

    public void addData(long timestamp, double xAccell, double yAccell, double zAccell) {
        waarde5 = Math.sqrt((Math.pow(xAccell, 2) + Math.pow(yAccell, 2) + Math.pow(zAccell, 2)));
        double lopendGem = 0.1 * waarde1 + 0.2 * waarde2 + 0.3 * waarde3 + 0.4 * waarde4;
        waarden[teller] = lopendGem;
        teller++;
        if (teller == waarden.length) {
            controleer(waarden);
            teller = 0;
        }

        logger.logData(getName(), "stappen", steps);
        waarde1 = waarde2;
        waarde2 = waarde3;
        waarde3 = waarde4;
        waarde4 = waarde5;
    }

    public void controleer(double[] waarden) {
        double max = 10;
        double min = 10;
        for (double i : waarden) {
            if (i > max) {
                  max = i;
            }
            if (i < min){
                min = i;
            }
        }

        if (max >= 10 + stapGrenswaarde || min <= 10 - stapGrenswaarde) {
            if (max >= 10 + loopGrenswaarde || max <= 10 - loopGrenswaarde) {
                run++;
            } else {
                steps++;
            }
        }
    }

    public void setDataLogger(DataLogger logger) {
        this.logger = logger;
    }

    public int getSteps() {
        return steps;
    }

    public int getRun() {
        return run;
    }

    public String getName() {
        return "complexStepDetector";
    }
}