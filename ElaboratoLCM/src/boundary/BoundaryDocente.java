package boundary;

import java.time.LocalDate;

import control.GestioneIstitutoScolasticoController;
import exception.DAOException;
import exception.OperationException;

public class BoundaryDocente {

	public void inserimentoVoto(int matricola, int voto, 
			LocalDate data_Voto, String materia, int matricola_docente)throws OperationException { 
		
		boolean matricolaCheck = false;
		boolean data_VotoCheck = false;
		boolean materiaCheck = false;
		
		String matr=String.valueOf(matricola);
		if(matr.length()==20) matricolaCheck = true;
		else matricolaCheck = false;
		
		if(!matricolaCheck) throw new OperationException("\n Matricola non è di dimensione 20");
		
		if(voto<0) throw new OperationException("\n Voto non valido poichè negativo");
		
		if(materia.length()<=100) materiaCheck = true;
		else materiaCheck = false;
		
		if(!materiaCheck) throw new OperationException("\n Materia è una stringa di lunghezza>100");
		
		GestioneIstitutoScolasticoController GIS = GestioneIstitutoScolasticoController.getInstance();
		
		try {
			GIS.inserimentoVoto(matricola, voto, data_Voto, materia, matricola_docente);
		}
		catch(OperationException ex) {	
			throw new OperationException("parametri non valido");
	}	
	}
	}

