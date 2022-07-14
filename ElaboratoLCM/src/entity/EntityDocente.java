package entity;

import java.sql.Date;

public class EntityDocente extends Utente {
	
	private int matricolaDocente;
	
	public EntityDocente(String nome, String cognome, Date dataDiNascita, String comuneDiResidenza, String username, String password, String numeroDiCellulare, String email, String codiceFiscale, int matricolaDocente) {
		super(nome, cognome, dataDiNascita, comuneDiResidenza, username, password, numeroDiCellulare, email, codiceFiscale);
		this.matricolaDocente = matricolaDocente;
	}
	
	public EntityDocente() {super();}

	public int getMatricolaDocente() {
		return matricolaDocente;
	}

	public void setMatricolaDocente(int matricolaDocente) {
		this.matricolaDocente = matricolaDocente;
	}

}
