package com.groover.bar.frame;

//improvised class to enhance IOExceptions to our needs
public class IOReport {
	
	public static final String CAUSE_UNKNOWN = "unknown_cause";
	public static final String CAUSE_UNACCESSIBLE_SD = "unaccessible_sd";
	public static final String CAUSE_NO_SD_MOUNTED = "no_sd";
	public static final String CAUSE_WRITING_EXCEPTION = "writing_error";
	public static final String CAUSE_READING_EXCEPTION = "reading_error";
	
	private boolean succes;
	private String cause;
	
	public IOReport(boolean s, String c ){
		
		succes = s;
		cause = c;
		
	}
	
	public boolean getSucces(){return succes;}
	public String getCause(){return cause;}
	
	public void setSucces(boolean s){
		succes = s;
	}
	
	public void setCause(String c){
		cause = c;
	}
}
