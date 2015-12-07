package androidintroductie.ugent.be.steps;

public interface StepDetector {
	public void addData(long timestamp, double xAccell, double yAccell, double zAccell);
	
	public void setDataLogger(DataLogger logger);
	
	public int getSteps();

	public int getRun();

	public int getToday();

	public int getTotal();
	
	public String getName();
}
