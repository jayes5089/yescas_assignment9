package yescas_assignment9;

public class RunWalk extends Exercise {
	private double distance;
	
	public RunWalk(String name, String date, double duration, String comment, double distance) {
		super(name, date, duration, comment);
		this.distance = distance;
	}
	
	@Override
	public String getSpecificInfo() {
		return distance + " miles";
	}
	
	@Override
	public double getCaloriesBurned() {
		return (distance / getDuration()) * 9000;
	}
	
	@Override
	public String getType() {
		return "Run/Walk";
	}
}