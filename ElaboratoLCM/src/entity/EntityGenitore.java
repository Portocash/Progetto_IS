package entity;

import java.sql.Date;
import java.util.ArrayList;

public class EntityGenitore extends Utente {
	
	private ArrayList<EntityAlunno> figli;

	public EntityGenitore(String nome, String cognome, Date dataDiNascita, String comuneDiResidenza, String username, String password, String numeroDiCellulare, String email, String codiceFiscale) {
		super(nome, cognome, dataDiNascita, comuneDiResidenza, username, password, numeroDiCellulare, email, codiceFiscale);
		this.figli = new ArrayList<EntityAlunno>();
	}
	
	public EntityGenitore() {super();}
	
	public boolean associaFiglio(EntityAlunno eA) {
		return figli.add(eA);
	}
	
	public boolean rimuoviFiglio(EntityAlunno eA)  {
		return figli.remove(eA);
	}
	
	public ArrayList<EntityAlunno> getFigli() {return figli;}
}