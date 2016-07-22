package com.biobam.blast2go.Submitter.job;

import java.util.ArrayList;
import java.util.List;

public class Gene {
	public Range geneCoordenates;
	public Range mRnaCoordenates;
	public boolean isReverseStrand;
	public List<Cds> cdss;
	public String header;

	private Gene() {
		this.cdss = new ArrayList<Cds>();
	}

	static class Builder {
		private Gene gene;

		public Builder(String header) {
			gene = new Gene();
			gene.header = header;
		}

		public Builder setGeneCoordenates(Range geneCoordenates) {
			gene.geneCoordenates = geneCoordenates;
			return this;
		}

		public Builder setMRnaCoordenates(Range mRnaCoordenates) {
			gene.mRnaCoordenates = mRnaCoordenates;
			return this;
		}

		public Builder setIsReverseStrand() {
			gene.isReverseStrand = true;
			return this;
		}

		public Builder addCds(Cds cds) {
			gene.cdss.add(cds);
			return this;
		}

		public Gene build() {
			if (gene.geneCoordenates == null || gene.mRnaCoordenates == null) {
				throw new IllegalAccessError();
			}
			return gene;
		}
	}

	@Override
	public String toString() {
		return "Gene [geneCoordenates=" + geneCoordenates + ", mRnaCoordenates=" + mRnaCoordenates
				+ ", isReverseStrand=" + isReverseStrand + ", cdss=" + cdss + "]";
	}
}
