package com.biobam.blast2go.submitter.job;
public interface IListKeyObjectParser<T> {

	public T parseFromString(String stringValue);

}