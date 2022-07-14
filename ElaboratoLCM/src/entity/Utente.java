package entity;

import java.sql.Date;

public abstract class Utente {
	
	private String nome;
	private String cognome;
	private Date dataDiNascita;
	private String comuneDiResidenza;
	private String username;
	private String password;
	private String numeroDiCellulare;
	private String email;
	private String codiceFiscale;
	
	
	public Utente(String nome, String cognome, Date dataDiNascita, String comuneDiResidenza, 
			String username, String password, String numeroDiCellulare, String email, String codiceFiscale) {
		this.nome = nome; this.cognome = cognome; this.dataDiNascita = dataDiNascita; this.comuneDiResidenza = comuneDiResidenza;
		this.username = username; this.password = password; this.numeroDiCellulare = numeroDiCellulare; this.email = email;
		this.codiceFiscale = codiceFiscale;
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