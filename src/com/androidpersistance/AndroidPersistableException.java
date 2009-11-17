package com.androidpersistance;

public class AndroidPersistableException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int NOT_PERSISTABLE_EXCEPTION=1;
	public static int TABLE_DOES_NOT_EXIST_EXCEPTION=2;
	public static int NO_PRIMARY_KEY_DEFINED_EXCEPTION=3;
	
	public AndroidPersistableException(int exceptionType) {
		super("PersistException");
	}

}
