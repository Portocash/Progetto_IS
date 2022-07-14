package entity;

public class EntityClasse {
	
	private char sezione;
	private int anno;
	private EntityFrequenza frequenza;
	
	public EntityClasse(char sezione, int anno) {this.sezione = sezione; this.anno = anno;}
	public EntityClasse(){}
	
	public char getSezione() {
		return sezione;
	}
	
	public void setSezione(char sezione) {
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