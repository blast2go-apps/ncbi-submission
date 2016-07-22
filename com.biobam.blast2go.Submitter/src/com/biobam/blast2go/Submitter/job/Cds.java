package com.biobam.blast2go.Submitter.job;

public class Cds {
	public final Range coordenates;

	public Cds(Range coordenates) {
		this.coordenates = coordenates;
	}

	@Override
	public String toString() {
		return "Cds [coordenates=" + coordenates + "]";
	}
}
