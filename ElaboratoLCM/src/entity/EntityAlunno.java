package entity;

import java.sql.Date;

//SERVE FORNIRE IL SET PER LA MATRICOLA?

abstract class Utente {
	
	private String nome;
	private String cognome;
	private String codiceFiscale;
	private String comuneDiResidenza;
	private String email;
	private String numeroDiCellulare;
	private String username;
	private String password;
	private Date dataDiNascita;
	
	
	public Utente(String nome, String cognome, String codFisc, String comuneDiResidenza, String email, String numeroCel, String username, String password, Date dataDiNascita){
		this.nome = nome; this.cognome = cognome; this.codiceFiscale = codFisc; this.comuneDiResidenza = comuneDiResidenza; this.email = email; this.dataDiNascita = dataDiNascita;
		this.numeroDiCellulare = numeroCel; this.username = username; this.password = password;
	}
	
	//costruttore di default costruito qualora si voglia istanziare un oggetto non specificando i valori per tutti i membri
	public Utente() {}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getComuneDiResidenza() {
		return comuneDiResidenza;
	}

	public void setComuneDiResidenza(String comuneDiResidenza) {
		this.comuneDiResidenza = comuneDiResidenza;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumeroDiCellulare() {
		return numeroDiCellulare;
	}

	public void setNumeroDiCellulare(String numeroDiCellulare) {
		this.numeroDiCellulare = numeroDiCellulare;
	}
	
	public Date getDataDiNascita() {
		return this.dataDiNascita;
	}
	
	public void setDataDiNascita(Date dataDiNascita) {
		this.dataDiNascita = dataDiNascita;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}

public class EntityAlunno extends Utente {
	
	private int matricola;

	public EntityAlunno(String nome, String cognome, String codFisc, String comuneDiResidenza, String email,
			String numeroCel, String username, String password, int matricola, Date dataDiNascita) {
		super(nome, cognome, codFisc, comuneDiResidenza, email, numeroCel, username, password, dataDiNascita);
		this.matricola = matricola;
	}
	
	public EntityAlunno() {super();}
	
	public int getMatricola() {
		return this.matricola;
	}
	
	public void setMatricola(int matricola) {
		this.matricola = matricola;
	}

}
