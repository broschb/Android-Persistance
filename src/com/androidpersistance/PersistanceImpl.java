package com.androidpersistance;

import com.androidpersistance.sqllite.APCursor;

import android.content.Context;

public abstract class PersistanceImpl {
	protected Context context;
	protected String dbName;
	protected int version;
	public PersistanceImpl(Context context,int version, String dbName) {
		this.context = context;
		this.dbName = dbName;
		this.version = version;
	}

	public abstract void create(Object persistObj)throws AndroidPersistableException;
	
	public abstract APCursor retrieve(Object retrieveObj)throws AndroidPersistableException;
	
	public abstract int delete(Object deleteObj)throws AndroidPersistableException;
	
	public abstract int update(Object o)throws AndroidPersistableException;
	
	public abstract void openDatastore();
	
	public abstract void closeDatastore();
}
