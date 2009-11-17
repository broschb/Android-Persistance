package com.androidpersistance;

import com.androidpersistance.annotation.AndroidPersistable;
import com.androidpersistance.sqllite.APCursor;
import com.androidpersistance.sqllite.AndroidSqlLitePersitanceImpl;

import android.content.Context;
import android.util.Log;

public class AndroidPersistanceManager {
	private static final String LOG_STRING="AndroidPersistantManager";
	private static AndroidPersistanceManager manager;
	private Context context;
	private String persistanceName;
	private int persistanceVersion;
	private PersistanceImpl persistanceImpl;
	private AndroidPersistanceManager(Context context, String persistanceName, int persistanceVersion) {
		this.context = context;
		this.persistanceName = persistanceName;
		this.persistanceVersion = persistanceVersion;
	}
	
	public static AndroidPersistanceManager getInstance(Context context, String persistanceName, int persistanceVersion){
		if(manager==null){
			manager = new AndroidPersistanceManager(context,persistanceName,persistanceVersion);
			manager.setupPersistanceImplementation();
		}
		
		return manager;
	}
	
	private void setupPersistanceImplementation(){
		persistanceImpl = new AndroidSqlLitePersitanceImpl(context,persistanceVersion,persistanceName);
	}
	
	
	public void create(Object persistObj)throws AndroidPersistableException{
		verifyPersistable(persistObj);
		persistanceImpl.create(persistObj);
	}
	
	public APCursor retrieve(Object retrieveObj)throws AndroidPersistableException{
		verifyPersistable(retrieveObj);
		return persistanceImpl.retrieve(retrieveObj);
	}
	
	public int delete(Object deleteObj)throws AndroidPersistableException{
		verifyPersistable(deleteObj);
		return persistanceImpl.delete(deleteObj);
	}
	
	public int update(Object updateObj) throws AndroidPersistableException{
		verifyPersistable(updateObj);
		return persistanceImpl.update(updateObj);
	}
	public void open(){
		persistanceImpl.openDatastore();
	}
	
	public void close(){
		persistanceImpl.closeDatastore();
	}
	
	private boolean verifyPersistable(Object persistObj)throws AndroidPersistableException{
		boolean persistable = false;
		if (persistObj != null) {
			Object annonation = persistObj.getClass().getAnnotation(
					AndroidPersistable.class);
			if (annonation != null)
				persistable = true;
			else
				throw new AndroidPersistableException(
						AndroidPersistableException.NOT_PERSISTABLE_EXCEPTION);

			String msg = "Creating Persistance for "
					+ persistObj.getClass().getName();
			Log.i(LOG_STRING, msg);
		}
		return persistable;
	}
}
