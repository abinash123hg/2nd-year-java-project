import javax.swing.*;
import java.sql.*;
import java.awt.*;

public class AdminBusSimple extends JFrame {

    static final String URL = "jdbc:mysql://127.0.0.1:3306/bus_management?useSSL=false&allowPublicKeyRetrieval=true";
    static final String USER = "root";
    static final String PASS = "kulu";

    JTextField idField, routeField, capField, timeField;
    JTextArea area;

    public AdminBusSimple() {
        setTitle("Admin Bus Management");
        setSize(400, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        panel.add(new JLabel("Bus ID:"));
        idField = new JTextField();
        panel.add(idField);

        panel.add(new JLabel("Route:"));
        routeField = new JTextField();
        panel.add(routeField);

        panel.add(new JLabel("Capacity:"));
        capField = new JTextField();
        panel.add(capField);

        panel.add(new JLabel("Time HH:MM:SS:"));
        timeField = new JTextField();
        panel.add(timeField);

        JButton add = new JButton("Add");
        JButton delete = new JButton("Delete");
        panel.add(add);
        panel.add(delete);

        add(panel, BorderLayout.NORTH);

        area = new JTextArea();
        add(new JScrollPane(area), BorderLayout.CENTER);

        add.addActionListener(e -> { addBus(); displayBuses(); });
        delete.addActionListener(e -> { deleteBus(); displayBuses(); });

        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS buses(" +
                    "bus_id INT PRIMARY KEY, " +
                    "route VARCHAR(100), " +
                    "capacity INT, " +
                    "departure_time TIME)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
        }

        displayBuses();
    }

    void addBus() {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement()) {
            s.executeUpdate("INSERT INTO buses VALUES(" +
                    idField.getText() + ",'" +
                    routeField.getText() + "'," +
                    capField.getText() + ",'" +
                    timeField.getText() + "')");
            idField.setText("");
            routeField.setText("");
            capField.setText("");
            timeField.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    void deleteBus() {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement()) {
            s.executeUpdate("DELETE FROM buses WHERE bus_id=" + idField.getText());
            idField.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    void displayBuses() {
        area.setText("");
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM buses")) {
            while (rs.next()) {
                area.append(rs.getInt("bus_id") + ". " +
                        rs.getString("route") + " - " +
                        rs.getInt("capacity") + " seats, " +
                        rs.getTime("departure_time") + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminBusSimple().setVisible(true));
    }
}
