package database;

//import dalla libreria sql per gestione delle operazioni CRUD

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

//gestione delle date (formato sql yyyy-mm-dd, formato string codice dd-mm-yyyy)

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter; 

import entity.EntityAlunno;
import exception.DAOException;
import exception.DBConnectionException;

public class AlunnoDAO {
	
	public static EntityAlunno readAlunno(String matricola) throws DAOException, DBConnectionException {
		EntityAlunno eA = null;
		
		try {
			Connection conn = DBManager.getConnection();
			String query = "SELECT * FROM ALUNNI WHERE MATRICOLA = ?;";
			
			try {
				
				PreparedStatement stmt = conn.prepareStatement(query);  //throws SQLException
				stmt.setString(1, matricola);  //throws SQLException
				
				ResultSet res = stmt.executeQuery();  //throws SQLException 
				eA = new EntityAlunno(res.getString(1), res.getString(2), res.getString(9), res.getString(4), res.getString(8), res.getString(7), res.getString(5), res.getString(6), res.getInt(10), res.getDate(3).toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
	
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
		
			String query = "INSERT INTO ALUNNI VALUES (?,?,?,?,?,?,?,?,?);";
		
			try {
			
				PreparedStatement stmt = conn.prepareStatement(query);
				
				try {
					
					String dataDiNascita = eA.getDataDiNascita();
					SimpleDateFormat sdfSrc = new java.text.SimpleDateFormat("dd-MM-yyyy");
					SimpleDateFormat sdfDst = new java.text.SimpleDateFormat("yyyy-MM-dd");
					dataDiNascita = sdfDst.format(sdfSrc.parse(dataDiNascita));
					
					stmt.setDate(3, (java.sql.Date)sdfDst.parse(dataDiNascita));
					stmt.setString(1, eA.getNome());
					stmt.setString(2, eA.getCognome());
					stmt.setString(4, eA.getComuneDiResidenza());
					stmt.setString(5, eA.getUsername());
					stmt.setString(6, eA.getPassword());
					stmt.setString(7, eA.getNumeroDiCellulare());
					stmt.setString(8, eA.getEmail());
					stmt.setString(9, eA.getCodiceFiscale());
					
				} catch (ParseException e) {
					e.printStackTrace();
					System.out.print("Creazione alunno fallita: causa formato data di nascita incorretto");
				}
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

}
