package com.biobam.blast2go.submitter.job;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class SupplementaryLabelValueEditingSupport extends EditingSupport{
	
	private final TableViewer viewer;
	private final CellEditor editor;
	
	public SupplementaryLabelValueEditingSupport (TableViewer viewer) {
		super (viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(viewer.getTable());
	}
	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}
	@Override
	protected Object getValue(Object element) {
		return ((SupplementaryLabel) element).getlabelValue();
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		((SupplementaryLabel) element).setlabelValue(String.valueOf(userInputValue));
		viewer.update(element, null);
	}
}
