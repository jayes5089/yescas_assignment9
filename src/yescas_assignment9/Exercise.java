package yescas_assignment9;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public abstract class Exercise{
	private String name;
	private Date date;
	private double duration;
	private String comment;
	
	public Exercise(String name, String date, double duration, String comment) {
		this.name = name;
		this.duration = duration;
		this.comment = comment;
		setDate(date);
	}
	
	private void setDate(String date) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			this.date = dateFormat.parse(date);
		}
		catch (Exception ex) {
			this.date = new Date();
		}
	}
	
	public String getDateAsString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return dateFormat.format(date);
	}
	
	public static boolean isValidDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(date);
			return true;
		}
		catch (ParseException e) {
			return false;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Date getDate() {
		return date;
	}
	
	
	public double getDuration() {
		return duration;
	}
	
	public String getComment() {
		return comment;
	}
	
	public abstract String getSpecificInfo();
	
	public abstract String getType();
	
	public abstract double getCaloriesBurned();

}