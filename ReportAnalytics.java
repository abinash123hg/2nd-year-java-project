import javax.swing.*;
import java.sql.*;
import java.awt.*;

public class ReportAnalytics extends JFrame {
    static final String URL = "jdbc:mysql://127.0.0.1:3306/bus_management?useSSL=false&allowPublicKeyRetrieval=true";
    static final String USER = "root";
    static final String PASS = "kulu";

    JTextArea area;
    JButton showBtn;

    public ReportAnalytics() {
        setTitle("Booking Reports");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        showBtn = new JButton("Show Reports");
        add(showBtn, BorderLayout.NORTH);

        area = new JTextArea();
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);

        JButton clearBtn = new JButton("Clear Report"); // New clear button
        add(clearBtn, BorderLayout.SOUTH);

        showBtn.addActionListener(e -> showReports());
        clearBtn.addActionListener(e -> area.setText(""));
    }

    void showReports() {
        area.setText("Reports:\n");
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT bus, COUNT(*) AS bookings FROM bookings GROUP BY bus")) {
            int totalRevenue = 0;
            while (rs.next()) {
                String bus = rs.getString("bus");
                int count = rs.getInt("bookings");
                int amount = count * 50;
                totalRevenue += amount;
                area.append(bus + " -> " + count + " bookings, Revenue: $" + amount + "\n");
            }
            area.append("Total Revenue: $" + totalRevenue);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReportAnalytics().setVisible(true));
    }
}