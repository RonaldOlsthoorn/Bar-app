package com.groover.bar.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.groover.bar.frame.DBHelper;

public class MemberImporter {

	private DBHelper DB;
	private File file;

	public MemberImporter(File file) {

		this.file = file;
	}

	public MemberImporter(){}

	public boolean importMembers() {

		boolean res = false;
		
		Document doc = getDomElement(file.getAbsolutePath());
		
		if(doc == null){
			
			return res;
		}
		
		
		
		return res;
	}

	public boolean importMembers(File f) {

		file = f;

		return importMembers();
	}
	
	public Document getDomElement(String path){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
 
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(path));
            doc = db.parse(is); 
 
            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
                // return DOM
            return doc;
    }
}
