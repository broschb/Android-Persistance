package com.androidpersistance.sqllite;

import com.androidpersistance.AndroidPersistableException;
import com.androidpersistance.PersistanceImpl;

import android.content.Context;

public class AndroidSqlLitePersitanceImpl extends PersistanceImpl{
	
	private SqlLiteDatastore datastore;

	public AndroidSqlLitePersitanceImpl(Context context, int version, String dbName) {
		super(context, version, dbName);
		datastore = new SqlLiteDatastore(context, dbName, version);
	}

	@Override
	public void create(Object persistObj) throws AndroidPersistableException {
		datastore.create(persistObj);
		
	}

	@Override
	public int delete(Object deleteObj) throws AndroidPersistableException {
		return datastore.delete(deleteObj);
		
	}
	
	@Override
	public int update(Object o)throws AndroidPersistableException{
		return datastore.update(o);
	}

	@Override
	public APCursor retrieve(Object retrieveObj) throws AndroidPersistableException {
		return datastore.retrieve(retrieveObj);
	}

	@Override
	public void closeDatastore() {
		datastore.closeDatastore();
		
	}

	@Override
	public void openDatastore() {
		datastore.openDatastore();
		
	}

}
