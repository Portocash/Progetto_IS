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
		
		int j=0;
		while(nomeCheck && j<Nome.length()) {
			
			if(Nome.charAt(j)<='Z' && Nome.charAt(j)>='A' || Nome.charAt(j)<='z'&& Nome.charAt(j)>='a') nomeCheck=true;
			else nomeCheck=false;
			j++;
		}
		
		if(!nomeCheck) throw new InputException("\n Il nome è una stringa di caratteri di lunghezza > 15 o con caratteri speciali");
		
		if(Cognome.length()<=15) cognomeCheck = true;
		else cognomeCheck = false;
		
		j=0;
		while(cognomeCheck && j<Cognome.length()) {
			
			if(Cognome.charAt(j)<='Z' && Cognome.charAt(j)>='A' || Cognome.charAt(j)<='z'&& Cognome.charAt(j)>='a') cognomeCheck=true;
			else cognomeCheck=false;
			j++;
		}
		//nome, cognome, comunediresidenza cofisicale e
		if(!cognomeCheck) throw new InputException("\n Il cognome è una stringa di caratteri di lunghezza > 15 o con caratteri speciali");
		
		if(codiceFiscale.length()==16) CodiceCheck = true;
		else CodiceCheck = false;
		j=0;
		while(CodiceCheck && j<codiceFiscale.length()) {
			
			if(comuneDiResidenza.charAt(j)<='Z' && comuneDiResidenza.charAt(j)>='A' || comuneDiResidenza.charAt(j)<='9'&& comuneDiResidenza.charAt(j)>='0') CodiceCheck=true;
			else CodiceCheck=false;
			j++;
		}
		
		
		if(!CodiceCheck) throw new InputException("\n Il codice fiscale è una stringa di caratteri di lunghezza > 16");
		
		if(comuneDiResidenza.length()<=30) cDRCheck = true;
		else cDRCheck = false;
		j=0;
		while(cDRCheck && j<comuneDiResidenza.length()) {
			
			if(comuneDiResidenza.charAt(j)<='Z' && comuneDiResidenza.charAt(j)>='A' || comuneDiResidenza.charAt(j)<='z'&& comuneDiResidenza.charAt(j)>='a') cDRCheck=true;
			else cDRCheck=false;
			j++;
		}
		
		if(!cDRCheck) throw new InputException("\n Il comune di residenza è una stringa di caratteri di lunghezza > 30 o con caratteri speciali");
		
		int i=0;
		while(EmailCheck && i<Email.length()) {
			
			if(Email.charAt(i)!='@') i++;
			else EmailCheck=true;
		}
		
		if(!EmailCheck) throw new InputException("\n L'email non presenta il carattere '@' ");
		
		if(Cellulare.length()==10) numeroCheck = true;
		else numeroCheck = false;
		
		j=0;
		
		while(numeroCheck && j<comuneDiResidenza.length()) {
			
			if(Cellulare.charAt(j)<='9' && Cellulare.charAt(j)>='0') numeroCheck=true;
			else numeroCheck=false;
			j++;
		}
		
		if(!numeroCheck) throw new InputException("\n Il numero di cellulare è una stringa di caratteri di lunghezza > 10 o contiene simboli che non sono numeri");
		
		
		
		
		
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
