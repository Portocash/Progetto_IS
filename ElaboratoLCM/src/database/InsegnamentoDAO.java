package database;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.EntityInsegnamento;
import entity.EntityDocente;
import entity.EntityClasse;
import exception.DAOException;
import exception.DBConnectionException;

	public class InsegnamentoDAO {

		public static void createInsegnamento(EntityInsegnamento eI) throws DAOException, DBConnectionException {
				

			try {

				Connection conn = DBManager.getConnection();
				String query = "INSERTO INTO Insegnamenti VALUES (?,?,?,?,?);";
				
				try {
				

					PreparedStatement stmt = conn.prepareStatement(query);
					
					stmt.setString(1,eI.getMateria());
					stmt.setInt(2,eI.associaDocente().get_matricolaDocente());
					stmt.setChar(3, eI.associaClasse().getSezione());
					stmt.setInt(4,eI.associaClasse().getClasseAnno());
					stmt.setString(5, eI.getAnnoScolastico());
				
					stmt.executeUpdate();

				}catch(SQLException e) {
					throw new DAOException("Errore create insegnamento");
				}finally {
					DBManager.closeConnection();
				}
				
			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}
		}

		public static EntityInsegnamento readInsegnamento(int matricola_docente, char classe_sezione, int classe_anno, String annoScolastico)throws DAOException, DBConnectionException {
		
			EntityInsegnamento eI = null;
			EntityDocente ed = new EntityDocente("","", null, "", "", "", "", "", "", matricola_docente);
			EntityClasse eC= new EntityClasse(classe_sezione, classe_anno);

			try {

				Connection conn = DBManager.getConnection();
				String query = "SELECT * FROM Insegnamenti WHERE MATRICOLA_DOCENTE = ? AND CLASSE_SEZIONE=? AND CLASSE_ANNO=? AND ANNOSCOLASTICO=? ;";
				try {
					
					PreparedStatement stmt = conn.prepareStatement(query);
							
					stmt.setInt(1,matricola_docente);
					stmt.setInt(2,classe_sezione);
					stmt.setInt(3,classe_anno);
					stmt.setString(4,annoScolastico);

					ResultSet result = stmt.executeQuery();

					if(result.next()) {
						eI = new EntityInsegnamento(result.getString(1),result.getString(5), ed,eC);	
					}

				}catch(SQLException e) {
					throw new DAOException("Errore lettura insegnamento");
				}finally {
					DBManager.closeConnection();
				}
				
			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}

			return eI;
		}


	/*	public static void updateInsegnamento(EntityInsegnamento eI) throws DAOException, DBConnectionException {
			

			try {

				Connection conn = DBManager.getConnection();

				try {
					String query = "UPDATE Insegnamenti MATERIA=? WHERE  MATRICOLA_DOCENTE=? AND CLASSE_SEZIONE=? AND CLASSE_ANNO=? AND ANNOSCOLASTICO=? ;";


					PreparedStatement stmt = conn.prepareStatement(query);
					
					stmt.setString(1,eI.getMateria());
					stmt.setInt(2,eI.associaDocente().get_matricolaDocente());
					stmt.setChar(3, eI.associaClasse().getSezione());
					stmt.setInt(4,eI.associaClasse.getClasseAnno());
					stmt.setString(5, eI.getAnnoScolastico());
					
					stmt.executeUpdate();

				}catch(SQLException e) {
					throw new DAOException("Errore update insegnamento");
				}finally {
					DBManager.closeConnection(); sono tutti chiave primary
				}
		
			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}

		}*/

		public static void deleteInsegnamento(EntityInsegnamento eI) throws DAOException, DBConnectionException {
			
			
			try {

				Connection conn = DBManager.getConnection();
				String query = "DELETE FROM Insegnamenti WHERE MATERIA=?, MATRICOLA_DOCENTE = ? AND CLASSE_SEZIONE=? AND CLASSE_ANNO=? AND ANNOSCOLASTICO=? ; ";

				try {
					PreparedStatement stmt = conn.prepareStatement(query);
					stmt.setString(1,eI.getMateria());
					stmt.setInt(2,eI.associaDocente().get_matricolaDocente());
					stmt.setChar(3, eI.associaClasse().getSezione());
					stmt.setInt(4,eI.associaClasse.getClasseAnno());
					stmt.setString(5,eI.getAnnoScolastico());
					stmt.executeUpdate();

				}catch(SQLException e) {
					throw new DAOException("Errore cancellazione insegnamento");
				} finally {
					DBManager.closeConnection();
				}

			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}
		}



		public static ArrayList<EntityInsegnamento> getInsegnamento (int matricola_docente, String annoScolasticoCorrente, char sezione_classe, int anno_classe)throws DAOException, DBConnectionException {
			
			ArrayList<EntityInsegnamento> eI = null; //vedi
			try {
				Connection conn = DBManager.getConnection();
				String query = "SELECT * FROM Insegnamenti  WHERE MATRICOLA_DOCENTE=? AND CLASSE_SEZIONE=? AND CLASSE_ANNO=? AND ANNOSCOLASTICO=?;";
				
				try {
					PreparedStatement stmt = conn.prepareStatement(query);

					stmt.setInt(1,matricola_docente);
					stmt.setInt(2,sezione_classe);
					stmt.setInt(3,anno_classe);
					stmt.setString(4,annoScolasticoCorrente);

					ResultSet result = stmt.executeQuery();

				//vedi come fare la lista 
				if(result.next()) {
						eI= new List<EntityInsegnamento>(result.getString(1), result.getInt(2), result.getChar(3), result.getInt(4), result.getString(5));
					}
				}catch(SQLException e) {
					throw new DAOException("Errore insegnamento getInsegnamento");
				} finally {
					DBManager.closeConnection();
				}

			}catch(SQLException e) {
				throw new DBConnectionException("Errore connessione database");
			}

			return eI;
		}

	}	

