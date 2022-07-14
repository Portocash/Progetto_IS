package entity;

import java.sql.Date;
import java.util.ArrayList;

public class EntityDocente extends Utente {
	
	private int matricola_docente;
	private ArrayList<EntityInsegnamento> insegnamento;
	
	public EntityDocente(String nome, String cognome, Date dataDiNascita, String comuneDiResidenza, 
			String username, String password, String numeroDiCellulare, String email, String codiceFiscale, 
			int matricola_docente)
	{
		super(nome, cognome, dataDiNascita,comuneDiResidenza,username, password, numeroDiCellulare, email, codiceFiscale);
		
		this.matricola_docente=matricola_docente;
		this.insegnamento=null;
	}
	

	public int get_matricolaDocente() {
		
		return matricola_docente;
		
	}
	
	public void associaInsegnamento(EntityInsegnamento eF ) {
		
		insegnamento.add(eF);
		
	}
	
	public void rimuoviInsegnamento(EntityInsegnamento eF) {
		
		insegnamento.remove(eF);
		
	}
}
