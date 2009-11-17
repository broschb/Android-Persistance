package com.androidpersistance.sqllite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.androidpersistance.AndroidPersistableException;

public class SqlLiteDatastore {

	private DatabaseHelper dbHelper;
	private Context context;
	private String databaseName;
	private int databaseVersion;
	private SQLiteDatabase db;
	private APCursorFactory cursorFactory;
	private Map<String, String> tables = new HashMap<String, String>();
	
	public SqlLiteDatastore(Context context, String name, int version) {
		this.databaseName = name;
		this.context = context;
		this.databaseVersion = version;
	}
	
	public void create(Object o)throws AndroidPersistableException{
		PersistObject po = new PersistObject(o);
		boolean ok = verifyCreateTables(po, true);
		if(ok){//insert the data
			//TODO verify primary key set
			ContentValues cv = createContentValues(po);
			db.insert(po.getTableName(), null, cv);
		}
	}
	
	public APCursor retrieve(Object o)throws AndroidPersistableException{
		PersistObject po = new PersistObject(o);

    	APCursor cursor = (APCursor) db.query(true, po.getTableName(), po.getColumnNameString(), po.generateWhereClause(), null,
                null, null, null, null);
    	
    	if (cursor != null) {
    		cursor.moveToFirst();
        }
    	return cursor;
	}
	
	public int delete(Object o)throws AndroidPersistableException{
		PersistObject po = new PersistObject(o);
		String where = po.generateWhereClause();
		int rowsDeleted = db.delete(po.getTableName(), po.generateWhereClause(), null);
		return rowsDeleted;
	}
	
	public int update(Object o)throws AndroidPersistableException{
		PersistObject po = new PersistObject(o);
		ContentValues cv = createContentValues(po);
		//TODO verify not updating primary key
		//TODO issue w/ update, tries to use new value in where clause for update, update generateWhereClause method to generatewhereclauseusPK
		int rowsUpdated = db.update(po.getTableName(), cv, po.generateWhereClause(), null);
		return rowsUpdated;
	}
	
	private ContentValues createContentValues(PersistObject o){
		ContentValues values = new ContentValues();
		Iterator ii = o.getColumnValues().keySet().iterator();
		while(ii.hasNext()){
			String key = (String) ii.next();
			Object value = o.getColumnValues().get(key);
			if(value!=null){
				if(value instanceof Boolean)
					values.put(key, (Boolean)value);
				else if(value instanceof Long)
					values.put(key, (Long)value);
				else if(value instanceof Byte)
					values.put(key, (Byte)value);
				else if(value instanceof Double)
					values.put(key, (Double)value);
				else if(value instanceof Float)
					values.put(key, (Float)value);
				else if(value instanceof Integer)
					values.put(key, (Integer)value);
				else if(value instanceof Short)
					values.put(key, (Short)value);
				else if(value instanceof String)
					values.put(key, (String)value);
			}
		}
		return values;
	}
	
	/**
	 * Checks to make sure a table exists for the entity, true if table exists, false else.
	 * if create then attempts to create table
	 * @param o
	 * @param create - if true attempts to create table for entity
	 * @return
	 */
	private boolean verifyCreateTables(PersistObject o, boolean create) throws AndroidPersistableException{
		boolean ok = false;
//		String className = o.getClass().getSimpleName();
		if(tables.containsKey(o.getTableName()))
			ok = true;
		else{//attempt to create table
			if (create) {
				String createString = buildTableCreateString(o);
				Log.i("CREATE STRING ",createString);
				if (createString.trim().length() > 0) {
					Log.i("SqlLiteDatastore-Creating Table",
							"CREATING TABLE - " + o.getTableName());
					db.execSQL(createString);
					tables.put(o.getTableName(), o.getClassName());
					cursorFactory.updateMappings(tables);
					ok=true;
				}
			}else{
				throw new AndroidPersistableException(AndroidPersistableException.TABLE_DOES_NOT_EXIST_EXCEPTION);
			}
		}
		return ok;
	}
	
	private String buildTableCreateString(PersistObject o)throws AndroidPersistableException{
		StringBuilder createString = new StringBuilder();
		createString.append("Create table IF NOT EXISTS ");
		createString.append(o.getTableName());
		createString.append(" ");
		createString.append(getFieldString(o));
		createString.append(";");
		Log.i("SqlLiteDatastore, TableCreationString",createString.toString());
		return createString.toString();
	}
	
	private String getPrimaryKeyString(PersistObject o)throws AndroidPersistableException{
		StringBuilder sb = new StringBuilder();
		sb.append(" primary key(");
		if(o.getPrimaryKey().size()>0){
			for(int i=0;i<o.getPrimaryKey().size();i++){
				String key = o.getPrimaryKey().get(i);
				sb.append(key);
				if(i<o.getPrimaryKey().size()-1)
					sb.append(",");
			}
		}else
			throw new AndroidPersistableException(AndroidPersistableException.NO_PRIMARY_KEY_DEFINED_EXCEPTION);
		sb.append(")");
		return sb.toString();
	
	}
	
	private String getFieldString(PersistObject o)throws AndroidPersistableException{
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		Iterator ii = o.getColumnTypes().keySet().iterator();
		while(ii.hasNext()){
			String columnName = (String) ii.next();
			String columnType = o.getColumnTypes().get(columnName);	
			sb.append(columnName);
			sb.append(" ");
			sb.append(columnType);
			sb.append(",");
		}
		sb.append(getPrimaryKeyString(o));
		sb.append(")");
		return sb.toString();
	}
	
	public void openDatastore(){
		cursorFactory = new APCursorFactory();
		dbHelper = new DatabaseHelper(context, databaseName, cursorFactory, databaseVersion);
		db = dbHelper.getWritableDatabase();
	}
	
	public void closeDatastore(){
		dbHelper.close();
		cursorFactory = null;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//FOR NOW DO NOTHING, WILL CREATE AS NEEDED.
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//FOR NOW AREN'T HANDLING UPGRADE
		}
	}
}
