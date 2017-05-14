package nl.groover.bar.frame;

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
 * that need information from the database, or need to update/insert/delete.
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

	// Returns the DBHelper object. Singleton pattern is used.
	public static DBHelper getDBHelper(Context context) {
		if (singleton == null) {
			singleton = new DBHelper(context);
		}
		return singleton;
	}

	// Constructor for new DBHelper object.
	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * This first bunch of functions are all queries of some sort.
	 */

	/*
	 * Used by the autocompleteTextView in the select customer activity to
	 * 
	 * Returns a cursor with all the members which have constraint in their
	 * respective first or last name
	 */
	public Cursor getFilteredMember(String constraint) {
		SQLiteDatabase db = getReadableDatabase();

		String query =	"SELECT * FROM " + MemberTable.TABLE_NAME
						+ " WHERE " + MemberTable.COLUMN_FIRST_NAME
						+ " LIKE \""
						+ constraint + "%\" "
						+ " OR "
						+ MemberTable.COLUMN_LAST_NAME
						+ " LIKE \""
						+ constraint + "%\" "
						+ " ORDER BY "
						+ MemberTable.COLUMN_FIRST_NAME + " COLLATE NOCASE ASC, "
						+ MemberTable.COLUMN_LAST_NAME + " COLLATE NOCASE ASC";

		return db.rawQuery(query, null);
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
	 * returns all the members that have placed an order within a day
	 */
	public Cursor getFrequentVisitors() {
		SQLiteDatabase db = getReadableDatabase();

		String queryString = "SELECT DISTINCT " + MemberTable.TABLE_NAME + "."
				+ MemberTable.COLUMN_GR_ID + " AS " + MemberTable.COLUMN_GR_ID + " , "
				+ MemberTable.COLUMN_FIRST_NAME + " , "
				+ MemberTable.COLUMN_PREFIX + " , "
				+ MemberTable.COLUMN_LAST_NAME + " , "
				+ MemberTable.COLUMN_ACCOUNT + " , "
				+ MemberTable.COLUMN_BALANCE + " FROM "
				+ MemberTable.TABLE_NAME + " , " + Order.TABLE_NAME + " WHERE "
				+ MemberTable.TABLE_NAME + "." + MemberTable.COLUMN_ACCOUNT
				+ "=" + Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT  + " AND " + Order.TABLE_NAME
				+ "." + Order.COLUMN_TS_CREATED + ">"
				+ "DATETIME(\'now\',\'-1 day\')" + " ORDER BY "
				+ MemberTable.COLUMN_FIRST_NAME + " COLLATE NOCASE ASC, "
				+ MemberTable.COLUMN_LAST_NAME + " COLLATE NOCASE ASC";

		return db.rawQuery(queryString, null);

	}

	public Cursor getAllGroups(){

		SQLiteDatabase db;
		db = getReadableDatabase();

		return db.query(GroupTable.TABLE_NAME, null, null, null, null, null,
				GroupTable.COLUMN_GROUP_NAME + " COLLATE NOCASE ASC ");
	}

	public Cursor getFilterGroups(String constraint){

		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT * FROM " + GroupTable.TABLE_NAME
				+ " WHERE " + GroupTable.COLUMN_GROUP_NAME
				+ " LIKE \""
				+ constraint + "%\" "
				+ " ORDER BY "
				+ GroupTable.COLUMN_GROUP_NAME + " COLLATE NOCASE ASC ";

		return db.rawQuery(query, null);
	}

	public Cursor getFrequentGroups() {

		SQLiteDatabase db = getReadableDatabase();

		String queryString = "SELECT DISTINCT " + GroupTable.TABLE_NAME + "."
				+ GroupTable.COLUMN_GR_ID + " AS " + GroupTable.COLUMN_GR_ID + " , "
				+ GroupTable.COLUMN_GROUP_NAME + " , "
				+ GroupTable.COLUMN_ACCOUNT + " , "
				+ GroupTable.COLUMN_BALANCE + " FROM "
				+ GroupTable.TABLE_NAME + " , " + Order.TABLE_NAME + " WHERE "
				+ GroupTable.TABLE_NAME + "." + MemberTable.COLUMN_ACCOUNT
				+ "=" + Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT  + " AND " + Order.TABLE_NAME
				+ "." + Order.COLUMN_TS_CREATED + ">"
				+ "DATETIME(\'now\',\'-1 day\')" + " ORDER BY "
				+ GroupTable.COLUMN_GROUP_NAME + " COLLATE NOCASE ASC ";

		return db.rawQuery(queryString, null);
	}

	public Cursor getGroupMembers(int groupId){

		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT "
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_GR_ID+" , "
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_FIRST_NAME+" , "
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_PREFIX+" , "
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_LAST_NAME+" , "
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_ACCOUNT+" , "
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_BALANCE
				+ " FROM " + MemberTable.TABLE_NAME + " , " + GroupMembers.TABLE_NAME
				+ " WHERE "
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_GR_ID + " = "
				+ GroupMembers.TABLE_NAME+"."+GroupMembers.COLUMN_MEMBER_ID
				+ " AND "
				+ GroupMembers.TABLE_NAME+"."+GroupMembers.COLUMN_GR_ID+" = "+groupId;

		return db.rawQuery(query, null);
	}

	/*
	 * Not used yet. Returns all accounts. Accounts can belong to both groups as
	 * members. Group feature NOT implemented yet.
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
		
		String t1 = "t1";
		String t2 = "t2";
		
		String query = "SELECT " + t1+"."+ItemList.COLUMN_ID+ ","
				+ t1+"."+ItemList.COLUMN_NAME_ITEM+","+t1+"."+ItemList.COLUMN_NAME_PRICE
				+ ","+t2+"."+"sum_amount"+","+t1+"."+ItemList.COLUMN_NAME_COLOR
				+" FROM "+ ItemList.TABLE_NAME +" "+t1+" " 
				+" LEFT OUTER JOIN "
				+"(SELECT "+Consumption.COLUMN_ARTICLE_ID+","
				+ "SUM("+Consumption.COLUMN_AMMOUNT+") AS sum_amount"
				+ " FROM "+ Consumption.TABLE_NAME
				+ " GROUP BY "+Consumption.COLUMN_ARTICLE_ID 
				+ " ) "+t2
				+" ON "+t1+"."+ItemList.COLUMN_ID + "="+Consumption.COLUMN_ARTICLE_ID
				+ " ORDER BY "+t1+"."+ItemList.COLUMN_NAME_ORDER
				;

		return db.rawQuery(query, null);
	}

	/*
	 * Returns all the categories at which the articles are ordered (ie food,
	 * liquor, whisky etc) Currently NOT used
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
	 * Returns a cursor containing all consumptions, sorted by article. 
	 */
	public Cursor getTotalConsumptionsCust(int account) {

		SQLiteDatabase db = getReadableDatabase();

		String t1 = "t1";
		String t2 = "t2";
		
		String query = "SELECT " + t1+"."+ItemList.COLUMN_ID+ ","
				+ t1+"."+ItemList.COLUMN_NAME_ITEM+","+t1+"."+ItemList.COLUMN_NAME_PRICE
				+ ","+t2+"."+"sum_amount"
				+" FROM "+ ItemList.TABLE_NAME +" "+t1+" " 
				+" JOIN "
				+"(SELECT "+Consumption.COLUMN_ARTICLE_ID+","
				+ "SUM("+Consumption.COLUMN_AMMOUNT+") AS sum_amount"
				+ " FROM "+ Consumption.TABLE_NAME+","+Order.TABLE_NAME
				+ " WHERE "+ Order.TABLE_NAME+"."+Order.COLUMN_ID+"="+Consumption.TABLE_NAME+"."+Consumption.COLUMN_ORDER_ID
				+ " AND "+ Order.COLUMN_ACCOUNT+"="+account
				+ " GROUP BY "+Consumption.COLUMN_ARTICLE_ID
				+ " ) "+t2
				+" ON "+t1+"."+ItemList.COLUMN_ID + "="+Consumption.COLUMN_ARTICLE_ID
				+ " ORDER BY "+t1+"."+ItemList.COLUMN_NAME_ORDER;

		return db.rawQuery(query, null);
	}

	public Cursor getGroupSettlementsByMember(int memberId){

		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT "
				+ GroupSettlement.TABLE_NAME+"."+GroupSettlement.COLUMN_GROUP_NAME+","
				+ GroupSettlement.TABLE_NAME+"."+GroupSettlement.COLUMN_SUBTOTAL
				+" FROM "
				+ GroupSettlement.TABLE_NAME+" , "+Order.TABLE_NAME+" , "+MemberTable.TABLE_NAME
				+" WHERE "
				+GroupSettlement.TABLE_NAME+"."+GroupSettlement.COLUMN_ORDER_ID + "="
				+Order.TABLE_NAME+"."+Order.COLUMN_ID
				+ " AND "
				+Order.TABLE_NAME+"."+Order.COLUMN_ACCOUNT + "="
				+MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_ACCOUNT
				+ " AND "
				+MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_GR_ID + "=" + memberId;

		return db.rawQuery(query, null);
	}

	/*
	* Returns a cursor containing all consumptions, sorted by article.
	*/
	public Cursor getUnsettledGroupConsumptions(int groupId) {

		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT "
				+ Order.TABLE_NAME+"."+Order.COLUMN_ID
				+","+Order.TABLE_NAME+"."+Order.COLUMN_TS_CREATED
				+","+Consumption.TABLE_NAME+"."+Consumption.COLUMN_ARTICLE_NAME
				+","+Consumption.TABLE_NAME+"."+Consumption.COLUMN_ARTICLE_PRICE
				+","+Consumption.TABLE_NAME+"."+Consumption.COLUMN_SUBTOTAL
				+ ","+"SUM("+ Order.TABLE_NAME+"."+Order.COLUMN_TOTAL+")"
				+" FROM "
				+Order.TABLE_NAME+","
				+Consumption.TABLE_NAME+","
				+GroupTable.TABLE_NAME
				+ " WHERE "
				+ Order.TABLE_NAME+"."+Order.COLUMN_ID
				+"="+Consumption.TABLE_NAME+"."+Consumption.COLUMN_ORDER_ID
				+ " AND "
				+ Order.TABLE_NAME+"."+Order.COLUMN_ACCOUNT+"="
				+ GroupTable.TABLE_NAME+"."+GroupTable.COLUMN_ACCOUNT
				+ " AND "
				+ GroupTable.TABLE_NAME+"."+GroupTable.COLUMN_GR_ID+"="+groupId;

		return db.rawQuery(query, null);
	}

	public void settleGroupConsumptions(int account){

		SQLiteDatabase db = getReadableDatabase();

		String query =
				"UPDATE " + Order.TABLE_NAME
				+ " SET " + Order.COLUMN_TS_SETTLED + "="+ "DATETIME()"
				+ " WHERE "+Order.COLUMN_ACCOUNT+"="+account;

		db.rawQuery(query, null);
	}

	public Cursor getAllOrders() {

		SQLiteDatabase db = getReadableDatabase();

		String query =
				"SELECT " + Order.TABLE_NAME + "." + Order.COLUMN_ID+" , "
				+ " COALESCE( "
				+ " TRIM"+"("
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_FIRST_NAME+" || "+"\" \""+" || "
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_PREFIX
				+ ")"
				+" || "+"\" \""+" || "+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_LAST_NAME
				+ " , "+ GroupTable.TABLE_NAME+"."+GroupTable.COLUMN_GROUP_NAME
				+" )"+" AS name"
				+ ","+ Order.TABLE_NAME+"." + Order.COLUMN_ACCOUNT
				+ "," + Order.TABLE_NAME+"."+ Order.COLUMN_TOTAL + "," + "DATETIME("
				+ Order.COLUMN_TS_CREATED + ", \'localtime\')"
				+ " FROM "
				+ Order.TABLE_NAME + " LEFT OUTER JOIN "+MemberTable.TABLE_NAME+" ON "
				+ Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT + "="
				+ MemberTable.TABLE_NAME + "." + MemberTable.COLUMN_ACCOUNT
				+ " LEFT OUTER JOIN "+GroupTable.TABLE_NAME+" ON "
				+ Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT + "="
				+ GroupTable.TABLE_NAME + "." + GroupTable.COLUMN_ACCOUNT
				+ " ORDER BY " + Order.COLUMN_TS_CREATED + " DESC";

		return db.rawQuery(query, null);
	}

	/*
	 * Get all orders from customer with customer accountNr id.
	 */
	public Cursor getOrdersCust(int accountNr) {

		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT " + Order.TABLE_NAME + "." + Order.COLUMN_ID+" , "
				+ " COALESCE( "
				+ " TRIM"+"("
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_FIRST_NAME+" || "+"\" \""+" || "
				+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_PREFIX
				+ ")"
				+" || "+"\" \""+" || "+ MemberTable.TABLE_NAME+"."+MemberTable.COLUMN_LAST_NAME
				+ " , "+ GroupTable.TABLE_NAME+"."+GroupTable.COLUMN_GROUP_NAME
				+" )"+" AS name"
				+ ","+ Order.TABLE_NAME+"." + Order.COLUMN_ACCOUNT
				+ "," + Order.TABLE_NAME+"."+ Order.COLUMN_TOTAL + "," + "DATETIME("
				+ Order.COLUMN_TS_CREATED + ", \'localtime\')"
				+ " FROM "
				+ Order.TABLE_NAME + " LEFT OUTER JOIN "+MemberTable.TABLE_NAME+" ON "
				+ Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT + "="
				+ MemberTable.TABLE_NAME + "." + MemberTable.COLUMN_ACCOUNT
				+ " LEFT OUTER JOIN "+GroupTable.TABLE_NAME+" ON "
				+ Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT + "="
				+ GroupTable.TABLE_NAME + "." + GroupTable.COLUMN_ACCOUNT
				+ " WHERE "
				+ Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT + "="+accountNr
				+ " ORDER BY " + Order.COLUMN_TS_CREATED + " DESC";

		return db.rawQuery(query, null);
	}

	/*
	 * Get all orders from customer with customer accountNr id.
	 */
	public Cursor getConsumptionsCust(int accountNr) {

		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT "
				+ Consumption.TABLE_NAME+"."+Consumption.COLUMN_ARTICLE_NAME
				+ ","+Consumption.TABLE_NAME+"."+Consumption.COLUMN_AMMOUNT
				+ ","+Consumption.TABLE_NAME+"."+Consumption.COLUMN_ARTICLE_PRICE
				+ ","+Consumption.TABLE_NAME+"."+Consumption.COLUMN_SUBTOTAL
				+ ","+"DATETIME("+Order.COLUMN_TS_CREATED+", \'localtime\')"
				+ " FROM "
				+ Consumption.TABLE_NAME+","
				+ Order.TABLE_NAME + " LEFT OUTER JOIN "+MemberTable.TABLE_NAME+" ON "
				+ Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT + "="
				+ MemberTable.TABLE_NAME + "." + MemberTable.COLUMN_ACCOUNT
				+ " LEFT OUTER JOIN "+GroupTable.TABLE_NAME+" ON "
				+ Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT + "="
				+ GroupTable.TABLE_NAME + "." + GroupTable.COLUMN_ACCOUNT
				+ " WHERE "
				+ Order.TABLE_NAME + "." + Order.COLUMN_ACCOUNT + "="+accountNr
				+" AND "
				+ Consumption.TABLE_NAME+"."+Consumption.COLUMN_ORDER_ID+"="
				+ Order.TABLE_NAME + "." + Order.COLUMN_ID
				+ " ORDER BY " + Order.COLUMN_TS_CREATED + " DESC";

		return db.rawQuery(query, null);
	}

	public Cursor getConsumptionOrder(int id) {
		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT " + Consumption.TABLE_NAME + "."
				+ Consumption.COLUMN_ID + "," + Consumption.TABLE_NAME + "."
				+ Consumption.COLUMN_ARTICLE_ID + "," + Consumption.TABLE_NAME
				+ "." + Consumption.COLUMN_AMMOUNT + ","
				+ Consumption.TABLE_NAME + "."
				+ Consumption.COLUMN_ARTICLE_NAME + ","
				+ Consumption.TABLE_NAME + "."
				+ Consumption.COLUMN_ARTICLE_PRICE + ","
				+ Consumption.TABLE_NAME + "." + Consumption.COLUMN_SUBTOTAL
				+ " FROM " + Consumption.TABLE_NAME + " WHERE "
				+ Consumption.TABLE_NAME + "." + Consumption.COLUMN_ORDER_ID
				+ "=" + id;

		return db.rawQuery(query, null);
	}

	public Cursor getCredentials() {

		SQLiteDatabase db = getReadableDatabase();

		return db.query(KeyValueLog.TABLE_NAME, new String[] {
				KeyValueLog.COLUMN_ID, KeyValueLog.COLUMN_VALUE },
				KeyValueLog.COLUMN_KEY + "='username' OR "
						+ KeyValueLog.COLUMN_KEY + " ='password'", null, null,
				null, null);
	}

	/**
	 * inserts a row in a table denoted by String table. The contentvalues are
	 * inserted. Any Error is ignored. Returns the inserted row id (_id in most
	 * cases) or -1 if the operation failed.
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

	public long createGroup(String groupName){

		ContentValues v = new ContentValues();
		v.put(GroupTable.COLUMN_GROUP_NAME, groupName);

		long res = insertOrIgnore(GroupTable.TABLE_NAME, v);

		return res;
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

	public boolean checkAllowedToUpdateGroup(int groupId){

		SQLiteDatabase db = getReadableDatabase();

		String query =
				"SELECT * FROM "
				+Order.TABLE_NAME+" , "+GroupTable.TABLE_NAME
				+" WHERE "
				+Order.TABLE_NAME+"."+Order.COLUMN_ACCOUNT+" = "
				+GroupTable.TABLE_NAME+"."+GroupTable.COLUMN_ACCOUNT
				+" AND "
				+GroupTable.TABLE_NAME+"."+GroupTable.COLUMN_GR_ID+" = " + groupId;

		Cursor c = db.rawQuery(query, null);

		boolean res = c.getCount()<1;

		c.close();

		return res;
	}

	public boolean addGroupMember(int groupId, int memberId){

		ContentValues v = new ContentValues();
		v.put(GroupMembers.COLUMN_GR_ID, groupId);
		v.put(GroupMembers.COLUMN_MEMBER_ID, memberId);

		long res = insertOrIgnore(GroupMembers.TABLE_NAME, v);
		return res != -1;
	}

	public boolean renameGroup(int groupId, String newName){

		ContentValues v = new ContentValues();
		v.put(GroupTable.COLUMN_GR_ID, groupId);
		v.put(GroupTable.COLUMN_GROUP_NAME, newName);

		long res = insertOrIgnore(GroupTable.TABLE_NAME, v);
		return res != -1;
	}

	public void settleAllGroups() throws InvalidGroupException{

		Cursor groups = getAllGroups();
		groups.moveToFirst();

		while(groups.getPosition()<groups.getCount()){

			int groupId = groups.getInt(0);
			int groupAccount = groups.getInt(2);

			Cursor groupMembers = getGroupMembers(groupId);

			if(groupMembers.getCount()==0){
				throw new InvalidGroupException(groups.getString(1));
			}

			Cursor groupOrders = getUnsettledGroupConsumptions(groupId);

			if(groupOrders.getCount()==0){

				groupMembers.close();
				groupOrders.close();
				groups.moveToNext();
				continue;
			}

			groupOrders.moveToFirst();
			double balance = groupOrders.getDouble(5);
			double balancePerMember = balance/groupMembers.getCount();

			groupMembers.moveToFirst();

			while (groupMembers.getPosition()<groupMembers.getCount()){

				ContentValues v = new ContentValues();
				v.put(Order.COLUMN_ACCOUNT, groupMembers.getInt(4));
				v.put(Order.COLUMN_TYPE, Order.ORDER_TYPE_SETTLEMENT);
				v.put(Order.COLUMN_TOTAL, balancePerMember);
				long orderId = insertOrIgnore(Order.TABLE_NAME, v);
				v.clear();

				v.put(GroupSettlement.COLUMN_ORDER_ID, orderId);
				v.put(GroupSettlement.COLUMN_GROUP_ID, groupId);
				v.put(GroupSettlement.COLUMN_GROUP_NAME, groups.getString(1));
				v.put(GroupSettlement.COLUMN_SUBTOTAL, balancePerMember);

				long settleId = insertOrIgnore(GroupSettlement.TABLE_NAME, v);

				settleGroupConsumptions(groupAccount);

				groupMembers.moveToNext();
			}

			groupMembers.close();
			groupOrders.close();

			groups.moveToNext();
		}
		groups.close();
	}

	public boolean updateCredentials(String username, String passwordUnhashed) {

		SQLiteDatabase db = getWritableDatabase();

		ContentValues v = new ContentValues();
		v.put(KeyValueLog.COLUMN_VALUE, username);
		int i = db.update(KeyValueLog.TABLE_NAME, v, KeyValueLog.COLUMN_KEY
				+ "='username'", null);
		v.clear();
		v.put(KeyValueLog.COLUMN_VALUE, MD5Hash.md5(passwordUnhashed));
		int j = db.update(KeyValueLog.TABLE_NAME, v, KeyValueLog.COLUMN_KEY
				+ "='password'", null);

		return (i + j) != 0;
	}

	/**
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

	public boolean deleteGroupMembers(int groupId){

		SQLiteDatabase db = getWritableDatabase();
		boolean res = false;

		try {
			db.delete(GroupMembers.TABLE_NAME, GroupMembers.COLUMN_GR_ID
					+ "=?", new String[] { groupId + "" });

			res = true;

		} catch (SQLException e) {
			Log.d(TAG, "deleteOrIgnore on " + groupId + " fail");
			res = false;
		}
		db.close();
		return res;
	}

	public boolean deleteGroup(int groupId){

		deleteGroupMembers(groupId);

		return deleteOrIgnore(GroupTable.TABLE_NAME, groupId);
	}

	/*
	 * Deletes all orders in the database. Both consumptions as groupclearances
	 * are deleted. Usually done after making the balance.
	 */
	public void deleteAllOrders() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(Order.TABLE_NAME, null, null);
		db.delete(Consumption.TABLE_NAME, null, null);
	}

	public boolean deleteOrder(int id) {
		SQLiteDatabase db = getWritableDatabase();
		boolean res = false;

		try {

			db.delete(Consumption.TABLE_NAME, Consumption.COLUMN_ORDER_ID
					+ "=?", new String[] { id + "" });
			db.delete(Order.TABLE_NAME, Order.COLUMN_ID + "=?",
					new String[] { id + "" });
			res = true;

		} catch (SQLException e) {
			Log.d(TAG, "deleteOrIgnore on " + id + " fail");
			res = false;
		} 
			db.close();
			return res;
	}

	/*
	 * Deletes all members from the database
	 */
	public void deleteAllMembers() {

		SQLiteDatabase db = getWritableDatabase();
		db.delete(MemberTable.TABLE_NAME, null, null);
	}

	/*
	 * Returns a string which represents the identifier column for a given table
	 * tableName
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

		return null;
	}

	/**
	 * Returns whether on not a backup needs to be made based on the number of
	 * orders since the last update. Returns true if an update needs to be made
	 * otherwise false
	 */
	public boolean checkNeedToBackup() {

		SQLiteDatabase db = getReadableDatabase();

		Cursor c = db.query(Order.TABLE_NAME, new String[] { Order.COLUMN_ID },
				null, null, null, null, null);

		if (c.getCount() == 0) {
			return false;
		}

		c = db.query(BackupLog.TABLE_NAME,
				new String[] { BackupLog.COLUMN_ID }, null, null, null, null,
				null);

		if (c.getCount() == 0) {
			return true;
		}

		String inner = "SELECT " + "MAX(" + BackupLog.COLUMN_TIME_STAMP
				+ ") AS " + BackupLog.COLUMN_TIME_STAMP + " FROM "
				+ BackupLog.TABLE_NAME + " WHERE " + "("
				+ BackupLog.COLUMN_TYPE + "=\"upload\"" + " OR "
				+ BackupLog.COLUMN_TYPE + "=\"SD\"" + ")" + " AND "
				+ BackupLog.COLUMN_SUCCESS + "=1";

		String query = "SELECT * " + " FROM " + Order.TABLE_NAME + " WHERE "
				+ "(" + inner + ")" + " < " + Order.COLUMN_TS_CREATED;

		c = db.rawQuery(query, null);

		if (c.getCount() > 0) {
			return true;
		}
		return false;

	}

	/**
	 * Returns whether on not a backup needs to be made based on the number of
	 * orders since the last update. Only SD balances are considered. Returns
	 * true if an update needs to be made otherwise false.
	 */
	public boolean checkNeedToBackupSD() {

		SQLiteDatabase db = getReadableDatabase();

		Cursor c = db.query(Order.TABLE_NAME, new String[] { Order.COLUMN_ID },
				null, null, null, null, null);

		Log.i("DB", c.getCount() + " orders");
		if (c.getCount() == 0) {
			return false;
		}

		c = db.query(BackupLog.TABLE_NAME,
				new String[] { BackupLog.COLUMN_ID },
				DBHelper.BackupLog.COLUMN_TYPE + "=\"SD\"" + " AND "
						+ DBHelper.BackupLog.COLUMN_SUCCESS + "=1", null, null,
				null, null);

		Log.i("DB", c.getCount() + " backups");
		if (c.getCount() == 0) {
			return true;
		}

		String inner = "SELECT " + "MAX(" + BackupLog.COLUMN_TIME_STAMP
				+ ") AS " + BackupLog.COLUMN_TIME_STAMP + " FROM "
				+ BackupLog.TABLE_NAME + " WHERE " + BackupLog.COLUMN_TYPE
				+ "=\"SD\"" + " AND " + BackupLog.COLUMN_SUCCESS + "=1";

		c = db.rawQuery(inner, null);
		Log.i("DB", c.getCount() + " inner ");

		String query = "SELECT * " + " FROM " + Order.TABLE_NAME + " WHERE "
				+ "(" + inner + ")" + " < " + Order.COLUMN_TS_CREATED;

		c = db.rawQuery(query, null);
		Log.i("DB", query);

		if (c.getCount() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether a certain id is present in a table. Useful for checking
	 * valid updates on for instance members.
	 */

	public boolean checkIdInTable(String table, int id) {

		SQLiteDatabase db = getReadableDatabase();

		Cursor c = db.query(table, new String[] { getIdColumnName(table) },
				getIdColumnName(table) + "=" + id, null, null, null, null);

		if (c.getCount() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * Called when the database is first created. Creates all the tables and
	 * triggers.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
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
		db.execSQL(GroupSettlement.SQL_CREATE_TABLE);


		db.execSQL(BackupLog.SQL_CREATE_TABLE);

		db.execSQL(KeyValueLog.SQL_CREATE_TABLE);
		db.execSQL(KeyValueLog.SQL_INSERT_CRED);
	}

	/**
	 * Called when the database is updated. Simply removes all the tables and
	 * recreates them
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(MemberTable.SQL_DELETE_ENTRIES);
		db.execSQL(GroupTable.SQL_DELETE_ENTRIES);
		db.execSQL(GroupMembers.SQL_DELETE_ENTRIES);
		db.execSQL(ItemList.SQL_DELETE_ENTRIES);
		db.execSQL(AccountList.SQL_DELETE_ENTRIES);
		db.execSQL(Order.SQL_DELETE_ENTRIES);
		db.execSQL(Consumption.SQL_DELETE_ENTRIES);
		db.execSQL(GroupSettlement.SQL_DELETE_ENTRIES);
		db.execSQL(BackupLog.SQL_DELETE_ENTRIES);
		db.execSQL(KeyValueLog.SQL_DELETE_ENTRIES);


		onCreate(db);
		DATABASE_VERSION = newVersion;
	}


	/**
	 * Inner class representing the table containing all the members
	 */
	public static abstract class MemberTable implements BaseColumns {

		public static final String TABLE_NAME = "members";
		public static final String COLUMN_GR_ID = _ID;
		public static final String COLUMN_FIRST_NAME = "first_name";
		public static final String COLUMN_PREFIX = "prefix";
		public static final String COLUMN_LAST_NAME = "last_name";
		public static final String COLUMN_ACCOUNT = "account";
		public static final String COLUMN_BALANCE = "balance";
		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_GR_ID
				+ " INTEGER PRIMARY KEY ,"
				+ COLUMN_FIRST_NAME
				+ " TEXT NOT NULL"
				+ ","
				+ COLUMN_PREFIX
				+ " TEXT"
				+ ","
				+ COLUMN_LAST_NAME
				+ " TEXT NOT NULL"
				+ ","
				+ COLUMN_ACCOUNT
				+ " INTEGER "
				+ ","
				+ COLUMN_BALANCE
				+ " DECIMAL(10,2) DEFAULT 0"
				+ " )";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static final String TRIGGER_NEW_ACCOUNT = "CREATE TRIGGER create_new_account_member "

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

		private static String getIdColumnName() {
			return COLUMN_GR_ID;
		}
	}

	/**
	 * Inner class representing the table containing all the members
	 */
	public static abstract class GroupTable implements BaseColumns {

		public static final String TABLE_NAME = "groups";
		public static final String COLUMN_GR_ID = _ID;
		public static final String COLUMN_GROUP_NAME = "group_name";
		public static final String COLUMN_ACCOUNT = "account";
		public static final String COLUMN_BALANCE = "balance";
		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_GR_ID
				+ " INTEGER PRIMARY KEY ,"
				+ COLUMN_GROUP_NAME
				+ " TEXT UNIQUE NOT NULL"
				+ ","
				+ COLUMN_ACCOUNT
				+ " INTEGER "
				+ ","
				+ COLUMN_BALANCE
				+ " DECIMAL(10,2) DEFAULT 0"
				+ " )";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static final String TRIGGER_NEW_ACCOUNT = "CREATE TRIGGER create_new_account_group "

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
				+ " VALUES(0,'group'); "
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

		private static String getIdColumnName() {
			return COLUMN_GR_ID;
		}
	}

	/**
	 * Inner class representing the table containing all the members
	 */
	public static abstract class GroupMembers implements BaseColumns {

		public static final String TABLE_NAME = "group_members";
		public static final String COLUMN_MEMBER_ID = "member_id";
		public static final String COLUMN_GR_ID = "group_id";
		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_GR_ID
				+ " INTEGER NOT NULL, "
				+ COLUMN_MEMBER_ID
				+ " INTEGER NOT NULL, "
				+ " PRIMARY KEY ("
				+ COLUMN_GR_ID+" , "+ COLUMN_MEMBER_ID
				+ " ) "
				+ " ) ";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		private static String getIdColumnName() {
			return COLUMN_GR_ID;
		}
	}

	/**
	 * Inner class representing the table containing all the articles
	 */
	public static abstract class ItemList implements BaseColumns {

		public static final String TABLE_NAME = "item_list";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_NAME_ITEM = "item_name";
		public static final String COLUMN_NAME_PRICE = "item_price";
		public static final String COLUMN_NAME_CAT = "item_category";
		public static final String COLUMN_NAME_ORDER = "item_order";
		public static final String COLUMN_NAME_COLOR = "item_color";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ COLUMN_NAME_ITEM
				+ " TEXT NOT NULL , "
				+ COLUMN_NAME_PRICE
				+ " DECIMAL(10,2) ,"
				+ COLUMN_NAME_CAT
				+ " TEXT DEFAULT 'overig', "
				+ COLUMN_NAME_ORDER
				+ " INTEGER ,"
				+ COLUMN_NAME_COLOR
				+ " INTEGER)";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;
		

		public static String getIdColumnName() {
			return COLUMN_ID;
		}

	}

	/**
	 * Inner class representing the table containing all the accounts belonging
	 * to both members and groups
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
				+ GroupTable.COLUMN_BALANCE
				+ " = NEW."
				+ COLUMN_BALANCE
				+ " WHERE "
				+ GroupTable.COLUMN_ACCOUNT
				+ " = NEW."
				+ COLUMN_ACCOUNT
				+ " ; " + "END";

		public static String getIdColumnName() {
			return COLUMN_ACCOUNT;
		}

	}

	/**
	 * Inner class representing the table containing all the orders
	 */
	public static abstract class Order implements BaseColumns {

		public static final String TABLE_NAME = "orders";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_TOTAL = "total_amount";
		public static final String COLUMN_ACCOUNT = "client_account";
		public static final String COLUMN_TYPE = "order_type";
		public static final String COLUMN_TS_CREATED = "ts_created";
		public static final String COLUMN_TS_SETTLED = "ts_settled";

		public static final String ORDER_TYPE_CONSUMPTION = "consumption";
		public static final String ORDER_TYPE_SETTLEMENT = "group_settlement";


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

		// trigger update_total_3 reduces balance to zero, trigger update_total_4 sets correct balance.
		// might refactor to one trigger.
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
			return COLUMN_ID;
		}

	}

	/**
	 * Inner class representing the table containing all the consumptions
	 */
	public static abstract class Consumption implements BaseColumns {

		public static final String TABLE_NAME = "consumptions";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_ORDER_ID = "order_id";
		public static final String COLUMN_ARTICLE_ID = "article_id";
		public static final String COLUMN_ARTICLE_NAME = "article_name";
		public static final String COLUMN_ARTICLE_PRICE = "article_price";
		public static final String COLUMN_AMMOUNT = "ammount";
		public static final String COLUMN_SUBTOTAL = "subtotal";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT"
				+ ","
				+ COLUMN_ORDER_ID
				+ " INTEGER"
				+ ","
				+ COLUMN_ARTICLE_ID
				+ " INTEGER"
				+ ","
				+ COLUMN_ARTICLE_NAME
				+ " TEXT"
				+ ","
				+ COLUMN_ARTICLE_PRICE
				+ " DECIMAL(4,2)"
				+ " , "
				+ COLUMN_AMMOUNT
				+ " INTEGER"
				+ ","
				+ COLUMN_SUBTOTAL + " DECIMAL(4,2)" + ")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static String getIdColumnName() {
			return COLUMN_ID;
		}

	}

	public static abstract class GroupSettlement implements BaseColumns {

		public static final String TABLE_NAME = "group_settlement";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_ORDER_ID = "order_id";
		public static final String COLUMN_GROUP_ID = "group_id";
		public static final String COLUMN_GROUP_NAME = "group_name";
		public static final String COLUMN_SUBTOTAL = "subtotal";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT"
				+ ","
				+ COLUMN_ORDER_ID
				+ " INTEGER"
				+ ","
				+ COLUMN_GROUP_ID
				+ " INTEGER"
				+ ","
				+ COLUMN_GROUP_NAME
				+ " TEXT"
				+ ","
				+ COLUMN_SUBTOTAL + " DECIMAL(4,2)" + ")";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static String getIdColumnName() {
			return COLUMN_ID;
		}

	}

	/**
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

	/**
	 * Inner class representing the table containing a log of all the backups
	 */
	public static abstract class KeyValueLog implements BaseColumns {

		public static final String TABLE_NAME = "keyvalue_logs";
		public static final String COLUMN_ID = _ID;
		public static final String COLUMN_KEY = "key";
		public static final String COLUMN_VALUE = "value";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS "
				+ TABLE_NAME
				+ " ("
				+ COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT"
				+ ","
				+ COLUMN_KEY
				+ " TEXT NOT NULL" + "," + COLUMN_VALUE + " TEXT " + ")";

		public static final String SQL_INSERT_CRED = "INSERT INTO "
				+ TABLE_NAME + " (" + COLUMN_KEY + "," + COLUMN_VALUE + ") "
				+ "VALUES " + "('username','admin')" + "," + "('password','"
				+ MD5Hash.md5("mcallen") + "')";

		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		public static String getIdColumnName() {
			return COLUMN_KEY;
		}
	}

	public class InvalidGroupException extends Exception{

		private String groupName;

		public InvalidGroupException(String groupName){
			this.groupName = groupName;
		}

		public String getGroupName() {
			return groupName;
		}
	}
}
