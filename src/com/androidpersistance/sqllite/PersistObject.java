package com.androidpersistance.sqllite;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.androidpersistance.annotation.Key;

public class PersistObject {

	private String tableName;
	private String className;
	private Map<String,String> columnTypes;
	private Map<String, Object> columnValues;
	private List<String> primaryKey;
	
	public PersistObject(Object o) {
		if(o!=null){
			columnTypes = new HashMap<String, String>();
			columnValues = new HashMap<String, Object>();
			primaryKey = new ArrayList<String>();
			setup(o);
		}
	}
	
	private void setup(Object o){
		tableName = o.getClass().getSimpleName();
		className = o.getClass().getName();
		Class c = o.getClass();
		Field[] fields = c.getFields();
		for(int i=0;i<fields.length;i++){
			Field f = fields[i];

			Class type = f.getType();
			String name = f.getName();
			String fieldType = getFieldTypeFromClass(type);	
			columnTypes.put(name, fieldType);
			columnValues.put(name, getValueFromField(name, o));
			
			Object annotation = f.getAnnotation(Key.class);
			if(annotation!=null){
				primaryKey.add(f.getName());
			}
		}
	}
	
	private Object getValueFromField(String fieldName, Object o){
		Object value = null;
		String mname = Character.toUpperCase(fieldName.charAt(0)) +fieldName.substring(1);

		String methodName = "get"+mname;
		Class[] types = new Class[] {};
		try {
			Method method = o.getClass().getMethod(methodName, types);
			value = method.invoke(o, new Object[0]);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return value;
	}
	
	private String getFieldTypeFromClass(Class type){
		String ft = "VARCHAR";
		if("java.util.Date".equals(type.getName()))
			ft="datetime";
		else if("java.lang.String".equals(type.getName()))
			ft="VARCHAR";
		else if("int".equals(type.getName()))
			ft="INTEGER";
		else if("float".equals(type.getName()))
			ft="REAL";
		return ft;
	}
	
	/**
	 * returns the names of the columns in the object as an array
	 * @return
	 */
	public String[] getColumnNameString(){
		String[] columnNames = new String[columnTypes.keySet().size()];
		Iterator ii = columnTypes.keySet().iterator();
		int i =0;
		while(ii.hasNext()){
			columnNames[i]=ii.next().toString();
			i++;
		}
		return columnNames;
	}
	
	/**
	 * Generates where clause to use in query
	 * @return
	 */
	public String generateWhereClause(){
		StringBuilder b = new StringBuilder();
		Iterator<String> ii = columnValues.keySet().iterator();
		boolean first = true;
		while(ii.hasNext()){
			String cn = ii.next();
			Object val = columnValues.get(cn);
			if(val!=null){
				if(!first)
					b.append(" and ");
				b.append(cn);
				b.append("=");
				b.append(val);
				first = false;
			}
		}
		
		return b.toString();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, String> getColumnTypes() {
		return columnTypes;
	}

	public void setColumnTypes(Map<String, String> columnTypes) {
		this.columnTypes = columnTypes;
	}

	public Map<String, Object> getColumnValues() {
		return columnValues;
	}

	public void setColumnValues(Map<String, Object> columnValues) {
		this.columnValues = columnValues;
	}

	public List<String> getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(List<String> primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
