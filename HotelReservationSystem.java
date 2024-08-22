import java.sql.*;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "Admin@123";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public boolean reserveRoom(String guestName, int roomNumber, String contactNumber) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, guestName);
                statement.setInt(2, roomNumber);
                statement.setString(3, contactNumber);
                int affectedRows = statement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet viewReservations() throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public int getRoomNumber(int reservationId, String guestName) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT room_number FROM reservations WHERE reservation_id = ? AND guest_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reservationId);
                statement.setString(2, guestName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("room_number");
                    } else {
                        return -1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateReservation(int reservationId, String newGuestName, int newRoomNumber, String newContactNumber) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE reservations SET guest_name = ?, room_number = ?, contact_number = ? WHERE reservation_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, newGuestName);
                statement.setInt(2, newRoomNumber);
                statement.setString(3, newContactNumber);
                statement.setInt(4, reservationId);
                int affectedRows = statement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReservation(int reservationId) {
        try (Connection connection = getConnection()) {
            String sql = "DELETE FROM reservations WHERE reservation_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reservationId);
                int affectedRows = statement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reservationExists(int reservationId) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reservationId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
