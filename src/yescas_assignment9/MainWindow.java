package yescas_assignment9;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class MainWindow extends JFrame {
	private JTable summaryTable;
	private DefaultTableModel tableModel;
	private final JTextField txtName = new JTextField(20);
	private final JTextField txtDate = new JTextField(20);
	private final JTextField txtDuration = new JTextField(20);
	private final JTextField txtComment = new JTextField(20);
	private final JTextField txtSpecific = new JTextField(20);
	private final JButton btnAddExercise = new JButton("Add Exercise");
	private final JButton btnSave = new JButton("Save");
	private final JLabel lblSpecificInfo = new JLabel("Specific Info:");
	private final JComboBox<String> exerciseType = new JComboBox<>(new String[] {"Run/Walk", "Weight Lifting", "Rock Climbing"});
	private final ArrayList<Exercise> exercises = new ArrayList<>();
	
	public MainWindow() {
		super("Exercise Tracker");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setupComponents();
		disableComponents();
		pack();
		setVisible(true);
		promptLogin();
	}
	private void promptLogin() {
		LoginFrame loginFrame = LoginFrame.V(this);
		loginFrame.setVisible(true);
	}
	private void setupComponents() {
		setLayout(new BorderLayout());
		setupMenuBar();
		JPanel leftPanel = setupExerciseInputPanel();
		JScrollPane rightPanel = setupExerciseSummaryPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		splitPane.setDividerLocation(150);
		splitPane.setOneTouchExpandable(false);
		splitPane.setEnabled(false);
		add(splitPane, BorderLayout.CENTER);
		setupSouthPanel();
	}
	
	private JPanel setupExerciseInputPanel() {	
		JPanel leftPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 1;
		gbc.weighty = 0;
		
		exerciseType.addActionListener(this::updateSpecificInput);
		updateSpecificInput(null);
		String[] labels = {"Exercise Type:", "Name", "Date (MM/DD/YYYY):", "Duration (min):", lblSpecificInfo.getText(), "Comment:"};
		JComponent[] components = {exerciseType, txtName, txtDate, txtDuration, txtSpecific, txtComment};
		
		for (int i = 0; i < labels.length; i++) {
			addInputComponent(leftPanel, labels[i], components[i], gbc);
		}
		
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		leftPanel.add(new JLabel(""), gbc);
		
		return leftPanel;
	}
	private void setupSouthPanel() {
		JPanel southPanel = new JPanel();
		southPanel.add(btnAddExercise);
		btnAddExercise.addActionListener(this::addExercise);
		add(southPanel, BorderLayout.SOUTH);
	}
	private void addInputComponent(JPanel panel, String label, JComponent component, GridBagConstraints gbc) {
		panel.add(new JLabel(label), gbc);
		panel.add(component, gbc);
	}
	private void updateSpecificInput(ActionEvent e) {
		String type = (String) exerciseType.getSelectedItem();
		lblSpecificInfo.setText(switch(type) {
		case "Run/Walk" -> "Distance (miles):";
		case "Weight Lifting" -> "Weight Lifted (lbs):";
		case "Rock Climbing" -> "Wall Height (ft), Repetitions:";
		default -> "Specific Info:";
		});
	}
	private void setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem("Save")).addActionListener(e -> saveExercises());;
		fileMenu.add(new JMenuItem("Load")).addActionListener(e -> loadExercises());
		fileMenu.add(new JMenuItem("Logout")).addActionListener(e -> logout());
		fileMenu.add(new JMenuItem("Exit")).addActionListener(e -> exitApplication());;
		menuBar.add(fileMenu);
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new JMenuItem("About")).addActionListener(e -> showAbout());
		menuBar.add(helpMenu);
		
		setJMenuBar(menuBar);
	}
	private void loadExercises() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open Exercise Data File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV FIles", "csv");
		fileChooser.setFileFilter(filter);
		
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File fileToLoad = fileChooser.getSelectedFile();
			List<Exercise> exercisesLoaded = loadExercisesFromFile(fileToLoad.getAbsolutePath());
			exercises.clear();
			exercises.addAll(exercisesLoaded);
			updateExerciseTable();
		}
	}
	private static Exercise createExerciseFromData(String[] data) {
		try {
			String name = data[0].replace("\"","");
			String type = data[1].replace("\"","");
			String date = data[2].replace("\"","");
			double duration = Double.parseDouble(data[3].replaceAll("[^\\d.]", ""));
			String specificInfo = data[4].replace("\"","");
			String comment = data[5].replace("\"","");
			double caloriesBurned = Double.parseDouble(data[6].replaceAll("[^\\d.]", ""));
			
			switch (type) {
			case "Run/Walk":
				return new RunWalk(name, date, duration, comment, Double.parseDouble(specificInfo));
			case "Weight Lifting":
				return new WeightLifting(name, date, duration, comment, Double.parseDouble(specificInfo));
			case "Rock Climbing":
				String[] parts = specificInfo.split(",");
				double wallHeight = Double.parseDouble(parts[0].replaceAll("[^\\d.]",""));
				int repetitions = Integer.parseInt(parts[1].replaceAll("[^\\d.]",""));
				return new RockClimbing(name, date, duration, comment, wallHeight, repetitions);
			default:
				throw new IllegalArgumentException("Unsupported exercise type");
			}
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Error parsing exercise data:" + e.getMessage(), "Parsing Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	private void updateExerciseTable() {
		tableModel.setRowCount(0);
		for (Exercise exercise : exercises) {
			addExerciseToSummary(exercise);
		}
	}
	private void logout() {
		tableModel.setRowCount(0);
		txtName.setText("");
		txtDate.setText("");
		txtDuration.setText("");
		txtSpecific.setText("");
		txtComment.setText("");
		disableComponents();
		exercises.clear();
		promptLogin();
	}
	private void exitApplication() {
		System.exit(0);
	}
	private void showAbout() {
		JOptionPane.showMessageDialog(this, "Exercise Tracker, Spring 2024", "About", JOptionPane.INFORMATION_MESSAGE);
	}
	private void disableComponents() {
		setComponentState(false);
	}
	public void enableComponents() {
		setComponentState(true);
	}
	private void setComponentState(boolean enabled) {
		Color color = enabled ? Color.white : new Color(235, 235, 235);
		btnAddExercise.setEnabled(enabled);
		txtName.setEnabled(enabled);
		txtDate.setEnabled(enabled);
		txtDuration.setEnabled(enabled);
		txtComment.setEnabled(enabled);
		txtSpecific.setEnabled(enabled);
		btnSave.setEnabled(enabled);
		exerciseType.setEnabled(enabled);
		
		txtName.setBackground(color);
		txtDate.setBackground(color);
		txtDuration.setBackground(color);
		txtComment.setBackground(color);
		exerciseType.setBackground(color);
		txtSpecific.setBackground(color);
	}
	
	private JScrollPane setupExerciseSummaryPanel() {
		String[] columnNames = {"Name", "Type", "Date", "Duration", "Measurements", "Comment", "Calories Burned"};
		tableModel = new DefaultTableModel(columnNames, 0);
		summaryTable = new JTable(tableModel);
		summaryTable.setFillsViewportHeight(true);
		return new JScrollPane(summaryTable);
	}
	private void addExerciseToSummary(Exercise exercise) {
		Object[] data = {
				exercise.getName(),
				exercise.getType(),
				exercise.getDateAsString(),
				String.format("%.2f min", exercise.getDuration()),
				exercise.getSpecificInfo(),
				exercise.getComment(),
				String.format("%.1f cal", exercise.getCaloriesBurned())
		};
		tableModel.addRow(data);
		sortTableByCalories();
	}
	private void sortTableByCalories() {
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
		summaryTable.setRowSorter(sorter);
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(6, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}
	
	private void addExercise(ActionEvent e) {
		if (validateInput()) {
			Exercise exercise = createExercise();
			if (exercise != null) {
				exercises.add(exercise);
				addExerciseToSummary(exercise);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "Error adding exercise: ", "Input Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	private boolean validateInput() {
		if (txtName.getText().isEmpty() ||
			!Exercise.isValidDate(txtDate.getText()) ||
			!isValidNumber(txtDuration.getText()) ||
			!isValidNumber(txtSpecific.getText())) {
			
			JOptionPane.showMessageDialog(this, "Please ensure all fields are filled correctly and contain valid data.", "Input Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	private boolean isValidNumber(String numberStr) {
		try {
			double num = Double.parseDouble(numberStr);
			return num > 0;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	private Exercise createExercise() throws NumberFormatException {
		String type = (String) exerciseType.getSelectedItem();
		String name = txtName.getText();
		String date = txtDate.getText();
		double duration = Double.parseDouble(txtDuration.getText());
		String comment = txtComment.getText();
		String specificInfo = txtSpecific.getText();
		return switch (type) {
			case "Run/Walk" -> new RunWalk(name, date, duration, comment, Double.parseDouble(specificInfo));
			case "Weight Lifting" -> new WeightLifting(name, date, duration, comment, Double.parseDouble(specificInfo));
			case "Rock Climbing" -> {
				String[] parts = specificInfo.split(",");
				if (parts.length != 2) {
					JOptionPane.showMessageDialog(this, "Please enter wall height and repetitions in the format: height, reps", "Input Error", JOptionPane.ERROR_MESSAGE);
					yield null;
				}
				yield new RockClimbing(name, date, duration, comment, Double.parseDouble(parts[0]), Integer.parseInt(parts[1]));
			}
		default -> throw new IllegalArgumentException("Unsupported exercise type");
		};
	}
	private void saveExercises() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Exercise Data");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
		fileChooser.addChoosableFileFilter(filter);
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
				fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
			}
			try (PrintWriter out = new PrintWriter(new FileWriter(fileToSave))){
				out.println("Name,Type,Date,Duration,Specific Info,Comment,Calories Burned");
				for (Exercise exercise : exercises) {
				String duration = String.format("%.2f", exercise.getDuration());
				String specificInfo = String.format("%.2f", Double.parseDouble(exercise.getSpecificInfo().replaceAll("[^\\d.]", "")));
				String caloriesBurned = String.format("%.2f", exercise.getCaloriesBurned());
				
				out.println(String.format("\"%s\",\"%s\",\"%s\",%s,%s,\"%s\",%s",
					exercise.getName(),
					exercise.getType(),
					exercise.getDate(),
					duration,
					specificInfo,
					exercise.getComment(),
					caloriesBurned));
				}
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage(), "File Write Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public List<Exercise> loadExercisesFromFile(String filePath) {
		List<Exercise> loadedExercises = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))){
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				Exercise exercise = createExerciseFromData(data);
				if (exercise != null) {
					loadedExercises.add(exercise);
				}
			}
		}
		catch (IOException e) {
			System.err.println("Error reading from file: " + e.getMessage());
		}
		return loadedExercises;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(MainWindow::new);
	}
}
