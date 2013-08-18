package com.groover.bar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Stack;

import com.example.groovertest.R;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class NumPadAdapter implements OnClickListener{

	int number;
	private Stack<PropertyChangeListener> listeners; 
	
	public NumPadAdapter(int start){
		
		number = start;
		listeners = new Stack<PropertyChangeListener>();
	}
	
	public NumPadAdapter(){
		
		number = 0;
		listeners = new Stack<PropertyChangeListener>();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		int buttonId = v.getId();
		switch(buttonId){
		case R.numPad.one: number = Integer.parseInt(number+"1"); 	
		break;
		case R.numPad.two: number = Integer.parseInt(number+"2"); 		
		break;
		case R.numPad.three: number = Integer.parseInt(number+"3"); 		
		break;
		case R.numPad.four: number = Integer.parseInt(number+"4"); 		
		break;
		case R.numPad.five: number = Integer.parseInt(number+"5"); 		
		break;
		case R.numPad.six: number = Integer.parseInt(number+"6"); 		
		break;
		case R.numPad.seven: number = Integer.parseInt(number+"7"); 		
		break;
		case R.numPad.eight: number = Integer.parseInt(number+"8"); 		
		break;
		case R.numPad.nine: number = Integer.parseInt(number+"9"); 		
		break;
		case R.numPad.zero: number = Integer.parseInt(number+"0"); 		
		break;
		case R.numPad.clear: number = 0;
		break;
		case R.numPad.plus: number++;
		break;
		case R.numPad.minus: number--;
		break;
		}
		
		PropertyChangeEvent p = new PropertyChangeEvent(this, "amount", new Integer(0), new Integer(number));
		
		for(int i =0; i<listeners.size();i++){
			
			listeners.get(i).propertyChange(p);
		}
	}
	
	public int getValue(){
		
		return number;
	}
	
	public void setIntValue(int i){
		
		number = i;
	}

	public void addPropertyListener(PropertyChangeListener c) {
		// TODO Auto-generated method stub
		listeners.add(c);
	}
}
