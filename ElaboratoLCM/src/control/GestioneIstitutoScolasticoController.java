package control;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.text.DateFormat;
import java.lang.Integer;
import java.time.LocalDate;
import database.AlunnoDAO;
import database.ClasseDAO;
import database.DocenteDAO;
import database.FrequenzaDAO;
import database.GenitoreDAO;
import database.InsegnamentoDAO;
import database.ValutazioneDAO;


import entity.EntityAlunno;
import entity.EntityClasse;
import entity.EntityDocente;
import entity.EntityFrequenza;
import entity.EntityGenitore;
import entity.EntityInsegnamento;
import entity.EntityValutazione;

import exception.DAOException;
import exception.DBConnectionException;
import exception.OperationException;



public class GestioneIstitutoScolasticoController {

	protected GestioneIstitutoScolasticoController(){
		
	}
	
	private static GestioneIstitutoScolasticoController gI = null;
	
	public static GestioneIstitutoScolasticoController getInstance() 
	{ 
		if (gI == null) 
			gI = new GestioneIstitutoScolasticoController(); 

		return gI; 
	}
	
	public void registraUtente(String Nome, String Cognome, Date dataDiNascita, 
			String codiceFiscale, String comuneDiResidenza, String Email, String Cellulare,
			String Ruolo, EntityDocente docente,EntityAlunno alunno, EntityGenitore genitore) throws OperationException {
		
		EntityDocente query_result;
		EntityAlunno query_result1;
		EntityGenitore query_result2;
		String username, password;
		String temp= Nome.substring(0, 2);
		String temp1= Cognome.substring(0,2);
		username=temp.concat(temp1);
		
		try {
		switch (Ruolo) {
		  case "docente":
			  
					//controllo non esistenza docente
					query_result = DocenteDAO.checkDocenteDatabase(codiceFiscale);
					if(query_result != null) {
					throw new OperationException("Docente già presente nel database.");
					}
					
					docente.setNome(Nome);
					docente.setCognome(Cognome);
					docente.setDataDiNascita(dataDiNascita);
					docente.setCodiceFiscale(codiceFiscale);
					docente.setComuneDiResidenza(comuneDiResidenza);
					docente.setEmail(Email);
					docente.setnumeroDiCellulare(Cellulare);
					docente.setUsername(username.concat("_docente"));
					docente.setPassword(codiceFiscale);
					genitore=null;
					alunno=null;
					DocenteDAO.createDocente(docente);		
					//crea matricola_docente con le set, e una query.
		    break;
		  case "alunno":
				query_result1 = AlunnoDAO.checkAlunnoDatabase(codiceFiscale);
				if(query_result1 != null) {
				throw new OperationException("Alunno già registrato nel sistema.");
				}
				
				alunno.setNome(Nome);
				alunno.setCognome(Cognome);
				alunno.setDataDiNascita(dataDiNascita);
				alunno.setCodiceFiscale(codiceFiscale);
				alunno.setComuneDiResidenza(comuneDiResidenza);
				alunno.setEmail(Email);
				alunno.setnumeroDiCellulare(Cellulare);
				alunno.setUsername(username.concat("_alunno"));
				alunno.setPassword(codiceFiscale);
				genitore=null;
				docente=null;
				AlunnoDAO.createAlunno(alunno);
					
		    break;
		  case "genitore":
				query_result2 = GenitoreDAO.checkGenitoreDatabase(codiceFiscale);
				if(query_result2 != null) {
				throw new OperationException("Genitore già registrato nel sistema.");
				}
				
				genitore.setNome(Nome);
				genitore.setCognome(Cognome);
				genitore.setDataDiNascita(dataDiNascita);
				genitore.setCodiceFiscale(codiceFiscale);
				genitore.setComuneDiResidenza(comuneDiResidenza);
				genitore.setEmail(Email);
				genitore.setnumeroDiCellulare(Cellulare);
				genitore.setUsername(username.concat("_genitore"));
				genitore.setPassword(codiceFiscale);
				alunno=null;
				docente=null;
				GenitoreDAO.createGenitore(genitore);
		    break;
		}
	}
	catch(DBConnectionException dbEx) {
			throw new OperationException("\nRiscontrato problema connessione!\n");
		}catch(DAOException ex) {
		throw new OperationException("Riscontrato problema database!\n");
		}		
	}
	
	public void inserimentoVoto(int matricola, int voto, LocalDate data_Voto, String materia, int matricola_docente)throws OperationException {
		
		String annoScolasticoCorrente;
		boolean valido=false;
		int i=0;
		String mat;
		int id_registro;
		ArrayList<EntityInsegnamento> ListaInsegnamento=new ArrayList<EntityInsegnamento>();
		EntityFrequenza eF;
		/*getAnnoScolasticoCorrente*/
		
		annoScolasticoCorrente=String.valueOf(data_Voto.getYear());
		
		try{
			/*char sezione_classe= FrequenzaDAO.getSezioneClasse(matricola, annoScolasticoCorrente);
			int anno_classe= FrequenzaDAO.getAnnoClasse(matricola, annoScolasticoCorrente); */
			
			eF= FrequenzaDAO.getFrequenza(matricola_docente, annoScolasticoCorrente);
			
			char sezione_classe= eF.getClasse().getSezione();
			int anno_classe= eF.getClasse().getAnno();
			
			InsegnamentoDAO.getInsegnamento(matricola_docente, annoScolasticoCorrente,
					sezione_classe, anno_classe, ListaInsegnamento);
			while(!valido && i<ListaInsegnamento.size()) {
				
				mat = ListaInsegnamento.get(i).getMateria();
				if(mat.equals(materia)==true) {
					valido=true;
				}
				else i++;
			}
			
			if(valido!=true) {
				
				throw new OperationException("Docente non associato alla classe dell'alunno per quella materia");
			}
			
			LocalDate todaysDate = LocalDate.now();
			String locale=String.valueOf(todaysDate.getYear());
			
			boolean check=annoScolasticoCorrente.equals(locale);
			
			if(check!=true) throw new OperationException("Errore anno scolastico non valido");	
			id_registro = ClasseDAO.readIdRegistro(sezione_classe, anno_classe);
			EntityAlunno ea = new EntityAlunno("","", null, "", "", "", "", "", "", matricola);
			EntityValutazione eV = new EntityValutazione(0, java.sql.Date.valueOf(data_Voto), materia, voto, ea);
			ValutazioneDAO.createValutazione(eV,id_registro);
			
		}
		catch(DBConnectionException dbEx) {
			throw new OperationException("\nRiscontrato problema connessione!\n");
		}catch(DAOException ex) {
		throw new OperationException("\nRiscontrato problema database!\n");
		}		
	}
}