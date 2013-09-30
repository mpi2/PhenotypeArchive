package uk.ac.ebi.phenotype.ontology;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Savepoint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.coode.owlapi.obo.parser.OBOOntologyFormat;
import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.writer.OBOFormatWriter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.AutoIRIMapper;

import owltools.graph.OWLGraphEdge;
import owltools.graph.OWLGraphWrapper;
import owltools.graph.OWLQuantifiedProperty;
import owltools.io.ParserWrapper;
import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
/**
 * 
 * @author ilinca tudose
 *
 */
public class OntologyHandler {

/*	public class main {

		public static void main(String[] args) {
		//	extractSlim(loadOntology("/Users/tudose/Documents/mp-ext-merged.owl"), "/Users/tudose/Documents/mpTerms.txt", null, "MP:", "/Users/tudose/Documents/newOnt.obo");
			
		//	ArrayList<String> relLabelsToFollow = new ArrayList<String>();
		//	relLabelsToFollow.add("part of");
		//	extractSlim(loadOBOOntology("/Users/tudose/Documents/adult_mouse_anatomy.obo"), "/Users/tudose/Documents/maTerms.txt", relLabelsToFollow, "MA:", "/Users/tudose/Documents/newMAOnt.obo");;
		
			grepMPReferencedMATerms("/Users/tudose/Documents/mpTerms.txt", "/Users/tudose/Documents/mp-ext-merged.owl", "/Users/tudose/Documents/maTermsReferencedByMP.txt", "/Users/tudose/Documents/ma-mp_newOnt.obo");
		}
*/		
		public static void grepMPReferencedMATerms(String mpTermsLocation, String mpExtLocation, String resultsFile, String ontSaveLocation){
			/**
			 * Parameters:
			 * 	mpTermsLocation - txt file containing one MP id per line
			 *	mpExtLocation - location of the mp extended merged file
			 *	resultsFile - file in which MA ids will be written, one per line
			 *	ontSaveLocation - location where the .obo ontology should be saved
			 *
			 * Action:
			 * - select all MA terms referenced by MP terms in the list via {inheres_in, towards, inheres_in_par_of} 
			 * - build a bridge ontology to follow relations between MP and MA terms, only for MP terms in the list and referenced MA terms
			 */
			OWLOntology ont = loadOntology(mpExtLocation);
			ParserWrapper pw = new ParserWrapper();
			try {
				//parse ontology into graph
				OWLGraphWrapper graph = new OWLGraphWrapper(ont);
				// load mp termslist
				ArrayList<String> wantedIDs = new ArrayList<String>();
				BufferedReader cin = new BufferedReader (new FileReader (mpTermsLocation));
				String line;
				while ((line = cin.readLine()) != null){
					wantedIDs.add(line.trim());
				}
				cin.close();
				// create new ontology
				OWLOntologyManager man = create("newOnt.owl");
				IRI example_iri = IRI.create("file://"+ontSaveLocation.replace(".obo",".owl"));
				OWLOntology newOnt = null;
				try {
					newOnt = man.createOntology(example_iri);
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
				// use a hasSet to prevent duplicate entries
				HashSet<String> res = new HashSet<String>();
				for (String id: wantedIDs){
					OWLClass cls = graph.getOWLClassByIdentifier(id);
					if ( cls!= null){
						Set<OWLClassExpression> eqList = cls.getEquivalentClasses(ont);
						for (OWLClassExpression eq: eqList){
							if (eq.getSignature().toString().contains("MA_")){
								for (OWLEntity x : eq.getSignature()){
									if (x.toStringID().contains("MA_")){ // if no MA terms are referenced we're not interested in the axiom 
										// add MA id to the list of MA terms
										res.add(graph.getIdentifier(x));
									}
								}							
								// Parse each relation at the time {towards, inheres_in, inheres_in_part_of}
								// TEST if one can get all object property labels by given ids. ID's might change, though unlikely!!
								// inheres_in
								Pattern p = Pattern.compile(".*ObjectSomeValuesFrom\\(<http://purl.obolibrary.org/obo/BFO_0000052> <(.*?)>\\)(.*)");
								Matcher m = p.matcher(eq.toString());
								while (m.find()){
									// add axiom if the object is MA
									if (m.group(1).contains("MA_"))
										addAxiom(cls, "http://purl.obolibrary.org/obo/BFO_0000052", m.group(1), newOnt, man, ont);
								}
								// towards
								p = Pattern.compile(".*ObjectSomeValuesFrom\\(<http://purl.obolibrary.org/obo/BFO_0000070> <(.*?)>\\)(.*)");
								m = p.matcher(eq.toString());
								while (m.find()){
									// add axiom if the object is MA
									if (m.group(1).contains("MA_"))
										addAxiom(cls, "http://purl.obolibrary.org/obo/BFO_0000070", m.group(1), newOnt, man, ont);
								}
								// inheres_in_part_of
								
									p = Pattern.compile(".*ObjectSomeValuesFrom\\(<http://purl.obolibrary.org/obo/mp/mp-logical-definitions#inheres_in_part_of> <(.*?)>\\)(.*)");
								m = p.matcher(eq.toString());
								while (m.find()){
									// add axiom if the object is MA
									if (m.group(1).contains("MA_"))
										addAxiom(cls, "http://purl.obolibrary.org/obo/mp/mp-logical-definitions#inheres_in_part_of", m.group(1), newOnt, man, ont);
								}
							}
						}
					}
				}
				man.saveOntology(newOnt);
				// export to obo too
				String namespaceGen = ontSaveLocation.split("/")[ontSaveLocation.split("/").length-1].split("\\.")[0] + ".ontology";
				saveAsOBO(newOnt, ontSaveLocation, namespaceGen);
				// write IDs out 
				BufferedWriter cout = new BufferedWriter (new FileWriter (resultsFile));
				for (String x: res){
					cout.write(x+ "\n");				
				}
				cout.close();
				System.out.println("Identified " + res.size()+ " referenced MA terms.");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public static void addAxiom(OWLClass cls, String relIRI, String objIRI, OWLOntology ont, OWLOntologyManager man, OWLOntology oldOnt){
			// Adds axiom of the form :
			// cls subClassOf rel some obj
			// Returns true for succes false otherwise
			OWLDataFactory factory = man.getOWLDataFactory();
			// add cls to the ontology
			OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(cls);
			man.addAxiom(ont, declarationAxiom);
			addAnnotations(cls, man, ont, oldOnt);
			// create class def for obj
			OWLClass obj = factory.getOWLClass(IRI.create(objIRI));
			declarationAxiom = factory.getOWLDeclarationAxiom(obj);
			man.addAxiom(ont, declarationAxiom);
			addAnnotations(obj, man, ont, oldOnt);
			// if not existing create rel definition axiom
			OWLObjectProperty rel = factory.getOWLObjectProperty(IRI.create(relIRI));
			declarationAxiom = factory.getOWLDeclarationAxiom(rel);
			man.addAxiom(ont, declarationAxiom);
			addAnnotationsTOObjProperties(rel, man, ont, oldOnt);
			// create existential axiom
			OWLObjectSomeValuesFrom some = factory.getOWLObjectSomeValuesFrom(rel, obj);
			// create subclassOf axiom
			OWLSubClassOfAxiom subclassOf = factory.getOWLSubClassOfAxiom(cls, some);
			// add everything to the ontology
			man.addAxiom(ont, subclassOf);
		}
		
		
		public static OWLOntology loadOBOOntology(String path)
		{
			String newIri;
			ParserWrapper pw = new ParserWrapper();
			try {
				newIri = IRI.create(path).toString();
				OWLOntology ont = pw.parse(newIri);
				OWLGraphWrapper graph = new OWLGraphWrapper(ont);
				System.out.println("Loaded ontology with " + graph.getAllOWLObjects().size() + " classes and object properties.");
				return ont;
			}catch (Exception e) {e.printStackTrace();}
			return null;
		}
		
		public static OWLOntology loadOntology(String path)
		{
			ParserWrapper pw = new ParserWrapper();
			String newIri;
			try {
				newIri = IRI.create(path).toString();
				OWLOntology ont = pw.parse(newIri);
				OWLGraphWrapper graph = new OWLGraphWrapper(ont);
				System.out.println("Loaded ontology with " + graph.getAllOWLObjects().size() + " classes and object properties.");
				return ont; 
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public static void extractSlim (OWLOntology ont, String termsListLocation, ArrayList<String> relLabelsToFollow, String prefix, String saveLocation){	
			ParserWrapper pw = new ParserWrapper();
			try {
				//parse ontology into graph
				OWLGraphWrapper graph = new OWLGraphWrapper(ont);
				OWLOntologyManager man = create("newOnt.owl");
				IRI example_iri = IRI.create("file://"+saveLocation.replace(".obo",".owl"));
				OWLOntology newOnt = null;
				try {
					newOnt = man.createOntology(example_iri);
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
				
				//read needed terms 
				ArrayList<String> wantedIDs = new ArrayList<String>();
				BufferedReader cin = new BufferedReader (new FileReader (termsListLocation));
				String line;
				while ((line = cin.readLine()) != null){
					wantedIDs.add(line.trim());
				}
				cin.close();
				
				ArrayList<OWLClass> mpTerms = new ArrayList<OWLClass>();
				
				// build a Set<OWLPropertyExpression> from the relLabelsToFollow
				HashSet<OWLPropertyExpression> op = new HashSet<OWLPropertyExpression>();
				if (relLabelsToFollow != null) 
					for (String lbl: relLabelsToFollow){
						op.add((OWLPropertyExpression) graph.getOWLObjectByLabel(lbl));
						System.out.println(graph.getOWLObjectByLabel(lbl));
					}
				
				// fill the ID - label map
				for (OWLObject obj: graph.getAllOWLObjects()){
					// only add labels in the hash
					String classID = graph.getIdentifier(obj).trim();
					if (OWLClassImpl.class.toString().equals(obj.getClass().toString())){
						if ( classID.startsWith(prefix) && wantedIDs.contains(classID)){ 
							mpTerms.add((OWLClass) obj);
							addClass(obj, man, graph, newOnt, ont);
		// here. check if getAncestors is the right thing. 
							Set<OWLObject> ancestors = graph.getAncestors(obj, op); 
							for (OWLObject anc: ancestors){
								OWLEntity tempObj;
								try{
									tempObj = (OWLEntity) anc;
									if ( graph.getIdentifier(anc).startsWith(prefix)){
										if (tempObj.toString().contains("0002093")){
											System.out.println("=" + tempObj + " ancestor for " + obj);
											System.out.println("\t ref axioms: " + ont.getReferencingAxioms(tempObj));
										}
										addClass(tempObj, man, graph, newOnt, ont);
									}
								}
								catch (Exception e){
								}
							}
						}
					}
				}
				man.saveOntology(newOnt);
				// export to OBO
				String namespaceGen = saveLocation.split("/")[saveLocation.split("/").length-1].split("\\.")[0] + ".ontology";
				saveAsOBO(newOnt, saveLocation, namespaceGen);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		public static void addClass(OWLObject cls, OWLOntologyManager man, OWLGraphWrapper graph, OWLOntology ont, OWLOntology oldOnt){

			OWLDataFactory factory = man.getOWLDataFactory();
			OWLAxiom ax = factory.getOWLDeclarationAxiom((OWLEntity) cls);
			man.addAxiom(ont, (OWLAxiom) ax);
			addAnnotations(cls, man, ont, oldOnt);
			// add subclassOf axioms
			Set<OWLAxiom> parents = ((OWLClass)cls).getReferencingAxioms(oldOnt, true);
			for(OWLAxiom p: parents){
				if(p.getClass() == OWLSubClassOfAxiomImpl.class){
					String clsIRI = graph.getIRIByIdentifier(graph.getIdentifier(cls)).toString();
					if( p.toString().split(" ")[0].contains(clsIRI)){
						man.addAxiom(ont, p);	
					}
				}
			}
		}
		
		public static void addAnnotations(OWLObject cls, OWLOntologyManager man, OWLOntology ont, OWLOntology oldOnt){
			Set<OWLAnnotationAssertionAxiom> annotations = ((OWLClass)cls).getAnnotationAssertionAxioms(oldOnt);
			for (OWLAnnotationAssertionAxiom a: annotations){
				man.addAxiom(ont, a);
			}
		}
		
		public static void addAnnotationsTOObjProperties(OWLObject cls, OWLOntologyManager man, OWLOntology ont, OWLOntology oldOnt){
			Set<OWLAnnotationAssertionAxiom> annotations = ((OWLObjectProperty)cls).getAnnotationAssertionAxioms(oldOnt);
			for (OWLAnnotationAssertionAxiom a: annotations){
				man.addAxiom(ont, a);
			}
		}
		
		public static void saveAsOBO(OWLOntology ont, String saveLocation, String defaultNamespace){
			// create a translator object and feed it the OBO Document
			Owl2Obo bridge = new Owl2Obo();
			try {
				OBODoc obodoc = bridge.convert(ont);
				BufferedWriter cout = new BufferedWriter (new FileWriter(new File(saveLocation)));
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				cout.write("date: "+dateFormat.format(cal.getTime())+"\nauto-generated-by: ilinca's script\ndefault-namespace: " + defaultNamespace + "\n");
				cout.write(renderOboToString(obodoc));
				cout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public static String renderOboToString(OBODoc oboDoc) throws IOException {
			OBOFormatWriter writer = new OBOFormatWriter();
			writer.setCheckStructure(true);
			StringWriter out = new StringWriter();
			BufferedWriter stream = new BufferedWriter(out);
			writer.write(oboDoc, stream);
			stream.close();
			return out.getBuffer().toString();
		}
		
		public static OWLOntologyManager create(String fileName) {
			/**
			 * Creates new, empty OWL ontology
			 */
			OWLOntologyManager m = OWLManager.createOWLOntologyManager();
			m.addIRIMapper(new AutoIRIMapper( new File(fileName), true));
			return m;
			}
}
