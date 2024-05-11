package yescas_assignment9;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.*;

class LoginFrame extends JDialog {
	private static LoginFrame v;
	private MainWindow mainWindow;
	
	private final JLabel lblUsername = new JLabel("Username");
	private final JLabel lblPassword = new JLabel("Password");
	private final JTextField txtUsername = new JTextField(8);
	private final JPasswordField txtPassword = new JPasswordField();
	private final JButton btnLogin = new JButton("Login");
	private final JButton btnCancel = new JButton("Cancel");
	
	public static LoginFrame V(MainWindow mainWindow) {
		if (v == null) {
			v = new LoginFrame(mainWindow);
		}
		return v;
	}
	
	private LoginFrame(MainWindow mainWindow) {
		super(mainWindow, "Login Window", true);
		this.mainWindow = mainWindow;
		
		setLayout(new BorderLayout());
		JPanel panel = new JPanel(new GridLayout(2,2));
		panel.add(lblUsername);
		panel.add(txtUsername);
		panel.add(lblPassword);
		panel.add(txtPassword);
		add(panel, BorderLayout.CENTER);
		
		JPanel btnPanel = new JPanel();
		btnLogin.addActionListener(this::loginActionPerformed);
		btnCancel.addActionListener(this::cancelActionPerformed);
		btnPanel.add(btnLogin);
		btnPanel.add(btnCancel);
		add(btnPanel, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(mainWindow);
	}
	
	private void loginActionPerformed(ActionEvent e) {
		String strPassword = String.valueOf(txtPassword.getPassword());
		String strUsername = txtUsername.getText().trim();
		if (verifyLogin(strUsername, strPassword)) {
			JOptionPane.showMessageDialog(this, "Login successful.", "Login", JOptionPane.INFORMATION_MESSAGE);
			mainWindow.enableComponents();
			setVisible(false);
			txtUsername.setText("");
			txtPassword.setText("");
		}
		
		else {
			JOptionPane.showMessageDialog(this, "Incorrect username/password.", "Login", JOptionPane.ERROR_MESSAGE);
			txtUsername.setText("");
			txtPassword.setText("");
			txtUsername.requestFocusInWindow();
		}
	}
	
	private void cancelActionPerformed(ActionEvent e) {
		txtPassword.setText("");
		txtUsername.setText("");
		setVisible(false);
	}
	
	private boolean verifyLogin(String username, String password) {
		return "healthy".equals(username) && "donuts".equals(password);
	}
}
