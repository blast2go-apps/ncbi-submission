package com.biobam.blast2go.apps.submitter.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;

import com.biobam.blast2go.api.wizard.page.B2GWizardPage;
import com.biobam.blast2go.api.wizard.page.widget.implementations.ComboWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.NoteWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.SpaceWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.StringWidget;
import com.biobam.blast2go.apps.submitter.job.SubmitterJobParameters;
import com.biobam.blast2go.apps.submitter.job.SupplementaryLabelTable;

public class FirstPage extends B2GWizardPage implements IWizardPage {

	private SubmitterJobParameters parameters;

	public FirstPage(SubmitterJobParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void definePage(Composite parent) {

		setTitle("Provide project details");

		addWidget(NoteWidget.createWithLink(parent, parameters.notebioProject,
				"https://submit.ncbi.nlm.nih.gov/subs/bioproject/"));
		// addWidget(SpaceWidget.create(parent));
		addWidget(StringWidget.create(parent, parameters.locusTag));
		addWidget(StringWidget.create(parent, parameters.labID));

		addWidget(SpaceWidget.create(parent));

		// B2GRadioGroup<SubType> radioGroup =
		// addWidget(B2GRadioGroup.create(parent, parameters.subType));
		// radioGroup.createSimpleRadioOption(SubType.single);
		// radioGroup.createSimpleRadioOption(SubType.genome);
		// radioGroup.createSimpleRadioOption(SubType.wgs);
		addWidget(ComboWidget.createSimple(parent, parameters.subType));

		// addWidget(SpaceWidget.create(parent));
		addWidget(StringWidget.create(parent, parameters.method));
		addWidget(StringWidget.create(parent, parameters.assemblyName));
		addWidget(StringWidget.create(parent, parameters.version));
		addWidget(StringWidget.create(parent, parameters.genomeCoverage));
		addWidget(ComboWidget.createEdit(parent, parameters.technology));
		addWidget(SpaceWidget.create(parent));
		addWidget(NoteWidget.create(parent, parameters.noteLabel));
		SupplementaryLabelTable supplementLabelWidget = new SupplementaryLabelTable(parent);
		supplementLabelWidget.setInput(parameters.supplementaryLabel.getValue());

	}
}
