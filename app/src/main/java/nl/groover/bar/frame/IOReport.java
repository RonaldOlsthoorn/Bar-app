package nl.groover.bar.frame;

//improvised class to enhance IOExceptions to our needs
public class IOReport {

	public static final String CAUSE_UNKNOWN = "unknown_cause";
	public static final String CAUSE_UNACCESSIBLE_SD = "unaccessible_sd";
	public static final String CAUSE_NO_SD_MOUNTED = "no_sd";
	public static final String CAUSE_WRITING_EXCEPTION = "writing_error";
	public static final String CAUSE_READING_EXCEPTION = "reading_error";
	public static final String CAUSE_ZERO_GROUP = "zero_group";


	private boolean succes;
	private String cause;
	private String groupName;

	public IOReport(boolean s, String c) {

		succes = s;
		cause = c;
	}

	public IOReport(boolean s, String c, String gn) {

		succes = s;
		cause = c;
		groupName = gn;
	}

	public boolean getSucces() {
		return succes;
	}

	public String getCause() {
		return cause;
	}

	public void setSucces(boolean s) {
		succes = s;
	}

	public void setCause(String c) {
		cause = c;
	}


	public String getGroupName() {
		return groupName;
	}
}