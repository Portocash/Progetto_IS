package entity;

public class EntityClasse {
	
	private String sezione;
	private int anno;
	private EntityFrequenza frequenza;
	
	public EntityClasse(String sezione, int anno) {this.sezione = sezione; this.anno = anno;}
	public EntityClasse(){}
	
	public String getSezione() {
		return sezione;
	}
	
	public void setSezione(String sezione) {
		this.sezione = sezione;
	}
	public int getAnno() {
		return anno;
	}
	public void setAnno(int anno) {
		this.anno = anno;
	}
	
	public void associaFrequenza(EntityFrequenza eA) {
		this.frequenza = eA;
	}
	
	public void removeFrequenza(EntityFrequenza eA) {
		this.frequenza = null;
	}
	
	public EntityFrequenza getFrequenza() {
		return frequenza;
	}
	
	

}
