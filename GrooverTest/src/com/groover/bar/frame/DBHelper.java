package com.groover.bar.frame;

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

		return db.rawQuery("SELECT rowid AS _id, " + MemberTable.COLUMN_GR_ID
				+ "," + MemberTable.COLUMN_EMAIL + ","
				+ MemberTable.COLUMN_FIRST_NAME + ","
				+ MemberTable.COLUMN_LAST_NAME + ","
				+ MemberTable.COLUMN_ACCOUNT + "," + MemberTable.COLUMN_BALANCE
				+ " FROM " + MemberTable.TABLE_NAME
				+ " ORDER BY "
				+ MemberTable.COLUMN_FIRST_NAME
				+ " COLLATE NOCASE ASC, " + MemberTable.COLUMN_LAST_NAME
				+ " COLLATE NOCASE ASC", null);

	}

	public Cursor getListMembers() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.rawQuery("SELECT rowid AS _id, " + MemberTable.COLUMN_GR_ID + ","
				+ MemberTable.COLUMN_EMAIL + ","
				+ MemberTable.COLUMN_FIRST_NAME + ","
				+ MemberTable.COLUMN_LAST_NAME + ","
				+ MemberTable.COLUMN_ACCOUNT + "," + MemberTable.COLUMN_BALANCE
				+ " FROM " + MemberTable.TABLE_NAME + " WHERE "
				+ MemberTable.COLUMN_ACTIVE + " = 1 "
				+ " ORDER BY "
				+ MemberTable.COLUMN_FIRST_NAME
				+ " COLLATE NOCASE ASC, " + MemberTable.COLUMN_LAST_NAME
				+ " COLLATE NOCASE ASC", null);


	}

	public Cursor getGroupsFancy() {

		SQLiteDatabase db;
		db = getReadableDatabase();
		String query = "SELECT " + GroupTable.COLUMN_GROUP_ID + " , "
				+ GroupTable.COLUMN_GROUP_NAME + " , COUNT("
				+ GroupMembers.COLUMN_NAME_MEMBER_ID + ") AS COUNT_MEMBERS"
				+ " FROM " + GroupTable.TABLE_NAME + " LEFT OUTER JOIN "
				+ GroupMembers.TABLE_NAME + " ON " + GroupTable.TABLE_NAME
				+ "." + GroupTable.COLUMN_GROUP_ID + "=" + GroupMembers.TABLE_NAME + "."
				+ GroupMembers.COLUMN_NAME_GROUP_ID + " GROUP BY "
				+ GroupTable._ID + " ORDER BY " + GroupTable.COLUMN_GROUP_NAME
				+ " COLLATE NOCASE";

		return db.rawQuery(query, null);
	}

	public Cursor getGroups() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(GroupTable.TABLE_NAME, null, null, null, null, null,
				GroupTable.COLUMN_GROUP_NAME + " COLLATE NOCASE ASC ");
	}

	public Cursor getGroupMembers(int grId) {

		SQLiteDatabase db;
		db = getReadableDatabase();

		String query = "SELECT members.rowid AS _id , "
				+ MemberTable.COLUMN_GR_ID + " , "
				+ MemberTable.COLUMN_EMAIL + " , "
				+ MemberTable.COLUMN_FIRST_NAME + " , "
				+ MemberTable.COLUMN_LAST_NAME + " , "
				+ MemberTable.COLUMN_ACCOUNT + " FROM "
				+ GroupMembers.TABLE_NAME + " , " + MemberTable.TABLE_NAME
				+ " WHERE " + GroupMembers.TABLE_NAME + "."
				+ GroupMembers.COLUMN_NAME_MEMBER_ID + "="
				+ MemberTable.TABLE_NAME + "." + MemberTable.COLUMN_EMAIL
				+ " AND " + GroupMembers.COLUMN_NAME_GROUP_ID + " = " + grId
				+ " ORDER BY " + MemberTable.COLUMN_FIRST_NAME
				+ " COLLATE NOCASE ASC, " + MemberTable.COLUMN_LAST_NAME
				+ " COLLATE NOCASE ASC";

		return db.rawQuery(query, null);

	}

	public Cursor getListGroups() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(GroupTable.TABLE_NAME, null, GroupTable.COLUMN_ACTIVE
				+ " = 1", null, null, null, GroupTable.COLUMN_GROUP_NAME
				+ " COLLATE NOCASE ASC ");

	}

	public Cursor getAccounts() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(AccountList.TABLE_NAME, null, null, null, null, null,
				null);
	}

	public Cursor getMemberOrders() {

		return null;
	}
	
	public Cursor getArticles() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(ItemList.TABLE_NAME, null, null, null, null, null,
				ItemList.COLUMN_NAME_CAT + " COLLATE NOCASE ASC, "
						+ ItemList.COLUMN_NAME_ITEM + " COLLATE NOCASE ASC");
	}

	public Cursor getCategories() {

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(ItemList.TABLE_NAME, new String[] { ItemList.COLUMN_ID,
				ItemList.COLUMN_NAME_CAT }, null, null,
				ItemList.COLUMN_NAME_CAT, null, ItemList.COLUMN_NAME_CAT
						+ " COLLATE NOCASE ASC");
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
	public boolean updateOrIgnore(String table, int id, ContentValues values) {


		boolean res = false;
		Log.d(TAG, "updateOrIgnore on " + table + " values " + values + " " + id);
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
	
	@SuppressWarnings("finally")
	public boolean updateOrIgnore(String tableName, String id,
			ContentValues v) {
		
		boolean res = false;
		Log.d(TAG, "updateOrIgnore on " + tableName + " values " + v + " " + id);
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.update(tableName, v, getIdColumnName(tableName) + "="+"'" + id+"'", null);
			res = true;

		} catch (SQLException e) {
			Log.d(TAG, "updateOrIgnore on " + tableName + " values " + v + " "
					+ id + " fail");
			res = false;
		} finally {
			db.close();
			return res;
		}
		// TODO Auto-generated method stub
		
	}

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

	@SuppressWarnings("finally")
	public boolean deleteOrIgnore(String table, int id) {

		boolean res = false;
		Log.d(TAG, "deleteOrIgnore on " + id);
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.delete(table, getIdColumnName(table) + "=" + id, null);
			res = true;

		} catch (SQLException e) {
			Log.d(TAG, "deleteOrIgnore on " + id + " fail");
			res = false;
		} finally {
			db.close();
			return res;
		}
	}

	@SuppressWarnings("finally")
	public boolean deleteOrIgnore(String tableName, String id) {
		// TODO Auto-generated method stub
		boolean res = false;
		Log.d(TAG, "deleteOrIgnore on " + id+"  "+getIdColumnName(tableName));
		SQLiteDatabase db = getWritableDatabase();
		try {
			
			db.delete(tableName, getIdColumnName(tableName) + "="+"'" + id+"'", null);
			res = true;

		} catch (SQLException e) {
			Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
			Log.d(TAG, "deleteOrIgnore on " + id + " fail");
			res = false;
		} finally {
			db.close();
			return res;
		}
	}
	
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
								+ groupmembers.getInt(2) + " AND "
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

	public boolean deleteGroupOrIgnore(int groupId) {

		boolean res = true;
		res = deleteGroupMembers(groupId);

		if (res = false) {
			return false;
		}

		res = deleteOrIgnore(DBHelper.GroupTable.TABLE_NAME, groupId);
		return res;
	}

	public String getIdColumnName(String tableName){
		
		if(tableName.equals(MemberTable.TABLE_NAME)){return MemberTable.getIdColumnName();}
		if(tableName.equals(GroupTable.TABLE_NAME)){return GroupTable.getIdColumnName();}
		if(tableName.equals(AccountList.TABLE_NAME)){return AccountList.getIdColumnName();}
		if(tableName.equals(ItemList.TABLE_NAME)){return ItemList.getIdColumnName();}
		if(tableName.equals(Order.TABLE_NAME)){return Order.getIdColumnName();}
		if(tableName.equals(Consumption.TABLE_NAME)){return Consumption.getIdColumnName();}
		if(tableName.equals(GroupClearances.TABLE_NAME)){return GroupClearances.getIdColumnName();}
		
		return null;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(MemberTable.SQL_CREATE_TABLE);
		db.execSQL(MemberTable.TRIGGER_NEW_ACCOUNT);
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
		public static final String COLUMN_GR_ID = "GR_ID";
		public static final String COLUMN_EMAIL = "email";
		public static final String COLUMN_FIRST_NAME = "first_name";
		public static final String COLUMN_LAST_NAME = "last_name";
		public static final String COLUMN_ACCOUNT = "account";
		public static final String COLUMN_BALANCE = "balance";
		public static final String COLUMN_ACTIVE = "active";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_GR_ID
				+ " INTEGER UNIQUE ,"
				+ COLUMN_EMAIL
				+ " TEXT PRIMARY KEY,"
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
				+ " BOOLEAN DEFAULT 1" + " )";

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
				+ COLUMN_EMAIL
				+ "= NEW."
				+ COLUMN_EMAIL
				+ ";"
				+ "END";

		private static String getIdColumnName(){
			return COLUMN_EMAIL;
		}
	}

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
				+ " TEXT"
				+ ","
				+ "PRIMARY KEY("
				+ COLUMN_NAME_GROUP_ID + "," + COLUMN_NAME_MEMBER_ID + "))";

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
		public static String getIdColumnName() {
			// TODO Auto-generated method stub
			return null;
		}

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
				+ " = OLD."
				+ COLUMN_TOTAL
				+ " - "
				+ AccountList.COLUMN_BALANCE
				+ " WHERE "
				+ AccountList.COLUMN_ACCOUNT
				+ " = OLD."
				+ COLUMN_ACCOUNT + ";" + "END";

		public static final String SQL_TRIGGER_UPDATE_TOTAL_3 = "CREATE TRIGGER update_total_3 "

				+ "AFTER UPDATE ON "
				+ TABLE_NAME
				+ " BEGIN "
				+ " UPDATE "
				+ AccountList.TABLE_NAME
				+ " SET "
				+ AccountList.COLUMN_BALANCE
				+ " = OLD."
				+ COLUMN_TOTAL
				+ " - "
				+ AccountList.COLUMN_BALANCE
				+ " WHERE "
				+ AccountList.COLUMN_ACCOUNT
				+ " = OLD."
				+ COLUMN_ACCOUNT + ";" + "END";

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
				+ " INTEGER" + ")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static String getIdColumnName() {
			// TODO Auto-generated method stub
			return COLUMN_ID;
		}

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

		public static String getIdColumnName() {
			// TODO Auto-generated method stub
			return COLUMN_ID;
		}
	}

	

	
}