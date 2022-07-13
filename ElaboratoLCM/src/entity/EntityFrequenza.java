package entity;

public class EntityFrequenza {

	private final EntityAlunno alunno;
	private final EntityClasse classe;
	private String annoScolastico;
	
	public EntityFrequenza(String anno, EntityAlunno a, EntityClasse c) {
		
		this.annoScolastico=anno;
		this.alunno=a;
		this.classe=c;
	}
	
	public EntityAlunno associaAlunno() {	
		return alunno;
	}
	
	public EntityClasse associaClasse() {
			return classe;
	}

	public String getAnnoScolastico() {
		
		return annoScolastico;
	}
	
	public void setAnnoScolastico(String anno) {
		
		this.annoScolastico=anno;
		
	}
}
