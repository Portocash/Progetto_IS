package entity;

public class EntityFrequenza {

	private EntityAlunno alunno;
	private EntityClasse classe;
	private String annoScolastico;
	
	public EntityFrequenza(String anno) {
		
		this.annoScolastico=anno;
	}
	
	public void associaAlunno(EntityAlunno ea) {
		
		this.alunno=ea;
		
	}
	
	public void associaClasse(EntityClasse ec) {
		
		this.classe=ec;
		
	}
	
	public String getAnnoScolastico() {
		
		return annoScolastico;
	}
	
	public void setAnnoScolastico(String anno) {
		
		this.annoScolastico=anno;
		
	}
}
