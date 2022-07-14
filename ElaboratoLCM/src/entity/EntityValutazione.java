package entity;

import java.sql.Date;

	public class EntityValutazione{
		private int id_voto;
		private Date data;
		private String materia;
		private int id_RegistroElettronico;
		private int voto;	
		private EntityAlunno studente;
		

		public EntityValutazione(int id, Date data, String materia, int idRegistroElettronico, int voto, EntityAlunno ea) {
			super();
			this.id_voto= id;
			this.data= data;
			this.materia=materia;
			this.id_RegistroElettronico=idRegistroElettronico;
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
	
		public int getIdRegistroElettronico() {
			return id_RegistroElettronico;
		}
		public void setIdRegistroElettronico(int idRegistro){
			this.id_RegistroElettronico=idRegistro;
		}
		public int getVoto() {
			return voto;
		}
		public void setVoto(int voto){
			this.voto=voto;
		}
	}

	