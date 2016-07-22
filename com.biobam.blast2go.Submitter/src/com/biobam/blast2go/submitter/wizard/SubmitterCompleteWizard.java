package com.biobam.blast2go.submitter.wizard;

import com.biobam.blast2go.api.wizard.B2GWizard;
import com.biobam.blast2go.submitter.job.SubmitterJobParameters;

public class SubmitterCompleteWizard extends B2GWizard<SubmitterJobParameters> {
	public SubmitterCompleteWizard() {
		setWindowTitle("Create GenBank Submission Files");
	}

	@Override
	protected boolean preFinish() {
		getParameters().supplementaryLabel.saveValue();
		getParameters().authorsList.saveValue();
		return super.preFinish();
	}

	@Override
	protected void definePages() {
		SubmitterJobParameters parameters = getParameters();
		addPage(new FirstPage(parameters));
		addPage(new SecondPage(parameters));
		addPage(new ThirdPage(parameters));
		//addPage(new FourthPage(parameters));


	}

}
