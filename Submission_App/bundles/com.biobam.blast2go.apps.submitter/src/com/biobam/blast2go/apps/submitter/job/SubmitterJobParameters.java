package com.biobam.blast2go.apps.submitter.job;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.biobam.blast2go.api.job.parameters.Parameters;
import com.biobam.blast2go.api.job.parameters.key.ComplexListKey;
import com.biobam.blast2go.api.job.parameters.key.DoubleKey;
import com.biobam.blast2go.api.job.parameters.key.FileKey;
import com.biobam.blast2go.api.job.parameters.key.ListKey;
import com.biobam.blast2go.api.job.parameters.key.MultipleFileKey;
import com.biobam.blast2go.api.job.parameters.key.NoteKey;
import com.biobam.blast2go.api.job.parameters.key.StringKey;
import com.biobam.blast2go.api.job.parameters.key.additional.ElementFromStringGenerator;
import com.biobam.blast2go.api.job.parameters.key.additional.FileExtension;
import com.biobam.blast2go.api.job.parameters.key.additional.ListKeyOption;
import com.biobam.blast2go.api.job.parameters.key.validator.IB2GValidator;
import com.biobam.blast2go.api.job.parameters.key.validator.MultiplePathValidator;
import com.biobam.blast2go.api.job.parameters.key.validator.PathValidator;
import com.biobam.blast2go.api.job.parameters.key.validator.StringValidator;
import com.biobam.blast2go.api.job.parameters.keys.internal.ParameterKey;

public class SubmitterJobParameters extends Parameters {

	public SubmitterJobParameters() {
		add(notebioProject, locusTag, labID, subType, supplementaryLabel, setDate, noteDate, noteFiles, outputDir,
				fastatFile, tagName, gff3File, noteGff, geneName, eVal, sim, coverage, noteContactAuthor, email, fax,
				phone, notetAuthor, authorsList, consortium, noteAffiliation, researchInstitution, researchDepartement,
				street, city, state, country, postcode, title, method, assemblyName, version, technology, genomeCoverage);
	}

	// Project---FirstPage============================================================================================================================

	public NoteKey notebioProject = NoteKey.builder(getBaseName(".notebioProject"))
			.setDescription(
					"Please register your project and proposed locus_tag prefix on the NCBI BioProject registration page prior to preparing your submission to GenBank."
							+ "\n\nBioProject website: https://submit.ncbi.nlm.nih.gov/subs/bioproject/")
			.build();

	public StringKey locusTag = StringKey.builder(getBaseName(".locusTag"), "").setName("Locus Tag")
			.setDescription(
					"This is the locus_tag number suggested or assigned by the NCBI on the page: 'https://submit.ncbi.nlm.nih.gov/subs/bioproject/'."
							+ "If you are suggesting the locus_tag, have in mind: \n" + "- it may be unique.\n\n"
							+ "- should be 3-12 alphanumeric characters and the first character may not be a digit.\n\n"
							+ "- the locus_tag prefix is followed by an underscore and then an alphanumeric identification number that is unique within the given genome.\n\n"
							+ "- the chromosome number can be embedded in the locus_tag, if desired, in the format Prefix_#g#####, where the first # is the chromosome number and ##### is the unique number of the gene (assigned during the submission).\n\n"
							+ "For example, Ath_4g00123 for a gene on chromosome 4 from A. thaliana.")
			.setMessage("Insert the BioProject ID").setValidator(StringValidator.notEmptyString()).build();

	public StringKey labID = StringKey.builder(getBaseName(".labID"), "").setName("Laboratory ID")
			.setDescription("This is the ID of your laboratory that you think will be unique (eg: SmithUCSD).  "
					+ "This identifier is saved with the record (in ASN.1 format), but it is not visible in the flatfile.")
			.setMessage("Insert the laboratory ID").setValidator(StringValidator.notEmptyString()).build();

	public enum SubType implements ListKeyOption {
		single("single", "One or a few nucleotide sequences"), genome("genome",
				"Complete Eukaryotic genome or chromosome"), wgs("wgs", "Incomplete genomes (WGS)");
		private String id;
		private String name;

		private SubType(String id, String name) {
			this.name = name;
			this.id = id;
		}

		@Override
		public String getDisplayName() {
			return name;
		}

		@Override
		public String getId() {
			return id;
		}
	}

	public ListKey<SubType> subType = ListKey
			.builder(getBaseName(".subType"), SubType.class, SubType.values(), SubType.single)
			.setName("Choose your type of submission").setDescription("Here you can choose the type of submission that fits your data type:\n\n"
					+ "- One or a few nucleotide sequences: this sequences correspond to a small dataset containing few sequences (less than a cromosome, or a chromosome on scaffold stage).\n\n"
					+ "- Complete eukaryotic genomes or chromosomes: this sequences correspond to complete data set without N's, conforming a chromosome or a whole genome.\n\n"
					+ "- Incomplete genomes (WGS):  genome assemblies of incomplete genomes or incomplete chromosomes of prokaryotes or eukaryotes that have been derived from data created by whole genome shotgun sequencing methods.").build();

	public StringKey setDate = StringKey.builder(getBaseName(".setDate"), DF.format(new Date()))
			.setName("Set the release date")
			.setDescription(
					"Insert the date when this sequences would be publicly available on the database.\n If the choosen date is the current date, the sequences will be available "
							+ "once the sequences are submited.")
			.setMessage("Insert the date (dd/mm/yyyy)").setValidator(StringValidator.notEmptyString())
			.setValidator(new IB2GValidator<String>() {
				@Override
				public IStatus validate(String string) {
					try {
						DF.parse(string);
						return Status.OK_STATUS;
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return new Status(Status.ERROR, "submit", "Date not valid, please use: dd/MM/yyyy");
				}

			}).build();
	public NoteKey noteDate = NoteKey.builder(getBaseName(".notedate"))
			.setDescription(
					"<b>Note:<b> If the choosen date is the current date, the sequences will be available once the sequences are submited and validated.")
			.build();

	public static final DateFormat DF = new SimpleDateFormat("dd/MM/yyyy");

	public NoteKey noteLabel = NoteKey.builder(getBaseName(".noteLabel"))
			.setDescription(
					"Optional source qualifiers attached to all your submitting sequences.")
			.build();

	SupplementaryLabel[] array = null;
	{
		List<SupplementaryLabel> labels = new ArrayList<SupplementaryLabel>();
		labels.add(new SupplementaryLabel("acronym", ""));
		labels.add(new SupplementaryLabel("altitude", ""));
		labels.add(new SupplementaryLabel("anamorph", ""));
		labels.add(new SupplementaryLabel("authority", ""));
		labels.add(new SupplementaryLabel("bio-material", ""));
		labels.add(new SupplementaryLabel("biotype", ""));
		labels.add(new SupplementaryLabel("biovar", ""));
		labels.add(new SupplementaryLabel("breed", ""));
		labels.add(new SupplementaryLabel("cell-line", ""));
		labels.add(new SupplementaryLabel("cell-type", ""));
		labels.add(new SupplementaryLabel("chemovar", ""));
		labels.add(new SupplementaryLabel("chromosome", ""));
		labels.add(new SupplementaryLabel("clone", ""));
		labels.add(new SupplementaryLabel("clone-lib", ""));
		labels.add(new SupplementaryLabel("collected-by", ""));
		labels.add(new SupplementaryLabel("collection-date", ""));
		labels.add(new SupplementaryLabel("common", ""));
		labels.add(new SupplementaryLabel("country", ""));
		labels.add(new SupplementaryLabel("cultivar", ""));
		labels.add(new SupplementaryLabel("culture-collection", ""));
		labels.add(new SupplementaryLabel("dev-stage", ""));
		labels.add(new SupplementaryLabel("ecotype", ""));
		labels.add(new SupplementaryLabel("endogenous-virus-name", ""));
		labels.add(new SupplementaryLabel("forma", ""));
		labels.add(new SupplementaryLabel("forma-specialis", ""));
		labels.add(new SupplementaryLabel("fwd-PCR-primer-name", ""));
		labels.add(new SupplementaryLabel("fwd-PCR-primer-seq", ""));
		labels.add(new SupplementaryLabel("gcode", ""));
		labels.add(new SupplementaryLabel("genotype", ""));
		labels.add(new SupplementaryLabel("group", ""));
		labels.add(new SupplementaryLabel("haplogroup", ""));
		labels.add(new SupplementaryLabel("haplotype", ""));
		labels.add(new SupplementaryLabel("host", ""));
		labels.add(new SupplementaryLabel("identified-by", ""));
		labels.add(new SupplementaryLabel("isolate", ""));
		labels.add(new SupplementaryLabel("isolation-source", ""));
		labels.add(new SupplementaryLabel("lab-host", ""));
		labels.add(new SupplementaryLabel("lat-lon", ""));
		labels.add(new SupplementaryLabel("linkage-group", ""));
		labels.add(new SupplementaryLabel("map", ""));
		labels.add(new SupplementaryLabel("mating-type", ""));
		labels.add(new SupplementaryLabel("note", ""));
		labels.add(new SupplementaryLabel("organism", ""));
		labels.add(new SupplementaryLabel("pathovar", ""));
		labels.add(new SupplementaryLabel("plasmid-name", ""));
		labels.add(new SupplementaryLabel("plastid-name", ""));
		labels.add(new SupplementaryLabel("pop-variant", ""));
		labels.add(new SupplementaryLabel("rev-PCR-primer-name", ""));
		labels.add(new SupplementaryLabel("rev-PCR-primer-seq", ""));
		labels.add(new SupplementaryLabel("segment", ""));
		labels.add(new SupplementaryLabel("serogroup", ""));
		labels.add(new SupplementaryLabel("serotype", ""));
		labels.add(new SupplementaryLabel("serovar", ""));
		labels.add(new SupplementaryLabel("sex", ""));
		labels.add(new SupplementaryLabel("specimen-voucher", ""));
		labels.add(new SupplementaryLabel("strain", ""));
		labels.add(new SupplementaryLabel("sub-species", ""));
		labels.add(new SupplementaryLabel("subclone", ""));
		labels.add(new SupplementaryLabel("subgroup", ""));
		labels.add(new SupplementaryLabel("substrain", ""));
		labels.add(new SupplementaryLabel("subtype", ""));
		labels.add(new SupplementaryLabel("synonym", ""));
		labels.add(new SupplementaryLabel("teleomorph", ""));
		labels.add(new SupplementaryLabel("tissue-lib", ""));
		labels.add(new SupplementaryLabel("tissue-type", ""));
		labels.add(new SupplementaryLabel("type", ""));
		labels.add(new SupplementaryLabel("variety", ""));

		array = new SupplementaryLabel[labels.size()];
		labels.toArray(array);
	}

	public MultipleListKeyObject supplementaryLabel = MultipleListKeyObject
			.builder(getBaseName(".supplementaryLabel1"), new IListKeyObjectParser<ListKeyObject>() {

				public ListKeyObject parseFromString(String stringValue) {
					String[] splitted = stringValue.split("/");
					if (splitted.length > 0) {
						if (splitted.length > 1) {
							return new SupplementaryLabel(splitted[0], splitted[1]);
						} else {
							return new SupplementaryLabel(splitted[0], "");

						}
					}
					return null;

				}
			}, array).setName("supplementaryLabel").setDescription("supplementaryLabel").build();

	// Files-SecondPage====================================================================================================================================

	public NoteKey noteFiles = NoteKey.builder(getBaseName(".noteFiles"))
			.setDescription(
					"To submit the chromosome or the genome to the NCBI, it's mandatory that the fasta file and the feature file (.tbl) have the same name."
							+ "Plus the fasta file, the feature file and the authors information must be in the same folder. The working directory will be the same of the fasta file.")
			.build();

	public FileKey outputDir = FileKey.builder(getBaseName(".outputDir")).setName("Set the output directory")
			.setValidator(PathValidator.existingFolder()).build();

	public FileKey fastatFile = FileKey.builder(getBaseName(".fastatFile")).setName("Reference genome (fasta file)")
			.setDescription(
					"The fasta file containing the sequence of the chromosome, or the chromosomes sequences of the genome."
							+ "The .fsa files can have up to 10,000 sequences per file. Larger submissions need to be split into multiple files. "
							+ "Submit only contigs >199nt. "
							+ "Remove any Ns from the beginning or end of each sequence. \n"
							+ "Important: the name of the entry must be the same as the .gff defline. "
							+ "\neg: \">A.thalianaCh2, group GND, whole genome shotgun sequence\" entry from fasta file needs the\n"
							+ "\"A.thalianaCh2, group GND, whole genome shotgun sequence.gff\" file.")
			.addFileFilter(FileExtension.create("*.fasta;*.fsa;*.fa;*.fna;*.fas",
					"Fasta file (.fasta, .fsa, .fa, .fna, .fas)"))
			.addFileFilter(FileExtension.ALL_FILES).setMessage("Choose file").setValidator(PathValidator.existingFile())
			.build();

	public MultipleFileKey gff3File = MultipleFileKey.builder(getBaseName(".gff3File"))
			.setName("Genome annotation (gff file)")
			.setDescription("A coordinate file containing the position of each gene on the chromosome"
					+ "Important: the name of the entry must be the same as the .gff defline. "
					+ "\neg: \">A.thalianaCh2, group GND, whole genome shotgun sequence\" entry from fasta file needs the\n"
					+ "\"A.thalianaCh2, group GND, whole genome shotgun sequence.gff\" file.")
			.addFileFilter(FileExtension.create("*.gff", "GFF file")).setMessage("Choose file")
			.setValidator(MultiplePathValidator.existingPaths()).addFileFilter(FileExtension.ALL_FILES).build();

	public NoteKey noteGff = NoteKey.builder(getBaseName(".noteGff"))
			.setDescription("Note: the name of the .gff file must be the same as the entry of the fasta file.").build();

	public StringKey tagName = StringKey.builder(getBaseName(".tagname"), "seqName").setName("Feature ID of annotation")
			.setDescription("Insert the tag name who will link the Blast2GO seqname with the gff feature. "
					+ "\neg: Here the tagName will be 'seqname', and A.thaliana_chr3_prot_1 correspond to the Blast2GO SeqName.\n"
					+ "A.thalianaCh3	AUGUSTUS	gene	542	1402	.	+	.	seqName=A.thaliana_chr3_prot_1;Name=1")
			.setMessage("eg: seqName; ID; B2GOname").setValidator(StringValidator.notEmptyString()).build();

	public enum GeneName implements ListKeyOption {
		Top_Blast_Hit("Top Blast Hit", "Top_Blast_Hit"), SeqName("Sequence Name",
				"SeqName"), Hypothetical_protein("hypothetical protein", "Hypothetical_protein");

		String name;
		String id;

		private GeneName(String name, String id) {
			this.name = name;
			this.id = id;
		}

		@Override
		public String getDisplayName() {
			return name;
		}

		@Override
		public String getId() {
			return id;
		}

	}

	public ListKey<GeneName> geneName = ListKey
			.builder(getBaseName(".geneName"), GeneName.class, GeneName.values(), GeneName.Top_Blast_Hit)
			.setName("Define gene names for submission:")
			.setDescription(" Choose if the genes names for your new annotated genes will be:\n" + "- His SeqName (must not contain underscores)\n"
					+ "- \"hypothetical protein\"\n" + "- Retrieved from the Top Blast Hit (must follow the UniProt-Protein Naming Guidelines)\n"
					+ "If the Top Blast Hit is selected, the blast resulst must reach the tresholds of e-value, similarity and coverage sets."
					+ "If this tresholds are not reach, the gene name is set to \"hypothetrical protein\"")
			.build();

	// public DoubleKey eVal = DoubleKey.builder(getBaseName(".eval"), new
	// BigDecimal("1E-3").doubleValue())
	// .setName("Insert the e-value treshold").setDescription("The e-value
	// treshold. Inferior values than '1E-3' will be shown as '0'; this field
	// accepts exponential numbers as '1E-72'.").build();
	//

	public StringKey eVal = StringKey.builder(getBaseName(".eval"), "1E-6").setValidator(new IB2GValidator<String>() {

		@Override
		public IStatus validate(String arg0) {
			try {
				new BigDecimal(arg0).doubleValue();
				return ValidationStatus.ok();
			} catch (NumberFormatException e) {
				return ValidationStatus.error("Input not valid.");
			}
		}
	}).setName("Insert the e-value treshold")
			.setDescription("The e-value treshold this field accepts exponential numbers as '1E-72'.").build();
	public DoubleKey coverage = DoubleKey.builder(getBaseName(".coverage"), (double) 100)
			.setName("Insert the HSP hit coverage treshold").setDescription("The coverage treshold").build();
	public DoubleKey sim = DoubleKey.builder(getBaseName(".sim"), (double) 99).setName("Insert the similarity treshold")
			.setDescription("The similarity treshold").build();

	// Contact Author
	// data---ThirdPage============================================================================================================================
	// public NoteKey noteContactAuthor =
	// NoteKey.builder(getBaseName(".noteContactAuthor"))
	// .setDescription(
	// "The contact person responsible for the submission. Contact information
	// will not be publicly available but visible to NCBI staff.")
	// .build();

	public StringKey authorFNameC = StringKey.builder(getBaseName(".authorFName"), "").setName("Author first name")
			.setDescription("Insert the first name of the author")
			.setMessage("Insert the fisrt name of the first author").setValidator(StringValidator.notEmptyString())
			.build();

	public StringKey authorLNameC = StringKey.builder(getBaseName(".authorLName"), "").setName("Author last name")
			.setDescription("Insert the last name of the author").setMessage("Insert the last name of the first author")
			.setValidator(StringValidator.notEmptyString()).build();

	public StringKey initialsC = StringKey.builder(getBaseName(".initials"), "").setName("Author's initials")
			.setDescription("Insert the author's initials").setMessage("Insert the author's initials")
			.setValidator(StringValidator.notEmptyString()).build();

	// Authors
	// data---FourthPage===================================================================================================
	// public List<AuthorEntry> authorsList = new ArrayList<AuthorEntry>();
	public NoteKey noteContactAuthor = NoteKey.builder(getBaseName(".noteContactAuthor"))
			.setDescription(
//					"Contact person may not belong to the institution and will not be publicly available."
					"Contact person may not belong to the institution, his details will not be published."
//					 "Contact details will not be public. Contact person may not belong to the institution."
					)
			.build();
	public StringKey email = StringKey.builder(getBaseName(".email"), "").setName("Contact email")
			.setDescription("Insert the contact email in case the NCBI needs to contact you")
			.setMessage("Insert the contact email").setValidator(StringValidator.validEmail()).build();

	public StringKey fax = StringKey.builder(".fax", "").setName("Fax").setDescription("Insert the fax number")
			.setMessage("Insert fax number").setValidator(StringValidator.create("[0-9]{0,15}", "Incorrect fax number"))
			.build();

	public StringKey phone = StringKey.builder(".phone", "").setName("Phone number")
			.setDescription("Insert the contact phone number in case the NCBI needs to contact you")
			.setMessage("Insert phone number")
			.setValidator(StringValidator.create("[0-9]{6,15}", "Incorrect phone number")).build();
	public MultipleListKeyObject authorsList = MultipleListKeyObject
			.builder(getBaseName(".authorsList"), new IListKeyObjectParser<ListKeyObject>() {

				@Override
				public ListKeyObject parseFromString(String stringValue) {
					String[] splitted = stringValue.split("/");
					if (splitted.length > 2) {
						return new AuthorEntry(splitted[0], splitted[1], splitted[2]);

					}
					return null;

				}
			}).setName("Author list").setDescription("Author list").build();

	public NoteKey notetAuthor = NoteKey.builder(getBaseName(".noteAuthor"))
			.setDescription(
					"Persons who should receive scientific credit for the generation of this sequences.")
			.setName("Authors Data").build();

	public StringKey consortium = StringKey.builder(getBaseName(".consortium"), "").setName("Consortium")
			.setMessage("eg: International Wheat Genome Sequencing Consortium")
			.setDescription("The authors belonging to the consortium should not appear listed as authors. "
					+ "External consortium authors must.")
			.build();

	// Affiliation---FifthhPage============================================================================================================================
	public NoteKey noteAffiliation = NoteKey.builder(getBaseName(".notetAffiliation"))
			.setDescription("Information about the institution where de sequencing was performed. ")

			.build();
//	public NoteKey noteAffiliation = NoteKey.builder(getBaseName(".notetAffiliation"))
//			.setDescription("Information about the institution where de sequencing was performed. "
//					+ "This is not necessarily the same as the workplace of the person described on contact.")
//			.build();
	public StringKey researchInstitution = StringKey.builder(getBaseName(".researchInstitution"), "")
			.setName("Research institution").setDescription("Insert the research institution")
			.setMessage("Insert the research institution").setValidator(StringValidator.notEmptyString()).build();

	public StringKey researchDepartement = StringKey.builder(getBaseName(".researchDepartement"), "")
			.setName("Research departement").setDescription("Insert the research departement")
			.setMessage("Insert the research departement").setValidator(StringValidator.notEmptyString()).build();

	public StringKey street = StringKey.builder(getBaseName(".street"), "").setName("Street")
			.setDescription("Street of the research institution").setMessage("Insert the street")
			.setValidator(StringValidator.notEmptyString()).build();

	public StringKey city = StringKey.builder(getBaseName(".city"), "").setName("City")
			.setDescription("City of the research institution").setMessage("Insert the city")
			.setValidator(StringValidator.notEmptyString()).build();

	public StringKey state = StringKey.builder(getBaseName(".state"), "").setName("State")
			.setDescription("State of the research institution").setMessage("Insert the state")
			.setValidator(StringValidator.notEmptyString()).build();

	public StringKey country = StringKey.builder(getBaseName(".country"), "").setName("Country")
			.setDescription("Country of the research institution").setMessage("Insert the country")
			.setValidator(StringValidator.notEmptyString()).build();

	public StringKey postcode = StringKey.builder(getBaseName(".postcode"), "").setName("Zip / Postal code")
			.setDescription("Postal code of the research institution").setMessage("Insert the postal code")
			.setValidator(StringValidator.notEmptyString()).build();

	public StringKey title = StringKey.builder(getBaseName(".title"), "").setName("Title of the manuscript")
			.setDescription("Title of the manuscript, could be modified.").setMessage("Insert title")
			.setValidator(StringValidator.notEmptyString()).build();

	// Assembly
	// data---SixthPage============================================================================================================================
	public StringKey method = StringKey.builder(getBaseName(".method"), "").setName("Assembly method")
			.setDescription("The program or algorithms used to achieve the assembly of the genome.")
			.setMessage("eg:Velvet, SOAPdenovo,...").setValidator(StringValidator.notEmptyString()).build();

	public StringKey version = StringKey.builder(getBaseName(".version"), "")
			.setName("Program version").setDescription("Version number of the assembler. ")
			.setMessage("e.g.: 3.4").build();

	public StringKey assemblyName = StringKey.builder(getBaseName(".assemblyName"), "").setName("Assembly name")
			.setDescription("The name of the your assembly. eg: LoxAfr3.0").setMessage("eg: LoxAfr3.0")
			.setValidator(StringValidator.notEmptyString()).build();

	public StringKey genomeCoverage = StringKey.builder(getBaseName(".genomeCoverage"), "").setName("Genome coverage")
			.setDescription("The coverage of your genome.").setMessage("eg: 12x, 76x, ...")
			.setValidator(StringValidator.create("[0-9]{1,3}x$", "Bad format coverage, please write it as 76x.")).build();

	public ComplexListKey<String> technology =  (ComplexListKey<String>) ComplexListKey
			.builder(getBaseName(".technology"), String.class,
					Arrays.asList("ABI3730", "Sanger", "454", "Illumina", "Illumina GAII", "Illumina GAIIx",
							"Illumina HiSeq", "Illumina MiSeq", "Illumina NextSeq", "Illumina NextSeq 500", "SOLiD",
							"PacBio", "IonTorrent", "Helicos", "CompleteGenomics", "Other (write here)"),
					"Illumina HiSeq")
			.setStringToElementCallBack(ElementFromStringGenerator.STRING_TO_STRING)
			.setDescription("The technology used to perform the assembly. You can type another one if it is not in the list.")
			.setName("Sequencing technology")
			.build();

	@Override
	public boolean isEnabled(ParameterKey<?> parameterKey) {
		if (parameterKey == method) {
			return subType.getValue().equals(SubType.wgs);
		}else if (parameterKey == assemblyName) {
			return subType.getValue().equals(SubType.wgs);
		}else if (parameterKey == version) {
			return subType.getValue().equals(SubType.wgs);
		}else if (parameterKey == genomeCoverage) {
			return subType.getValue().equals(SubType.wgs);
		}else if (parameterKey == technology) {
			return subType.getValue().equals(SubType.wgs);
		}else if (parameterKey == eVal) {
			return geneName.getValue().equals(GeneName.Top_Blast_Hit);
		}else if (parameterKey == sim) {
			return geneName.getValue().equals(GeneName.Top_Blast_Hit);
		}else if (parameterKey == coverage) {
			return geneName.getValue().equals(GeneName.Top_Blast_Hit);
		}
		return super.isEnabled(parameterKey);
	}

}
