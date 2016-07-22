package com.biobam.blast2go.Submitter.job;
public interface IListKeyObjectParser<T> {

	public T parseFromString(String stringValue);

}