package Esercitazione;

//importante importare i moduli java.io.BufferedReader, IOException e InputStreamReader

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GestioneIO {

	
	public static void stampa(String s) {
		System.out.println(s);
	}
	
	public static String input() {
		System.out.print("Inserisci un input tipo stringa: ");
		
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		/*
		try {
			String input = cin.readLine();
			//return input;
		} catch (IOException e) {
			e.printStackTrace();
			//return null;
		}
		*/
		String input2;
		int number = 0;
		
		try {
			input2 = cin.readLine();
			number = Integer.parseInt(input2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Integer num = number;
		
		return num.toString();
	}
}
