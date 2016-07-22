package com.biobam.blast2go.submitter.job;

public class SupplementaryLabel implements ListKeyObject{

	private String label;
	private String value;

	public SupplementaryLabel(String label, String value) {
		this.setLabel(label);
		this.setlabelValue(value);
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getlabelValue() {
		return value;
	}
	public void setlabelValue(String value) {
		this.value = value;
	}
	@Override
	public String toStringFormat() {
		return label + "/" + value ;
	}


}
