package com.biobam.blast2go.Submitter.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.biobam.blast2go.preferences.proxy.ConnectionUtilities;

public class RetrieveGeneFromNCBI {
	static String getNames(Object allGeneIDs) throws MalformedURLException, IOException{
		URLConnection connection = null;
		final String HTML = "http://www.ncbi.nlm.nih.gov/protein?cmd=Retrieve&dopt=GenPept&list_uids=";
		String[] geneIDs = allGeneIDs.toString().split(",");
		String geneID = geneIDs[0].substring(1);
		String geneurl = HTML + geneID;
		System.out.println(geneurl);
		URL geneHtml = new URL(geneurl);
		String geneName = "hypothetical protein";
		try{
			//connection =  new URL(geneHtml).openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(ConnectionUtilities.toInputStream(geneHtml)));
			String line;
			while((line = br.readLine()) != null){
//				System.out.println(line);
				if (line.contains( "<title>")) {
					Pattern PAT = Pattern.compile(">([^}]*)\\[");
					Matcher matcher = PAT.matcher(line);
					if (matcher.find()) {
						geneName = matcher.group(1);
						return geneName;
					}

				}

			}

		}finally{

		}
		return geneName;

	}
	}


