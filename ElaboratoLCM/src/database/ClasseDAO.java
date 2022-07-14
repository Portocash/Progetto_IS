package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.EntityClasse;
import entity.EntityDocente;
import entity.EntityInsegnamento;
import exception.DAOException;
import exception.DBConnectionException;

	public class ClasseDAO {
		
		public static EntityClasse readClasse(String annoScolastico, int matricola) throws DAOException, DBConnectionException {
			EntityClasse eC = null;
			
			try {
				
				Connection conn = DBManager.getConnection();
				String query = "SELECT sezioneClasse, annoClasse FROM GestioneIstitutoScolastico.alunni_classi WHERE matricolaAlunno = ? AND annoScolasticoFrequenza = ?;";
				
				try {
					
					PreparedStatement stmt = conn.prepareStatement(query);  //throws SQLException
					stmt.setInt(1, matricola);  //throws SQLException
					stmt.setString(2, annoScolastico);
					
					ResultSet res = stmt.executeQuery();  //throws SQLException 
					
					if(res.next()) {
						 eC = new EntityClasse(res.getString(1), res.getInt(2));
					}
				}
				catch(SQLException e ) {
					throw new DAOException("Errore alunno readClasse");
				}
				finally {
					DBManager.closeConnection();
				}
				
			} catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione al database");
			}
			
			return eC;
		}
		
		public static int readIdRegistro(String sezione_classe, int anno_classe) throws DAOException, DBConnectionException  {
			
			int idRegistro=0;

			try {

				Connection conn = DBManager.getConnection();
				String query = "SELECT id_registroElettronico FROM Classi WHERE sezione =? AND anno=?";
				try {
					
					PreparedStatement stmt = conn.prepareStatement(query);
							
					stmt.setString(1,sezione_classe);
					stmt.setInt(2,anno_classe);
				

					ResultSet result = stmt.executeQuery();

					if(result.next()) {
					idRegistro = result.getInt(1);	
					}

				}catch(SQLException e) {
					throw new DAOException("Errore lettura insegnamento");
				}finally {
					DBManager.closeConnection();
				}
				
			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}

			return idRegistro;
		}

		}
		

