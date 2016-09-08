package com.biobam.blast2go.apps.submitter.job;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.blast2go.api.datatype.basics.blast.HspKeys;
import com.biobam.blast2go.api.datatype.basics.html.B2GHtml;
import com.biobam.blast2go.api.datatype.basics.mapping.GoMapping;
import com.biobam.blast2go.api.datatype.basics.mapping.GoMappingKeys;
import com.biobam.blast2go.api.datatype.basics.mapping.GoMappings;
import com.biobam.blast2go.api.job.B2GJob;
import com.biobam.blast2go.api.job.IB2GProgressMonitor;
import com.biobam.blast2go.api.job.input.ItemsOrderList;
import com.biobam.blast2go.api.utils.FileUtils;
import com.biobam.blast2go.apps.submitter.job.SubmitterJobParameters.SubType;
import com.biobam.blast2go.dag.model.GONode;
import com.biobam.blast2go.dag.model.IGODag;
import com.biobam.blast2go.namedef.NameDef;
import com.biobam.blast2go.project.model.interfaces.ILightSequence;
import com.biobam.blast2go.project.model.interfaces.SeqCondImpl;
import com.biobam.blast2go.workbench.services.IB2GFilesDirectory;

import es.blast2go.data.IB2GObjectProject;
import es.blast2go.data.IProject;

public class SubmitterCompleteJob extends B2GJob<SubmitterJobParameters> {
	private final static Logger log = LoggerFactory.getLogger(SubmitterCompleteJob.class);

	public SubmitterCompleteJob() {
		super("NCBI GenBank Submission Files creation", new SubmitterJobParameters());
	}

	@Override
	protected boolean showJobMessagesOnExecution() {
		return true;
	}

	@Override
	public void run() throws InterruptedException {
		SubmitterJobParameters parameters = getParameters();
		IB2GProgressMonitor monitor = (IB2GProgressMonitor) getIProgressMonitor();
		IB2GFilesDirectory b2gFilesDirectory = getInput(SubmitterCompleteJobMetadata.ADDITIONAL_B2GFILES_DIRECTORY);
		String b2gFilePath = b2gFilesDirectory.getPath();

		try {
			IProject project = getInput(SubmitterCompleteJobMetadata.INPUT_PROJECT);
			beginTask("NCBI submission", project.getSelectedSequencesCount() + 4);

			MyJobUtils.sbtParser(parameters, monitor);
			worked(1);
			if (isCanceled()) {
				return;
			}

			// Check for gff files
			MyJobUtils.existingFilesAndFolders(parameters, monitor);

			if (parameters.subType.getValue().equals(SubType.wgs) && !isCanceled()) {
				MyJobUtils.cmtParser(parameters, monitor);
				worked(1);

			}

			if (isCanceled()) {
				return;
			}
			ReturnIDs geneNumber = tblCreator();
			postJobMessage("Number of processed sequences: " + geneNumber.getNumberIDsFound().toString());
			if (isCanceled()) {
				return;
			}

			String b2gSubmitterExecutable = DownloadBinaries.download(b2gFilePath);
			MyJobUtils.tbl2asn(parameters, b2gSubmitterExecutable, monitor);
			// tbl2asn(parameters, b2gSubmitterExecutable);
			worked(1);
			if (isCanceled()) {
				return;
			}
			worked(1);
			postOutputResults(summary(parameters, geneNumber));
			postJobMessage("Validation files created. Please revise them and make the appropiate modifications");
			setFinishMessage("Validation files created. Please revise them and make the appropiate modifications");

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public class ReturnIDs {
		public final List<String> a;
		public final Integer b;

		public ReturnIDs(List<String> a, Integer b) {
			this.a = a;
			this.b = b;

		}

		public List<String> getIDsNotFound() {
			return a;
		}

		public Integer getNumberIDsFound() {
			return b;
		}

	};

	private ReturnIDs tblCreator() throws Exception {
		postJobMessage("Creating .tbl file.");
		SubmitterJobParameters parameters = getParameters();
		deleteFile(parameters);
		List<Annotation> annotations = new ArrayList<Annotation>();
		IProject project = getInput(SubmitterCompleteJobMetadata.INPUT_PROJECT);
		Iterator<ILightSequence> iterator = project.onlySelectedSequencesIterator(ItemsOrderList.emptyList());
		File originFasta = new File(parameters.fastatFile.getValue());
		String newFasta = parameters.outputDir.getValue() + Path.SEPARATOR
				+ FileUtils.filenameWithoutExtension(parameters.fastatFile.getValue()) + ".fsa";
		File newFastaFile = new File(newFasta);
		Files.copy(originFasta.toPath(), newFastaFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		while (iterator.hasNext() && !isCanceled()) {
			String geneName = "hypothetical protein";
			String productName = geneName;
			ILightSequence sequence = iterator.next();
			if (sequence.hasConditions(SeqCondImpl.COND_HAS_BLAST_RESULT, SeqCondImpl.COND_HAS_MAPPING_RESULT,
					SeqCondImpl.COND_HAS_ANNOT_RESULT)) {
				Double seqEval = sequence.getBlastOutput().getTopHit().getTopHsp().get(HspKeys.EVALUE);
				Double seqCov = sequence.getBlastOutput().getTopHit().getHspHitCoverage();
				Double seqSim = sequence.getBlastOutput().getTopHit().getSimilarity();
				double eValue = new BigDecimal(parameters.eVal.getValue()).doubleValue();

				if ((seqEval.compareTo(eValue) <= 0) && (seqCov.compareTo(parameters.coverage.getValue()) >= 0)
						&& (seqSim.compareTo(parameters.sim.getValue()) >= 0)) {
					// Obtain the geneName from NCBI datbase
					Object geneID = sequence.getBlastOutput().getTopHit().getGis();
					System.out.println(geneID);
					String GeneProd = RetrieveGeneFromNCBI.getNames(geneID);
					System.out.println(GeneProd);

					// String[] name = GeneProd.split("\\$");
					// String ncbiGeneName = name[0];
					// String ncbiProdName = name[1];
					if (GeneProd != "" && GeneProd != "hypothetical protein" && GeneProd != "$") {
						geneName = GeneProd;
						// geneName = ncbiGeneName;
						// productName = ncbiProdName;

					} else {
						geneName = getGeneName(
								sequence.getGoMappings().getGoMappings((sequence.getBlastOutput().getTopHit())));
					}
					if (sequence.hasConditions(SeqCondImpl.COND_HAS_MANUAL_ANNOT_RESULT)) {
						geneName = sequence.getDescription();

					}

				}
				List<String> goIds = getSequenceAnnotationGos(sequence);
				List<String> ecCodes = getSequenceEnzymes(sequence);
				Annotation annotation = new Annotation(sequence.getName(), sequence.getDescription(), geneName, goIds,
						ecCodes);
				annotations.add(annotation);

			} else if (sequence.hasConditions(SeqCondImpl.COND_HAS_BLAST_RESULT)) {

				geneName = guessGeneName(sequence.getDescription());
				if (sequence.hasConditions(SeqCondImpl.COND_HAS_MANUAL_ANNOT_RESULT)) {
					geneName = sequence.getDescription();

				}
				List<String> goIds = Collections.emptyList();
				List<String> ecCodes = Collections.emptyList();
				Annotation annotation = new Annotation(sequence.getName(), sequence.getDescription(), geneName, goIds,
						ecCodes);
				annotations.add(annotation);
			} else {
				geneName = "hypothetical protein";
				if (sequence.hasConditions(SeqCondImpl.COND_HAS_MANUAL_ANNOT_RESULT)) {
					geneName = sequence.getDescription();

				}
				List<String> goIds = Collections.emptyList();
				List<String> ecCodes = Collections.emptyList();
				Annotation annotation = new Annotation(sequence.getName(), sequence.getDescription(), geneName, goIds,
						ecCodes);
				annotations.add(annotation);
			}
			worked(1);
		}

		IGODag goDag = getInput(SubmitterCompleteJobMetadata.GO_DAG);
		Map<String, Gene> features = readGff(parameters);
		String fileName = parameters.fastatFile.getValue();
		File fsaFile = new File(fileName);
		String fsaName = fsaFile.getName();
		String tblPath = parameters.outputDir.getValue();
		String tblName = FileUtils.filenameWithoutExtension(fsaName);
		String tblPathName = tblPath + File.separator + tblName + ".tbl";

		final String TAB = "\t";
		final String TAB_3 = TAB + TAB + TAB;
		Integer geneCounter = 0;
		List<String> missingIds = new ArrayList<String>();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(tblPathName, true))) {
			int locusTagCounter = 1;
			String reference = "";
			for (Annotation annotation : annotations) {
				if (isCanceled()) {
					break;
				}
				String locusTag = parameters.locusTag.getValue() + locusTagCounter++;
				Gene gene = features.get(annotation.name);

				if (gene != null && !reference.equals(gene.header)) {
					reference = gene.header;
					bw.write(">feature " + reference);
					bw.newLine();
				}
				if (gene == null) {
					missingIds.add(annotation.name);
					// postJobMessage(annotation.name + " features not found in
					// the " + reference + ".gff file");
					log.warn("{} feature not found in the .gff file", annotation.name);
					continue;
				}
				if (gene.isReverseStrand) {
					geneCounter++;
					bw.write(gene.geneCoordenates.end + TAB + gene.geneCoordenates.start + TAB + "gene");
					bw.newLine();
				} else {
					geneCounter++;
					bw.write(gene.geneCoordenates.start + TAB + gene.geneCoordenates.end + TAB + "gene");
					bw.newLine();
				}

				String geneNAME = null;
				String productName = null;
				switch (parameters.geneName.getValue()) {
				case Hypothetical_protein:
					geneNAME = "hypothetical protein";
					productName = geneNAME;
					break;
				case Top_Blast_Hit:
					String[] names = annotation.geneName.split("\\$");
					if (names.length < 2) {
						geneNAME = names[0];
						productName = geneNAME;
					}else{
					geneNAME = names[0];
					productName = names[1];
					}
					if (geneNAME.equals("")){geneNAME = "hypothetical protein";}
					if (productName.equals("")){productName = "hypothetical protein";}
					// geneNAME = annotation.geneName;

					break;
				case SeqName:
					geneNAME = annotation.name;
					productName = geneNAME;
					break;
				default:
					break;
				}
				// bw.newLine();
				bw.write(TAB_3 + "gene" + TAB + geneNAME);
				bw.newLine();
				if (gene.isReverseStrand) {
					bw.write(gene.mRnaCoordenates.end + TAB + gene.mRnaCoordenates.start + TAB + "mRNA");
					bw.newLine();
				} else {
					bw.write(gene.mRnaCoordenates.start + TAB + gene.mRnaCoordenates.end + TAB + "mRNA");
					bw.newLine();
				}
				bw.write(TAB_3 + "product" + TAB + productName);
				bw.newLine();
				if (gene.isReverseStrand) {
					for (int i = gene.cdss.size() - 1; i >= 0; i--) {
						Cds cds = gene.cdss.get(i);
						bw.write(cds.coordenates.end + TAB + cds.coordenates.start
								+ (i == gene.cdss.size() - 1 ? TAB + "CDS" : ""));
						bw.newLine();
					}
				} else {
					for (int i = 0; i < gene.cdss.size(); i++) {
						Cds cds = gene.cdss.get(i);
						bw.write(cds.coordenates.start + TAB + cds.coordenates.end + (i == 0 ? TAB + "CDS" : ""));
						bw.newLine();
					}
				}
				bw.write(TAB_3 + "product" + TAB + productName);
				bw.newLine();
				bw.write(TAB_3 + "protein_id" + TAB + "gln|" + parameters.labID.getValue() + "|" + locusTag);
				bw.newLine();
				bw.write(TAB_3 + "transcript_id" + TAB + "gln|" + parameters.labID.getValue() + "|" + "mrna."
						+ locusTag);
				bw.newLine();
				for (String goId : annotation.goIds) {
					if (goDag.containsGOId(goId)) {
						GONode goNode = goDag.getNodeById(goId);
						bw.write(TAB_3 + "GO_" + getDomain(goNode) + TAB + goNode.getName() + "|"
								+ goNode.getGoId().substring(3) + "||IEA");
						bw.newLine();

					}
				}
				for (String ecCode : annotation.ecCodes) {
					if (StringUtils.countMatches(ecCode, ".") == 3) {
						bw.write(TAB_3 + "EC_number" + TAB + ecCode.substring(3));
						bw.newLine();

					} else if (StringUtils.countMatches(ecCode, ".") == 2) {
						bw.write(TAB_3 + "EC_number" + TAB + ecCode.substring(3) + ".-");
						bw.newLine();

					}
				}
				bw.write(TAB_3 + "note" + TAB + annotation.description.replace("fragment", ""));
				bw.newLine();
				bw.write(TAB_3 + "codon_start" + TAB + "0");
				bw.newLine();

			}
			bw.flush();
			bw.close();
			if (missingIds.size() > 0) {
				postApplicationMessage("Number of sequences not found: " + missingIds.size());
			} else if (missingIds.size() <= 100) {
				postJobMessage("The following sequences have not been found on the gff:");
				for (String missedId : missingIds) {
					postJobMessage(missedId);
				}

			}

		}
		return new ReturnIDs(missingIds, geneCounter);
	}

	private static List<String> getSequenceAnnotationGos(ILightSequence sequence) {
		if (!sequence.hasConditions(SeqCondImpl.COND_HAS_ANNOT_RESULT)) {
			return Collections.emptyList();
		}
		@SuppressWarnings("unchecked")
		Vector<String> annotgos = (Vector<String>) sequence.getAnnotr().get(NameDef.ANNOTRESULT_GOACC);
		return annotgos;
	}

	private static List<String> getSequenceEnzymes(ILightSequence sequence) {
		if (!sequence.hasConditions(SeqCondImpl.COND_HAS_ENZYMES)) {
			return Collections.emptyList();
		}
		@SuppressWarnings("unchecked")
		Vector<String> enzymes = (Vector<String>) sequence.getAnnotr().get(NameDef.ANNOTRESULT_ENZYME_CODE);
		return enzymes;
	}

	private String getGeneName(GoMappings goMappings) {

		for (GoMapping goMapping : goMappings) {
			if (goMapping.containsKey(GoMappingKeys.GN)) {
				return goMapping.get(GoMappingKeys.GN);
			}
		}
		return "hypothetical protein";
	}

	private B2GHtml summary(SubmitterJobParameters parameters, ReturnIDs Ids)
			throws FileNotFoundException, IOException {
		StringBuilder summaryFile = new StringBuilder();
		summaryFile.append(parameters.outputDir.getValue() + File.separator + "errorsummary.val");
		File valFile = new File(summaryFile.toString());

		StringBuilder onlyErrors = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(valFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.replace("ERROR", "<b>Error</b>");
				onlyErrors.append(line + "\n");

			}

		}
		File html = FileUtils.getFileFromBundle(getClass(), "/res/summary.html");
		StringBuilder errorSummary = new StringBuilder();

		File tblPath = new File(parameters.outputDir.getValue());
		// File fsaName = new File(parameters.fastatFile.getValue());
		String tblName = FileUtils.filenameWithoutExtension(parameters.fastatFile.getValue());
		String sqnPathName = tblPath.toString() + File.separator + tblName + ".sqn";
		File sqnFile = new File(sqnPathName);
		try (BufferedReader br = new BufferedReader(new FileReader(html))) {
			String line;
			final String PROJECT = "$PROJECT$";
			final String FILENAME = "$FILENAME$";
			final String PATH = "$pathtofiles$";
			final String IMAGE = "$IMAGE$";
			final String ANNOTATED_SEQ = "$ANNOTATED_SEQ$";
			final String ERRORS = "$errorsummary$";
			final String NUM_NOT_ANNOTATED = "$NUM_NOT_ANNOTATED$";
			final String NOT_ANNOTATED = "$NOT_ANNOTATED$";
			final String URL = "$URL$";
			final String WEBNAME = "$WEBNAME$";
			while ((line = br.readLine()) != null) {
				if (line.contains(PROJECT)) {
					line = line.replace(PROJECT,
							((IB2GObjectProject) getInput(SubmitterCompleteJobMetadata.INPUT_PROJECT)).getName());
				}
				if (line.contains(PATH)) {
					line = line.replace(PATH, parameters.outputDir.getValue());
				}
				if (line.contains(FILENAME)) {
					line = line.replace(FILENAME, tblName);
				}
				if (line.contains(IMAGE)) {
					final Bundle bundle = Platform.getBundle("com.biobam.blast2go.apps.submitter");
					java.net.URL entry = bundle.getEntry("/res/ncbi-logo.png");

					String ncbiImg = FileUtils.imageToDataURI(entry);
					line = line.replace(IMAGE, "\""+ncbiImg+"\"");
				}
				if (line.contains(ANNOTATED_SEQ)) {
					line = line.replace(ANNOTATED_SEQ, Ids.getNumberIDsFound().toString());
				}
				if (line.contains(NUM_NOT_ANNOTATED)) {
					line = line.replace(NUM_NOT_ANNOTATED, Integer.toString(Ids.getIDsNotFound().size()));
				}
				if (line.contains(URL)) {
					String url = null;
					String webName = null;
					switch (parameters.subType.getValue().getId()) {
					case "wgs":
						url = "https://submit.ncbi.nlm.nih.gov/subs/wgs/";
						webName = "WGS Submission Portal";

						break;
					case "single":
						url = "http://www.ncbi.nlm.nih.gov/LargeDirSubs/dir_submit.cgi";
						webName = "SequinMacroSend";
						break;
					case "genome":
						url = "http://www.ncbi.nlm.nih.gov/projects/GenomeSubmit/genome_submit.cgi";
						webName = "GenomesMacroSend Direct Submission Tool for Genomes Files";
						break;

					default:
						break;
					}
					line = line.replace(URL, url);
					line = line.replace(WEBNAME, webName);
				}
				if (line.contains(NOT_ANNOTATED)) {
					StringBuilder IDsNotFound = new StringBuilder();
					for (String id : Ids.getIDsNotFound()) {
						IDsNotFound.append(id + "<br>");
					}
					if (Ids.getIDsNotFound().size() > 0) {
						errorSummary.append("<p><u>Sequences not processed:</u> <br></p>");
					}
					line = line.replace(NOT_ANNOTATED, IDsNotFound);
				}
				if (Ids.getNumberIDsFound() == 0) {
					line = line.replace(ERRORS, "<b>ERROR:</b> No sequences have been processed.");
					errorSummary.append(line + "\n");
				} else if (line.contains(ERRORS) && (sqnFile.length() > 0)) {
					line = line.replace(ERRORS, onlyErrors);
					errorSummary.append(line + "\n");
				} else {
					line = line.replace(ERRORS,
							"<b> ERROR:</b> the .sqn file couldn't be created, please check the error report from the Job and Aplication messages.");
					errorSummary.append(line + "\n");
				}

			}
		}
		return B2GHtml.newInstance("GenBank Submission File Creation Results", errorSummary.toString()

		// new IBrowserProtocolHtmlExtension() {
		//
		// @Override
		// public B2GHtml treat(String arg0) {
		// Program.launch(arg0);
		// return null;
		// }
		//
		// @Override
		// public boolean canDo(String arg0) {
		// // wololo
		// return true;
		// }
		// }
		);

	}

	private Map<String, Gene> readGff(SubmitterJobParameters parameters) throws IOException, FileNotFoundException {
		Map<String, Gene> features = new HashMap<String, Gene>();

		List<String> fastaIdList = fastaIdFinder(parameters.fastatFile.getValue());

		Map<String, String> fsaIdToFileGff = new HashMap<String, String>();
		for (String file : parameters.gff3File.getValue()) {
			File gffFile = new File(file);
			if (gffFile.isDirectory()) {
				List<File> filesList = new ArrayList<File>();
				FileUtils.handlerDirectory(gffFile, "gff", filesList);
				for (File fileInDir : filesList) {
					fsaIdToFileGff.put(FileUtils.filenameWithoutExtension(fileInDir.getAbsolutePath()),
							fileInDir.getAbsolutePath());
				}
			} else {
				fsaIdToFileGff.put(FileUtils.filenameWithoutExtension(file), file);
			}

		}

		for (String id : fastaIdList) {
			if (fsaIdToFileGff.containsKey(id)) {
				File file = new File(fsaIdToFileGff.get(id));
				if (file.length() == 0) {
					postApplicationMessage("Error: .gff file is empty");
				}
				try (BufferedReader br = new BufferedReader(new FileReader(fsaIdToFileGff.get(id)))) {
					String line = null;

					Gene.Builder geneBuilder = null;
					String sequenceId = null;
					String header = null;
					while ((line = br.readLine()) != null) {

						header = id;
						if (line.startsWith("#")) {
							continue;
						}
						String[] split = line.split("\t");
						if (split.length < 7) {
							continue;
						}
						if (split[2].equalsIgnoreCase("gene")) {
							if (geneBuilder != null) {
								features.put(sequenceId, geneBuilder.build());
							}
							geneBuilder = new Gene.Builder(header);

							sequenceId = MyJobUtils.extractAttribute(split[8], parameters.tagName.getValue());

							geneBuilder.setGeneCoordenates(new Range(split[3], split[4]));
							if (split[6].equals("-")) {
								geneBuilder.setIsReverseStrand();
							}
						} else if (split[2].equals("mRNA")) {
							geneBuilder.setMRnaCoordenates(new Range(split[3], split[4]));
						} else if (split[2].equals("CDS")) {
							geneBuilder.addCds(new Cds(new Range(split[3], split[4])));
						}
					}
					if (geneBuilder != null) {
						features.put(sequenceId, geneBuilder.build());
					}

				} catch (Exception e) {

					postApplicationMessage("ERROR: Gff file incorrectly composed.");
				}
			}
		}
		return features;
	}

	private String getDomain(GONode goNode) {
		switch (goNode.getNamespace()) {
		case BIOLOGICAL_PROCESS:
			return "process";
		case CELLULAR_COMPONENT:
			return "component";
		case MOLECULAR_FUNCTION:
			return "function";
		default:
			return "";
		}
	}

	private String guessGeneName(String string) {
		String gene;
		String[] mayID = string.split(" ");
		if (StringUtils.isAllUpperCase(mayID[mayID.length - 1])) {
			gene = mayID[mayID.length - 1];
		} else {
			gene = "hypothetical protein";
		}
		return gene;
	}

	private List<String> fastaIdFinder(String path) {
		List<String> fastaID = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains(">")) {
					fastaID.add(line.substring(1));
				}
			}
		} catch (FileNotFoundException e) {
			postApplicationMessage("Fasta file not found");
			e.printStackTrace();
		} catch (IOException e) {
			postApplicationMessage("Fasta file not found");
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		{
			return fastaID;
		}
	}

	private void deleteFile(SubmitterJobParameters parameters) {
		boolean bool = false;
		String fileName = parameters.fastatFile.getValue();
		File fsaFile = new File(fileName);
		String fsaName = fsaFile.getName();
		String tblPath = fsaFile.getParent();
		String tblName = FileUtils.filenameWithoutExtension(fsaName);
		String tblPathName = tblPath + File.separator + tblName + ".tbl";
		File tblFile = new File(tblPathName);
		bool = tblFile.exists();
		if (bool) {
			tblFile.delete();
		}

	}

}
