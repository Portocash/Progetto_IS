package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.EntityFrequenza;
import entity.EntityInsegnamento;
import entity.EntityAlunno;
import entity.EntityClasse;
import entity.EntityDocente;
import exception.DAOException;
import exception.DBConnectionException;
public class FrequenzaDAO {

	

	public static void createFrequenza(EntityFrequenza eF) throws DAOException, DBConnectionException {
			

		try {

			Connection conn = DBManager.getConnection();
			String query = "INSERTO INTO Insegnamenti VALUES (?,?,?,?);";
			
			try {

				PreparedStatement stmt = conn.prepareStatement(query);
				
				stmt.setString(1,eF.getAnnoScolastico());
				stmt.setInt(2,eF.getAlunno().getMatricola());
				stmt.setInt(3, eF.getClasse().getSezione());
				stmt.setInt(4,eF.getClasse().getAnno());
		
			
				stmt.executeUpdate();

			}catch(SQLException e) {
				throw new DAOException("Errore create frequenza");
			}finally {
				DBManager.closeConnection();
			}
			
		}catch(SQLException e) {
			throw new DBConnectionException("Errore di connessione DB");
		}
	}
	
	public static EntityFrequenza readFrequenza(String annoScolastico, int matricola_alunno, char classe_sezione, int classe_anno)throws DAOException, DBConnectionException {
		
		EntityFrequenza eF = null;
		EntityAlunno ea = new EntityAlunno("","", null, "", "", "", "", "", "", matricola_alunno);
		EntityClasse eC= new EntityClasse(classe_sezione, classe_anno);

		try {

			Connection conn = DBManager.getConnection();
			String query = "SELECT * FROM Frquenze WHERE ANNOSCOLASTICO=?, MATRICOLA_ALUNNO=?, SEZIONE_CLASSE=?, ANNO_CLASSE=? ;";
			
			try {
				
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setString(1, annoScolastico);
				stmt.setInt(2,matricola_alunno);
				stmt.setInt(3,classe_sezione);
				stmt.setInt(4,classe_anno);

				ResultSet result = stmt.executeQuery();

				if(result.next()) {
					eF = new EntityFrequenza(result.getString(1), ea,eC);	
				}

			}catch(SQLException e) {
				throw new DAOException("Errore lettura frequenza");
			}finally {
				DBManager.closeConnection();
			}
			
		}catch(SQLException e) {
			throw new DBConnectionException("Errore di connessione DB");
		}

		return eF;
	}

	public static void updateFrequenza(EntityFrequenza eF) throws DAOException, DBConnectionException {
		

		try {

			Connection conn = DBManager.getConnection();
			String query = "UPDATE Frequenze SET ANNOSCOLASTICO=? WHERE  MATRICOLA_ALUNNO=? AND SEZIONE_CLASSE=? AND ANNO_CLASSE=?;";


			try {
			
				PreparedStatement stmt = conn.prepareStatement(query);
				

				stmt.setString(1,eF.getAnnoScolastico());
				stmt.setInt(2,eF.getAlunno().getMatricola());
				stmt.setInt(3, eF.getClasse().getSezione());
				stmt.setInt(4,eF.getClasse().getAnno());
		
			
				
				stmt.executeUpdate();

			}catch(SQLException e) {
				throw new DAOException("Errore update frequenza");
			}finally {
				DBManager.closeConnection();
			}
			
		}catch(SQLException e) {
			throw new DBConnectionException("Errore di connessione DB");
		}

	}	
	
	public static void deleteFrequenza(EntityFrequenza eF) throws DAOException, DBConnectionException {
		
		
		try {

			Connection conn = DBManager.getConnection();
			String query = "DELETE FROM Frequenze WHERE  ANNOSCOLASTICO=? AND MATRICOLA_ALUNNO=? AND SEZIONE_CLASSE=? AND ANNO_CLASSE=?";

			try {
				PreparedStatement stmt = conn.prepareStatement(query);
				
				stmt.setString(1, eF.getAnnoScolastico());
				stmt.setInt(1,eF.getAlunno().getMatricola());
				stmt.setInt(2, eF.getClasse().getSezione());
				stmt.setInt(3,eF.getClasse().getAnno());
				stmt.executeUpdate();

			}catch(SQLException e) {
				throw new DAOException("Errore cancellazione frequenza");
			} finally {
				DBManager.closeConnection();
			}

		}catch(SQLException e) {
			throw new DBConnectionException("Errore di connessione DB");
		}
	}

	
	
	
	public static EntityFrequenza getFrequenza(int matricola, String annoScolasticoCorrente) throws DAOException, DBConnectionException {
		
		EntityFrequenza eF = null;
	
		
		try {
			Connection conn = DBManager.getConnection();
			String query = "SELECT * FROM Frequenze WHERE MATRICOLA_ALUNNO=? AND ANNOSCOLASTICO=? ;";

			try {
				PreparedStatement stmt = conn.prepareStatement(query);
			
				stmt.setInt(1, matricola);
				stmt.setString(2, annoScolasticoCorrente);
				
				ResultSet result = stmt.executeQuery();

				if(result.next()) {
					EntityAlunno ea = new EntityAlunno("","", null, "", "", "", "", "", "", result.getInt(2));
					EntityClasse eC= new EntityClasse((char)result.getInt(3), result.getInt(4));
					eF = new EntityFrequenza(result.getString(1), ea,eC);	}
			}catch(SQLException e) {
				throw new DAOException("Errore sezione_classe non trovata");
			} finally {
				DBManager.closeConnection();
			}

		}catch(SQLException e) {
			throw new DBConnectionException("Errore connessione database");
		}

		return eF;
	}
		
	/*public static int getAnnoClasse(int matricola, String annoScolasticoCorrente)  throws DAOException, DBConnectionException{
		
		int anno_classe=0;

		try {
			Connection conn = DBManager.getConnection();
			String query = "SELECT anno_classe FROM Frequenze WHERE MATRICOLA_ALUNNO=? AND ANNOSCOLASTICO=? ;";

			try {
				PreparedStatement stmt = conn.prepareStatement(query);
			
				stmt.setInt(1, matricola);
				stmt.setString(2, annoScolasticoCorrente);
				
				ResultSet result = stmt.executeQuery();

				if(result.next()) {
					 anno_classe = result.getInt(1);
				}
			}catch(SQLException e) {
				throw new DAOException("Errore sezione_classe non trovata");
			} finally {
				DBManager.closeConnection();
			}

		}catch(SQLException e) {
			throw new DBConnectionException("Errore connessione database");
		}

		return anno_classe;
	} */
	} 
	
	

