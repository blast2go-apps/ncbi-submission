package com.biobam.blast2go.apps.submitter.job;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class AuthorsTable extends TableViewer {


	public AuthorsTable(Composite parent) {
		super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, this);
		final Table table = getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		setContentProvider(new ArrayContentProvider());

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.minimumHeight = 80;
		getControl().setLayoutData(gridData);


	}


	// This will create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "First name", "Initials", "Last Name"};
		int[] bounds = { 200, 100, 200};

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		// First column is for the first name
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				AuthorEntry p = (AuthorEntry) element;
				return p.getFirstName();
			}
		});
		col.setEditingSupport(new FirstNameEditingSupport(this));

		// Second column is for initials
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				AuthorEntry p = (AuthorEntry) element;
				return p.getInitials();
			}
		});
		col.setEditingSupport(new InitialsEditingSupport(this));

		// now the last name
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				AuthorEntry p = (AuthorEntry) element;
				return p.getLastName();
			}
		});
		col.setEditingSupport(new LastNameEditingSupport(this));
//		/*
//		TableColumnLayout layout = new TableColumnLayout();
//		parent.setLayout( layout );
//
//		layout.setColumnData( column1, new ColumnWeightData( 40) );
//		layout.setColumnData( column2, new ColumnWeightData( 20 ) );
//		layout.setColumnData( column2, new ColumnWeightData( 40 ) );*/
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}


}