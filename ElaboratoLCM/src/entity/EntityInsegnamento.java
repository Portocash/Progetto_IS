package entity;

public class EntityInsegnamento {
	private String materia;
	private String  annoScolastico;
	private EntityDocente docente;
	private EntityClasse classe;
	
	

	public EntityInsegnamento(String materia, String annoScolastico, EntityDocente d, EntityClasse c) {
		super();
		this.materia = materia;
		this.annoScolastico=annoScolastico;
		this.docente=d;
		this.classe=c;
	}
	
	public EntityDocente getDocente() {
		return docente;
	}
	
	public EntityClasse getClasse() {
		return classe;
	}
	
	public String getMateria() {
		return materia;
	}
	public void setMateria(String materia) {
		this.materia = materia;
	}

	public String getAnnoScolastico() {
		return annoScolastico;
	}
	public void setAnnoScolastico(String annoScolastico){
		this.annoScolastico= annoScolastico;
	}	
}
