package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import entity.EntityAlunno;
import exception.DAOException;
import exception.DBConnectionException;

public class AlunnoDAO {
	
	public static ArrayList<EntityAlunno> getFigli(String codiceFiscaleGenitore) throws DAOException, DBConnectionException {
		
		ArrayList<EntityAlunno> figli = new ArrayList<EntityAlunno>();
		
		try {
			
			Connection conn = DBManager.getConnection();
			String query = "SELECT nomeAlunno, cognomeAlunno, dataDiNascitaAlunno, comuneDiResidenzaAlunno, usernameAlunno, passwordAlunno, "
					+ "numeroDiCellulareAlunno, emailAlunno, codiceFiscaleAlunno, matricolaAlunno FROM GestioneIstitutoScolastico.alunni_genitori "
					+ "WHERE codiceFiscaleGenitore = ?;";
			
			try {
				
				PreparedStatement stmt = conn.prepareStatement(query);  //throws SQLException
				stmt.setString(1, codiceFiscaleGenitore);  //throws SQLException
				
				ResultSet res = stmt.executeQuery();  //throws SQLException 
				
				while(res.next()) {
					 figli.add(new EntityAlunno(res.getString(1), res.getString(2), res.getDate(3), res.getString(9), res.getString(4), res.getString(8), res.getString(7), res.getString(5), res.getString(6), res.getInt(10)));
				}
			}
			catch(SQLException e ) {
				throw new DAOException("Errore alunno readAlunno");
			}
			finally {
				DBManager.closeConnection();
			}
			
		} catch(SQLException e) {
			throw new DBConnectionException("Errore di connessione al database");
		}
		return figli;
	}

	
	public static EntityAlunno readAlunno(String username, String password) throws DAOException, DBConnectionException {
		
		EntityAlunno eA = null;
		
		try {
			
			Connection conn = DBManager.getConnection();
			String query = "SELECT * FROM GestioneIstitutoScolastico.Alunni WHERE username = ? AND password = ?;";
			
			try {
				
				PreparedStatement stmt = conn.prepareStatement(query);  //throws SQLException
				stmt.setString(1, username);  //throws SQLException
				stmt.setString(2, password);
				
				ResultSet res = stmt.executeQuery();  //throws SQLException 
				
				if(res.next()) {
					eA = new EntityAlunno(res.getString(1), res.getString(2), res.getDate(3), res.getString(9), res.getString(4), res.getString(8), res.getString(7), res.getString(5), res.getString(6), res.getInt(10));
					
				}
			}
			catch(SQLException e ) {
				throw new DAOException("Errore alunno readAlunno");
			}
			finally {
				DBManager.closeConnection();
			}
			
		} catch(SQLException e) {
			throw new DBConnectionException("Errore di connessione al database");
		}
		return eA;
	}
	
	
	public static void createAlunno(EntityAlunno eA) throws DAOException, DBConnectionException {
		
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "INSERT INTO GestioneIstitutoScolastico.Alunni VALUES (?,?,?,?,?,?,?,?,?);";
		
			try {
			
				PreparedStatement stmt = conn.prepareStatement(query);
					
				stmt.setDate(3, eA.getDataDiNascita()); 
				stmt.setString(1, eA.getNome());
				stmt.setString(2, eA.getCognome());
				stmt.setString(4, eA.getComuneDiResidenza());
				stmt.setString(5, eA.getUsername());
				stmt.setString(6, eA.getPassword());
				stmt.setString(7, eA.getNumeroDiCellulare());
				stmt.setString(8, eA.getEmail());
				stmt.setString(9, eA.getCodiceFiscale());
					
				stmt.executeUpdate();
				
			} 
		
			catch(SQLException e) {
				throw new DAOException("Errore creazione alunno");
			} 
		
			finally {
				DBManager.closeConnection();
			}

		} catch(SQLException e) {
			throw new DBConnectionException("Errore connessione database");
		}
	}
	
	public static void deleteAlunno(String matricola) throws DAOException, DBConnectionException {
		
		try {
			
			Connection conn = DBManager.getConnection();
			String query = "DELETE FROM GestioneIstitutoScolastico.Alunni WHERE matricola = ?;";
			
			try {
				
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setString(1, matricola);
				stmt.executeUpdate();
				
			}
			
			catch(SQLException e) {
				throw new DAOException("Errore alunno deleteAlunno");
			}
			
			finally {
				DBManager.closeConnection();
			}
		}
		
		catch(SQLException e) {
			throw new DBConnectionException("Errore connessione database");
		}
	}
	
	public static boolean update() {return true;}

}
