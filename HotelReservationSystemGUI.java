import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HotelReservationSystemGUI {

    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Admin@123";

    private Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelReservationSystemGUI().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Hotel Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JButton reserveButton = new JButton("Reserve a Room");
        JButton viewButton = new JButton("View Reservations");
        JButton getRoomButton = new JButton("Get Room Number");
        JButton updateButton = new JButton("Update Reservation");
        JButton deleteButton = new JButton("Delete Reservation");
        JButton exitButton = new JButton("Exit");

        panel.add(reserveButton);
        panel.add(viewButton);
        panel.add(getRoomButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(exitButton);

        frame.add(panel, BorderLayout.EAST);

        reserveButton.addActionListener(e -> reserveRoom(textArea));
        viewButton.addActionListener(e -> viewReservations(textArea));
        getRoomButton.addActionListener(e -> getRoomNumber(textArea));
        updateButton.addActionListener(e -> updateReservation(textArea));
        deleteButton.addActionListener(e -> deleteReservation(textArea));
        exitButton.addActionListener(e -> exit());

        frame.setVisible(true);
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void reserveRoom(JTextArea textArea) {
        String guestName = JOptionPane.showInputDialog("Enter guest name:");
        String roomNumber = JOptionPane.showInputDialog("Enter room number:");
        String contactNumber = JOptionPane.showInputDialog("Enter contact number:");

        try {
            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, guestName);
            statement.setInt(2, Integer.parseInt(roomNumber));
            statement.setString(3, contactNumber);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                textArea.append("Reservation successful!\n");
            } else {
                textArea.append("Reservation failed.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewReservations(JTextArea textArea) {
        try {
            String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            textArea.setText("Current Reservations:\n");
            textArea.append("+----------------+-----------------+---------------+----------------------+-------------------------+\n");
            textArea.append("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |\n");
            textArea.append("+----------------+-----------------+---------------+----------------------+-------------------------+\n");
            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();
                textArea.append(String.format("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate));
            }
            textArea.append("+----------------+-----------------+---------------+----------------------+-------------------------+\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getRoomNumber(JTextArea textArea) {
        String reservationId = JOptionPane.showInputDialog("Enter reservation ID:");
        String guestName = JOptionPane.showInputDialog("Enter guest name:");

        try {
            String sql = "SELECT room_number FROM reservations WHERE reservation_id = ? AND guest_name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(reservationId));
            statement.setString(2, guestName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int roomNumber = resultSet.getInt("room_number");
                textArea.append("Room number for Reservation ID " + reservationId + " and Guest " + guestName + " is: " + roomNumber + "\n");
            } else {
                textArea.append("Reservation not found for the given ID and guest name.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateReservation(JTextArea textArea) {
        String reservationId = JOptionPane.showInputDialog("Enter reservation ID to update:");
        if (reservationId == null || reservationId.isEmpty()) return;

        String newGuestName = JOptionPane.showInputDialog("Enter new guest name:");
        String newRoomNumber = JOptionPane.showInputDialog("Enter new room number:");
        String newContactNumber = JOptionPane.showInputDialog("Enter new contact number:");

        try {
            String sql = "UPDATE reservations SET guest_name = ?, room_number = ?, contact_number = ? WHERE reservation_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newGuestName);
            statement.setInt(2, Integer.parseInt(newRoomNumber));
            statement.setString(3, newContactNumber);
            statement.setInt(4, Integer.parseInt(reservationId));
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                textArea.append("Reservation updated successfully!\n");
            } else {
                textArea.append("Reservation update failed.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteReservation(JTextArea textArea) {
        String reservationId = JOptionPane.showInputDialog("Enter reservation ID to delete:");

        try {
            String sql = "DELETE FROM reservations WHERE reservation_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(reservationId));
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                textArea.append("Reservation deleted successfully!\n");
            } else {
                textArea.append("Reservation deletion failed.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void exit() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            System.out.println("Thank you for using the Hotel Reservation System!");
            System.exit(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
