package yescas_assignment9;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ExerciseWriter {
	public static void writeExercisesToFile(List<Exercise> exercises, String fileName) {
		try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
			out.println("Name,Type,Date,Duration,Specific Info,Comment,Calories Burned");
			for (Exercise exercise : exercises) {
				out.println(formatExerciseForOutput(exercise));
			}
		}
		catch (IOException e) {
			System.err.println("Error writing to file: " + e.getMessage());
		}
	}
	
	private static String formatExerciseForOutput(Exercise exercise) {
		return String.format("\"%s\",\"%s\",\"%s\",%.2f,\"%s\",\"%s\",%.2f",
				exercise.getName(),
				exercise.getType(),
				exercise.getDate(),
				exercise.getDuration(),
				exercise.getSpecificInfo(),
				exercise.getComment(),
				exercise.getCaloriesBurned());
	}
}

