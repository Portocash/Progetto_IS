package entity;

public class EntityParentela {

	private String genitore_cf;
	private int matricola;
	
	public EntityParentela( String cf, int matricola) {
		
		this.genitore_cf=cf;
		this.matricola=matricola;
		
	}
	public String get_Genitorecf() {
		return this.genitore_cf;
	}
	public void set_Genitorecf( String cf) {
		
		this.genitore_cf=cf;
	}
	
	public int getMatricola() {
		
		return this.matricola;
	}
//setMatricola dubbio	
}
