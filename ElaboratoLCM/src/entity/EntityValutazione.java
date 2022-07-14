package entity;


import java.sql.Date;
import database.ValutazioneDAO;
import exception.DBConnectionException;


	public class EntityValutazione{
		private int id_voto;
		private Date data;
		private String materia;
		private int voto;	
		private EntityAlunno studente;
		

		public EntityValutazione(int id, Date data, String materia, int voto, EntityAlunno ea) {
			super();
			this.id_voto= id;
			this.data= data;
			this.materia=materia;
			this.voto=voto;
			this.studente=ea;
		}
		
		public EntityAlunno getAlunno() {
			return studente;
		}
		
		public int getId() {
			return id_voto;
		}
		public void setId(int id){
			this.id_voto=id;
		} 
		public Date getData() {
			return data;
		}
		public void setData(Date data){
			this.data=data;
		}
		public String getMateria() {
			return materia;
		}
		public void setMateria(String materia){
			this.materia=materia;
		}
		
		public int getVoto() {
			return voto;
		}
		public void setVoto(int voto){
			this.voto=voto;
		}
	}

	
