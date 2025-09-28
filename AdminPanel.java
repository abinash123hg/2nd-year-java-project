import javax.swing.*;
import java.sql.*;
import java.awt.*;

public class AdminPanel extends JFrame {

    static final String URL = "jdbc:mysql://127.0.0.1:3306/bus_management?useSSL=false&allowPublicKeyRetrieval=true";
    static final String USER = "root";
    static final String PASS = "kulu";

    JTextField userField;
    JPasswordField passField;
    JTextArea area;
    JButton loginButton;

    public AdminPanel() {
        setTitle("Admin Login");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        userField = new JTextField();
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        panel.add(passField);
        loginButton = new JButton("Login");
        panel.add(loginButton);
        add(panel, BorderLayout.NORTH);

        area = new JTextArea();
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (user.equals("admin") && pass.equals("pass123")) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                displayBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username/password");
            }
        });
    }

    void displayBookings() {
        area.setText("All Bookings:\n");
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM bookings")) {
            while (rs.next()) {
                area.append(rs.getInt("id") + ". " + rs.getString("username") + " -> " + rs.getString("bus") + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPanel().setVisible(true));
    }
}