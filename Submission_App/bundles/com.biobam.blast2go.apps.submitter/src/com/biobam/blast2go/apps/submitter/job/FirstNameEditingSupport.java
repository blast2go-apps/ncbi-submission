package com.biobam.blast2go.apps.submitter.job;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class FirstNameEditingSupport extends EditingSupport {

  private final TableViewer viewer;
  private final CellEditor editor;

  public FirstNameEditingSupport(TableViewer viewer) {
    super(viewer);
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
    return ((AuthorEntry) element).getFirstName();
  }

  @Override
  protected void setValue(Object element, Object userInputValue) {
    ((AuthorEntry) element).setFirstName(String.valueOf(userInputValue));
    viewer.update(element, null);
  }
}
