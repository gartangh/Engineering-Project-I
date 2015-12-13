package androidintroductie.ugent.be.steps;

public class ComplexStepDetector implements StepDetector {

    private int steps = 95;
    private int run = 0;
    private int today = 95;
    private int total = 1234;

    private DataLogger logger = null;

    private double evenwicht = 24.525;

    private double waarde1 = 9.81;
    private double waarde2 = 9.81;
    private double waarde3 = 9.81;
    private double waarde4 = 9.81;
    private double waarde5 = 9.81;
    private double waarde6 = 9.81;
    private double waarde7 = 9.81;
    private double waarde8 = 9.81;
    private double waarde9;

    private double stapGrenswaarde = 5;
    private double loopGrenswaarde = 15;

    private double[] waarden = new double[40];

    private int teller = 0;

    public void addData(long timestamp, double xAccell, double yAccell, double zAccell) {
        waarde9 = Math.sqrt((Math.pow(xAccell, 2) + Math.pow(yAccell, 2) + Math.pow(zAccell, 2)));
        double lopendGem = 0.1 * waarde1 + 0.2 * waarde2 + 0.3 * waarde3 + 0.4 * waarde4 + 0.5 * waarde5 + 0.4 * waarde6 + 0.3 * waarde7 + 0.2 * waarde8 + 0.1 * waarde9;
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
        waarde5 = waarde6;
        waarde6 = waarde7;
        waarde7 = waarde8;
        waarde8 = waarde9;
    }

    public void controleer(double[] waarden) {
        double max = evenwicht;
        double min = evenwicht;
        for (double i : waarden) {
            if (i > max) {
                max = i;
            }
            if (i < min) {
                min = i;
            }
        }

        if (max >= evenwicht + stapGrenswaarde || min <= evenwicht - stapGrenswaarde) {
            if (max >= evenwicht + loopGrenswaarde || max <= evenwicht - loopGrenswaarde) {
                run++;
                today++;
                total++;
            } else {
                steps++;
                today++;
                total++;
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

    public int getToday() {
        return today;
    }

    public int getTotal() {
        return total;
    }

    public String getName() {
            return "complexStepDetector";
        }
}