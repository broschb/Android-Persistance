package com.androidpersistance.sqllite;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

public class APCursor extends SQLiteCursor{//for money or love encore
	private static final String tag = "APCursor";
	private String table;
	private Map<String,String> tableToClassMappings;
	public APCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
			String editTable, SQLiteQuery query,Map<String,String> tableToClassMappings) {
		super(db, driver, editTable, query);
		table = editTable;
		this.tableToClassMappings = tableToClassMappings;
	}
	
	public Object getPersistObject(){
		Object rtnObject = null;
		if(tableToClassMappings.containsKey(table)){
			try {
				String clsName = tableToClassMappings.get(table);
				Class clazz = Class.forName(clsName);
				rtnObject = clazz.newInstance();
				setClassValues(rtnObject);
			} catch (ClassNotFoundException e) {
				Log.e(tag, "ClassNotFoundException", e);
			} catch (IllegalAccessException e) {
				Log.e(tag, "IllegalAccessException", e);
			} catch (InstantiationException e) {
				Log.e(tag, "InstantiationException", e);
			}
		}
		return rtnObject;
	}
	
	private void setClassValues(Object o){
		String[] columnNames = getColumnNames();
		for(String s : columnNames){
			setValue(s, o);
		}
	}
	
	private Method setValue(String fieldName, Object o){
		Method m = null;
		String mname = "set"+Character.toUpperCase(fieldName.charAt(0)) +fieldName.substring(1);
		Field f;
		try {
			f = o.getClass().getField(fieldName);
			Class fieldType = f.getType();
			Class[] types = new Class[] {fieldType};
			m = o.getClass().getMethod(mname, types);
			if("Boolean".equals(fieldType.getSimpleName())){
				Boolean b = new Boolean(getString(getColumnIndexOrThrow(fieldName)));
				m.invoke(o, new Object[]{b});
			}else if("Long".equals(fieldType.getSimpleName())){
				Long l = getLong(getColumnIndexOrThrow(fieldName));
				m.invoke(o, new Object[]{l});
			}else if("Byte".equals(fieldType.getSimpleName())){
				//WHAT TO DO HERE!!
			}else if("Double".equals(fieldType.getSimpleName())){
				Double d = getDouble(getColumnIndexOrThrow(fieldName));
				m.invoke(o, new Object[]{d});
			}else if("Float".equals(fieldType.getSimpleName())){
				Float fl = getFloat(getColumnIndexOrThrow(fieldName));
				m.invoke(o, new Object[]{fl});
			}else if("Integer".equals(fieldType.getSimpleName())){
				Integer i = getInt(getColumnIndexOrThrow(fieldName));
				m.invoke(o, new Object[]{i});
			}else if("Short".equals(fieldType.getSimpleName())){
				Short s = getShort(getColumnIndexOrThrow(fieldName));
				m.invoke(o, new Object[]{s});
			}else if("String".equals(fieldType.getSimpleName())){
				String s = getString(getColumnIndexOrThrow(fieldName));
				m.invoke(o, new Object[]{s});
			}
		} catch (SecurityException e) {
			Log.e(tag, "SecurityException", e);
		} catch (NoSuchFieldException e) {
			Log.e(tag, "NoSuchFieldException", e);
		} catch (NoSuchMethodException e) {
			Log.e(tag, "NoSuchMethodException", e);
		} catch (IllegalArgumentException e) {
			Log.e(tag, "IllegalArgumentException", e);
		} catch (IllegalAccessException e) {
			Log.e(tag, "IllegalAccessException", e);
		} catch (InvocationTargetException e) {
			Log.e(tag, "InvocationTargetException", e);
		}
		return m;
	}

}
