package nl.groover.bar.frame;

import android.database.Cursor;
import android.database.CursorWrapper;

public class SearchCursor extends CursorWrapper{

	public SearchCursor(Cursor cursor) {
		super(cursor);
	}
	
	public boolean moveToId(int id){
		
		if(super.getCount()>0){
			
			super.moveToFirst();
			
			while(super.getPosition()<super.getCount()){
				
				if(super.getInt(0)== id){
					
					return true;
				}
				
				super.moveToNext();
			}
			
			return false;
		}
		return false;
	}

}
