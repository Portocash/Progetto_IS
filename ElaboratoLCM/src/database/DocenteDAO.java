package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.EntityGenitore;
import exception.DAOException;
import exception.DBConnectionException;
import entity.EntityDocente;
import java.sql.Date;

public class DocenteDAO {


	public static void createDocente(EntityDocente eD) throws DAOException, DBConnectionException {
			
		try {

			Connection conn = DBManager.getConnection();
			String query = "INSERT INTO Docenti VALUES (?,?,?,?,?,?,?,?,?);";
			
			try {
		
				PreparedStatement stmt = conn.prepareStatement(query);
				
				stmt.setString(1,eD.getNome());
				stmt.setString(2,eD.getCognome());
				stmt.setDate(3, eD.getDataDiNascita());
				stmt.setString(4,eD.getcomuneDiResidenza());
				stmt.setString(5, eD.getUsername());
				stmt.setString(6,eD.getPassword());
				stmt.setString(7,eD.getNumeroCellulare());
				stmt.setString(8,eD.getEmail());
				stmt.setString(9,eD.getCodiceFiscale());
	//			stmt.setInt(10, eD.get_matricolaDocente());
				stmt.executeUpdate();

			}catch(SQLException e) {
				throw new DAOException("Errore create docente");
			}finally {
				DBManager.closeConnection();
			}
			
		}catch(SQLException e) {
			throw new DBConnectionException("Errore di connessione DB");
		}
	}
		
	public static EntityDocente checkDocenteDatabase(String codiceFiscale) throws DAOException, DBConnectionException {

		EntityDocente eD = null;


		try {

			Connection conn = DBManager.getConnection();
			String query = "SELECT * FROM Docenti WHERE CODICEFISCALE = ? ;";


			try {
				
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

	public static void updateDocente(EntityDocente eD) throws DAOException, DBConnectionException {
		

		try {

			Connection conn = DBManager.getConnection();
			String query = "UPDATE Docenti SET NOME=?, COGNOME=?, DATADINASCITA=?, COMUNEDIRESIDENZA=?, EMAIL=?, NUMERODICELLULARE=?, USERNAME = ?, PASSWORD = ? WHERE MATRICOLA_DOCENTEE=? ;";
		
			try {
				
				PreparedStatement stmt = conn.prepareStatement(query);
				
				stmt.setString(1,eD.getNome());
				stmt.setString(2,eD.getCognome());
				stmt.setDate(3, eD.getDataDiNascita());
				stmt.setString(4,eD.getcomuneDiResidenza());
				stmt.setString(5, eD.getEmail());
				stmt.setString(6,eD.getNumeroCellulare());
				stmt.setString(7,eD.getUsername());
				stmt.setString(8,eD.getPassword());
				stmt.setString(9,eD.get_matricolaDocente());
				
				stmt.executeUpdate();

			}catch(SQLException e) {
				throw new DAOException("Errore update docente");
			}finally {
				DBManager.closeConnection();
			}
			
		}catch(SQLException e) {
			throw new DBConnectionException("Errore di connessione DB");
		}

	}

	public static void deleteDocente(int matricola_docente) throws DAOException, DBConnectionException {
		
		
		try {

			Connection conn = DBManager.getConnection();
			String query = "DELETE FROM Docenti WHERE MATRICOLA_DOCENTE = ?; ";

			try {
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setInt(1,matricola_docente); 
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
