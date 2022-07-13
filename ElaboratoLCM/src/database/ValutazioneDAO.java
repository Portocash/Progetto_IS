package database;

	

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.EntityAlunno;
import entity.EntityValutazione;
import exception.DAOException;
import exception.DBConnectionException;

public class ValutazioneDAO {

	public static void createValutazione(EntityValutazione eV) throws DAOException, DBConnectionException {
			
		try {
				
				Connection conn = DBManager.getConnection();
				String query = "INSERT INTO Valutazioni VALUES (?,?,?,?,?,?);";

				try {
					PreparedStatement stmt = conn.prepareStatement(query);
					
					stmt.setInt(1,eV.getId());
					stmt.setDate(2, eV.getData());
					stmt.setString(3, eV.getMateria());
					stmt.setInt(4, eV.getAlunno().getMatricola());
					stmt.setInt(5, eV.getIdRegistroElettronico());
					stmt.setInt(6,eV.getVoto());

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
				String query = "SELECT * FROM Valutazioni WHERE ID_VOTO = ? ;";
				try {
					

					PreparedStatement stmt = conn.prepareStatement(query);
					
					
					stmt.setInt(1,id_voto);

					ResultSet result = stmt.executeQuery();

					if(result.next()) {
						eV = new EntityValutazione(result.getInt(1),result.getDate(2),result.getString(3),result.getInt(5), result.getInt(6),ea);	
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
				String query = "UPDATE Valutazioni SET DATA=?, MATERIA=?, MATRICOLAALUNNO=?, ID_REGISTROELETTRONICO=?, VOTO=? WHERE ID_VOTO=? ;";

				try {
					

					PreparedStatement stmt = conn.prepareStatement(query);
					
					stmt.setDate(1, eV.getData());
					stmt.setString(2, eV.getMateria());
					stmt.setInt(3, eV.getAlunno().getMatricolaAlunno());
					stmt.setInt(4, eV.getIdRegistroElettronico());
					stmt.setInt(5,eV.getVoto());
					stmt.setInt(6,eV.getId());
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
				String query = "DELETE FROM Valutazioni WHERE ID_VOTO = ?; ";

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
	}

