package entity;

public class EntityRegistroElettronico {

	private int idRegistro;
	private EntityValutazione voto;
	
	public EntityRegistroElettronico(int id, EntityValutazione eV) {
		
		this.idRegistro=id;
		this.voto=eV;
		
	}
	
	public int getIdRegistro() { return idRegistro; }
	public void setIdRegistro(int id) {
		this.idRegistro=id;
	}
	
	
}
