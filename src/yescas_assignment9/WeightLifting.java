package yescas_assignment9;

public class WeightLifting extends Exercise {
	private double weightLifted;
	
	public WeightLifting(String name, String date, double duration, String comment, double weightLifted) {
		super(name, date, duration, comment);
		this.weightLifted = weightLifted;
	}
	
	@Override
	public String getSpecificInfo() {
		return weightLifted + " lbs";
	}
	
	@Override
	public double getCaloriesBurned() {
		return (weightLifted / getDuration()) * 50;
	}
	
	@Override
	public String getType() {
		return "Weight Lifting";
	}
}