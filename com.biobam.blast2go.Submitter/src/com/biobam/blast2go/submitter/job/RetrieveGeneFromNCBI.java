package com.biobam.blast2go.submitter.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.biobam.blast2go.preferences.proxy.ConnectionUtilities;

public class RetrieveGeneFromNCBI {
	private static final long CONNECTION_TIMEOUT_SECONDS = 10;
	private static final long READ_TIMEOUT_SECONDS = 15;

	static String getNames(Object allGeneIDs) throws MalformedURLException, IOException {
		String geneName = "";
		String protName = "";
		// final String HTML =
		// "http://www.ncbi.nlm.nih.gov/protein?cmd=Retrieve&dopt=GenPept&list_uids=";
		String[] geneIDs = allGeneIDs.toString().split(",");
		String geneID = geneIDs[0].replace("[", "").replace("]", "");
		// =============================

		URL ncbi_url;

		try {
			ncbi_url = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi");

			HttpURLConnection connection = null;
			OutputStream os = null;
			OutputStreamWriter writer = null;

			boolean success = false;

			connection = (HttpURLConnection) ConnectionUtilities.getUrlConnection(ncbi_url);
			connection.setConnectTimeout(
					(int) TimeUnit.MILLISECONDS.convert(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS));
			connection.setReadTimeout((int) TimeUnit.MILLISECONDS.convert(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS));
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			os = connection.getOutputStream();
			writer = new OutputStreamWriter(os);
			System.out.println(geneID);
			writer.write("db=protein&id=" + geneID + "&rettype=gb&retmode=xml");
			writer.flush();
			final int statusCode = connection.getResponseCode();
			//if (statusCode != 200){

			success = true;
			InputStream in = connection.getInputStream();
//			String decoded = Utilities.convertStreamToString(in);
//			System.out.println(decoded);
			os.flush();
			os.close();
			// Retrieve the gene name and protein name
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {
//
				if (line.contains("<GBQualifier_name>gene</GBQualifier_name>")) {
					line = br.readLine();
					if (line != null) {
					Pattern PAT = Pattern.compile("<GBQualifier_value>([^}]*)</GBQualifier_value>");
					Matcher matcher = PAT.matcher(line);
					if (matcher.find()) {
						geneName = matcher.group(1);

					}
					}
				}
				if (line.contains("<GBQualifier_name>product</GBQualifier_name>")) {
//					"<Prot-ref_name_E>([^}]*)</Prot-ref_name_E>"
					line = br.readLine();
					if (line != null) {
					Pattern PAT = Pattern.compile("<GBQualifier_value>([^}]*)</GBQualifier_value>");
					Matcher matcher = PAT.matcher(line);
					if (matcher.find()) {
						protName = matcher.group(1);
					}
					}

				//} else {System.out.println("Error retrieving names from NCBI database; ID doesn't match:" + geneID);}

			}
			}
			System.out.println(geneName);
			System.out.println(protName);
			String GeneProd = geneName + "$" + protName;
			return GeneProd;
		} finally {

		}
	}
}
