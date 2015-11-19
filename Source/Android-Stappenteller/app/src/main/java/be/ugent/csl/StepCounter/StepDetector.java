package be.ugent.csl.StepCounter;

/*
 * Interface for a step detector. 
 * 
 * @author Andy Georges
 * @author Christophe Foket
 */
public interface StepDetector {
	public void addData(long timestamp, double xAccell, double yAccell, double zAccell);
	
	public void setDataLogger(DataLogger logger);
	
	public int getSteps();
	
	public String getName();
}