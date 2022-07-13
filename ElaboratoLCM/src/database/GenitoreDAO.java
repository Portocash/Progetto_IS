package database;

	import java.util.ArrayList;
	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.SQLException;

	import entity.EntityGenitore;
	import exception.DAOException;
	import exception.DBConnectionException;	
 
	public class GenitoreDAO {

		public static void createGenitore(EntityGenitore eg) throws DAOException, DBConnectionException {
			
			try {

				Connection conn = DBManager.getConnection();
				String query = "INSERTO INTO Genitori VALUES (?,?,?,?,?,?,?,?,?);";
				
				try {

					PreparedStatement stmt = conn.prepareStatement(query);
					
					stmt.setString(1,eg.getNome());
					stmt.setString(2,eg.getCognome());
					stmt.setDate(3, eg.getDataDiNascita());
					stmt.setString(4,eg.getcomuneDiResidenza());
					stmt.setString(5, eg.getUsername());
					stmt.setString(6,eg.getPassword());
					stmt.setString(7,eg.getNumeroCellulare());
					stmt.setString(8,eg.getEmail());
					stmt.setString(9,eg.getCodiceFiscale());

					stmt.executeUpdate();

				}catch(SQLException e) {
					throw new DAOException("Errore create genitore");
				}finally {
					DBManager.closeConnection();
				}
				
			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}
		}



		public static EntityGenitore checkGenitoreDatabase(String codiceFiscale) throws DAOException, DBConnectionException {

			EntityGenitore eG = null;



			try {

				Connection conn = DBManager.getConnection();
				String query = "SELECT * FROM Genitori WHERE CODICEFISCALE = ? ;";

				try {
			
					PreparedStatement stmt = conn.prepareStatement(query);				
					stmt.setString(1,codiceFiscale);

					ResultSet result = stmt.executeQuery();

					if(result.next()) {
						eG = new EntityGenitore(result.getString(1),result.getString(2),result.getDate(3),result.getString(4),result.getString(5), result.getString(6), result.getString(7), result.getString(8), result.getString(9));	
					}

				}catch(SQLException e) {
					throw new DAOException("Errore lettura genitore");
				}finally {
					DBManager.closeConnection();
				}
				
			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}

			return eG;
		}

	public static void updateGenitore(EntityGenitore eg) throws DAOException, DBConnectionException {
			

			try {

				Connection conn = DBManager.getConnection();
				String query = "UPDATE Genitori SET NOME=?, COGNOME=?, DATADINASCITA=?, COMUNEDIRESIDENZA=?, EMAIL=?, NUMERODICELLULARE=?, USERNAME = ?, PASSWORD = ? WHERE CODICEFISCALE=? ;";
				
				try {
					
					PreparedStatement stmt = conn.prepareStatement(query);
					
					stmt.setString(1,eg.getNome());
					stmt.setString(2,eg.getCognome());
					stmt.setDate(3, eg.getDataDiNascita());
					stmt.setString(4,eg.getcomuneDiResidenza());
					stmt.setString(5, eg.getEmail());
					stmt.setString(6,eg.getNumeroCellulare());
					stmt.setString(7,eg.getUsername());
					stmt.setString(8,eg.getPassword());
					stmt.setString(9,eg.getCodiceFiscale());
					
					stmt.executeUpdate();

				}catch(SQLException e) {
					throw new DAOException("Errore update genitore");
				}finally {
					DBManager.closeConnection();
				}
				
			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}

		}

	public static void deleteGenitore(String codicefiscale) throws DAOException, DBConnectionException {
			
			
			try {

				Connection conn = DBManager.getConnection();
				String query = "DELETE FROM Genitori WHERE CODICEFISCALE = ?; ";

				try {
					PreparedStatement stmt = conn.prepareStatement(query);
					stmt.setString(1,codicefiscale); 
					stmt.executeUpdate();

				}catch(SQLException e) {
					throw new DAOException("Errore cancellazione genitore");
				} finally {
					DBManager.closeConnection();
				}

			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}
		}
	
	}
