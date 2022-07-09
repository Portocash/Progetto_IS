package main;

import Esercitazione.GestioneIO;

public class principale {

	public static void main(String[] args) {
		System.out.println("Hello world");
		String toStamp = "Ciao, bella vita";
		GestioneIO.stampa(toStamp);
		System.out.print(GestioneIO.input());
	}

}
