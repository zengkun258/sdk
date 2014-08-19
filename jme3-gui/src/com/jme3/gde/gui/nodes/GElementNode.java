/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.gui.nodes;

import com.jme3.gde.gui.propertyeditors.ResourceEditor;
import com.jme3.gde.gui.propertyeditors.SizeEditor;
import jada.ngeditor.controller.CommandProcessor;
import jada.ngeditor.controller.commands.EditAttributeCommand;
import jada.ngeditor.model.elements.GElement;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import sun.beans.editors.BoolEditor;
import sun.beans.editors.BooleanEditor;

/**
 *
 * @author cris
 */
public class GElementNode extends AbstractNode{
    private final GElement element;

    public GElementNode(GElement element) {
        super(Children.create(new GElementChildFactory(element), false));
        
        this.element = element;
        this.setName(element.getID());
        
    }
    
    public GElement getGelement(){
        return element;
    }
    
    public void updateChildren(){
        
    }
    @Override
    protected Sheet createSheet() {
        Sheet s = super.createSheet();
        Sheet.Set set = s.get(Sheet.PROPERTIES);
        if (set == null) {
            set = Sheet.createPropertiesSet();
            s.put(set);
        }
        set.setName("Element Properties");
        set.setShortDescription("You can set element properties");
        for(Entry<String,String> pair : this.element.listAttributes().entrySet()){
            final ElementAttributeProperty elementAttributeProperty = new ElementAttributeProperty(element,pair.getKey());
            pickEditor(pair, elementAttributeProperty);
            set.put(elementAttributeProperty);
        }
        s.put(set);
        return s; 
    }

    private void pickEditor(Entry<String, String> pair, final ElementAttributeProperty elementAttributeProperty) {
        if(pair.getKey().equals("width")||pair.getKey().equals("height") || pair.getKey().equals("x") || pair.getKey().equals("y") ){
           elementAttributeProperty.setPropertyEditor(new SizeEditor());
        }else if(pair.getKey().equals("filename")){
            elementAttributeProperty.setPropertyEditor(new ResourceEditor());
        }
        
    }
    
    public  class ElementAttributeProperty extends Node.Property {

        private String attributeName;
        private GElement element;
        private PropertyEditor editor;

        public ElementAttributeProperty(GElement element, String attributeName) {
            super(String.class);
            this.element = element;
            this.attributeName = attributeName;
            this.setName(attributeName);
            this.setDisplayName(attributeName);
           
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException{
            return element.getAttribute(attributeName);
        }

        @Override
        public boolean canWrite() {
            return true;
        }
        
        public void setPropertyEditor(PropertyEditor editor){
            this.editor = editor;
        }
        @Override
        public PropertyEditor getPropertyEditor() {  
            
            return this.editor; //To change body of generated methods, choose Tools | Templates.
        }
        
        

        @Override
        public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException {
            try {
                EditAttributeCommand command = CommandProcessor.getInstance().getCommand(EditAttributeCommand.class); 
               command.setAttribute(attributeName);
                command.setValue(val.toString());
               CommandProcessor.getInstance().excuteCommand(command);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
            
        }
    };
}
