package database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import control.GestioneIstitutoScolasticoController;

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
				String query = "INSERTO INTO GestioneIstitutoScolastico.Insegnamenti VALUES (?,?,?,?,?);";
				
				try {
				

					PreparedStatement stmt = conn.prepareStatement(query);
					
					stmt.setString(1,eI.getMateria());
					stmt.setInt(2,eI.getDocente().get_matricolaDocente());
					stmt.setInt(3, eI.getClasse().getSezione());
					stmt.setInt(4,eI.getClasse().getAnno());
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
				String query = "SELECT * FROM GestioneIstitutoScolastico.Insegnamenti WHERE MATRICOLA_DOCENTE = ? AND CLASSE_SEZIONE=? AND CLASSE_ANNO=? AND ANNOSCOLASTICO=? ;";
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
				String query = "DELETE FROM GestioneIstitutoScolastico.Insegnamenti WHERE MATERIA=?, MATRICOLA_DOCENTE = ? AND CLASSE_SEZIONE=? AND CLASSE_ANNO=? AND ANNOSCOLASTICO=? ; ";

				try {
					PreparedStatement stmt = conn.prepareStatement(query);
					stmt.setString(1,eI.getMateria());
					stmt.setInt(2,eI.getDocente().get_matricolaDocente());
					stmt.setInt(3, eI.getClasse().getSezione());
					stmt.setInt(4,eI.getClasse().getAnno());
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



		public static void getInsegnamento (int matricola_docente, String annoScolasticoCorrente, char sezione_classe, int anno_classe, ArrayList<EntityInsegnamento> ListaeI )throws DAOException, DBConnectionException {
			
	
			EntityInsegnamento temp=null;
			EntityDocente ed = new EntityDocente("","", null, "", "", "", "", "", "", matricola_docente);
			EntityClasse eC= new EntityClasse(sezione_classe, anno_classe);

			try {
				Connection conn = DBManager.getConnection();
				String query = "SELECT * FROM GestioneIstitutoScolasticoController.Insegnamenti  WHERE MATRICOLA_DOCENTE=? AND CLASSE_SEZIONE=? AND CLASSE_ANNO=? AND ANNOSCOLASTICO=?;";
				
				try {
					PreparedStatement stmt = conn.prepareStatement(query);

					stmt.setInt(1,matricola_docente);
					stmt.setInt(2,sezione_classe);
					stmt.setInt(3,anno_classe);
					stmt.setString(4,annoScolasticoCorrente);

					ResultSet result = stmt.executeQuery();
					
					do {
					
					temp= new EntityInsegnamento(result.getString(1),result.getString(5),ed,eC);
					ListaeI.add(temp);
						
					}
					while(result.next());
				}catch(SQLException e) {
					throw new DAOException("Errore insegnamento getInsegnamento");
				} finally {
					DBManager.closeConnection();
				}

			}catch(SQLException e) {
				throw new DBConnectionException("Errore connessione database");
			}
		}
		
		public static ArrayList<String> getInsegnamento(String annoScolasticoCorrente, String sezione, int anno) throws DAOException, DBConnectionException {
			
			List<String> materie = new ArrayList<String>();
			
			try {
				
				Connection conn = DBManager.getConnection();
				String query = "SELECT materia FROM GestioneIstitutoScolastico.Insegnamenti WHERE classe_sezione = ? AND classe_anno = ? AND annoScolastico = ?;";
				
				try {
					PreparedStatement stmt = conn.prepareStatement(query);
					
					stmt.setString(2,sezione);
					stmt.setInt(3,anno);
					stmt.setString(4,annoScolasticoCorrente);

					ResultSet result = stmt.executeQuery();
					
					while(result.next()) {
						materie.add(result.getString(1));
					}
					
				}catch(SQLException e) {
					throw new DAOException("Errore insegnamento getInsegnamento");
				} finally {
					DBManager.closeConnection();
				}
				
				return (ArrayList<String>)materie;

			}catch(SQLException e) {
				throw new DBConnectionException("Errore connessione database");
			}

		}

	}	
