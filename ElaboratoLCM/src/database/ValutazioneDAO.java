package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entity.EntityAlunno;
import entity.EntityValutazione;
import exception.DAOException;
import exception.DBConnectionException;

public class ValutazioneDAO {

	public static void createValutazione(EntityValutazione eV, int idRegistro) throws DAOException, DBConnectionException {
			
		try {
				
				Connection conn = DBManager.getConnection();
				String query = "INSERT INTO GestioneIstitutoScolasticoController.Valutazioni(data,materia, matricolaAlunno, id_registroElettronico,voto) VALUES (?,?,?,?,?);";

				try {
					PreparedStatement stmt = conn.prepareStatement(query);
					
				//	stmt.setInt(1,eV.getId());
					stmt.setDate(1,eV.getData());
					stmt.setString(2, eV.getMateria());
					stmt.setInt(3, eV.getAlunno().getMatricola());
					stmt.setInt(4, idRegistro);
					stmt.setInt(5,eV.getVoto());

					stmt.executeUpdate();

				}catch(SQLException e) {
					throw new DAOException("Errore scrittura valutazione");
				} finally {
					DBManager.closeConnection();
				}

			}catch(SQLException e) {
				throw new DBConnectionException("Errore connessione database");
			}

		}

		public static EntityValutazione readValutazione(int id_voto, int matricola_alunno) throws DAOException, DBConnectionException {

			EntityValutazione eV = null;
			EntityAlunno ea = new EntityAlunno("","", null, "", "", "", "", "", "", matricola_alunno);


			try {

				Connection conn = DBManager.getConnection();
				String query = "SELECT * FROM GestioneIstitutoScolasticoController.Valutazioni WHERE ID_VOTO = ? ;";
				try {
					

					PreparedStatement stmt = conn.prepareStatement(query);
					
					
					stmt.setInt(1,id_voto);

					ResultSet result = stmt.executeQuery();	
					if(result.next()) {
						eV = new EntityValutazione(result.getInt(1),result.getDate(2),result.getString(3),result.getInt(5), ea);	
					}

				}catch(SQLException e) {
					throw new DAOException("Errore lettura valutazione");
				}finally {
					DBManager.closeConnection();
				}
				
			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}

			return eV;
		}

		public static void updateValutazione(EntityValutazione eV) throws DAOException, DBConnectionException {
			

			try {

				Connection conn = DBManager.getConnection();
				String query = "UPDATE GestioneIstitutoScolasticoController.Valutazioni SET DATA=?, MATERIA=?, MATRICOLAALUNNO=?, VOTO=? WHERE ID_VOTO=? ;";
				
				try {
					

					PreparedStatement stmt = conn.prepareStatement(query);
					
					stmt.setDate(1, eV.getData());
					stmt.setString(2, eV.getMateria());
					stmt.setInt(3, eV.getAlunno().getMatricola());
					stmt.setInt(4,eV.getVoto());
					stmt.setInt(5,eV.getId());
					stmt.executeUpdate();

				}catch(SQLException e) {
					throw new DAOException("Errore update valutazione");
				}finally {
					DBManager.closeConnection();
				}
				
			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}

		}


		public static void deleteValutazione(int id_voto) throws DAOException, DBConnectionException {
			
			
			try {

				Connection conn = DBManager.getConnection();
				String query = "DELETE FROM GestioneIstitutoScolasticoController.Valutazioni WHERE ID_VOTO = ?; ";

				try {
					PreparedStatement stmt = conn.prepareStatement(query);
					stmt.setInt(1,id_voto); 
					stmt.executeUpdate();

				}catch(SQLException e) {
					throw new DAOException("Errore cancellazione valutazione");
				} finally {
					DBManager.closeConnection();
				}

			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}
		}
		
		public static float readValutazione(int matricola, String materia, String annoScolastico) throws DAOException, DBConnectionException {
			
			float voto = -1f;
			
			try {

				Connection conn = DBManager.getConnection();
				String query = "SELECT AVG(voto) FROM GestioneIstitutoScolasticoController.Valutazioni WHERE matricolaAlunno = ? AND materia = ? AND data BETWEEN ?-01-01 AND ?-12-31 GROUP BY materia;";
				
				try {
					
					PreparedStatement stmt = conn.prepareStatement(query);
					
					stmt.setInt(1, matricola);
					stmt.setString(2, materia);
					stmt.setString(3, annoScolastico);
					stmt.setString(4, annoScolastico);

					ResultSet result = stmt.executeQuery();
					
					if(result.next()) 
						voto = result.getFloat(1);

				}catch(SQLException e) {
					throw new DAOException("Errore lettura valutazione");
				}finally {
					DBManager.closeConnection();
				}
				
			}catch(SQLException e) {
				throw new DBConnectionException("Errore di connessione DB");
			}

			return voto;
		}
	}
