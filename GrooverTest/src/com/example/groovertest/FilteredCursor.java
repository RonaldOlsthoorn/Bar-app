package com.example.groovertest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

public class FilteredCursor extends CursorWrapper{

	
	private List<Integer> filterMap;

	private int mPos = -1;
	
	public FilteredCursor(Cursor cursor) {
		super(cursor);
		// TODO Auto-generated constructor stub
		filterMap =  new ArrayList<Integer>(cursor.getCount());
		
		for(int i =0; i<cursor.getCount();i++){
			filterMap.add(Integer.valueOf(i));
		}
		
	}
	
	@Override
	public int getCount() { return filterMap.size(); }

	@Override
	public boolean moveToPosition(int pos) {
		

		
		if(pos < 0 || pos > filterMap.size()){	
			return false;		
		}
		
		if(pos == filterMap.size()){
			
			Integer res = filterMap.get(pos-1);
			
			boolean moved = super.moveToPosition(res);
		    if (moved) mPos = pos;
		    return moved;
		    
		}
		
		Integer res = filterMap.get(pos); 	
	    boolean moved = super.moveToPosition(res);

	    if (moved) mPos = pos;
	    return moved;
	}

	@Override
	public final boolean move(int offset) {
	    return moveToPosition(mPos + offset);
	}

	@Override
	public final boolean moveToFirst() {
	    return moveToPosition(0);
	}

	@Override
	public final boolean moveToLast() {
	    return moveToPosition(getCount() - 1);
	}

	@Override
	public final boolean moveToNext() {
	    return moveToPosition(mPos + 1);
	}

	@Override
	public final boolean moveToPrevious() {
	    return moveToPosition(mPos - 1);
	}

	@Override
	public final boolean isFirst() {
	    return mPos == 0 && getCount() != 0;
	}

	@Override
	public final boolean isLast() {
	    int cnt = getCount();
	    return mPos == (cnt - 1) && cnt != 0;
	}

	@Override
	public final boolean isBeforeFirst() {
	    if (getCount() == 0) {
	        return true;
	    }
	    return mPos == -1;
	}

	@Override
	public final boolean isAfterLast() {
	    if (getCount() == 0) {
	        return true;
	    }
	    return mPos >= getCount();
	}

	@Override
	public int getPosition() {

	    return mPos;
	}
	
	public void addPos(int pos){
		
		filterMap.add(Integer.valueOf(pos));
		Collections.sort(filterMap);
	}
	
	public void filter(int pos){
		
		filterMap.remove(pos);
		Collections.sort(filterMap);
		
	}
	
	public void clearAllRows(){
		
		filterMap.clear();
	}
}
