//DA CANCELLARE

package entity;
import java.util.Date;

abstract class EntityUtente  {

	private String nome;
	private String cognome;
	private Date dataDiNascita;
	private String codiceFiscale;
	private String comuneDiResidenza;
	private String email;
	private String numeroDiCellulare;
	private String username;
	private String password;
	


	public EntityUtente(String nome, String cognome, date dataDiNascita, String codiceFiscale, String comuneDiResidenza,String email, String numeroDiCellulare, String username, String password) {
		super();
		this.nome= nome;
		this.cognome=cognome;
		this.dataDiNascita=dataDiNascita;
		this.codiceFiscale=codiceFiscale;
		this.comuneDiResidenza=comuneDiResidenza;
		this.email=email;
		this.numeroDiCellulare=numeroDiCellulare;
		this.username=username;
		this.password=password;
		
	
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome){
		this.nome= nome;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome){
		this.cognome= cognome;
	}
	public date getDataDiNascita() {
		return dataDiNascita;
	}
	public void setDataDiNascita(date dataDiNascita){
		this.dataDiNascita= dataDiNascita;
	}
	public String getcomuneDiResidenza() {
		return comuneDiResidenza;
	}
	public void setComuneDiResidenza(String comuneDiResidenza){
		this.comuneDiResidenza=comuneDiResidenza;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username){
		this.username=username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password){
		this.password=password;
	}
	public String getNumeroCellulare() {
		return numeroDiCellulare;
	}
	public void setnumeroDiCellulare()(String numeroDiCellulare){
		this.numeroDiCellulare=numeroDiCellulare;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email){
		this.email=email;
	}
	public String getCodiceFiscale() {
		return codiceFiscale;
	}
	public void setCodiceFiscale(String codiceFiscale){
		this.codiceFiscale=codiceFiscale;
	}
}
