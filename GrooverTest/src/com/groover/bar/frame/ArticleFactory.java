package com.groover.bar.frame;

import android.database.Cursor;
import android.util.Log;

//Creates articles as proxy objects from the database
public class ArticleFactory {

	private Cursor source;
	
	//pre: Cursor c contains all the articles stored in the database.
	//returns factory which is able to produce all articles in the database
	public ArticleFactory(Cursor c){
		
		source = c;
	}
	
	//pre: id is the identifier (same id as stored in the database) of the article
	//returns the article. If article is not in the database it returns null
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
