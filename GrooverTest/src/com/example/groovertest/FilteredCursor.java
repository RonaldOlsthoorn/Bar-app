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
	private Cursor base;
	
	public FilteredCursor(Cursor cursor) {
		super(cursor);
		base = cursor;

		filterMap =  new ArrayList<Integer>(cursor.getCount());
		
	}
	
	public Cursor getCursorWrapper(){	
		return base;
	}
	
	public void setAll(){
		
		clearAllRows();
				
		for(int i =0 ; i<super.getCount();i++){
			filterMap.add(Integer.valueOf(i));
		}
	}

	public void clearAllRows(){
		
		filterMap.clear();
	}

	public boolean addPos(int pos){
		
		boolean res=false;
		
		if(!filterMap.contains(Integer.valueOf(pos))){
			
			res = true;
			filterMap.add(Integer.valueOf(pos));
			
			if(mPos>pos){
				mPos++;
			}
			sort();
			if(mPos<getCount() && mPos >-1 ){
				super.moveToPosition(filterMap.get(mPos));
			}
		}
		
		return res;
		
	}
	
	public boolean addId(int int1) {
		// TODO Auto-generated method stub
				
		super.moveToFirst();	
		
		
		while(super.getPosition()<super.getCount()){
			

			if(super.getInt(0) == int1){
				addPos(super.getPosition());
				return true;
			}
			super.moveToNext();
		}
		return false;
	}
	
	public void filter(int pos){
		
		filterMap.remove(pos);
				
		if(pos<=mPos && mPos>0){
			
			mPos--;

		}
		sort();
		
		if(!filterMap.isEmpty() && mPos>-1){
			
			super.moveToPosition(filterMap.get(mPos));
			
		}

	}
	
	public void filterIntern(int pos){
		
		boolean res = filterMap.remove(Integer.valueOf(pos));
		if(res && mPos>0 && pos<=filterMap.get(mPos) ){
			mPos--;
		}
		
		sort();
		
		if(!filterMap.isEmpty() && mPos>-1){
			
			super.moveToPosition(filterMap.get(mPos));
			
		}		
	}
	
	public void filterId(int int1) {
		// TODO Auto-generated method stub
		
		super.moveToFirst();	
		
		while(super.getPosition()<super.getCount()){
						
			if(super.getInt(0) == int1){
				
				filterIntern(super.getPosition());
				
				break;
				
			}			
			
			super.moveToNext();
			
		}
	}
	
	
	public void sort(){
		
		Collections.sort(filterMap);
	}
	
	
	public int getUnfilteredPosition(){
		return filterMap.get(mPos);
	}




	public FilteredCursor mirrorCursor(){

		FilteredCursor res = new FilteredCursor(base);
		res.setAll();
		
		for(int i =0; i<filterMap.size();i++){
			
			res.filterIntern(filterMap.get(i));
		}

		return res;
	}
	
	public Cursor getBase(){
		
		return base;
	}
	
	
	
	@Override
	public int getCount() { return filterMap.size(); }

	@Override
	public boolean moveToPosition(int pos) {
		
		if(pos < 0 ){	
			mPos = -1;			
			return false;		
		}
		
		if(pos >= filterMap.size()){
			
			mPos=filterMap.size();
			return false;
		}
	
		int res = filterMap.get(pos);
		
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

	public List<Integer> getMap() {
		// TODO Auto-generated method stub
		return filterMap;
	}
	
	public int getSuperPosition(){
		
		return super.getPosition();
	}
}
