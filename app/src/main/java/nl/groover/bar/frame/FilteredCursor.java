package nl.groover.bar.frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Cursor with extra functionality. Able to Hide/Show rows of the cursor 
 */
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
	
	/**
	 * Set all rows to visible
	 */
	public void setAll(){
		
		clearAllRows();
				
		for(int i =0 ; i<super.getCount();i++){
			filterMap.add(Integer.valueOf(i));
		}
	}

	/**
	 * Set all rows to invisible
	 */
	public void clearAllRows(){
		
		filterMap.clear();
	}

	/**
	 * Set a row at position pos to visible. pos is the position on 
	 * the base cursor.
	 * 
	 * Returns true if operation succeeded otherwise false 
	 * (row was already visible or non-existent)
	 */
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
	
	/**
	 * Set a row at with identifier id to visible. Assumed is that 
	 * There is an identifier column at column 0. Otherwise this 
	 * function crashes like a meteor.
	 * 
	 * If there is a row found with identifier id then this will be set visible,
	 * the function returns true.
	 * Otherwise false is returned.
	 */
	public boolean addId(int id) {
		super.moveToFirst();	
		
		
		while(super.getPosition()<super.getCount()){
			

			if(super.getInt(0) == id){
				addPos(super.getPosition());
				return true;
			}
			super.moveToNext();
		}
		return false;
	}
	
	/**
	 * Sets a row at position pos to invisible. pos is the position of
	 * the filtered cursor.
	 */
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
	
	/**
	 * Sets a row at position pos to invisible. pos is the position of
	 * the base cursor.
	 */
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
	
	/**
	 * Sets a row at with identifier id to invisible. Assumed is that the 
	 * identifier column is at column position 0.
	 */
	public void filterId(int id) {
		super.moveToFirst();	
		
		while(super.getPosition()<super.getCount()){
						
			if(super.getInt(0) == id){
				
				filterIntern(super.getPosition());
				break;
			}			
			super.moveToNext();
		}
	}
	
	/**
	 * Sorts the filtered cursor
	 */
	public void sort(){
		
		Collections.sort(filterMap);
	}
	
	/**
	 * Returns the position of the base cursor
	 */
	public int getUnfilteredPosition(){
		return filterMap.get(mPos);
	}

	/**
	 * Returns a filtered cursor with all the 
	 * invisible entries of current cursor visible and vice versa
	 */
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
		return filterMap;
	}
	
	public int getSuperPosition(){
		
		return super.getPosition();
	}
}