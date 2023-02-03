package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnector 
{
	private static final String PRIMARY_DB_URL = "jdbc:mysql://localhost:3306/encryptionlocker";
	private static final String Secondary_DB_URL = "jdbc:mysql://localhost:3306/encryptionlocker";
	private static final String username = "root";
	private static final String password = "Raman231!";
	
	private Connection connection;
	
	public DatabaseConnector()
	{
		initConnect();
	}

	private void initConnect() 
	{
		try {
			connection = DriverManager.getConnection(PRIMARY_DB_URL, username, password);
			System.out.println("Connection is granted to the server.");
		}catch (Exception e) 
		{
			System.out.println("Failed to connect with primary server.");
			try {
				connection = DriverManager.getConnection(Secondary_DB_URL, username, password);
				System.out.println("Connection is granted to the backup server.");
			}catch (Exception e2) 
			{
				System.out.println("Failed to connect with backup server.");
			}
		}
	}
	
	public boolean isConnected() 
	{
        return connection != null;
    }
	
	public boolean updatePasswordAttempt(String password)
	{
		String sql = "UPDATE users SET attempts = attempts - 1 WHERE password=?";
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, password);
			int rowsEffected = statement.executeUpdate();
			return (rowsEffected > 0)? true : false;
		} catch (SQLException e) {
			System.out.println("(Update) Database access error occursor this method is called on a closed connection");
		}
		return false;
	}
}