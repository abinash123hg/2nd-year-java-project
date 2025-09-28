import javax.swing.*;
import java.sql.*;
import java.awt.*;

public class PaymentSimple extends JFrame {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/bus_management?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "kulu";

    private JTextField nameField;
    private JComboBox<String> busBox;
    private JButton confirmBtn;

    public PaymentSimple() {
        setTitle("Booking Payment");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Your Name:"));
        nameField = new JTextField(15);
        add(nameField);
        add(new JLabel("Select Bus:"));
        busBox = new JComboBox<>();
        add(busBox);
        confirmBtn = new JButton("Confirm Booking & Pay");
        add(confirmBtn);
        add(new JLabel());

        initializeDatabase();
        loadBuses();

        confirmBtn.addActionListener(e -> confirmBooking());
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50), " +
                    "bus VARCHAR(100), " +
                    "booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database initialization error: " + e.getMessage());
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
            if (busBox.getItemCount() == 0) {
                busBox.addItem("No buses available");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading buses: " + e.getMessage());
            busBox.addItem("Error loading buses");
        }
    }

    private void confirmBooking() {
        String name = nameField.getText().trim();
        String bus = busBox.getSelectedItem() != null ? busBox.getSelectedItem().toString() : "";

        if (name.isEmpty() || bus.isEmpty() || bus.equals("No buses available") || bus.equals("Error loading buses")) {
            JOptionPane.showMessageDialog(this, "Please enter your name and select a valid bus.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO bookings (username, bus) VALUES (?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, bus);
            pstmt.executeUpdate();
            int amount = 50;
            JOptionPane.showMessageDialog(this, "Booking & Payment Confirmed!\nPassenger: " + name + 
                                          "\nBus: " + bus + "\nAmount Paid: $" + amount + "\nThank you!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Booking error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaymentSimple().setVisible(true));
    }
}