package boundary;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import control.GestioneIstitutoScolasticoController;
import exception.DAOException;
import exception.OperationException;

public class BoundaryDocente {

	//forse qui fare un altro tipo di eccezione di tipo input non valido.
	
	public void inserimentoVoto(int matricola, int voto, 
			String dataVoto, String materia, int matricola_docente)throws OperationException { 
		
		boolean matricolaCheck = false;
		boolean materiaCheck = false;
		boolean dataCheck = false;
		LocalDate data_Voto;
		String matr=String.valueOf(matricola);
		if(matr.length()==20) matricolaCheck = true;
		else matricolaCheck = false;
		
		if(!matricolaCheck) throw new OperationException("\n Matricola non è di dimensione 20");
		
		if(voto<0) throw new OperationException("\n Voto non valido poichè negativo");
		
		if(materia.length()<=100) materiaCheck = true;
		else materiaCheck = false;

		DateFormat df= new SimpleDateFormat("yyyy-mm-dd");
		df.setLenient(false);
		try {
		 df.parse(dataVoto);
		 dataCheck=true;
		}
		catch(ParseException e) {
			dataCheck = false;
		}
		try{
			 data_Voto = LocalDate.parse(dataVoto);
		}
		catch(ParseException e) {
			return;
		}
		if(!materiaCheck) throw new OperationException("\n Materia è una stringa di lunghezza>100");
		
		GestioneIstitutoScolasticoController GIS = GestioneIstitutoScolasticoController.getInstance();
		
		try {
			GIS.inserimentoVoto(matricola, voto, data_Voto, materia, matricola_docente);
		}
		catch(OperationException ex) {	
			return ex.getMessage();
		}
	}	
	}
}

