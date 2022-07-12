package entity;

import java.util.ArrayList;
import java.util.Date;
//SERVE FORNIRE IL SET PER LA MATRICOLA?

public class EntityAlunno extends Utente {
	
	private int matricola;
	private ArrayList<EntityParentela> genitore;
	private ArrayList<EntityFrequenza> classe;
	public EntityAlunno(String nome, String cognome, Date dataDiNascita, String codFisc, String comuneDiResidenza, String email,
			String numeroCel, String username, String password, int matricola) {
		super(nome, cognome,  dataDiNascita, codFisc, comuneDiResidenza, email, numeroCel, username, password);
		this.matricola = matricola;
	}
	
	public EntityAlunno() {super();}
	
	public int getMatricola() {
		return this.matricola;
	}
	
	public void setMatricola(int matricola) {
		this.matricola = matricola;
	}

	public void associaParentela(EntityParentela ep) {
		
		genitore.add(ep);
		
	}
	
	public void associaFrequenza(EntityFrequenza ef) {
		
		classe.add(ef);
		
	}
}
