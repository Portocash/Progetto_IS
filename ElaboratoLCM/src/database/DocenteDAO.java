package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.EntityGenitore;
import exception.DAOException;
import exception.DBConnectionException;

public class DocenteDAO {

	public static EntityDocente checkDocenteDatabase(String codiceFiscale) throws DAOException, DBConnectionException {

		EntityGenitore eG = null;


		try {

			Connection conn = DBManager.getConnection();

			try {
				String query = "SELECT * FROM DOCENTI WHERE CODICEFISCALE = ? ;";


				PreparedStatement stmt = conn.prepareStatement(query);
				
				
				stmt.setString(1,codiceFiscale);

				ResultSet result = stmt.executeQuery();

				if(result.next()) {
					eD = new EntityDocente(result.getString(1),result.getString(2),result.getDate(3),result.getString(4),result.getString(5), result.getString(6), result.getString(7), result.getString(8), result.getString(9),result.getInt(10));	
				}

			}catch(SQLException e) {
				throw new DAOException("Errore check docente database");
			}finally {
				DBManager.closeConnection();
			}
			
		}catch(SQLException e) {
			throw new DBConnectionException("Errore di connessione DB");
		}

		return eD;
	}

	
}
