package com.biobam.blast2go.submitter.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;

import com.biobam.blast2go.api.wizard.page.B2GWizardPage;
import com.biobam.blast2go.api.wizard.page.widget.IB2GWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.B2GGroup;
import com.biobam.blast2go.api.wizard.page.widget.implementations.ComboWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.DoubleWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.FileWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.NoteWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.SpaceWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.StringWidget;
import com.biobam.blast2go.submitter.job.SubmitterJobParameters;
import com.biobam.blast2go.submitter.job.SubmitterJobParameters.GeneName;

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

		//B2GGroup compositeGroup1 = B2GGroup.create(parent, "Fasta File:");
		addWidget(FileWidget.createFileOpenBuilder(parent, parameters.fastatFile).build());
		//addWidget(BooleanWidget.create(compositeGroup1, parameters.multiFasta));

		addWidget(SpaceWidget.create(parent));


		B2GGroup compositeGroup2 = B2GGroup.create(parent, "GFF File:");
		addWidget(NoteWidget.create(compositeGroup2, parameters.noteGff));
		addWidget(FileWidget.createMultipleFilesOpen(compositeGroup2, parameters.gff3File));
		addWidget(StringWidget.create(compositeGroup2, parameters.tagName));

		addWidget(SpaceWidget.create(parent));

		B2GGroup compositeGroup3 = B2GGroup.create(parent, "Select the gene names: ");
		addWidget(ComboWidget.createSimple(compositeGroup3, parameters.geneName));
		IB2GWidget eval = addWidget(StringWidget.create(compositeGroup3, parameters.eVal));
		IB2GWidget sim = addWidget(DoubleWidget.create(compositeGroup3, parameters.sim));
		IB2GWidget coverage = addWidget(DoubleWidget.create(compositeGroup3, parameters.coverage));
		enhabler(eval, sim, coverage);

		parameters.geneName.addPropertyChangeListener("value", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				enhabler(eval, sim, coverage);
			}
		});
	}

	private void enhabler(IB2GWidget eval, IB2GWidget sim, IB2GWidget coverage) {
		if (parameters.geneName.getValue() == GeneName.Top_Blast_Hit) {
			eval.setEnabled(true);
			sim.setEnabled(true);
			coverage.setEnabled(true);
		} else {
			eval.setEnabled(false);
			sim.setEnabled(false);
			coverage.setEnabled(false);
		}
	}
}
