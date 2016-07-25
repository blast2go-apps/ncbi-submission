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

public class SupplementaryLabelTable extends TableViewer{

	public SupplementaryLabelTable (Composite parent) {
		super (parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, this);
		final Table table = getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		setContentProvider(new ArrayContentProvider());
	    //setInput(SupplementaryLabelsFixed.INSTANCE.getLabels());
	    GridData gridData = new GridData();
	    gridData.verticalAlignment = GridData.CENTER;
	    gridData.horizontalSpan = 2;
	    gridData.grabExcessHorizontalSpace = false;
	    gridData.grabExcessVerticalSpace = false;
	    gridData.horizontalAlignment = GridData.FILL;
	    gridData.heightHint = 180;
	    getControl().setLayoutData(gridData);



	}
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Label", "Value"};
		int[] bounds = { 150, 100};

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		// First column is for the first name
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SupplementaryLabel p = (SupplementaryLabel) element;
				return p.getLabel();
			}
		});
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SupplementaryLabel p = (SupplementaryLabel) element;
				return p.getlabelValue();
			}
		});
		col.setEditingSupport(new SupplementaryLabelValueEditingSupport(this));
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
