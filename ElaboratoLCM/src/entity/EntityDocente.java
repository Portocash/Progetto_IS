package entity;

import java.util.Date;

public class EntityDocente extends Utente {
	
	private int matricola_docente;
	
	public EntityDocente(String nome, String cognome, Date dataDiNascita, String codFisc, String comuneDiResidenza, String email,
			String numeroCel, String username, String password, int matricola_docente) {
		super(nome, cognome, dataDiNascita, codFisc, comuneDiResidenza, email, numeroCel, username, password,matricola_docente);
	}
	
	public EntityDocente() {super();}

	public int get_matricolaDocente() {
		
		return matricola_docente;
		
	}
}
