package boundary;

import java.time.LocalDate;
import java.util.Scanner;

import exception.OperationException;

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
				//da fare
				break;
			case "2": 
				// inserimentoVoto(int matricola, int voto, String data_Voto, String materia, int matricola_docente)
				 int matricola, matricola_docente;
				 String data_voto;
				 String materia;
				 int voto;
				 
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
				 System.out.println("inserisci la data del voto che vuoi inserire nel formato (yyyy-mm-dd)");
				 data_voto=scan.nextLine();
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
				 catch(OperationException e) {
					 
					 System.out.println("inserimento non andato a buon fine!");
				 }
				 break;
			case "3":
				
				break;
			}
			}
		
		
		}	
		
	
}
