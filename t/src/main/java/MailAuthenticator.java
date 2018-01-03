import javax.mail.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MailAuthenticator extends Authenticator {
	private JDialog passwordDialog = new JDialog(new JFrame(), true);
	private JTextField usernameField = new JTextField(20);
	private JPasswordField passwordField = new JPasswordField(20);
	private JButton okButton = new JButton("OK");

	public MailAuthenticator() {
		this("");
	}

	public MailAuthenticator(String username) {
		JLabel mainLabel = new JLabel(
				"Please enter your username and password: ");
		JLabel userLabel = new JLabel("Username: ");
		JLabel passwordLabel = new JLabel("Password: ");
		Container pane = passwordDialog.getContentPane();
		pane.setLayout(new GridLayout(4, 1));
		pane.add(mainLabel);
		JPanel p2 = new JPanel();
		p2.add(userLabel);
		p2.add(usernameField);
		usernameField.setText(username);
		pane.add(p2);
		JPanel p3 = new JPanel();
		p3.add(passwordLabel);
		p3.add(passwordField);
		pane.add(p3);
		JPanel p4 = new JPanel();
		p4.add(okButton);
		pane.add(p4);
		passwordDialog.pack();
		ActionListener listener = new HideDialog();
		okButton.addActionListener(listener);
		usernameField.addActionListener(listener);
		passwordField.addActionListener(listener);
	}

	class HideDialog implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			passwordDialog.setVisible(false);
		}
	}

	public PasswordAuthentication getPasswordAuthentication() {
		passwordDialog.setVisible(true);
		// getPassword() returns an array of chars for security reasons.
		// We need to convert that to a String for
		// the PasswordAuthentication() constructor.
		String password = new String(passwordField.getPassword());
		String username = usernameField.getText();
		// Erase the password in case this is used again.
		// The provider should cache the password if necessary.
		passwordField.setText("");
		return new PasswordAuthentication(username, password);
	}
}
