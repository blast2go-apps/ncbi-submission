package com.biobam.blast2go.submitter.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;

import com.biobam.blast2go.api.wizard.page.B2GWizardPage;
import com.biobam.blast2go.api.wizard.page.widget.implementations.NoteWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.StringWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.TextWidget;
import com.biobam.blast2go.submitter.job.SubmitterJobParameters;

public class FourthPage extends B2GWizardPage implements IWizardPage {

	private SubmitterJobParameters parameters;

	public FourthPage(SubmitterJobParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void definePage(Composite parent) {
		setTitle("Affiliation data");
		addWidget(NoteWidget.create(parent, parameters.noteAffiliation));
		addWidget(StringWidget.create(parent, parameters.researchInstitution));
		addWidget(StringWidget.create(parent, parameters.researchDepartement));
		addWidget(StringWidget.create(parent, parameters.street));
		addWidget(StringWidget.create(parent, parameters.city));
		addWidget(StringWidget.create(parent, parameters.state));
		addWidget(StringWidget.create(parent, parameters.country));
		addWidget(StringWidget.create(parent, parameters.postcode));
		addWidget(TextWidget.create(parent, parameters.title, 74));
		addWidget(StringWidget.create(parent, parameters.setDate));
	}
}