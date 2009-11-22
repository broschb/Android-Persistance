package com.androidpersistance.sqllite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
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
	private static final String GET_TABLE_QUERY="SELECT name FROM sqlite_master WHERE type='table' ORDER BY name";
	
	public SqlLiteDatastore(Context context, String name, int version) {
		this.databaseName = name;
		this.context = context;
		this.databaseVersion = version;
		init();
	}
	
	private void init(){
		openDatastore();
		Cursor c = db.rawQuery(GET_TABLE_QUERY, null);
		if(c!=null){
			c.moveToFirst();
			while(!c.isAfterLast()){
				String tableName = c.getString(c.getColumnIndex("name"));
				tables.put(tableName, null);
				c.moveToNext();
			}
		}
		
		closeDatastore();
	}
	
	public void create(Object o)throws AndroidPersistableException{
		PersistObject po = new PersistObject(o);
		boolean ok = verifyCreateTables(po, true);
		if(ok){//insert the data
			//TODO verify primary key set
			ContentValues cv = po.createContentValues();
			db.insert(po.getTableName(), null, cv);
		}
	}
	
	public APCursor retrieve(Object o)throws AndroidPersistableException{
		PersistObject po = new PersistObject(o);
		verifyCreateTables(po,false);
    	APCursor cursor = (APCursor) db.query(true, po.getTableName(), po.getColumnNameString(), po.generateWhereClause(), null,
                null, null, null, null);
    	
    	if (cursor != null) {
    		cursor.moveToFirst();
        }
    	return cursor;
	}
	
	public int delete(Object o)throws AndroidPersistableException{
		PersistObject po = new PersistObject(o);
		verifyCreateTables(po,false);
		int rowsDeleted = db.delete(po.getTableName(), po.generatePrimaryKeyWhereClause(), null);
		return rowsDeleted;
	}
	
	public int update(Object o)throws AndroidPersistableException{
		PersistObject po = new PersistObject(o);
		verifyCreateTables(po,false);
		ContentValues cv = po.createContentValues();
		//TODO verify not updating primary key
		//TODO issue w/ update, tries to use new value in where clause for update, update generateWhereClause method to generatewhereclauseusPK
		int rowsUpdated = db.update(po.getTableName(), cv, po.generatePrimaryKeyWhereClause(), null);
		return rowsUpdated;
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
		if(tables.containsKey(o.getTableName())){
			if(tables.get(o.getTableName())==null){
				tables.put(o.getTableName(), o.getClassName());
			}
			
			ok = true;
		}else{//attempt to create table
			if(create){//TODO need to check if table exists and throw if not create
				String createString = buildTableCreateString(o);
				Log.i("CREATE STRING ",createString);
				if (createString.trim().length() > 0) {
					Log.i("SqlLiteDatastore-Creating Table",
							"CREATING TABLE - " + o.getTableName());
					db.execSQL(createString);
					tables.put(o.getTableName(), o.getClassName());
					ok=true;
				}
				}
//			tables.put(o.getTableName(), o.getClassName());
//			cursorFactory.updateMappings(tables);
//			ok=true;
			}
		cursorFactory.updateMappings(tables);
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
				if(o.getAutoIncrementKey().contains(key)){
					sb.append(" ");
					sb.append("AUTOINCREMENT");
				}
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
			//check primary key
			if(o.getPrimaryKey().contains(columnName)){
				sb.append(" ");
				sb.append("primary key");
				//check for auto increment
				if(o.getAutoIncrementKey().contains(columnName)){
					sb.append(" ");
					sb.append("AUTOINCREMENT");
				}
			}
			if(ii.hasNext())
				sb.append(",");
		}
//		sb.append(getPrimaryKeyString(o));
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
