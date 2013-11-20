package com.groover.bar.frame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/*
 * DBHelper is the applications connection to the database.
 * All the queries are stored as functions of this class.
 * These functions are called by all the classes of the application
 * that need information from the database, or need to updat/insert/delete.
 * 
 * Also, all the information about the databases' layout are stored in the inner classes
 * each inner class represents a table.
 * 
 * To secure thread safety, singleton pattern is used. Only ONE instance of this class exists.
 */
public class DBHelper extends SQLiteOpenHelper {

	public static final String TAG = "DB";
	public static int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "GrooverMembers.db";
	private static DBHelper singleton;
	
	//Returns the DBHelper object. Singleton pattern is used.
	public static DBHelper getDBHelper(Context context) {
		if (singleton == null) {
			singleton = new DBHelper(context);
		}
		return singleton;
	}

	//Constructor for new DBHelper object. 
	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * This first bunch of functions are all queries of some sort.
	 */
	
	/* Used by the autocompleteTextView in the select customer activity to
	 * 
	 * Returns a cursor with all the members which have constraint in
	 * their respective first or last name
	 */
	public Cursor getFilteredMember(String constraint) {
		SQLiteDatabase db = getReadableDatabase();

		return db.rawQuery("SELECT * FROM " + MemberTable.TABLE_NAME
				+ " WHERE " + MemberTable.COLUMN_FIRST_NAME + " LIKE \""
				+ constraint + "%\" OR " + MemberTable.COLUMN_LAST_NAME
				+ " LIKE \"" + constraint + "%\"", null);

	}

	/*
	 * Returns a cursor containing all members, Ordered by firstname lastname
	 */
	public Cursor getMembers() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(MemberTable.TABLE_NAME, null, null, null, null, null,
				MemberTable.COLUMN_FIRST_NAME + " COLLATE NOCASE ASC, "
						+ MemberTable.COLUMN_LAST_NAME + " COLLATE NOCASE ASC");

	}

	/*
	 * Returns a cursor containing all members which are active and can be
	 * displayed on the list (alle leden die actif op de turflijst staan)
	 */
	
	public Cursor getListMembers() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(MemberTable.TABLE_NAME, null, MemberTable.COLUMN_ACTIVE
				+ " = 1 ", null, null, null, MemberTable.COLUMN_FIRST_NAME
				+ " COLLATE NOCASE ASC, " + MemberTable.COLUMN_LAST_NAME
				+ " COLLATE NOCASE ASC");

	}

	/*
	 * Returns a cursor containing all groups with some extra information:
	 * It will contain how many members the group has as well
	 */
	public Cursor getGroupsFancy() {

		SQLiteDatabase db;
		db = getReadableDatabase();
		String query = "SELECT " + GroupTable.COLUMN_GROUP_ID + " , "
				+ GroupTable.COLUMN_GROUP_NAME + " , COUNT("
				+ GroupMembers.COLUMN_NAME_MEMBER_ID + ") AS COUNT_MEMBERS"
				+ " FROM " + GroupTable.TABLE_NAME + " LEFT OUTER JOIN "
				+ GroupMembers.TABLE_NAME + " ON " + GroupTable.TABLE_NAME
				+ "." + GroupTable.COLUMN_GROUP_ID + "="
				+ GroupMembers.TABLE_NAME + "."
				+ GroupMembers.COLUMN_NAME_GROUP_ID + " GROUP BY "
				+ GroupTable._ID + " ORDER BY " + GroupTable.COLUMN_GROUP_NAME
				+ " COLLATE NOCASE";

		return db.rawQuery(query, null);
	}

	/*
	 * Returns a cursor containing all the groups.
	 * 
	 */
	public Cursor getGroups() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(GroupTable.TABLE_NAME, null, null, null, null, null,
				GroupTable.COLUMN_GROUP_NAME + " COLLATE NOCASE ASC ");
	}

	/*
	 * Returns a cursor containing all the members of the group with 
	 * identifier grId 
	 */
	public Cursor getGroupMembers(int grId) {

		SQLiteDatabase db;
		db = getReadableDatabase();

		String query = "SELECT " + MemberTable.COLUMN_GR_ID + " , "
				+ MemberTable.COLUMN_FIRST_NAME + " , "
				+ MemberTable.COLUMN_LAST_NAME + " , "
				+ MemberTable.COLUMN_ACCOUNT + " FROM "
				+ GroupMembers.TABLE_NAME + " , " + MemberTable.TABLE_NAME
				+ " WHERE " + GroupMembers.TABLE_NAME + "."
				+ GroupMembers.COLUMN_NAME_MEMBER_ID + "="
				+ MemberTable.TABLE_NAME + "." + MemberTable.COLUMN_GR_ID
				+ " AND " + GroupMembers.COLUMN_NAME_GROUP_ID + " = " + grId
				+ " ORDER BY " + MemberTable.COLUMN_FIRST_NAME
				+ " COLLATE NOCASE ASC, " + MemberTable.COLUMN_LAST_NAME
				+ " COLLATE NOCASE ASC";

		return db.rawQuery(query, null);

	}

	/*
	 * Returns a cursor with all the groups that are on the list ("turflijst")
	 */
	public Cursor getListGroups() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(GroupTable.TABLE_NAME, null, GroupTable.COLUMN_ACTIVE
				+ " = 1", null, null, null, GroupTable.COLUMN_GROUP_NAME
				+ " COLLATE NOCASE ASC ");

	}

	/*
	 * Not used yet. Returns all accounts. Accounts can belong to both groups as members.
	 */
	public Cursor getAccounts() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(AccountList.TABLE_NAME, null, null, null, null, null,
				null);
	}

	
	/*
	 * Returns a cursor containing all articles that are stored in the database
	 */
	
	public Cursor getArticles() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(ItemList.TABLE_NAME, null, null, null, null, null,
				ItemList.COLUMN_NAME_CAT + " COLLATE NOCASE ASC, "
						+ ItemList.COLUMN_NAME_ITEM + " COLLATE NOCASE ASC");
	}

	/*
	 * Returns all the categories at which the articles are ordered (ie food, liquor, whisky etc)
	 * Currently NOT used
	 */
	public Cursor getCategories() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(ItemList.TABLE_NAME, new String[] { ItemList.COLUMN_ID,
				ItemList.COLUMN_NAME_CAT }, null, null,
				ItemList.COLUMN_NAME_CAT, null, ItemList.COLUMN_NAME_CAT
						+ " COLLATE NOCASE ASC");
	}

	/*
	 * Returns a cursor containing all consumptions. Note that group clearances ("groeps afrekeningen")
	 * are stored in another table. In this version the groups version is disabled so it does not matter.
	 * 
	 */
	public Cursor getConsumptions(int memberId) {

		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT " + Order.COLUMN_TOTAL + ","
				+ Order.COLUMN_TS_CREATED + "," + Order.COLUMN_TYPE + ","
				+ Consumption.COLUMN_ARTICLE_NAME + ","
				+ Consumption.COLUMN_AMMOUNT + ","
				+ Consumption.COLUMN_ARTICLE_PRICE + " FROM "
				+ Order.TABLE_NAME + " , " + Consumption.TABLE_NAME + " WHERE "
				+ Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT + "="
				+ memberId + " AND " + Order.TABLE_NAME + "." + Order.COLUMN_ID
				+ "=" + Consumption.TABLE_NAME + "." + Consumption.COLUMN_ID
				+ " ORDER BY " + Order.COLUMN_TS_CREATED;

		return db.rawQuery(query, null);
	}

	/*
	 * Returns a cursor containing all the groupclearances ("groep afrekeningen").
	 * Not used since the groups feature is disabled 
	 */
	
	public Cursor getGroupClearances(int memberId) {

		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT " + Order.COLUMN_TOTAL + ","
				+ Order.COLUMN_TS_CREATED + "," + Order.COLUMN_TYPE + ","
				+ GroupClearances.COLUMN_GROUP_NAME + " FROM "
				+ Order.TABLE_NAME + " , " + GroupClearances.TABLE_NAME
				+ " WHERE " + Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT
				+ "=" + memberId + " AND " + Order.TABLE_NAME + "."
				+ Order.COLUMN_ID + "=" + GroupClearances.TABLE_NAME + "."
				+ GroupClearances.COLUMN_ID + " AND " + " ORDER BY "
				+ Order.COLUMN_TS_CREATED;

		Log.d(TAG, query);

		return db.rawQuery(query, null);
	}

	
	/*
	 * inserts a row in a table denoted by String table. The contentvalues are inserted.
	 * Any Error is ignored. 
	 * Returns the inserted row id (_id in most cases) or -1 if the operation failed.
	 */
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

	/*
	 * Processes an update on a table of row "table" with identifier "id". The 
	 * new values are stored in the ContentValues.
	 * 
	 * Returns true if operation succeeded and false if not
	 */
	@SuppressWarnings("finally")
	public boolean updateOrIgnore(String table, int id, ContentValues values) {

		boolean res = false;
		Log.d(TAG, "updateOrIgnore on " + table + " values " + values + " "
				+ id);
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.update(table, values, getIdColumnName(table) + "=" + id, null);
			res = true;

		} catch (SQLException e) {
			Log.d(TAG, "updateOrIgnore on " + table + " values " + values + " "
					+ id + " fail");
			res = false;
		} finally {
			db.close();
			return res;
		}
	}

	/*
	 * Used when a group is adjusted or deleted or when the balance is made.
	 * Since the group feature is disabled it is not used at all. 
	 * 
	 * Makes group clearances for each of the group members.
	 * Returns true if succeeded otherwise false.
	 */
	public boolean PayOffGroupOrIgnore(int groupid) {

		Cursor members = getGroupMembers(groupid);
		SQLiteDatabase db;
		db = getReadableDatabase();
		Cursor group = db.query(GroupTable.TABLE_NAME,
				new String[] { GroupTable.COLUMN_GROUP_ACCOUNT,
						GroupTable.COLUMN_GROUP_BALANCE,
						GroupTable.COLUMN_GROUP_NAME }, GroupTable._ID + " = "
						+ groupid, null, null, null, null);
		group.moveToFirst();
		members.moveToFirst();
		double avg = group.getDouble(1) / ((double) members.getCount());

		while (members.getPosition() < members.getCount()) {

			ContentValues v = new ContentValues();
			v.put(Order.COLUMN_TOTAL, avg);
			v.put(Order.COLUMN_ACCOUNT, members.getInt(3));
			v.put(Order.COLUMN_TYPE, "group clearance: " + group.getString(2));

			long l = insertOrIgnore(Order.TABLE_NAME, v);

			if (l == -1) {
				return false;
			}

			members.moveToNext();
		}

		db.close();

		return true;
	}

	/*
	 * Deletes row from table with identifier id
	 * 
	 * Returns true if the operation succeeded otherwise false
	 */
	@SuppressWarnings("finally")
	public boolean deleteOrIgnore(String table, int id) {

		boolean res = false;
		Log.d(TAG, "deleteOrIgnore on " + table + " " + id);
		SQLiteDatabase db = getWritableDatabase();
		try {

			int i = db.delete(table, getIdColumnName(table) + "=" + id, null);
			res = true;

		} catch (SQLException e) {
			Log.d(TAG, "deleteOrIgnore on " + id + " fail");
			res = false;
		} finally {
			db.close();
			return res;
		}
	}
	
	/*
	 * Deletes all members from a group with identifier groupId
	 * 
	 * Returns true if the operation succeeded, false otherwise
	 */
	public boolean deleteGroupMembers(int groupId) {

		Cursor groupmembers = getGroupMembers(groupId);
		groupmembers.moveToFirst();

		SQLiteDatabase db = getWritableDatabase();

		while (groupmembers.getPosition() < groupmembers.getCount()) {

			Log.d(TAG, "deleteOrIgnore on " + groupmembers.getInt(0));

			try {
				db.delete(
						GroupMembers.TABLE_NAME,
						GroupMembers.COLUMN_NAME_MEMBER_ID + "="
								+ groupmembers.getInt(0) + " AND "
								+ GroupMembers.COLUMN_NAME_GROUP_ID + "="
								+ groupId, null);

			} catch (SQLException e) {
				db.close();
				return false;
			}

			groupmembers.moveToNext();
		}
		db.close();
		return true;
	}
	
	/*
	 * Deletes a group id groupId from the database.
	 */

	public boolean deleteGroupOrIgnore(int groupId) {

		boolean res = true;
		res = deleteGroupMembers(groupId);

		if (res = false) {
			return false;
		}

		res = deleteOrIgnore(DBHelper.GroupTable.TABLE_NAME, groupId);
		return res;
	}

	/*
	 * Deletes all orders in the database. Both consumptions as groupclearances are
	 * deleted. Usually done after making the balance.
	 */
	public void deleteAllOrders() {
		// TODO Auto-generated method stub
		SQLiteDatabase db = getWritableDatabase();
		db.delete(Order.TABLE_NAME, null, null);
		db.delete(Consumption.TABLE_NAME, null, null);
		db.delete(GroupClearances.TABLE_NAME, null, null);

	}
	
	/*
	 * Deletes all members from the database
	 */
	public void deleteAllMembers(){
		
		SQLiteDatabase db = getWritableDatabase();
		db.delete(MemberTable.TABLE_NAME, null, null);
	}

	/*
	 * Returns a string which represents the identifier column for a given table tableName
	 */
	public String getIdColumnName(String tableName) {

		if (tableName.equals(MemberTable.TABLE_NAME)) {
			return MemberTable.getIdColumnName();
		}
		if (tableName.equals(GroupTable.TABLE_NAME)) {
			return GroupTable.getIdColumnName();
		}
		if (tableName.equals(AccountList.TABLE_NAME)) {
			return AccountList.getIdColumnName();
		}
		if (tableName.equals(ItemList.TABLE_NAME)) {
			return ItemList.getIdColumnName();
		}
		if (tableName.equals(Order.TABLE_NAME)) {
			return Order.getIdColumnName();
		}
		if (tableName.equals(Consumption.TABLE_NAME)) {
			return Consumption.getIdColumnName();
		}
		if (tableName.equals(GroupClearances.TABLE_NAME)) {
			return GroupClearances.getIdColumnName();
		}

		return null;
	}

	/* Returns whether on not a backup needs to be made based on the number of
	 * orders since the last update. 
	 * Returns true if an update needs to be made
	 * otherwise false
	 */
	public boolean checkNeedToBackup() {

		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query(Order.TABLE_NAME, new String[]{Order.COLUMN_ID}, null, null, null, null, null);
		
		if(c.getCount()==0){
			return false;
		}

		c = db.query(BackupLog.TABLE_NAME, new String[]{BackupLog.COLUMN_ID}, null, null, null, null, null);
		
		if(c.getCount()==0){
			return true;
		}
		
		String inner = "SELECT "+ "MAX("+ BackupLog.COLUMN_TIME_STAMP + ") AS "+BackupLog.COLUMN_TIME_STAMP 
				+ " FROM "+ BackupLog.TABLE_NAME 
				+ " WHERE " 
				+ "("+ BackupLog.COLUMN_TYPE	+ "=\"upload\"" 
				+ " OR " +BackupLog.COLUMN_TYPE	+ "=\"SD\"" +")"
				+ " AND " + BackupLog.COLUMN_SUCCESS + "=1";
		
		String query = "SELECT * " 
				+" FROM " + Order.TABLE_NAME
				+ " WHERE "+ "("
				+ inner +")"
				+ " < " + Order.COLUMN_TS_CREATED;

		c = db.rawQuery(query, null);


		if (c.getCount() > 0) {
			return true;
		}
		return false; 

	}
	
	/*
	 * Returns whether on not a backup needs to be made based on the number of
	 * orders since the last update. Only SD balances are considered.
	 * Returns true if an update needs to be made
	 * otherwise false.
	 */
	public boolean checkNeedToBackupSD() {

		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query(Order.TABLE_NAME, new String[]{Order.COLUMN_ID}, null, null, null, null, null);
		
		Log.i("DB",c.getCount()+" orders");
		if(c.getCount()==0){
			return false;
		}

		c = db.query(BackupLog.TABLE_NAME, new String[]{BackupLog.COLUMN_ID}, DBHelper.BackupLog.COLUMN_TYPE+"=\"SD\"" +" AND "+DBHelper.BackupLog.COLUMN_SUCCESS+"=1", null, null, null, null);
		
		Log.i("DB",c.getCount()+" backups");
		if(c.getCount()==0){
			return true;
		}
		
		String inner = "SELECT "+ "MAX("+ BackupLog.COLUMN_TIME_STAMP + ") AS "+BackupLog.COLUMN_TIME_STAMP 
				+ " FROM "+ BackupLog.TABLE_NAME 
				+ " WHERE " 
				+ BackupLog.COLUMN_TYPE	+ "=\"SD\"" 
				+ " AND " + BackupLog.COLUMN_SUCCESS + "=1";
		
		c = db.rawQuery(inner, null);
		Log.i("DB",c.getCount()+" inner ");

		
		String query = "SELECT * " 
				+" FROM " + Order.TABLE_NAME
				+ " WHERE "+ "("
				+ inner +")"
				+ " < " + Order.COLUMN_TS_CREATED;

		c = db.rawQuery(query, null);
		Log.i("DB",query);
	

		if (c.getCount() > 0) {
			return true;
		}
		return false; 

	}
	
	/*
	 * Returns whether a certain id is present in a table.
	 * Useful for checking valid updates on for instance members.
	 */
	
	public boolean checkIdInTable(String table,int id){
		
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query(table,new String[]{ getIdColumnName(table)}, getIdColumnName(table)+"="+id, null, null, null, null);
		
		if(c.getCount()==0){
			
			return false;
		}
		
		return true;

	}

	/* Called when the database is first created. Creates all the tables and triggers.
	 * 
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(MemberTable.SQL_CREATE_TABLE);
		db.execSQL(MemberTable.TRIGGER_NEW_ACCOUNT);
		db.execSQL(MemberTable.TRIGGER_DEL_FROM_GROUPS);
		db.execSQL(GroupTable.SQL_CREATE_TABLE);
		db.execSQL(GroupTable.TRIGGER_NEW_ACCOUNT);
		db.execSQL(GroupMembers.SQL_CREATE_TABLE);
		db.execSQL(ItemList.SQL_CREATE_TABLE);
		db.execSQL(AccountList.SQL_CREATE_TABLE);
		db.execSQL(AccountList.SQL_TRIGGER_UPDATE_BALANCE1);
		db.execSQL(AccountList.SQL_TRIGGER_UPDATE_BALANCE2);
		db.execSQL(Order.SQL_CREATE_TABLE);
		db.execSQL(Order.SQL_TRIGGER_UPDATE_TOTAL_1);
		db.execSQL(Order.SQL_TRIGGER_UPDATE_TOTAL_2);
		db.execSQL(Order.SQL_TRIGGER_UPDATE_TOTAL_3);
		db.execSQL(Order.SQL_TRIGGER_UPDATE_TOTAL_4);
		db.execSQL(Consumption.SQL_CREATE_TABLE);
		db.execSQL(GroupClearances.SQL_CREATE_TABLE);
		db.execSQL(BackupLog.SQL_CREATE_TABLE);

	}

	/* Called when the database is updated. Simply removes all the tables and recreates them
	 * 
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
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
		db.execSQL(BackupLog.SQL_DELETE_ENTRIES);
		onCreate(db);
		DATABASE_VERSION = newVersion;
	}

	/*
	 * Inner class representing the table containing all the members
	 */
	public static abstract class MemberTable implements BaseColumns {

		public static final String TABLE_NAME = "members";
		public static final String COLUMN_GR_ID = _ID;
		public static final String COLUMN_FIRST_NAME = "first_name";
		public static final String COLUMN_LAST_NAME = "last_name";
		public static final String COLUMN_ACCOUNT = "account";
		public static final String COLUMN_BALANCE = "balance";
		public static final String COLUMN_ACTIVE = "active";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_GR_ID
				+ " INTEGER PRIMARY KEY ,"
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
				+ COLUMN_ACTIVE + " BOOLEAN DEFAULT 1" + " )";

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
				+ COLUMN_GR_ID
				+ "= NEW."
				+ COLUMN_GR_ID
				+ ";"
				+ "END";

		public static final String TRIGGER_DEL_FROM_GROUPS = "CREATE TRIGGER del_member_from_groups "

				+ "AFTER DELETE ON "
				+ TABLE_NAME
				+ " FOR EACH ROW "
				+ "BEGIN "
				+ "DELETE FROM "
				+ GroupMembers.TABLE_NAME
				+ " WHERE "
				+ GroupMembers.TABLE_NAME
				+ "."
				+ GroupMembers.COLUMN_NAME_MEMBER_ID
				+ "="
				+ "OLD"
				+ "."
				+ MemberTable.COLUMN_GR_ID + ";" + "END";

		private static String getIdColumnName() {
			return COLUMN_GR_ID;
		}
	}

	/*
	 * Inner class representing the table containing all the groups
	 */
	public static abstract class GroupTable implements BaseColumns {

		public static final String TABLE_NAME = "groups";
		public static final String COLUMN_GROUP_ID = _ID;
		public static final String COLUMN_GROUP_NAME = "group_name";
		public static final String COLUMN_GROUP_ACCOUNT = "group_account";
		public static final String COLUMN_GROUP_BALANCE = "group_balance";
		public static final String COLUMN_ACTIVE = "active";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_GROUP_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COLUMN_GROUP_NAME
				+ " TEXT NOT NULL ,"
				+ COLUMN_GROUP_ACCOUNT
				+ " INTEGER ,"
				+ COLUMN_GROUP_BALANCE
				+ " DECIMAL(10,2) DEFAULT 0"
				+ ","
				+ COLUMN_ACTIVE + " BOOLEAN DEFAULT 1 " + ");";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static final String TRIGGER_NEW_ACCOUNT = "CREATE TRIGGER create_new_account2 "
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
				+ ";"
				+ "END";

		public static String getIdColumnName() {
			// TODO Auto-generated method stub
			return COLUMN_GROUP_ID;
		}

	}

	/*
	 * Inner class representing the table containing all the members which
	 * are in groups
	 */
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
				+ COLUMN_NAME_GROUP_ID + "," + COLUMN_NAME_MEMBER_ID + "))";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
	}

	/*
	 * Inner class representing the table containing all the articles
	 */
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

		public static String getIdColumnName() {
			// TODO Auto-generated method stub
			return COLUMN_ID;
		}

	}

	/*
	 * Inner class representing the table containing all the accounts
	 * belonging to both members and groups
	 */
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

		public static final String SQL_TRIGGER_UPDATE_BALANCE1 = "CREATE TRIGGER update_balance1 "

				+ "AFTER UPDATE ON "
				+ TABLE_NAME
				+ " WHEN( OLD."
				+ COLUMN_TYPE
				+ "='individual')"
				+ " BEGIN "
				+ " UPDATE "
				+ MemberTable.TABLE_NAME
				+ " SET "
				+ MemberTable.COLUMN_BALANCE
				+ " = NEW."
				+ COLUMN_BALANCE
				+ " WHERE "
				+ MemberTable.COLUMN_ACCOUNT
				+ " = NEW."
				+ COLUMN_ACCOUNT
				+ " ; " + "END";

		public static final String SQL_TRIGGER_UPDATE_BALANCE2 = "CREATE TRIGGER update_balance2 "

				+ "AFTER UPDATE ON "
				+ TABLE_NAME
				+ " WHEN( OLD."
				+ COLUMN_TYPE
				+ "='group')"
				+ " BEGIN "
				+ " UPDATE "
				+ GroupTable.TABLE_NAME
				+ " SET "
				+ GroupTable.COLUMN_GROUP_BALANCE
				+ " = NEW."
				+ COLUMN_BALANCE
				+ " WHERE "
				+ GroupTable.COLUMN_GROUP_ACCOUNT
				+ " = NEW." + COLUMN_ACCOUNT + " ; " + "END";

		public static String getIdColumnName() {
			// TODO Auto-generated method stub
			return COLUMN_ACCOUNT;
		}

	}

	/*
	 * Inner class representing the table containing all the orders
	 * An order can be a consumption or a groupclearance
	 */
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
				+ ","
				+ COLUMN_TYPE
				+ " TEXT NOT NULL" + ")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static final String SQL_TRIGGER_UPDATE_TOTAL_1 = "CREATE TRIGGER update_total_1 "

				+ "AFTER INSERT ON "
				+ TABLE_NAME
				+ " BEGIN "
				+ " UPDATE "
				+ AccountList.TABLE_NAME
				+ " SET "
				+ AccountList.COLUMN_BALANCE
				+ " = NEW."
				+ COLUMN_TOTAL
				+ " + "
				+ AccountList.COLUMN_BALANCE
				+ " WHERE "
				+ AccountList.COLUMN_ACCOUNT
				+ " = NEW."
				+ COLUMN_ACCOUNT + ";" + "END";

		public static final String SQL_TRIGGER_UPDATE_TOTAL_2 = "CREATE TRIGGER update_total_2 "

				+ "AFTER DELETE ON "
				+ TABLE_NAME
				+ " BEGIN "
				+ " UPDATE "
				+ AccountList.TABLE_NAME
				+ " SET "
				+ AccountList.COLUMN_BALANCE
				+ " = "
				+ AccountList.COLUMN_BALANCE
				+ " - "
				+ " OLD."
				+ COLUMN_TOTAL
				+ " WHERE "
				+ AccountList.COLUMN_ACCOUNT
				+ " = OLD." + COLUMN_ACCOUNT + ";" + "END";

		public static final String SQL_TRIGGER_UPDATE_TOTAL_3 = "CREATE TRIGGER update_total_3 "

				+ "AFTER UPDATE ON "
				+ TABLE_NAME
				+ " BEGIN "
				+ " UPDATE "
				+ AccountList.TABLE_NAME
				+ " SET "
				+ AccountList.COLUMN_BALANCE
				+ " = "
				+ AccountList.COLUMN_BALANCE
				+ " - "
				+ " OLD."
				+ COLUMN_TOTAL
				+ " WHERE "
				+ AccountList.COLUMN_ACCOUNT
				+ " = OLD." + COLUMN_ACCOUNT + ";" + "END";

		public static final String SQL_TRIGGER_UPDATE_TOTAL_4 = "CREATE TRIGGER update_total_4 "

				+ "AFTER UPDATE ON "
				+ TABLE_NAME
				+ " BEGIN "
				+ " UPDATE "
				+ AccountList.TABLE_NAME
				+ " SET "
				+ AccountList.COLUMN_BALANCE
				+ " = NEW."
				+ COLUMN_TOTAL
				+ " + "
				+ AccountList.COLUMN_BALANCE
				+ " WHERE "
				+ AccountList.COLUMN_ACCOUNT
				+ " = NEW."
				+ COLUMN_ACCOUNT + ";" + "END";

		public static String getIdColumnName() {
			// TODO Auto-generated method stub
			return COLUMN_ID;
		}

	}

	/*
	 * Inner class representing the table containing all the consumptions
	 */
	public static abstract class Consumption implements BaseColumns {

		public static final String TABLE_NAME = "consumptions";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_ARTICLE_NAME = "article_name";
		public static final String COLUMN_ARTICLE_PRICE = "article_price";
		public static final String COLUMN_AMMOUNT = "ammount";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_ID
				+ " INTEGER PRIMARY KEY"
				+ ","
				+ COLUMN_ARTICLE_NAME
				+ " TEXT"
				+ ","
				+ COLUMN_ARTICLE_PRICE
				+ " DECIMAL(4,2)" + " , " + COLUMN_AMMOUNT + " INTEGER" + ")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static String getIdColumnName() {
			// TODO Auto-generated method stub
			return COLUMN_ID;
		}

	}

	/*
	 * Inner class representing the table containing all the groupclearances
	 */
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

		public static String getIdColumnName() {
			// TODO Auto-generated method stub
			return COLUMN_ID;
		}
	}

	/*
	 * Inner class representing the table containing a log of all the backups
	 */
	public static abstract class BackupLog implements BaseColumns {

		public static final String TABLE_NAME = "backup_logs";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_TIME_STAMP = "ts";
		public static final String COLUMN_TYPE = "type";
		public static final String COLUMN_SUCCESS = "success";
		public static final String COLUMN_COMMENT = "comment";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT"
				+ ","
				+ COLUMN_TIME_STAMP
				+ " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL"
				+ ","
				+ COLUMN_TYPE
				+ " TEXT NOT NULL"
				+ ","
				+ COLUMN_SUCCESS
				+ " BOOLEAN" + "," + COLUMN_COMMENT + " TEXT" + ")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static String getIdColumnName() {
			// TODO Auto-generated method stub
			return COLUMN_ID;
		}
	}

}
