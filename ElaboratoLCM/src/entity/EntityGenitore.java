package entity;

import java.sql.Date;

public class EntityGenitore extends Utente {

	public EntityGenitore(String nome, String cognome, String codFisc, String comuneDiResidenza, String email,
			String numeroCel, String username, String password, Date dataDiNascita) {
		super(nome, cognome, codFisc, comuneDiResidenza, email, numeroCel, username, password, dataDiNascita);
	}
	
	public EntityGenitore() {super();}
	
}
