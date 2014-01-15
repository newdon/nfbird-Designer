package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.IOParameter;
import org.activiti.designer.property.ModelUpdater;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class IOParameterEditor extends TableFieldEditor {
  
  protected Composite parent;
  protected ModelUpdater modelUpdater;
  
  public PictogramElement pictogramElement;
  public Diagram diagram;
  public boolean isInputParameters = false;
	
  public IOParameterEditor(String key, Composite parent, ModelUpdater modelUpdater) {
    
    super(key, "", new String[] {"Source", "Source expression", "Target", "Target expression"},
        new int[] {150, 150, 150, 150}, parent);
    this.parent = parent;
    this.modelUpdater = modelUpdater;
  }

  public void initialize(List<IOParameter> parameterList) {
    removeTableItems();
    if(parameterList == null || parameterList.size() == 0) return;
    for (IOParameter parameter : parameterList) {
      addTableItem(parameter);
    }
  }

  @Override
  protected String createList(String[][] items) {
    return null;
  }

  @Override
  protected String[][] parseString(String string) {
    return null;
  }
  
  protected void addTableItem(IOParameter parameter) {
    
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      String source = parameter.getSource() != null ? parameter.getSource() : "";
      tableItem.setText(0, source);
      String sourceExpression = parameter.getSourceExpression() != null ? parameter.getSourceExpression() : "";
      tableItem.setText(1, sourceExpression);
      String target = parameter.getTarget() != null ? parameter.getTarget() : "";
      tableItem.setText(2, target);
      String targetExpression = parameter.getTargetExpression() != null ? parameter.getTargetExpression() : "";
      tableItem.setText(3, targetExpression);
    }
  }

  @Override
  protected String[] getNewInputObject() {
    IOParameterDialog dialog = new IOParameterDialog(parent.getShell(), getItems());
    dialog.open();
    if((StringUtils.isNotEmpty(dialog.source) || StringUtils.isNotEmpty(dialog.sourceExpression)) &&
            (StringUtils.isNotEmpty(dialog.target) || StringUtils.isNotEmpty(dialog.targetExpression))) {
      return new String[] { dialog.source, dialog.sourceExpression, dialog.target, dialog.targetExpression};
    } else {
      return null;
    }
  }
  
  @Override
  protected String[] getChangedInputObject(TableItem item) {
    IOParameterDialog dialog = new IOParameterDialog(parent.getShell(), getItems(), 
            item.getText(0), item.getText(1), item.getText(2), item.getText(3));
    dialog.open();
    if((StringUtils.isNotEmpty(dialog.source) || StringUtils.isNotEmpty(dialog.sourceExpression)) &&
        (StringUtils.isNotEmpty(dialog.target) || StringUtils.isNotEmpty(dialog.targetExpression))) {
    	return new String[] { dialog.source, dialog.sourceExpression, dialog.target, dialog.targetExpression};
    } else {
      return null;
    }
  }
  
  @Override
  protected void removedItem(int index) {
	  // TODO Auto-generated method stub 
  }
  
  @Override
  protected void selectionChanged(boolean updated) {
    super.selectionChanged(updated);
    if(updated) {
      saveIOParameters();
    }
  }
  
  private void saveIOParameters() {
    if (pictogramElement != null) {
      Object updatableBo = modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
      
    	List<IOParameter> newParameterList = new ArrayList<IOParameter>();
      for (TableItem item : getItems()) {
        String source = item.getText(0);
        String sourceExpression = item.getText(1);
        String target = item.getText(2);
        String targetExpression = item.getText(3);
        if((StringUtils.isNotEmpty(source) || StringUtils.isNotEmpty(sourceExpression)) &&
            (StringUtils.isNotEmpty(target) || StringUtils.isNotEmpty(targetExpression))) {
          
        	IOParameter newParameter = new IOParameter();
        	newParameter.setSource(source);
        	newParameter.setSourceExpression(sourceExpression);
        	newParameter.setTarget(target);
        	newParameter.setTargetExpression(targetExpression);
          newParameterList.add(newParameter);
        }
      }
      if(isInputParameters == true) {
        ((CallActivity) updatableBo).getInParameters().clear();
        ((CallActivity) updatableBo).getInParameters().addAll(newParameterList);
      } else {
        ((CallActivity) updatableBo).getOutParameters().clear();
        ((CallActivity) updatableBo).getOutParameters().addAll(newParameterList);
      }
     modelUpdater.executeModelUpdater();
    }
  }
}
