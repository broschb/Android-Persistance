package com.androidpersistance.sqllite;

import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public class APCursorFactory implements SQLiteDatabase.CursorFactory{
	
	private Map<String,String> tableToClassMappings;
	private APCursor cursor;
	
	public void updateMappings(Map<String,String> tableToClassMappings){
		this.tableToClassMappings = tableToClassMappings;
		if(cursor!=null)
			cursor.setTableToClassMappings(tableToClassMappings);
	}

	@Override
	public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery,
			String editTable, SQLiteQuery query) {
		cursor = new APCursor(db, masterQuery, editTable, query,tableToClassMappings); 
		return cursor;
	}

}
