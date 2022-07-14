package entity;

import java.sql.Date;
import java.util.ArrayList;


public class EntityAlunno extends EntityUtente {
	
	private int matricola;
	
	private ArrayList<EntityFrequenza> frequenza;
	private ArrayList<EntityValutazione> voto;
	public ArrayList<EntityGenitore> genitori;
	

	public EntityAlunno(String nome, String cognome, Date dataDiNascita, String comuneDiResidenza, String username, String password, String numeroDiCellulare, String email, String codiceFiscale, int matricola) {
		super(nome, cognome, dataDiNascita, comuneDiResidenza, username, password, numeroDiCellulare, email, codiceFiscale);
		this.matricola = matricola; this.voto = new ArrayList<EntityValutazione>(); this.frequenza = new ArrayList<EntityFrequenza>(); this.genitori = new ArrayList<EntityGenitore>();
	}
	
	public EntityAlunno() {super();}
	
	public boolean associaVoto(EntityValutazione eV ) {return voto.add(eV);}
		 
	public boolean rimuoviVoto(EntityValutazione eV) {return voto.remove(eV);}
	
	public ArrayList<EntityValutazione> getVoti() {return voto;}
	
	public boolean associaFrequenza(EntityFrequenza eF) {
		return frequenza.add(eF);
	}
	
	public boolean rimuoviFrequenza(EntityFrequenza eF) {
		return frequenza.remove(eF);
	}
	
	public ArrayList<EntityFrequenza> getFrequenze() {return frequenze;}
	
	
	public ArrayList<EntityGenitore> getGenitori() {
		return genitori;
	}
	
	public boolean associaGenitore(EntityGenitore eG) {
		return genitori.add(eG);
	}
	
	public boolean removeGenitore(EntityGenitore eG) {
		return genitori.remove(eG);
	}
	
	public int getMatricola() {
		return this.matricola;
	}
	
	public void setMatricola(int matricola) {
		this.matricola = matricola;
	}

}
Footer
© 2022 GitHub, Inc.
Footer navigation
Terms
Privacy
Security
Status
Docs
Contact GitHub
Pricing
API
Training
Blog
About
