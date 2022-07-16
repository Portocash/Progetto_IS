package control;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import exception.DBManagementException;


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
	
	static void registrUtente(String Nome, String Cognome, Date dataDiNascita, 
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
					throw new OperationException("Docente gi� presente nel database.");
					}
					
					docente.setNome(Nome);
					docente.setCognome(Cognome);
					docente.setDataDiNascita(dataDiNascita);
					docente.setCodiceFiscale(codiceFiscale);
					docente.setComuneDiResidenza(comuneDiResidenza);
					docente.setEmail(Email);
					docente.setNumeroDiCellulare(Cellulare);
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
				throw new OperationException("Alunno gi� registrato nel sistema.");
				}
				
				alunno.setNome(Nome);
				alunno.setCognome(Cognome);
				alunno.setDataDiNascita(dataDiNascita);
				alunno.setCodiceFiscale(codiceFiscale);
				alunno.setComuneDiResidenza(comuneDiResidenza);
				alunno.setEmail(Email);
				alunno.setNumeroDiCellulare(Cellulare);
				alunno.setUsername(username.concat("_alunno"));
				alunno.setPassword(codiceFiscale);
				genitore=null;
				docente=null;
				AlunnoDAO.createAlunno(alunno);
					
		    break;
		  case "genitore":
				query_result2 = GenitoreDAO.checkGenitoreDatabase(codiceFiscale);
				if(query_result2 != null) {
				throw new OperationException("Genitore gi� registrato nel sistema.");
				}
				
				genitore.setNome(Nome);
				genitore.setCognome(Cognome);
				genitore.setDataDiNascita(dataDiNascita);
				genitore.setCodiceFiscale(codiceFiscale);
				genitore.setComuneDiResidenza(comuneDiResidenza);
				genitore.setEmail(Email);
				genitore.setNumeroDiCellulare(Cellulare);
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
	
	static void inserimentoVoto(int matricola, int voto, LocalDate data_Voto, String materia, int matricola_docente)throws OperationException {
		
		String annoScolasticoCorrente;
		String temp;
		String annoSistema;
		boolean valido=false;
		int i=0;
		String mat;
		
		ArrayList<EntityInsegnamento> ListaInsegnamento=new ArrayList<EntityInsegnamento>();
		
		/* getAnnoScolasticoCorrente*/
		
		annoScolasticoCorrente=String.valueOf(data_Voto.getYear());
		
		try{
			char sezione_classe= FrequenzaDAO.getSezioneClasse(matricola, annoScolasticoCorrente);
			int anno_classe= FrequenzaDAO.getAnnoClasse(matricola, annoScolasticoCorrente);
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
			
		}
		catch(DBConnectionException dbEx) {
			throw new OperationException("\nRiscontrato problema connessione!\n");
		}catch(DAOException ex) {
		throw new OperationException("\nRiscontrato problema database!\n");
		}		
	}
	
	public static void getGenitoreClasseAlunno(EntityAlunno eA, EntityGenitore currentG, EntityClasse eC, String username, String password, String nome, String annoScolastico) throws DAOException, DBConnectionException {
		
		try {
			
			GenitoreDAO.readGenitore(username, password);
			
			ArrayList<EntityAlunno> figli = AlunnoDAO.getFigli(currentG.getCodiceFiscale());
			
			boolean found = false;
			int i = 0;
			
			while(!found && i<figli.size()) {
	            if(figli.get(i).getNome().compareTo(nome) == 0) {
	            	eA = figli.get(i);
	            	found = true;
	            } else i++;
	        }
			
			LocalDate todaysDate = LocalDate.now();
			annoScolastico = String.valueOf(todaysDate.getYear());
			
			eC = ClasseDAO.readClasse(annoScolastico, eA.getMatricola());
			
		} catch (DAOException e) { 
			throw e;
		} catch (DBConnectionException e) {
			throw e;
		}
	}
	
	public static EntityGenitore login(String username, String password) throws DAOException, DBConnectionException {
		
		EntityGenitore eG = null;
		
		try {
			eG = GenitoreDAO.readGenitore(username, password);
		}
		
		catch (DAOException e) { 
			throw e;
		} catch (DBConnectionException e) {
			throw e;
		}
		return eG;
	}
	
	
	public static ArrayList<String> consultazioneVotiMediPerMateria(EntityGenitore currentG) throws DAOException, DBConnectionException, DBManagementException {
		
		List <String> listaNomi = new ArrayList<String>();
		
		try {
			
			ArrayList<EntityAlunno> figli = AlunnoDAO.getFigli(currentG.getCodiceFiscale());
			if(figli.isEmpty()) throw new DBManagementException("Genitore non associato a nessu alunno");
			
			for(EntityAlunno alunno : figli) {
	            listaNomi.add(alunno.getNome());
	        }
			
			return (ArrayList<String>)listaNomi;
			
		} catch (DAOException e) { 
			throw e;
		} catch (DBConnectionException e) {
			throw e;
		}
	}
	
	public static ArrayList<String> consultazioneVotiMediPerMateria(String username, String password, String nome) throws DAOException, DBConnectionException {
		
		EntityGenitore currentG = null;
		EntityAlunno eA = null;
		EntityClasse eC = null;
		List <String> listaMaterie = new ArrayList<String>();
		String annoScolastico = null;
		
		try {
			
			GestioneIstitutoScolasticoController.getGenitoreClasseAlunno(eA, currentG, eC, username, password, nome, annoScolastico);
			listaMaterie = InsegnamentoDAO.getInsegnamento(annoScolastico, eC.getSezione(), eC.getAnno());
			
		} catch (DAOException e) { 
			throw e;
		} catch (DBConnectionException e) {
			throw e;
		}
		
		return (ArrayList<String>)listaMaterie;
	}
	
	public static HashMap<String, Float> consultazioneVotiMediPerMateria(String username, String password, ArrayList<String> materie, String nomeFiglio) throws DAOException, DBConnectionException {
		
		EntityGenitore currentG = null;
		EntityAlunno eA = null;
		EntityClasse eC = null;
		String annoScolastico = null;
		HashMap<String, Float> voti = new HashMap<String, Float>();
	
		try {
			
			getGenitoreClasseAlunno(eA, currentG, eC, username, password, nomeFiglio, annoScolastico);
			float voto = 0f;
			
			for(String materia: materie) {
				voto = ValutazioneDAO.readValutazione(eA.getMatricola(), materia, annoScolastico);
				if(voto == -1f)
					voti.put(materia, 0f);
				else 
					voti.put(materia, voto);
			}
			
		} catch (DAOException e) { 
			throw e;
		} catch (DBConnectionException e) {
			throw e;
		}
		
		return voti;
	}
	
	
}
