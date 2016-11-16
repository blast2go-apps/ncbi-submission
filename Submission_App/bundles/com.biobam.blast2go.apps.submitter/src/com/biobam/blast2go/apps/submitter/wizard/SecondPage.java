package com.biobam.blast2go.apps.submitter.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;

import com.biobam.blast2go.api.wizard.page.B2GWizardPage;
import com.biobam.blast2go.api.wizard.page.widget.implementations.B2GGroup;
import com.biobam.blast2go.api.wizard.page.widget.implementations.ComboWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.DoubleWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.FileWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.NoteWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.SpaceWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.StringWidget;
import com.biobam.blast2go.apps.submitter.job.SubmitterJobParameters;

public class SecondPage extends B2GWizardPage implements IWizardPage {

	private SubmitterJobParameters parameters;

	public SecondPage(SubmitterJobParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void definePage(Composite parent) {
		setTitle("Provide sequence data and annotations files");
		addWidget(NoteWidget.create(parent, parameters.noteFiles));

		addWidget(SpaceWidget.create(parent));
		addWidget(FileWidget.createFolderSaveBuilder(parent, parameters.outputDir).build());

		addWidget(FileWidget.createFileOpenBuilder(parent, parameters.fastatFile).build());

		addWidget(SpaceWidget.create(parent));

		B2GGroup compositeGroup2 = B2GGroup.create(parent, "GFF File:");
//		addWidget(NoteWidget.create(compositeGroup2, parameters.noteGff));
		addWidget(FileWidget.createMultipleFilesOpen(compositeGroup2, parameters.gff3File));
		addWidget(StringWidget.create(compositeGroup2, parameters.tagName));

		addWidget(SpaceWidget.create(parent));

		B2GGroup compositeGroup3 = B2GGroup.create(parent, "Select the gene names: ");
		addWidget(ComboWidget.createSimple(compositeGroup3, parameters.geneName));
		addWidget(StringWidget.create(compositeGroup3, parameters.eVal));
		addWidget(DoubleWidget.create(compositeGroup3, parameters.sim));
		addWidget(DoubleWidget.create(compositeGroup3, parameters.coverage));
//		
	}
}
