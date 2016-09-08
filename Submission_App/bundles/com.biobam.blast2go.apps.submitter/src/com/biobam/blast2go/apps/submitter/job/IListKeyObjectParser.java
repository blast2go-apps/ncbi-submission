package com.biobam.blast2go.apps.submitter.job;
public interface IListKeyObjectParser<T> {

	public T parseFromString(String stringValue);

}