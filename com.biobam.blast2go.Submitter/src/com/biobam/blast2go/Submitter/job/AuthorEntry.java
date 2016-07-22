package com.biobam.blast2go.Submitter.job;

public class AuthorEntry implements ListKeyObject {

	private String firstName;
	private String lastName;
	private String initials;

	public AuthorEntry(String firstName, String initials, String lastName) {
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setInitials(initials);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	@Override
	public String toStringFormat() {
		return firstName + "/" + initials + "/" + lastName;
	}


	public boolean authorSame(Object obj) {
		// TODO Auto-generated method stub

		if (obj instanceof AuthorEntry) {
			AuthorEntry identity = (AuthorEntry) obj;
			if (!identity.firstName.equals(firstName)) {
				return false;
			}
			if (!identity.lastName.equals(lastName)) {
				return false;
			}
			if (!identity.initials.equals(initials)) {
				return false;
			}
		}
		return true;
	}

}