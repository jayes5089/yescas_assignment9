package yescas_assignment9;
import java.util.Comparator;

public class ExerciseCompareByCalories implements Comparator<Exercise> {
	@Override
	public int compare(Exercise e1, Exercise e2) {
		return Double.compare(e1.getCaloriesBurned(), e2.getCaloriesBurned());
	}
}
