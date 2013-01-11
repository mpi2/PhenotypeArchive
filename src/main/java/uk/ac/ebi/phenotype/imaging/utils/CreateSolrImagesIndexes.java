/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.imaging.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.phenotype.imaging.persistence.AlleleMpi;
import uk.ac.ebi.phenotype.imaging.persistence.AnnAnnotation;
import uk.ac.ebi.phenotype.imaging.persistence.ImaExperimentDict;
import uk.ac.ebi.phenotype.imaging.persistence.ImaImageRecord;
import uk.ac.ebi.phenotype.imaging.persistence.ImaImageTag;
import uk.ac.ebi.phenotype.imaging.persistence.ImaMouseImageVw;
import uk.ac.ebi.phenotype.imaging.persistence.MtsMouseAllele;
import uk.ac.ebi.phenotype.imaging.springrest.images.Images;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesDao;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.Synonym;

/**
 * Creates one xml file to be posted as the imaging index to solr that represents all the images data
 * Gets it's config from the index-config.xml which is a copy of the app-config.xml file but with basic data connection so we don't need a tomcat context to get it.
 * @author jwarren
 *
 */
public class CreateSolrImagesIndexes {

	private final String nl;
	private final CreateSolrIndexerUtil util = new CreateSolrIndexerUtil();
	private ImagesDao dao;
	private MpHigherLevelOntologyDao ontologyDao;
	private MaHigherLevelOntologyDao maDao;

	public static void main(String[] args) {
		String homeDir = "/Users/jwarren/Documents/mysql/imaging/indexWithAttribution/";// args[0];

		CreateSolrImagesIndexes solr = new CreateSolrImagesIndexes();
		solr.creatSolrIndexes(homeDir);
	}

	public CreateSolrImagesIndexes() {
		nl = util.getLineSeperator();
		ApplicationContext contx = new ClassPathXmlApplicationContext(
				"index-config.xml");
		dao = (ImagesDao) contx.getBean("imagingSourceManager");
		ontologyDao = (MpHigherLevelOntologyDao) contx
				.getBean("mpHigherLevelOntologyDAO");
		maDao = (MaHigherLevelOntologyDao) contx
				.getBean("maHigherLevelOntologyDAO");

	}
/**
 * main method to run to get the solrDoc.xml
 * @param homeDir the directory where the xml file will be put.
 */
	public void creatSolrIndexes(String homeDir) {

		File sourcesFile = new File(homeDir
				+ "ckUnderscores.xml");
		OutputStreamWriter out = null;
		try {
			// out = new OutputStreamWriter(System.out);
			out = new OutputStreamWriter(new FileOutputStream(sourcesFile),
					"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		System.out.println("writing images for solrIndexer at "
				+ sourcesFile.getAbsolutePath());

		PrintWriter writer = null;
		writer = new PrintWriter(out);
		// NEED to add code to remove <a href from any fields!!!!
		writer.write("<add overwrite=\"true\" commitWithin=\"10000\">" + nl);

		int step = 100;
		int lastPosition = 0;
		// need a hibernate method to get the number of records before running
		// the index so we can replace the hard coded number of 89008
		long totalImages = dao.getTotalNumberOfImages();
		System.out.println("Total number of image records=" + totalImages);
		for (int i = 0; i < totalImages; i += step) {
			lastPosition = i + step;
			System.out.println("getting images for entries:" + i + " to"
					+ lastPosition);
			Images images = dao.getAllImages(i, step, "");
			for (ImaImageRecord image : images.getImages()) {
				this.writeSolrImageRecord(image, writer);
			}

		}

		writer.write("</add>");
		writer.flush();
		writer.close();

	}

	private void writeSolrImageRecord(ImaImageRecord image, PrintWriter out) {
		// if experiment name is Mouse Necropsy don't write to index!
		ImaExperimentDict experiment = image.getImaSubcontext()
				.getImaExperimentDict();
		if (!experiment.getName().equals("Mouse Necropsy")) {
			//if(image.getImaPublishedDict().getId()==1){
			out.write("<doc>" + nl);
			createField(out, "id", Integer.toString(image.getId()));
			// System.out.println("id="+image.getId());
			createField(out, "fullResolutionFilePath",
					image.getFullResolutionFilePath());
			createField(out, "largeThumbnailFilePath",
					image.getLargeThumbnailFilePath());
			createField(out, "originalFileName", image.getOriginalFileName());
			createField(out, "smallThumbnailFilePath",
					image.getSmallThumbnailFilePath());
			writeInstituteAttribution(image, out);

			ImaMouseImageVw mouse = image.getImaMouseImageVw();
			if (mouse != null) {
				createField(out, "mouseId", Integer.toString(mouse.getId()));
				createField(out, "gender", mouse.getGender());
				createField(out, "colonyName", mouse.getColonyName());
				createField(out, "genotype", mouse.getGenotype());
				createField(out, "ageInWeeks", mouse.getAgeInWeeks().toString());
				writeLiveSample(mouse, out);
				writeSangerMouseInfo(mouse, out);
			}
			writeExperiment(experiment, out);

			List<ImaImageTag> tags = image.getImaImageTags();
			for (ImaImageTag tag : tags) {
				createTagsIndex(tag, out);
			}

			out.write("</doc>" + nl);
		//}
		}
	}

	private void writeSangerMouseInfo(ImaMouseImageVw mouse, PrintWriter out) {
		if (mouse.getMtsMouseAlleleMv() == null)
			return;

		if (mouse.getMtsMouseAlleleMv().getMtsMouseAlleles() != null) {
			Set<MtsMouseAllele> mtsMouseAlleles = mouse.getMtsMouseAlleleMv()
					.getMtsMouseAlleles();
			for (MtsMouseAllele mtsMouseAllele : mtsMouseAlleles) {
				String name = mtsMouseAllele.getMtsGenotypeDict().getName();
				createField(out, "alleleName", name);
			}
		}

		if (mouse.getMtsMouseAlleleMv().getAlleleMpi() == null)
			return;
		AlleleMpi alleleMpi = mouse.getMtsMouseAlleleMv().getAlleleMpi();
		createField(out, "sangerSymbol", alleleMpi.getSymbol());
		createField(out, "accession", alleleMpi.getId().getAccession());
		// createField(out, "accession", alleleMpi.getGfAcc());
		// synonyms and subtype + marker name??? from genomic feature
		// getting these from the genotype string-> allele id (mgi) -> gf_acc ->
		// genomic feature so hoping to catch some not in europhenome already!!!
		if (alleleMpi.getGenomicFeature() == null)
			return;
		GenomicFeature feature = alleleMpi.getGenomicFeature();

		createField(out, "accession", feature.getId().getAccession());
		createField(out, "symbol", feature.getSymbol());
		createField(out, "symbol_gene", feature.getSymbol()+"_"+feature.getId().getAccession());
		createField(out, "geneName", feature.getName());
		createField(out, "subtype", feature.getSubtype().getName());
		for (Synonym synonym : feature.getSynonyms()) {
			createField(out, "geneSynonyms", synonym.getSymbol());
		}

	}

	private void writeInstituteAttribution(ImaImageRecord image, PrintWriter out) {
		createField(out, "organisation", image.getOrganisation().getName());
	}

	private void writeExperiment(ImaExperimentDict experiment, PrintWriter out) {
		createField(out, "expName", experiment.getName());
		createField(out, "expDescription", experiment.getDescription());
		createField(out, "expName_exp", experiment.getName()+"_exp");
		
	}

	private void writeLiveSample(ImaMouseImageVw mouse, PrintWriter out) {
		if (mouse.getLiveSample() == null)
			return;
		String group = mouse.getLiveSample().getGroup();
		createField(out, "liveSampleGroup", group);
		if (mouse.getLiveSample().getBiologicalModel().getGenomicFeatures() == null)
			return;
		List<GenomicFeature> genomicFeatures = mouse.getLiveSample()
				.getBiologicalModel().getGenomicFeatures();
		for (GenomicFeature feature : genomicFeatures) {
			createField(out, "accession", feature.getId().getAccession());
			createField(out, "symbol", feature.getSymbol());
			createField(out, "symbol_gene", feature.getSymbol()+"_"+feature.getId().getAccession());
			createField(out, "geneName", feature.getName());
			createField(out, "subtype", feature.getSubtype().getName());
			for (Synonym synonym : feature.getSynonyms()) {
				createField(out, "geneSynonyms", synonym.getSymbol());
			}
		}
	}

	private void createField(PrintWriter out, String name, String content) {
		if (!content.equals("null")) {
			util.createField(out, name, content);
		}
	}

	private void createTagsIndex(ImaImageTag tag, PrintWriter out) {

		if (tag.getTagValue() != null && !tag.getTagValue().equals("NULL")
				&& !tag.getTagValue().equals("null")) {
			if (tag.getTagName().equalsIgnoreCase(("comment"))) {
				// System.out.println("illegal char="+tag.getTagValue());
			}
			createField(out, "tagName", tag.getTagName());
			createField(out, "tagValue", tag.getTagValue());
		}
		if (tag.getXStart() != null) {
			if (tag.getXStart() != 0.0 || tag.getXEnd() != 0.0 ||

			tag.getYStart() != 0.0 || tag.getYEnd() != 0.0) {
				createField(out, "xStart", Float.toString(tag.getXStart()));
				createField(out, "xEnd", Float.toString(tag.getXEnd()));
				createField(out, "yStart", Float.toString(tag.getYStart()));
				createField(out, "yEnd", Float.toString(tag.getYEnd()));
			}
		}
		Set<AnnAnnotation> annotations = tag.getAnnAnnotations();
		for (AnnAnnotation annotation : annotations) {
			createAnnotationIndex(annotation, out);

		}

	}

	private void createAnnotationIndex(AnnAnnotation anno, PrintWriter out) {
		createField(out, "annotationTermId", anno.getTermId());
		createField(out, "annotationTermName", anno.getTermName());
		getMpHigherLevel(anno, out);
		getMaHigherLevel(anno, out);

	}

	private void getMpHigherLevel(AnnAnnotation anno, PrintWriter out) {
		if (anno.getTermId().startsWith("MP:")) {
			createField(out, "mpTermId", anno.getTermId());
			createField(out, "mpTermName", anno.getTermName()+"_"+anno.getTermId());
			// get the higher level MP term from the komp2 ontology database
			// (not komp2!)
			Map<String, String> higherLevelMP = this.getHigherLevelMpTerm(anno
					.getTermId());
			for (String higherLevelMpTermId : higherLevelMP.keySet()) {
				createField(out, "higherLevelMpTermId", higherLevelMpTermId);
				createField(out, "higherLevelMpTermName",
						higherLevelMP.get(higherLevelMpTermId));
			}
		}
	}

	private Map<String, String> getHigherLevelMpTerm(String lowerLevelTerm) {
		Map<String, String> higherLevelMp = ontologyDao
				.getHigherLevelTerm(lowerLevelTerm);

		return higherLevelMp;
	}

	private void getMaHigherLevel(AnnAnnotation anno, PrintWriter out) {
		if (anno.getTermId().startsWith("MA:")) {
			createField(out, "maTermId", anno.getTermId());
			createField(out, "maTermName", anno.getTermName()+"_"+anno.getTermId());
			
			List<Map<String, Object>> higherLevelMA = this
					.getHigherLevelMaTerm(anno.getTermId());
			for (Map<String, Object> row : higherLevelMA) {

				createField(out, "higherLevelMaTermId",
						(String) row.get("HIGHER_TERM_ID"));
				createField(out, "higherLevelMaTermName",
						(String) row.get("TERM_NAME"));

			}

		}
	}

	private List<Map<String, Object>> getHigherLevelMaTerm(String lowerLevelTerm) {
		List<Map<String, Object>> higherLevelMp = maDao
				.getHigherLevelTerm(lowerLevelTerm);
		if (higherLevelMp.size() == 0)
			System.out
					.println("Error no Higher Level MA term found for this lower level term="
							+ lowerLevelTerm);
		return higherLevelMp;
	}
}
