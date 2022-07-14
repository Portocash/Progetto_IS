package boundary;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import control.GestioneIstitutoScolasticoController;
import entity.EntityAlunno;
import entity.EntityDocente;
import entity.EntityGenitore;
import exception.InputException;
import exception.OperationException;

public class BuondarySegreteria {

	
	public void registraUtente(String Nome, String Cognome, Date dataDiNascita, 
			String codiceFiscale, String comuneDiResidenza, String Email, String Cellulare,
			String Ruolo) throws OperationException,
	  InputException{ 
		
		boolean nomeCheck = false;
		boolean cognomeCheck = false;
		boolean CodiceCheck = false;
		boolean cDRCheck = false;
		boolean EmailCheck = false;
		boolean numeroCheck = false;
		boolean Ruolocheck = false;
		
	
		if(Nome.length()<=15) nomeCheck = true;
		else nomeCheck = false;
		
		if(!nomeCheck) throw new InputException("\n Il nome è una stringa di caratteri di lunghezza > 15");
		
		if(Cognome.length()<=15) cognomeCheck = true;
		else cognomeCheck = false;
		
		if(!cognomeCheck) throw new InputException("\n Il cognome è una stringa di caratteri di lunghezza > 15");
		
		if(codiceFiscale.length()==16) CodiceCheck = true;
		else CodiceCheck = false;
		
		if(!CodiceCheck) throw new InputException("\n Il codice fiscale è una stringa di caratteri di lunghezza > 16");
		
		if(comuneDiResidenza.length()<=30) cDRCheck = true;
		else cDRCheck = false;
		
		if(!cDRCheck) throw new InputException("\n Il codice fiscale è una stringa di caratteri di lunghezza > 16");
		
		int i=0;
		while(EmailCheck && i<Email.length()) {
			
			if(Email.charAt(i)!='@') i++;
			else EmailCheck=true;
		}
		
		if(!EmailCheck) throw new InputException("\n L'email non presenta il carattere '@' ");
		
		if(Cellulare.length()==10) numeroCheck = true;
		else numeroCheck = false;
		
		if(!numeroCheck) throw new InputException("\n Il numero di cellulare è una stringa di caratteri di lunghezza > 10");
		
		if(Ruolo.compareTo("Utente")==0) throw new InputException("\n Ruolo non inserito e utente per default");
		
		
		
		//controllo la data come la fa lui.
	/*	DateFormat df= new SimpleDateFormat("yyyy-mm-dd");
		df.setLenient(false);
		try {
		 df.parse(dataVoto);
		 dataCheck=true;
		}
		catch(ParseException e) {
			System.out.println("Errore parsing ..");
		}
		//try{
			 data_Voto = LocalDate.parse(dataVoto);
	//	}
		//catch(ParseException e) {
			//System.out.println("Errore parsing ..");
		//}
		*/
		
		EntityDocente docente = null;
		EntityAlunno alunno =null;
		EntityGenitore genitore = null;
		
		GestioneIstitutoScolasticoController GIS = GestioneIstitutoScolasticoController.getInstance();
		
		try {
			GIS.registraUtente(Nome,Cognome, dataDiNascita, 
					codiceFiscale, comuneDiResidenza, Email, Cellulare,
					Ruolo, docente,alunno, genitore);
		}
		catch(OperationException ex) {	
			 System.out.println(ex.getMessage());
		}
	}	
}
