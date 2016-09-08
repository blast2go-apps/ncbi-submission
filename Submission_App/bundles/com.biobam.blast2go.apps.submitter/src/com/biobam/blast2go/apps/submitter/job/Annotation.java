package com.biobam.blast2go.apps.submitter.job;

import java.util.List;

public class Annotation {
	public final String name;
	public final String description;
	public final String geneName;
	public final List<String> goIds;
	public final List<String> ecCodes;

	public Annotation(String name, String description, String geneName, List<String> goIds, List<String> ecCodes) {
		this.name = name;
		this.description = description;
		this.geneName = geneName;
		this.goIds = goIds;
		this.ecCodes = ecCodes;
	}

	@Override
	public String toString() {
		return "Annotation [name=" + name + ", description=" + description + ", geneName=" + geneName + ", goIds="
				+ goIds + ", ecCodes=" + ecCodes + "]";
	}
}
