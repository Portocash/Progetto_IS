package boundary;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import control.GestioneIstitutoScolasticoController;
import exception.DAOException;

import exception.OperationException;
import exception.InputException;

public class BoundaryDocente {

	//forse qui fare un altro tipo di eccezione di tipo input non valido.
	
	public void inserimentoVoto(int matricola, int voto, 
			LocalDate dataVoto, String materia, int matricola_docente)throws  InputException{ 
		
		boolean matricolaCheck = false;
		boolean materiaCheck = false;
		boolean dataCheck = false;
		LocalDate data_Voto;
		String matr=String.valueOf(matricola);
		if(matr.length()==20) matricolaCheck = true;
		else matricolaCheck = false;
		
		if(!matricolaCheck) throw new InputException("\n Matricola non è di dimensione 20");
		
		if(voto<0) throw new InputException("\n Voto non valido poichè negativo");
		
		if(materia.length()<=20) materiaCheck = true;
		else materiaCheck = false;
		

	
		

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
		 * 
		 */
		
		
		int j=0;
		while(materiaCheck && j<materia.length()) {
			
			if(materia.charAt(j)<='Z' && materia.charAt(j)>='A' || materia.charAt(j)<='z'&& materia.charAt(j)>='a') materiaCheck=true;
			else materiaCheck=false;
			j++;
		}
		
		if(!materiaCheck) throw new InputException("\n Materia è una stringa di lunghezza>100");
		
		GestioneIstitutoScolasticoController GIS = GestioneIstitutoScolasticoController.getInstance();
		
		try {
			GIS.inserimentoVoto(matricola, voto, dataVoto, materia, matricola_docente);
		}
		catch(OperationException ex) {	
			 System.out.println(ex.getMessage());
		}
	}	
	}


