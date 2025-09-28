import javax.swing.*;
import java.sql.*;
import java.awt.*;

public class BusBookingStep1 extends JFrame {

    static final String URL = "jdbc:mysql://127.0.0.1:3306/bus_management?useSSL=false&allowPublicKeyRetrieval=true";
    static final String USER = "root";
    static final String PASS = "kulu";

    JTextField name;
    JComboBox<String> busBox;
    JTextArea area;
    JButton showBookings;

    public BusBookingStep1() {
        setTitle("Bus Booking - Signup");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Enter Name:"));
        name = new JTextField();
        panel.add(name);

        panel.add(new JLabel("Select Bus:"));
        busBox = new JComboBox<>(new String[]{"Route A", "Route B", "Route C"});
        panel.add(busBox);

        JButton book = new JButton("Book");
        panel.add(book);
        showBookings = new JButton("Show Bookings");
        panel.add(showBookings);
        add(panel, BorderLayout.NORTH);

        area = new JTextArea();
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);

        book.addActionListener(e -> bookBus());
        showBookings.addActionListener(e -> displayBookings());

        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS bookings(id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50), bus VARCHAR(50))");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex);
        }
    }

    void bookBus() {
        String user = name.getText().trim();
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name!");
            return;
        }
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement()) {
            s.executeUpdate("INSERT INTO bookings(username, bus) VALUES('" + user + "','" + busBox.getSelectedItem() + "')");
            name.setText("");
            JOptionPane.showMessageDialog(this, "Booking Successful!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e);
        }
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
        SwingUtilities.invokeLater(() -> new BusBookingStep1().setVisible(true));
    }
}