package com.biobam.blast2go.submitter.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.biobam.blast2go.api.wizard.page.B2GWizardPage;
import com.biobam.blast2go.api.wizard.page.widget.implementations.NoteWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.StringWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.TextWidget;
import com.biobam.blast2go.submitter.job.AuthorEntry;
import com.biobam.blast2go.submitter.job.AuthorsTable;
import com.biobam.blast2go.submitter.job.ListKeyObject;
import com.biobam.blast2go.submitter.job.SubmitterJobParameters;

public class ThirdPage extends B2GWizardPage implements IWizardPage {

	private SubmitterJobParameters parameters;
	private int index;

	public ThirdPage(SubmitterJobParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void definePage(Composite parent) {
		setTitle("Authors");
		//B2GGroup compositeGroup = B2GGroup.create(parent, "Contact data:");
		addWidget(NoteWidget.create(parent, parameters.noteContactAuthor));
		addWidget(StringWidget.create(parent, parameters.email));
		addWidget(StringWidget.create(parent, parameters.fax));
		addWidget(StringWidget.create(parent, parameters.phone));

//		addWidget(SpaceWidget.create(parent));

//		addWidget(NoteWidget.create(parent, parameters.noteAffiliation));
		addWidget(StringWidget.create(parent, parameters.researchInstitution));
		addWidget(StringWidget.create(parent, parameters.researchDepartement));
		addWidget(StringWidget.create(parent, parameters.street));
		addWidget(StringWidget.create(parent, parameters.city));
		addWidget(StringWidget.create(parent, parameters.state));
		addWidget(StringWidget.create(parent, parameters.country));
		addWidget(StringWidget.create(parent, parameters.postcode));
		addWidget(TextWidget.create(parent, parameters.title, 40));
		addWidget(StringWidget.create(parent, parameters.setDate));
		addWidget(NoteWidget.create(parent, parameters.notetAuthor));
		AuthorsTable authorsWidget = new AuthorsTable(parent);
		List<AuthorEntry> authors = new ArrayList<AuthorEntry>();
		for (ListKeyObject option : parameters.authorsList.getValue()) {
			AuthorEntry author = (AuthorEntry) option;
			authors.add(author);
		}
//		if (authors.isEmpty()) {
//			authors.add(new AuthorEntry(parameters.authorFNameC.getValue(), parameters.initialsC.getValue(),
//					parameters.authorLNameC.getValue()));
//		}
		authorsWidget.setInput(authors);


		Composite myComposite = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout();
		layout.wrap = true;
		layout.pack = true;
		layout.justify = false;
		layout.type = SWT.HORIZONTAL;
		myComposite.setLayout(layout);
		Button button = new Button(myComposite, SWT.None);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				authors.add(new AuthorEntry("", "", ""));
				authorsWidget.setInput(authors);
				parameters.authorsList.setValue(authors.toArray(new ListKeyObject[authors.size()]));

			}

		});
		button.setText("Add author");

		// remove

		authorsWidget.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				// TODO Auto-generated method stub
				index = authorsWidget.getTable().getSelectionIndex();

			}
		});
		Button button2 = new Button(myComposite, SWT.None);
		button2.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				authors.remove(index);
				authorsWidget.setInput(authors);
				parameters.authorsList.setValue(authors.toArray(new ListKeyObject[authors.size()]));
			}

		});
		button2.setText("Remove selected author");

		addWidget(StringWidget.create(parent, parameters.consortium));

	}

}