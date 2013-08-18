package com.groover.bar;

import android.database.Cursor;
import android.util.Log;

public class ArticleFactory {

	private Cursor source;
	
	
	public ArticleFactory(Cursor c){
		
		source = c;
	}
	
	public Article getArticle(int id){

		source.moveToFirst();
		while(source.getPosition()<source.getCount()){
			
			if(source.getInt(0)==id){
				return new Article(id,source.getDouble(2),source.getString(1));
			}
			
			source.moveToNext();
		}
		return null;
	}
}
