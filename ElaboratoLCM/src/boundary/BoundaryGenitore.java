package boundary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import control.GestioneIstitutoScolasticoController;
import entity.EntityGenitore;
import exception.DAOException;
import exception.DBConnectionException;
import exception.DBManagementException;

public class BoundaryGenitore {
	
	static private Scanner scan = new Scanner(System.in);
	
	public static void getVotoMedioPerMateria() {
		
		String username = null;
		String password = null;
		String nomeFiglio = null;
		EntityGenitore eG = null;
			
		//login
		
		boolean login = false;
		
		try {
			
			while(!login) {
				
				System.out.print("Inserire username e password per il login\n"
						+ "-> username: ");
				username = scan.nextLine();
				
				System.out.print("-> password: ");
				password = scan.nextLine();
				
				eG = GestioneIstitutoScolasticoController.login(username, password);
				
				if(eG == null)
					System.out.println("Credenziali non corrette");
				else
					login = true;
			}
			
			//stampa dei figli
			
			ArrayList<String> figli = new ArrayList<String>();
			
			figli = GestioneIstitutoScolasticoController.consultazioneVotiMediPerMateria(eG);
			for(String alunno: figli) {
				System.out.println("-> " + alunno);
			}
			
			boolean validName = false;
			while(!validName) {
				
				System.out.print("Inserire il nome dell'alunno di cui si desidera conoscere la media voti: ");
				nomeFiglio = scan.nextLine();
				if(figli.contains(nomeFiglio)) validName = true;
				else 
					System.out.println("Sintassi errata: riprovare!");
			}
			
			ArrayList<String> elencoMaterie = new ArrayList<String>();
			ArrayList<String> elencoMaterieInput = new ArrayList<String>();
			
			elencoMaterie = GestioneIstitutoScolasticoController.consultazioneVotiMediPerMateria(username, password, nomeFiglio);
			
			//acquisizione materie dal genitore
			
			System.out.println("Inserire le materie di cui si vuole conoscere la media voti:");
			
			while(scan.hasNext()) {
				String next = scan.next();
				if(elencoMaterie.contains(next)) {
					elencoMaterieInput.add(next);
				}
				else
					System.out.println("Materia non valida");
			}
			
			// acquisizione voti dal database
			
			HashMap<String, Float> materie = null;
			
			materie = GestioneIstitutoScolasticoController.consultazioneVotiMediPerMateria(username, password, elencoMaterieInput, nomeFiglio);
			for(HashMap.Entry<String, Float> entry : materie.entrySet()) 
			    System.out.println(entry.getKey() + ": " + entry.getValue().toString());
			    
			
		} catch (DAOException | DBConnectionException | DBManagementException e) {
			System.out.print(e.getMessage());
		}
		
	}

}
