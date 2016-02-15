/*
 * Copyright (c) 2014 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.qscript.editor;

import javax.swing.table.AbstractTableModel;

import org.qscript.Variable;


/**
 * Used by the QScript IDE to display variables and their values.
 * 
 * @author Peter Lager
 *
 */
class DataStoreModel extends AbstractTableModel {

	private static final long serialVersionUID = -8561200261655563485L;


	private Variable[] vars = new Variable[0];
	private String[] columnNames = {"Identifier", "Value"};
	
	/**
	 * 
	 */
	public DataStoreModel() {
		vars = new Variable[0];
	}

	public void clear(){
		vars = new Variable[0];
	}
	
	/**
	 * Save a variable to the current score
	 * @param var
	 */
	public void updateStoreVariable(Variable[] variables){
		this.vars = variables;
		this.fireTableDataChanged();
	}

	public Variable[] getVariables(){
		return vars;
	}
	
	@Override
	public int getRowCount() {
		return vars.length;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

    public String getColumnName(int col) {
        return columnNames[col].toString();
    }
    
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rowIndex >= 0 && rowIndex < vars.length && columnIndex < 2 ){
			if(columnIndex == 0)
				return vars[rowIndex].getIdentifier();
			else
				return vars[rowIndex];
		}
		return null;
	}
	
}
