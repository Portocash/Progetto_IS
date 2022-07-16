package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.EntityGenitore;
import exception.DAOException;
import exception.DBConnectionException;

public class GenitoreDAO {
	
	public static EntityGenitore readGenitore(String username, String password) throws DAOException, DBConnectionException {
		
		EntityGenitore eG = null;
		
		try {
			
			Connection conn = DBManager.getConnection();
			String query = "SELECT * FROM GestioneIstitutoScolastico.Genitori WHERE username = ? AND password = ?;";
			
			try {
				
				PreparedStatement stmt = conn.prepareStatement(query);  //throws SQLException
				stmt.setString(1, username);  //throws SQLException
				stmt.setString(2, password);
				
				ResultSet res = stmt.executeQuery();  //throws SQLException 
				
				if(res.next()) {
					eG = new EntityGenitore(res.getString(1), res.getString(2), res.getDate(3), res.getString(9), res.getString(4), res.getString(8), res.getString(7), res.getString(5), res.getString(6));
				}
			}
			catch(SQLException e ) {
				throw new DAOException("Errore genitore readGenitore");
			}
			finally {
				DBManager.closeConnection();
			}
			
		} catch(SQLException e) {
			throw new DBConnectionException("Errore di connessione al database");
		}
		return eG;
	}

}
