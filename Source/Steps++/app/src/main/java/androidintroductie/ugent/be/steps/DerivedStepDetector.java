package androidintroductie.ugent.be.steps;

import java.util.ArrayList;

/**
 * Created by student on 19/11/15.
 */
public class DerivedStepDetector implements StepDetector {
    private int steps = 0;
    private DataLogger logger = null;
    private double waarde1 = 0;
    private double waarde2 = 0;
    private double waarde3 = 0;
    private int teller = 0;
    private double stapGrenswaardePos = 0.20;
    private double stapGrenswaardeNeg = -0.18;
    private double[] waarden = new double[75];
    boolean hoog = false;


    public void addData(long timestamp, double xAccell, double yAccell, double zAccell) {

        waarde3 = Math.sqrt((Math.pow(xAccell, 2) + Math.pow(yAccell, 2) + Math.pow(zAccell, 2)));
        double lopendGem = waarde2 - waarde1;
        waarden[teller] = lopendGem;
        teller++;
        if (teller == waarden.length) {
            controleerAfgeleiden(waarden);
            teller = 0;
        }

        logger.logData(getName(), "stappen", steps);
        waarde1 = waarde2;
        waarde2 = waarde3;
    }

    private int controleerAfgeleiden (double[] waarden)
    {
        int begin = 0;
        int stappen = 0;
        double [] afgeleiden= new double[75] ;
        ArrayList<Integer> Stap= new ArrayList<Integer>();
        afgeleiden[0] = waarden[0]-begin;
        for(int i=1;i<75;i++)
        {
            afgeleiden[i] = waarden[i]-waarden[i-1];
        }
        for(int i=0;i<75;i++)
        {
            if(afgeleiden[i]<0.005)
            {
                Stap.add(i);
            }
        }
        for(int i=0;i<Stap.size();i++)
        {
            if(hoog == false){
                if(waarden[Stap.get(i)]>stapGrenswaardePos)
                {
                    stappen++;
                    hoog=true;
                }}
            else{
                if(waarden[Stap.get(i)]<stapGrenswaardeNeg)
                {
                    stappen++;
                    hoog=false;
                }
            }
        }
        steps+=Math.round(stappen/2);
        return steps;
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

    public int getTotal() {
        return 0;
    }

    public int getToday() {
        return 0;
    }

    public String getName() {
        return "derivedStepDetector";
    }
}
