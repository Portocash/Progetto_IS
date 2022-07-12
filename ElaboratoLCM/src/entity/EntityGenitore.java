package entity;
import java.util.ArrayList;
import java.util.Date;
public class EntityGenitore extends Utente {

	private ArrayList<EntityParentela> figlio;
	
	public EntityGenitore(String nome, String cognome, Date dataDiNascita, String codFisc, String comuneDiResidenza, String email,
			String numeroCel, String username, String password) {
		super(nome, cognome, dataDiNascita, codFisc, comuneDiResidenza, email, numeroCel, username, password);
	}
	
	public EntityGenitore() {super();}
	
	public void associaFiglio(EntityParentela ep ) {
		
		figlio.add(ep);
		
	}
	
}
