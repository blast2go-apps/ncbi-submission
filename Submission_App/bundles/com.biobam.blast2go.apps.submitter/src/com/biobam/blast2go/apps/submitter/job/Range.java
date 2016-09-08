package com.biobam.blast2go.apps.submitter.job;

public class Range {
	public int start;
	public int end;

	public Range(String start, String end) {
		this.start = Integer.valueOf(start);
		this.end = Integer.valueOf(end);
	}

	@Override
	public String toString() {
		return "Range [start=" + start + ", end=" + end + "]";
	}
}
