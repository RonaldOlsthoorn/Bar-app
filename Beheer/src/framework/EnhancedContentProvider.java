package framework;

import android.content.Context;


public class EnhancedContentProvider {

	private DB dbHelper; // add final
	private static EnhancedContentProvider singleton;
	
	public static EnhancedContentProvider getCP(Context context){
		
		if (singleton == null) {
			singleton = new EnhancedContentProvider(context);
		}
		return singleton;
	}
	
	public EnhancedContentProvider(Context context){
		
		dbHelper = DB.getDB(context);
	}
}