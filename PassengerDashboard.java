import javax.swing.*;
import java.sql.*;
import java.awt.*;

public class PassengerDashboard extends JFrame {
    static final String URL = "jdbc:mysql://127.0.0.1:3306/bus_management?useSSL=false&allowPublicKeyRetrieval=true";
    static final String USER = "root";
    static final String PASS = "kulu";

    JTextField nameField;
    JComboBox<String> busBox;
    JTextArea area;

    public PassengerDashboard() {
        setTitle("Passenger Dashboard");
        setSize(400, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Your Name:"));
        nameField = new JTextField();
        panel.add(nameField);
        panel.add(new JLabel("Select Bus:"));
        busBox = new JComboBox<>();
        panel.add(busBox);
        JButton bookBtn = new JButton("Book");
        panel.add(bookBtn);
        JButton showBtn = new JButton("Show My Bookings");
        panel.add(showBtn);
        JButton resetBtn = new JButton("Reset Name"); // New reset button
        panel.add(resetBtn);
        add(panel, BorderLayout.NORTH);

        area = new JTextArea();
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);

        initializeDatabase();
        loadBuses();

        bookBtn.addActionListener(e -> { if (bookBus()) displayMyBookings(); });
        showBtn.addActionListener(e -> displayMyBookings());
        resetBtn.addActionListener(e -> nameField.setText(""));
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50), bus VARCHAR(100), booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB init error: " + e.getMessage());
        }
    }

    private void loadBuses() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT bus_id, route FROM buses")) {
            busBox.removeAllItems();
            while (rs.next()) {
                busBox.addItem(rs.getInt("bus_id") + " - " + rs.getString("route"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load buses error: " + e.getMessage());
        }
    }

    private boolean bookBus() {
        try {
            String user = nameField.getText().trim();
            String bus = busBox.getSelectedItem() != null ? busBox.getSelectedItem().toString() : "";
            if (user.isEmpty() || bus.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter name & select bus");
                return false;
            }
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO bookings(username, bus) VALUES(?, ?)")) {
                pstmt.setString(1, user);
                pstmt.setString(2, bus);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Booked Successfully!");
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Booking error: " + e.getMessage());
            return false;
        }
    }

    private void displayMyBookings() {
        area.setText("My Bookings:\n");
        try {
            String user = nameField.getText().trim();
            if (user.isEmpty()) {
                area.append("Enter name to view bookings.\n");
                return;
            }
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM bookings WHERE username = ?")) {
                pstmt.setString(1, user);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        area.append("No bookings found.\n");
                    } else {
                        do {
                            area.append(rs.getInt("id") + ". " + rs.getString("username") + " -> " +
                                    rs.getString("bus") + " (Booked: " + rs.getTimestamp("booking_time") + ")\n");
                        } while (rs.next());
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PassengerDashboard().setVisible(true));
    }
}