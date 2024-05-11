package yescas_assignment9;

public class RockClimbing extends Exercise {
	private double wallHeight;
	private int repetitions;
	
	public RockClimbing(String name, String date, double duration, String comment, double wallHeight, int repetitions) {
		super(name, date, duration, comment);
		this.wallHeight = wallHeight;
		this.repetitions = repetitions;
	}
	
	@Override
	public String getSpecificInfo() {
		return wallHeight + " ft, " + repetitions + " reps";
	}
	
	@Override
	public double getCaloriesBurned() {
		return (wallHeight * repetitions / getDuration()) * 100;
	}
	
	@Override
	public String getType() {
		return "Rock Climbing";
	}

}