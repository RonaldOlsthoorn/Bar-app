package com.groover.bar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import android.provider.BaseColumns;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public static final String TAG = "DB";
	public static int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "GrooverMembers.db";

	private static DBHelper singleton;

	public static DBHelper getDBHelper(Context context) {

		if (singleton == null) {
			singleton = new DBHelper(context);

		}
		return singleton;

	}

	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public Cursor getMembers() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(MemberTable.TABLE_NAME, null,
				null, null, null, null, MemberTable.COLUMN_FIRST_NAME
						+ " COLLATE NOCASE ASC, "+MemberTable.COLUMN_LAST_NAME+ " COLLATE NOCASE ASC");
	}
	
	public Cursor getListMembers() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(MemberTable.TABLE_NAME, null,
				MemberTable.COLUMN_ACTIVE+" = 1", null, null, null, MemberTable.COLUMN_FIRST_NAME
						+ " COLLATE NOCASE ASC, "+MemberTable.COLUMN_LAST_NAME+ " COLLATE NOCASE ASC");
	}
	
	public Cursor getGroupsFancy() {
		
		SQLiteDatabase db;
		db = getReadableDatabase();
		String query = "SELECT "+GroupTable._ID+" , "+GroupTable.COLUMN_GROUP_NAME+" , COUNT("+GroupMembers.COLUMN_NAME_MEMBER_ID+") AS COUNT_MEMBERS"
				+" FROM "+GroupTable.TABLE_NAME+" LEFT OUTER JOIN "+GroupMembers.TABLE_NAME+" ON "+GroupTable.TABLE_NAME+"."+GroupTable._ID+"="+GroupMembers.TABLE_NAME+"."+GroupMembers.COLUMN_NAME_GROUP_ID
				+" GROUP BY "+GroupTable._ID
				+" ORDER BY "+GroupTable.COLUMN_GROUP_NAME+" COLLATE NOCASE";
			
		return db.rawQuery(query, null);
	}
	
	public Cursor getGroups(){
		
		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(GroupTable.TABLE_NAME, null,
				null, null, null, null, GroupTable.COLUMN_GROUP_NAME
						+ " COLLATE NOCASE ASC ");
	}
	
	public Cursor getListGroups(){
		
		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(GroupTable.TABLE_NAME, null,
				GroupTable.COLUMN_ACTIVE+" = 1", null, null, null, GroupTable.COLUMN_GROUP_NAME
						+ " COLLATE NOCASE ASC ");
		
	}
	
	public Cursor getGroupMembers(int grId){
		
		SQLiteDatabase db;
		db = getReadableDatabase();

		String query = "SELECT "+MemberTable._ID+" , "+MemberTable.COLUMN_FIRST_NAME+" , "+MemberTable.COLUMN_LAST_NAME+" , "+MemberTable.COLUMN_ACCOUNT
				+" FROM "+GroupMembers.TABLE_NAME+" , "+MemberTable.TABLE_NAME
				+" WHERE "+GroupMembers.TABLE_NAME+"."+GroupMembers.COLUMN_NAME_MEMBER_ID+"="+MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_ID
				+" AND "+GroupMembers.COLUMN_NAME_GROUP_ID + " = "+grId
				+" ORDER BY "+MemberTable.COLUMN_FIRST_NAME+" COLLATE NOCASE ASC, "+MemberTable.COLUMN_LAST_NAME+" COLLATE NOCASE ASC";
			
		return db.rawQuery(query, null);
		
	}
		
	public Cursor getAccounts() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(AccountList.TABLE_NAME, null, null, null, null, null,
				null);
	}
	
	public Cursor getArticles() {
		
		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(ItemList.TABLE_NAME, null, null, null, null, null,
				ItemList.COLUMN_NAME_CAT+" COLLATE NOCASE ASC, "+ItemList.COLUMN_NAME_ITEM+" COLLATE NOCASE ASC");
	}
	
	public Cursor getCategories() {
		
		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(ItemList.TABLE_NAME, new String[]{ItemList.COLUMN_ID,ItemList.COLUMN_NAME_CAT}, null, null, ItemList.COLUMN_NAME_CAT, null,
				ItemList.COLUMN_NAME_CAT+" COLLATE NOCASE ASC");
	}

	@SuppressWarnings("finally")
	public long insertOrIgnore(String table, ContentValues values) {

		long res = -1;
		Log.d(TAG, "insertOrIgnore on " + values);
		SQLiteDatabase db = getWritableDatabase();
		try {
			res = db.insertOrThrow(table, null, values);
		} catch (SQLException e) {
			Log.d(TAG, "insertOrIgnore on " + values + " fail");
		} finally {
			db.close();
			return res;
		}
	}

	@SuppressWarnings("finally")
	public boolean updateOrIgnore(String table, int memberid,
			ContentValues values) {

		boolean res = false;
		Log.d(TAG, "updateOrIgnore on "+table+" values" + values + " " + memberid);
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.update(table, values, MemberTable.COLUMN_ID + "=" + memberid,
					null);
			res = true;

		} catch (SQLException e) {
			res = false;
		} finally {
			db.close();
			return res;
		}
	}

	public boolean PayOffGroupOrIgnore(int groupid){

		Cursor members = getGroupMembers(groupid);
		SQLiteDatabase db;
		db = getReadableDatabase();
		Cursor group = db.query(GroupTable.TABLE_NAME, new String[]{GroupTable.COLUMN_GROUP_ACCOUNT , GroupTable.COLUMN_GROUP_BALANCE}, GroupTable._ID+" = "+groupid, null, null, null, null);
		group.moveToFirst();
		members.moveToFirst();
		double avg = group.getDouble(1)/((double)members.getCount());

		while(members.getPosition()<members.getCount()){
			
			ContentValues v = new ContentValues();
			v.put(Order.COLUMN_TOTAL, avg);
			v.put(Order.COLUMN_ACCOUNT, members.getInt(3));
			v.put(Order.COLUMN_TS_CREATED, "NOW");
			
			long l = insertOrIgnore(Order.TABLE_NAME,v);
			
			if(l==-1){return false;}
			
			members.moveToNext();
		}

		db.close();
		
		return true;
	}
	
	@SuppressWarnings("finally")
	public boolean deleteOrIgnore(String table, int memberid) {

		boolean res = false;
		Log.d(TAG, "deleteOrIgnore on " + memberid);
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.delete(table, MemberTable.COLUMN_ID + "=" + memberid, null);
			res = true;

		} catch (SQLException e) {
			res = false;
		} finally {
			db.close();
			return res;
		}
	}
	
	public boolean deleteGroupMembers(int groupId){
		
		Cursor groupmembers = getGroupMembers(groupId);
		groupmembers.moveToFirst();
		
		SQLiteDatabase db = getWritableDatabase();
		
		while(groupmembers.getPosition()<groupmembers.getCount()){
			
			Log.d(TAG, "deleteOrIgnore on " + groupmembers.getInt(0));
					
			try {
				db.delete(GroupMembers.TABLE_NAME, 
						GroupMembers.COLUMN_NAME_MEMBER_ID + "=" + groupmembers.getInt(0)+" AND "+GroupMembers.COLUMN_NAME_GROUP_ID + "="+groupId, 
						null);

			} catch (SQLException e) {
				db.close();
				return false;
			} 
			
			groupmembers.moveToNext();
		}
		db.close();
		return true;
	}
	
	public boolean deleteGroupOrIgnore(int groupId){		
		
		boolean res = true; 
		res = deleteGroupMembers(groupId);
		
		if(res = false){
			return false;
		}
		
		res = deleteOrIgnore(DBHelper.GroupTable.TABLE_NAME, groupId);
		return res;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(MemberTable.SQL_CREATE_TABLE);
		db.execSQL(MemberTable.TRIGGER_NEW_ACCOUNT);
		db.execSQL(GroupTable.SQL_CREATE_TABLE);
		db.execSQL(GroupMembers.SQL_CREATE_TABLE);
		db.execSQL(ItemList.SQL_CREATE_TABLE);
		db.execSQL(AccountList.SQL_CREATE_TABLE);
		db.execSQL(Order.SQL_CREATE_TABLE);
		db.execSQL(Order.SQL_TRIGGER_UPDATE_TOTAL_1);
		db.execSQL(Order.SQL_TRIGGER_UPDATE_TOTAL_2);
		db.execSQL(Order.SQL_TRIGGER_UPDATE_TOTAL_3);
		db.execSQL(Order.SQL_TRIGGER_UPDATE_TOTAL_4);
		db.execSQL(Consumption.SQL_CREATE_TABLE);
		db.execSQL(GroupClearances.SQL_CREATE_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(MemberTable.SQL_DELETE_ENTRIES);
		db.execSQL(GroupTable.SQL_DELETE_ENTRIES);
		db.execSQL(GroupMembers.SQL_DELETE_ENTRIES);
		db.execSQL(ItemList.SQL_DELETE_ENTRIES);
		db.execSQL(AccountList.SQL_DELETE_ENTRIES);
		db.execSQL(Order.SQL_DELETE_ENTRIES);
		db.execSQL(Consumption.SQL_DELETE_ENTRIES);
		db.execSQL(GroupClearances.SQL_DELETE_ENTRIES);

		onCreate(db);
		DATABASE_VERSION = newVersion;
	}

	public static abstract class MemberTable implements BaseColumns {

		public static final String TABLE_NAME = "members";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_FIRST_NAME = "first_name";
		public static final String COLUMN_LAST_NAME = "last_name";
		public static final String COLUMN_ACCOUNT = "account";
		public static final String COLUMN_BALANCE = "balance";
		public static final String COLUMN_ACTIVE = "active";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COLUMN_FIRST_NAME
				+ " TEXT NOT NULL"
				+ ","
				+ COLUMN_LAST_NAME
				+ " TEXT NOT NULL"
				+ ","
				+ COLUMN_ACCOUNT
				+ " INTEGER "
				+ ","
				+ COLUMN_BALANCE
				+ " DECIMAL(10,2) DEFAULT 0"
				+ ","
				+ COLUMN_ACTIVE
				+ " BOOLEAN DEFAULT 1"
				+ " )";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static final String TRIGGER_NEW_ACCOUNT = "CREATE TRIGGER create_new_account "  
				
				+ "AFTER INSERT ON "
				+ TABLE_NAME
				+ " FOR EACH ROW "
				+ "WHEN NEW."
				+ COLUMN_ACCOUNT
				+ " IS NULL "
				+ "BEGIN "
				+ "INSERT INTO "
				+ AccountList.TABLE_NAME
				+ "("
				+ AccountList.COLUMN_BALANCE
				+ ", "
				+ AccountList.COLUMN_TYPE
				+ ") "
				+ " VALUES(0,'individual'); "
				+ "UPDATE "
				+ TABLE_NAME
				+ " SET "
				+ COLUMN_ACCOUNT
				+ " = last_insert_rowid() "
				+ "WHERE "
				+ COLUMN_ID
				+ "= NEW."
				+ AccountList.COLUMN_ACCOUNT
				+ ";" + "END";
		
	}

	public static abstract class GroupTable implements BaseColumns {

		public static final String TABLE_NAME = "groups";
		public static final String COLUMN_GROUP_NAME = "group_name";
		public static final String COLUMN_GROUP_ACCOUNT = "group_account";
		public static final String COLUMN_GROUP_BALANCE = "group_balance";
		public static final String COLUMN_ACTIVE = "active";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COLUMN_GROUP_NAME
				+ " TEXT NOT NULL ,"
				+ COLUMN_GROUP_ACCOUNT
				+ " INTEGER ,"
				+ COLUMN_GROUP_BALANCE
				+ " DECIMAL(10,2) DEFAULT 0"
				+ ","
				+ COLUMN_ACTIVE
				+ " BOOLEAN DEFAULT 1 "
				+ ");";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
		
		public static final String TRIGGER_NEW_ACCOUNT = "CREATE TRIGGER create_new_account "
				+ "AFTER INSERT ON "
				+ TABLE_NAME
				+ " FOR EACH ROW "
				+ "WHEN NEW."
				+ COLUMN_GROUP_ACCOUNT
				+ " IS NULL "
				+ "BEGIN "
				+ "INSERT INTO "
				+ AccountList.TABLE_NAME
				+ "("
				+ AccountList.COLUMN_BALANCE
				+ ", "
				+ AccountList.COLUMN_TYPE
				+ ") "
				+ " VALUES(0,'group'); "
				+ "UPDATE "
				+ TABLE_NAME
				+ " SET "
				+ COLUMN_GROUP_ACCOUNT
				+ " = last_insert_rowid() "
				+ "WHERE "
				+ _ID
				+ "= NEW."
				+ AccountList.COLUMN_ACCOUNT
				+ ";" + "END";
		
	}

	public static abstract class GroupMembers implements BaseColumns {

		public static final String TABLE_NAME = "group_members";
		public static final String COLUMN_NAME_GROUP_ID = "group_id";
		public static final String COLUMN_NAME_MEMBER_ID = "member_id";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_NAME_GROUP_ID
				+ " INT"
				+ ","
				+ COLUMN_NAME_MEMBER_ID
				+ " INT"
				+ ","
				+ "PRIMARY KEY("
				+ COLUMN_NAME_GROUP_ID
				+ ","
				+ COLUMN_NAME_MEMBER_ID
				+ "))";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	public static abstract class ItemList implements BaseColumns {

		public static final String TABLE_NAME = "item_list";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_NAME_ITEM = "item_name";
		public static final String COLUMN_NAME_PRICE = "item_price";
		public static final String COLUMN_NAME_CAT = "item_category";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ COLUMN_NAME_ITEM
				+ " TEXT NOT NULL , "
				+ COLUMN_NAME_PRICE
				+ " DECIMAL(10,2) ,"
				+ COLUMN_NAME_CAT + " TEXT DEFAULT 'overig' )";
		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

	}

	public static abstract class AccountList implements BaseColumns {

		public static final String TABLE_NAME = "account_list";
		public static final String COLUMN_ACCOUNT = _ID;
		public static final String COLUMN_TYPE = "type";
		public static final String COLUMN_BALANCE = "balance";
		public static final String SQL_CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_ACCOUNT
				+ " INTEGER PRIMARY KEY AUTOINCREMENT"
				+ ","
				+ COLUMN_TYPE
				+ " TEXT NOT NULL" + "," + COLUMN_BALANCE + " DOUBLE" + ")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	public static abstract class Order implements BaseColumns {

		public static final String TABLE_NAME = "orders";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_TOTAL = "total_amount";
		public static final String COLUMN_ACCOUNT = "client_account";
		public static final String COLUMN_TYPE = "order_type";
		public static final String COLUMN_TS_CREATED = "ts_created";
		public static final String COLUMN_TS_SETTLED = "ts_settled";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT"
				+ ","
				+ COLUMN_TOTAL
				+ " DECIMAL(10,2)"
				+ ","
				+ COLUMN_ACCOUNT
				+ " INTEGER NOT NULL"
				+ ","
				+ COLUMN_TS_CREATED
				+ " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL"
				+ ","
				+ COLUMN_TS_SETTLED
				+ " DATETIME" 
				+","
				+ COLUMN_TYPE
				+" TEXT NOT NULL"
				+")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
		
		public static final String SQL_TRIGGER_UPDATE_TOTAL_1 =  "CREATE TRIGGER update_total_1 "  
				
				+ "AFTER INSERT ON "
				+ TABLE_NAME
				+ " BEGIN "
				+ " UPDATE "
				+ MemberTable.TABLE_NAME
				+ " SET "
				+ MemberTable.COLUMN_BALANCE +" = NEW."+COLUMN_TOTAL+" + "+MemberTable.COLUMN_BALANCE
				+ " WHERE "
				+ MemberTable.COLUMN_ACCOUNT+" = NEW."+COLUMN_ACCOUNT
				+ ";" + "END";
		
	public static final String SQL_TRIGGER_UPDATE_TOTAL_2 =  "CREATE TRIGGER update_total_2 "  
				
				+ "AFTER DELETE ON "
				+ TABLE_NAME
				+ " BEGIN "
				+ " UPDATE "
				+ MemberTable.TABLE_NAME
				+ " SET "
				+ MemberTable.COLUMN_BALANCE +" = OLD."+COLUMN_TOTAL+" - "+MemberTable.COLUMN_BALANCE
				+ " WHERE "
				+ MemberTable.COLUMN_ACCOUNT+" = OLD."+COLUMN_ACCOUNT
				+ ";" + "END";
	
	public static final String SQL_TRIGGER_UPDATE_TOTAL_3 =  "CREATE TRIGGER update_total_3 "  
			
				+ "AFTER DELETE ON "
				+ TABLE_NAME
				+ " BEGIN "
				+ " UPDATE "
				+ MemberTable.TABLE_NAME
				+ " SET "
				+ MemberTable.COLUMN_BALANCE +" = OLD."+COLUMN_TOTAL+" - "+MemberTable.COLUMN_BALANCE
				+ " WHERE "
				+ MemberTable.COLUMN_ACCOUNT+" = OLD."+COLUMN_ACCOUNT
				+ ";" + "END";
		
	public static final String SQL_TRIGGER_UPDATE_TOTAL_4 =  "CREATE TRIGGER update_total_4 "  
			
				+ "AFTER DELETE ON "
				+ TABLE_NAME
				+ " BEGIN "
				+ " UPDATE "
				+ MemberTable.TABLE_NAME
				+ " SET "
				+ MemberTable.COLUMN_BALANCE +" = NEW."+COLUMN_TOTAL+" + "+MemberTable.COLUMN_BALANCE
				+ " WHERE "
				+ MemberTable.COLUMN_ACCOUNT+" = NEW."+COLUMN_ACCOUNT
				+ ";" + "END";
	
	}

	public static abstract class Consumption implements BaseColumns {

		public static final String TABLE_NAME = "consumptions";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_ARTICLE = "article";
		public static final String COLUMN_AMMOUNT = "ammount";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_ID
				+ " INTEGER PRIMARY KEY"
				+ ","
				+ COLUMN_ARTICLE
				+ " INTEGER"
				+ ","
				+ COLUMN_AMMOUNT
				+ " INTEGER"
				+ ")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
				
	}

	public static abstract class GroupClearances implements BaseColumns {

		public static final String TABLE_NAME = "clearance";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_GROUP = "group_id";
		public static final String COLUMN_GROUP_NAME = "group_name";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_ID
				+ " INTEGER PRIMARY KEY"
				+ ","
				+ COLUMN_GROUP
				+ " INTEGER"
				+ ","
				+ COLUMN_GROUP_NAME
				+ " TEXT"
				+ ")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}
}