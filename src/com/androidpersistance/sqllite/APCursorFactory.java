package com.androidpersistance.sqllite;

import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public class APCursorFactory implements SQLiteDatabase.CursorFactory{
	
	Map<String,String> tableToClassMappings;
	
	public void updateMappings(Map<String,String> tableToClassMappings){
		this.tableToClassMappings = tableToClassMappings;
	}

	@Override
	public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery,
			String editTable, SQLiteQuery query) {
		return new APCursor(db, masterQuery, editTable, query,tableToClassMappings);
	}

}
