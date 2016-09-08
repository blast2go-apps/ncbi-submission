package com.biobam.blast2go.apps.submitter.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.swt.program.Program;

import com.biobam.blast2go.api.datatype.basics.html.B2GHtml;
import com.biobam.blast2go.api.datatype.basics.html.IBrowserProtocolHtmlExtension;
import com.biobam.blast2go.api.job.IB2GProgressMonitor;
import com.biobam.blast2go.api.utils.B2GSystemProcess;
import com.biobam.blast2go.api.utils.B2GSystemProcess.Builder;
import com.biobam.blast2go.api.utils.B2GSystemProcess.ILogMessageHandler;
import com.biobam.blast2go.api.utils.FileUtils;

public class MyJobUtils {

	static void sbtParser(SubmitterJobParameters parameters, IB2GProgressMonitor monitor) {

		monitor.postJobMessage("Creating .sbt file.");
		try {

			// Beginning and Author
			// contact========================================================================
			StringBuilder tbl = new StringBuilder();
			String COM = "\"";
			String start = "Submit-block ::= {\n  contact {\n    contact {\n      name\n        name {\n";

			tbl.append(start);

			final String LNameContact = "$Lname$";
			final String FNameContact = "$Fname$";
			final String InitialsContact = "$F.M.I.$";
			String contact = "          last " + LNameContact + " ,\n          first " + FNameContact
					+ " ,\n          initials " + InitialsContact + " } ,\n";
			AuthorEntry contactAuthor = (AuthorEntry) parameters.authorsList.getValue().clone()[0];
			contact = contact.replace(LNameContact, COM + contactAuthor.getLastName() + COM);
			contact = contact.replace(FNameContact, COM + contactAuthor.getFirstName() + COM);
			contact = contact.replace(InitialsContact, COM + contactAuthor.getInitials() + COM);

			tbl.append(contact);

			// Affiliation=======================================================================================
			final String RInst = "$Research institution$";
			final String RDepa = "$Research departement$";
			final String City = "$City$";
			final String StreetAdd = "$Street address$";
			final String Mail = "$email$";
			final String Fax = "$fax$";
			final String Phones = "$phone$";
			final String Pcode = "$PostalCode$";
			final String State = "$State$";
			final String Country = "$Country$";
			String affilContact = "      affil\n        std {\n          affil " + RInst + " ,\n          div " + RDepa
					+ " ,\n          city " + City + " ,\n          sub " + State + " ," + "\n          country "
					+ Country + " ,\n          street " + StreetAdd + " ,\n          email " + Mail
					+ " ,\n          fax " + Fax + " ,\n          phone " + Phones + " ,\n          postal-code "
					+ Pcode + " } }";
			affilContact = affilContact.replace(RInst, COM + parameters.researchInstitution.getValue() + COM);
			affilContact = affilContact.replace(RDepa, COM + parameters.researchDepartement.getValue() + COM);
			affilContact = affilContact.replace(City, COM + parameters.city.getValue() + COM);
			affilContact = affilContact.replace(StreetAdd, COM + parameters.street.getValue() + COM);
			affilContact = affilContact.replace(Mail, COM + parameters.email.getValue() + COM);
			affilContact = affilContact.replace(Fax, COM + parameters.fax.getValue() + COM);
			affilContact = affilContact.replace(Phones, COM + parameters.phone.getValue() + COM);
			affilContact = affilContact.replace(Pcode, COM + parameters.postcode.getValue() + COM);
			affilContact = affilContact.replace(State, COM + parameters.state.getValue() + COM);
			affilContact = affilContact.replace(Country, COM + parameters.country.getValue() + COM);

			affilContact = affilContact.replace(RDepa, parameters.researchDepartement.getValue());
			tbl.append(affilContact);
			tbl.append(" } ,\n");

			// Authors=======================================================================================

			// adding the contact author

			final String LastName = "$Author$";
			final String FirstName = "$Another$";
			final String Initials = "$A.M.I.$";

			// not last author
			tbl.append("  cit {\n    authors {\n      names\n        std {\n");
			for (ListKeyObject option : parameters.authorsList.getValue()) {
				AuthorEntry author = (AuthorEntry) option;
				if (!author.getFirstName().trim().equals("")) {
					String Authors = "          {\n            name\n              name {\n                last "
							+ LastName + " ,\n                first " + FirstName + " ," + "\n                initials "
							+ Initials + " } } ,\n";
					Authors = Authors.replace(LastName, COM + author.getLastName() + COM);
					Authors = Authors.replace(FirstName, COM + author.getFirstName() + COM);
					Authors = Authors.replace(Initials, COM + author.getInitials() + COM);
					tbl.append(Authors);
				}

			}
			if (!parameters.consortium.getValue().trim().equals("")) {
				final String CONSORTIUM = "$CONSORTIUM$";
				String Consortium = "          {\n            name\n              consortium \"$CONSORTIUM$\" } } ,\n";
				Consortium = Consortium.replace(CONSORTIUM, parameters.consortium.getValue());
				tbl.append(Consortium);

			} else {

				tbl.delete(tbl.length() - 3, tbl.length());
				// add to the last author
				tbl.append(" } ,\n");
			}
			// Affiliation
			String affil = "      affil\n        std {\n          affil " + RInst + " ,\n          div " + RDepa
					+ " ,\n          city " + City + " ,\n          sub " + State + " ," + "\n          country "
					+ Country + " ,\n          street " + StreetAdd + " ,\n          postal-code " + Pcode + " } } ,\n";
			affil = affil.replace(RInst, COM + parameters.researchInstitution.getValue() + COM);
			affil = affil.replace(RDepa, COM + parameters.researchDepartement.getValue() + COM);
			affil = affil.replace(City, COM + parameters.city.getValue() + COM);
			affil = affil.replace(State, COM + parameters.state.getValue() + COM);
			affil = affil.replace(Country, COM + parameters.country.getValue() + COM);
			affil = affil.replace(StreetAdd, COM + parameters.street.getValue() + COM);
			affil = affil.replace(Pcode, COM + parameters.postcode.getValue() + COM);
			tbl.append(affil);

			// Release-Date=======================================================================================

			final String Year = "$year$";
			final String Month = "$month$";
			final String Day = "$day$";

			// if release today
			Calendar cal = Calendar.getInstance();
			Date date = SubmitterJobParameters.DF.parse(parameters.setDate.getValue());
			cal.setTime(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MMM dd");
			Date today = new Date();

			// if the date has a difference of 24h of the same day...
			// today.compareTo(date);

			if (today.getTime() - date.getTime() > 8.64e+7) {
				monitor.postJobMessage("WARNING:The release date cannot be earlier than today.");

			}

			if (dateFormat.format(today).equals(dateFormat.format(date))) {
				String year = String.valueOf(cal.get(Calendar.YEAR));
				String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
				String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
				String Date = "    date\n      std {\n        year " + Year + " ,\n        month " + Month
						+ " ,\n        day " + Day + " } } ,\n";
				Date = Date.replace(Year, year);
				Date = Date.replace(Month, month);
				Date = Date.replace(Day, day);
				tbl.append(Date);
			}

			if (!dateFormat.format(today).equals(dateFormat.format(date))) {
				SimpleDateFormat dayT = new SimpleDateFormat("dd");
				String dayToday = dayT.format(today);

				SimpleDateFormat monthT = new SimpleDateFormat("MM");
				String monthToday = monthT.format(today);

				SimpleDateFormat yearT = new SimpleDateFormat("yyyy");
				String yearToday = yearT.format(today);

				String Date = "    date\n      std {\n        year " + Year + " ,\n        month " + Month
						+ " ,\n        day " + Day + " } } ,\n";
				Date = Date.replace(Year, (CharSequence) yearToday);
				Date = Date.replace(Month, (CharSequence) monthToday);
				Date = Date.replace(Day, (CharSequence) dayToday);
				tbl.append(Date);

				String year = String.valueOf(cal.get(Calendar.YEAR));
				String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
				String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
				final String releaseYear = "$Ryear$";
				final String releaseMonth = "$Rmonth$";
				final String releaseDay = "$Rday$";
				String releaseDate = "  hup TRUE ,\n  reldate\n    std {\n      year " + releaseYear
						+ " ,\n      month " + releaseMonth + " ,\n      day " + releaseDay + " } ,\n";
				releaseDate = releaseDate.replace(releaseYear, year);
				releaseDate = releaseDate.replace(releaseMonth, month);
				releaseDate = releaseDate.replace(releaseDay, day);
				tbl.append(releaseDate);
			}

			final String Status = "$status$";
			String pub = "  subtype new }\nSeqdesc ::= pub {\n  pub {\n    gen {\n      cit " + Status
					+ " ,\n      authors {\n        names\n          std {\n";
			pub = pub.replace(Status, COM + "unpublished" + COM);
			tbl.append(pub);

			// not last author
			for (ListKeyObject option : parameters.authorsList.getValue()) {
				AuthorEntry author = (AuthorEntry) option;
				if (!author.getFirstName().trim().equals("")) {
					String Authors = "            {\n              name\n                name {\n                  last "
							+ LastName + " ,\n                  first " + FirstName + " ,"
							+ "\n                  initials " + Initials + " } } ,\n";
					Authors = Authors.replace(LastName, COM + author.getLastName() + COM);
					Authors = Authors.replace(FirstName, COM + author.getFirstName() + COM);
					Authors = Authors.replace(Initials, COM + author.getInitials() + COM);
					tbl.append(Authors);
				}
			}
			if (!parameters.consortium.getValue().trim().equals("")) {
				final String CONSORTIUM = "$CONSORTIUM$";
				String Consortium = "            {\n              name\n                consortium \"$CONSORTIUM$\" } } ,\n";
				Consortium = Consortium.replace(CONSORTIUM, parameters.consortium.getValue());
				tbl.append(Consortium);

			} else {

				tbl.delete(tbl.length() - 3, tbl.length());
				// add to the last author
				tbl.append(" } ,\n");
			}

			String affil2 = "        affil\n          std {\n            affil " + RInst + " ,\n            div "
					+ RDepa + " ,\n            city " + City + " ,\n            sub " + State + " ,"
					+ "\n            country " + Country + " ,\n            street " + StreetAdd
					+ " ,\n            postal-code " + Pcode + " } } ,\n";
			affil2 = affil2.replace(RInst, COM + parameters.researchInstitution.getValue() + COM);
			affil2 = affil2.replace(RDepa, COM + parameters.researchDepartement.getValue() + COM);
			affil2 = affil2.replace(City, COM + parameters.city.getValue() + COM);
			affil2 = affil2.replace(State, COM + parameters.state.getValue() + COM);
			affil2 = affil2.replace(Country, COM + parameters.country.getValue() + COM);
			affil2 = affil2.replace(StreetAdd, COM + parameters.street.getValue() + COM);
			affil2 = affil2.replace(Pcode, COM + parameters.postcode.getValue() + COM);
			tbl.append(affil2);

			final String Title = "$title$";
			String provisionalTitle = "      title " + Title + " } } }";
			provisionalTitle = provisionalTitle.replace(Title, COM + parameters.title.getValue() + COM);
			tbl.append(provisionalTitle);

			File sbtPath = new File(parameters.outputDir.getValue());
			String sbtName = File.separator + "Authors.sbt";
			String sbtPathName = sbtPath + sbtName;

			PrintWriter newFile = new PrintWriter(sbtPathName, "UTF-8");
			newFile.write(tbl.toString());
			newFile.close();
		}

		catch (Exception e) {
			monitor.postApplicationMessage("ERROR: the .sbt file cannot be created");

			e.printStackTrace();
		}

	}

	static void cmtParser(SubmitterJobParameters parameters, IB2GProgressMonitor monitor) {
		monitor.postJobMessage("Creating .asm file.");
		try {
			StringBuilder cmt = new StringBuilder();
			final String METHOD = "$METHOD$";
			final String VERS = "$VERSION$";
			final String NAME = "$NAME$";
			final String COVERAGE = "$COVERAGE$";
			final String TECH = "$TECH$";
			String TEMPLATE1 = "StructuredCommentPrefix	##Genome-Assembly-Data-START##\n" + "Assembly Method	"
					+ METHOD + VERS + "\n" + "Assembly Name	" + NAME + "\n";
			String TEMPLATE2 = "Genome Coverage	" + COVERAGE + "\n" + "Sequencing Technology	" + TECH;
			TEMPLATE1 = TEMPLATE1.replace(METHOD, parameters.method.getValue());
			TEMPLATE1 = TEMPLATE1.replace(NAME, parameters.assemblyName.getValue());
			if (!parameters.version.getValue().trim().equals("")) {
				String version = " v. " + parameters.version.getValue();
				TEMPLATE1 = TEMPLATE1.replace(VERS, version);
			} else {TEMPLATE1 = TEMPLATE1.replace(VERS,"");}
			cmt.append(TEMPLATE1);
			TEMPLATE2 = TEMPLATE2.replace(COVERAGE, parameters.genomeCoverage.getValue());
			TEMPLATE2 = TEMPLATE2.replace(TECH, parameters.technology.getValue());
			cmt.append(TEMPLATE2);

			File cmtPath = new File(parameters.outputDir.getValue());
			String cmtName = File.separator + "Comment.asm";
			String cmtPathName = cmtPath + cmtName;

			PrintWriter newFile = new PrintWriter(cmtPathName, "UTF-8");
			newFile.write(cmt.toString());
			newFile.close();

		} catch (Exception e) {
			monitor.postApplicationMessage("ERROR: the .sbt file cannot be created");

			e.printStackTrace();
		}
	}

	static void existingFilesAndFolders(SubmitterJobParameters parameters, IB2GProgressMonitor monitor) {
		// Check for gff files
		// gff unique file
		StringBuilder sb = new StringBuilder();
		for (String file : parameters.gff3File.getValue()) {
			File gffFile = new File(file);
			// gff folder
			if (gffFile.isDirectory()) {
				List<File> filesList = new ArrayList<File>();
				FileUtils.handlerDirectory(gffFile, "gff", filesList);
				for (File fileInDir : filesList) {
					if (!fileInDir.exists()) {
						sb.append(fileInDir.getName() + "\n");
						// monitor.setFinishMessage("ERROR: " + fileInDir + "
						// file not found");

					}
				}

			}
			if (!gffFile.exists()) {
				sb.append(gffFile.getName() + "\n");

			}
		}
		if (sb.length() > 0) {
			monitor.setFinishMessage("ERROR file(s) not found:\n" + sb.toString());
		}
	}

	static void tbl2asn(SubmitterJobParameters parameters, String b2gSubmitterExecutable, IB2GProgressMonitor monitor) {
		monitor.postJobMessage("Validating annotation via tbl2asn (v. 24.9). This may take several minutes.");
		File fastafile = new File(parameters.fastatFile.getValue());
		if (fastafile.length() == 0) {
			monitor.postApplicationMessage("Error: fasta file is empty");
		}
		try {
			BasicConfigurator.configure();

			// File WORKDIR = new File(parameters.outputDir.getValue());
			// String workPath = WORKDIR.getParent();
			StringBuilder comment = new StringBuilder();
			for (ListKeyObject option : parameters.supplementaryLabel.getValue()) {
				SupplementaryLabel label = (SupplementaryLabel) option;
				if (!label.getlabelValue().trim().equals("")) {
					comment.append("[" + label.getLabel() + "=" + label.getlabelValue() + "]");
				}
			}
			Builder processBuilder = B2GSystemProcess.builder(b2gSubmitterExecutable);
			processBuilder.addParameter("-t", "Authors.sbt").addParameter("-p", parameters.outputDir.getValue());
			comment.insert(0, "\"");
			comment.insert(comment.length(), "\"");
			processBuilder.addParameter("-j", comment.toString());

			List<String> fastaIdList = fastaIdFinder(parameters.fastatFile.getValue(), monitor);
			boolean multifasta = false;
			if (fastaIdList.size() > 1) {
				multifasta = true;
			}
			if (multifasta) {
				processBuilder.addParameter("-a", "s");
			}
			if ("single".equals(parameters.subType.getValue().getId())) {
				processBuilder.addParameter("-V", "vb");
			}
			if ("genome".equals(parameters.subType.getValue().getId())) {
				processBuilder.addParameter("-M", "n");
			}
			if ("wgs".equals(parameters.subType.getValue().getId())) {
//				processBuilder.addParameter("-V", "vbg");
				processBuilder.addParameter("-V", "vbg");
				processBuilder.addParameter("-w", "Comment.asm");
			}
			B2GSystemProcess process = processBuilder.addParameter("-Z", "Discrepancies.txt")
					.setDirectory(new File(parameters.outputDir.getValue()))
					.redirectOutputMessages(new ILogMessageHandler() {

						@Override
						public void handleLogMessage(String arg0) {
							System.out.println("output: " + arg0);
						}
					}).redirectErrorMessages(new ILogMessageHandler() {

						@Override
						public void handleLogMessage(String arg0) {
							monitor.postJobMessage(arg0);

						}
					}).build();
			try {

				process.execute();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static List<String> fastaIdFinder(String path, IB2GProgressMonitor monitor) {

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
			monitor.postApplicationMessage("Fasta file not found");
			e.printStackTrace();
		} catch (IOException e) {
			monitor.postApplicationMessage("Fasta file not found");
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

	public static class ReturnIDs {
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



	static B2GHtml summary(SubmitterJobParameters parameters, ReturnIDs Ids, String projectName)
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
		File html = FileUtils.getFileFromBundle(MyJobUtils.class, "/res/summary.html");
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
			final String ANNOTATED_SEQ = "$ANNOTATED_SEQ$";
			final String ERRORS = "$errorsummary$";
			final String NUM_NOT_ANNOTATED = "$NUM_NOT_ANNOTATED$";
			final String NOT_ANNOTATED = "$NOT_ANNOTATED$";
			while ((line = br.readLine()) != null) {
				if (line.contains(PROJECT)) {
					line = line.replace(PROJECT, projectName);
				}
				if (line.contains(PATH)) {
					line = line.replace(PATH, parameters.outputDir.getValue());
				}
				if (line.contains(FILENAME)) {
					line = line.replace(FILENAME, tblName);
				}
				if (line.contains(ANNOTATED_SEQ)) {
					line = line.replace(ANNOTATED_SEQ, Ids.getNumberIDsFound().toString());
				}
				if (line.contains(NUM_NOT_ANNOTATED)) {
					line = line.replace(NUM_NOT_ANNOTATED, Integer.toString(Ids.getIDsNotFound().size()));
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
		return B2GHtml.newInstance("GenBank Submission File Creation Results", errorSummary.toString(),
				new IBrowserProtocolHtmlExtension() {

					@Override
					public B2GHtml treat(String arg0) {
						Program.launch(arg0);
						return null;
					}

					@Override
					public boolean canDo(String arg0) {
						return true;
					}
				});

	}

	static String extractAttribute(String text, String key) {
		String[] split = text.split(";");
		Map<String, String> map = new HashMap<>();
		for (String element : split) {
			String[] split2 = element.split("=");
			map.put(split2[0], split2[1]);
		}
		return map.get(key);
	}

}
