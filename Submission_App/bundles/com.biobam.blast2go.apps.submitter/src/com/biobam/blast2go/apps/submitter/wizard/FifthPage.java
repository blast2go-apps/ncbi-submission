package com.biobam.blast2go.apps.submitter.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;

import com.biobam.blast2go.api.wizard.page.B2GWizardPage;
import com.biobam.blast2go.api.wizard.page.widget.implementations.ComboWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.StringWidget;
import com.biobam.blast2go.apps.submitter.job.SubmitterJobParameters;

public class FifthPage extends B2GWizardPage implements IWizardPage {

	private SubmitterJobParameters parameters;

	public FifthPage(SubmitterJobParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void definePage(Composite parent) {
		setTitle("Provide assembly data");
		addWidget(StringWidget.create(parent, parameters.method));
		addWidget(StringWidget.create(parent, parameters.assemblyName));
		addWidget(StringWidget.create(parent, parameters.version));
		addWidget(StringWidget.create(parent, parameters.genomeCoverage));
		addWidget(ComboWidget.createEdit(parent, parameters.technology));
	}

}
