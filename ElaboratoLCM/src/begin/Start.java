package begin;

import boundary.BoundaryDocente;
import boundary.BuondarySegreteria;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import exception.OperationException;
import exception.InputException;

public class Start {

	public static void main(String[] args) {		
		
		boolean exit = false;
		Scanner scan = new Scanner(System.in);
		
		while(!exit) {
			System.out.println("\n Scegli la funzionalità da testare. ");
			System.out.println("1. Registra Utente");
			System.out.println("2. Inserimento Voto");
			System.out.println("3. Consultazione media voti per materia");
			System.out.println("4. Esci");
			
			String op = scan.nextLine();

			switch(op) {
			
			case "1": 
				
				boolean inputValido=false;
				Date dataDiNascita=null;
				String nome, cognome, comuneDiResidenza, numeroDiCellulare;
				String email, codiceFiscale,Ruolo;
				
				BuondarySegreteria bs= new BuondarySegreteria();
				 System.out.println("benvenuto, inserisci il tuo nome!");
				 nome=scan.nextLine();
				 System.out.println("inserisci il tuo cognome!");
				 cognome=scan.nextLine();
				
				 while (!inputValido) {
				 try {
						System.out.println("Inserisci la data (aaaa-MM-gg)");
						String dataTemp = scan.nextLine();
						dataDiNascita = Date.valueOf(dataTemp);

						inputValido = true;
					} catch (IllegalArgumentException e) {
						System.out.println("Errore nell'acquisizione di data riprovare..");
						System.out.println();
					}
				 }
				 
				 System.out.println("inserisci il tuo comune di residenza!");
				 comuneDiResidenza=scan.nextLine();
				 System.out.println("inserisci il tuo numeroDiCellulare!");
				 numeroDiCellulare=scan.nextLine();
				 
				 System.out.println("inserisci la tua email!");
				 email=scan.nextLine();
				 
				 System.out.println("inserisci il tuo codice fiscale!");
				 codiceFiscale=scan.nextLine();
				 
				 System.out.println("inserisci il tuo ruolo nell'istituo scolastico!");
				 Ruolo=scan.nextLine();
				 try {
					 
					 bs.registraUtente(nome, cognome, dataDiNascita, 
								codiceFiscale, comuneDiResidenza, email, numeroDiCellulare,
								Ruolo);
						 
					 }
					 catch(InputException e) {
						 System.out.println(e.getMessage());
					 }
				//da fare
				break;
			case "2": 
				// inserimentoVoto(int matricola, int voto, String data_Voto, String materia, int matricola_docente)
				 int matricola, matricola_docente;
				 LocalDate data_voto=null;
				 String materia;
				 int voto;
				 boolean input1 = false;
				 
				 BoundaryDocente bd= new BoundaryDocente();
				 System.out.println("benvenuto, inserisci la tua matricola docente!");
				 try {
					 
					 matricola_docente=Integer.parseInt(scan.nextLine());
				 }
				 catch (NumberFormatException ex){
			           System.out.println("Hai inserito un carattere torna al menu!");
			           break;
			        }
				 System.out.println("inserisci la matricola dell'alunno!");	 
				 try {
					 
					 matricola=Integer.parseInt(scan.nextLine());
				 }
				 catch (NumberFormatException ex){
			           System.out.println("Hai inserito un carattere torna al menu!");
			           break;
			        }
				 while (!input1) {
					 try {
							System.out.println("Inserisci la data (aaaa-MM-gg)");
							String dataTemp = scan.nextLine();
							data_voto = LocalDate.parse(dataTemp);

							input1 = true;
						} catch (DateTimeParseException e) {
							System.out.println("Errore nell'acquisizione di data riprovare..");
							System.out.println();
						}
					 }
				 System.out.println("inserisci la materia del voto da inserire");
				 materia=scan.nextLine();
				 
				 System.out.println("inserisci il voto da inserire");
				 try {
					  voto=Integer.parseInt(scan.nextLine());
				 }
				 catch (NumberFormatException ex){
			           System.out.println("Hai inserito un carattere torna al menu!");
			           break;
			        }
				 try {
					 
					bd.inserimentoVoto(matricola, voto, data_voto, materia, matricola_docente);
					System.out.println("Voto inserito!");
					 
				 }
				 catch(InputException e) {
					 System.out.println(e.getMessage());
				 }
				 
			 }
				 break;
		/*	case "3":
				
				break;
			} */
			}
		exit=true;
		
		}	
}
