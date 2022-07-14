package entity;

import java.sql.Date;
import java.util.ArrayList;

public class EntityDocente extends EntityUtente {
	
	private int matricola_docente;
	private ArrayList<EntityInsegnamento> insegnamento;
	
	public EntityDocente(String nome, String cognome, Date dataDiNascita, String codiceFiscale, String comuneDiResidenza,
			String email, String numeroDiCellulare, String username, String password, int matricola_docente) {
		super(nome, cognome, dataDiNascita, codiceFiscale, comuneDiResidenza, email, numeroDiCellulare, username, password);
		
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
