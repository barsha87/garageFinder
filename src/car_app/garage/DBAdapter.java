package car_app.garage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class DBAdapter
{
public static final String KEY_ROWID = "_id";
public static final String KEY_NAME = "name";
public static final String KEY_CASHLESS = "cashless";
public static final String KEY_MANUFACTURER = "manufacturer";
public static final String KEY_STREET = "street";
public static final String KEY_CITY = "city";
public static final String KEY_PINCODE = "pincode";
public static final String KEY_STATE = "state";
public static final String KEY_CONTACT_PERSON = "contact_person";
public static final String KEY_LANDLINE = "landline";
public static final String KEY_MOBILE = "mobile";
public static final String KEY_EMAIL = "email";

private static final String TAG = "DBAdapter";
private static final String DATABASE_NAME = "garage_list";
private static final String DATABASE_TABLE = "garage";
private static final int DATABASE_VERSION = 1;

private static final String DATABASE_CREATE =
"CREATE TABLE IF NOT EXISTS garage (_id integer primary key autoincrement, "
+ "name text not null, cashless text not null, "
+ "manufacturer text not null, street text not null, "
+ "city text not null, pincode number not null, "
+ "state text not null, contact_person text not null, "
+ "landline text, mobile number, "
+ "email text);";

private final Context context;
private DatabaseHelper DBHelper;
private static SQLiteDatabase db;
public DBAdapter(Context ctx)
{
this.context = ctx;
DBHelper = new DatabaseHelper(context);
}
private static class DatabaseHelper extends SQLiteOpenHelper
{
DatabaseHelper(Context context)
{
super(context, DATABASE_NAME, null, DATABASE_VERSION);
}
@Override
public void onCreate(SQLiteDatabase db)
{
db.execSQL(DATABASE_CREATE);
}
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion,
int newVersion)
{
	Log.w(TAG, "Upgrading database from version " + oldVersion
	+ " to "
	+ newVersion + ", which will destroy all old data");
	db.execSQL("DROP TABLE IF EXISTS garage");
	onCreate(db);
	}
	}
	//---opens the database---
	public DBAdapter open() throws SQLException
	{
	db = DBHelper.getWritableDatabase();
	return this;
	}
	//---closes the database---
	public void close()
	{
	DBHelper.close();
	}
	//---insert a title into the database---
	public long insertGarage(String name, String cashless, String manufacturer, 
			String street, String city, String pincode,String state, 
			String contact_person, String landline, String mobile, String email)
	{
	ContentValues initialValues = new ContentValues();
	initialValues.put(KEY_NAME, name);
	initialValues.put(KEY_CASHLESS, cashless);
	initialValues.put(KEY_MANUFACTURER, manufacturer);
	initialValues.put(KEY_STREET, street);
	initialValues.put(KEY_CITY, city);
	initialValues.put(KEY_PINCODE, pincode);
	initialValues.put(KEY_STATE, state);
	initialValues.put(KEY_CONTACT_PERSON, contact_person);
	initialValues.put(KEY_LANDLINE, landline);
	initialValues.put(KEY_MOBILE, mobile);
	initialValues.put(KEY_EMAIL, email);
	return db.insert(DATABASE_TABLE, null, initialValues);
	}
	//---deletes a particular garage---
	public boolean deleteGarage(long rowId)
	{
	return db.delete(DATABASE_TABLE, KEY_ROWID +
	"=" + rowId, null) > 0;
	}
	
	//---retrieves all the garages---
	public Cursor getAllGarages()
	{
	return db.query(DATABASE_TABLE, new String[] {
	KEY_ROWID, KEY_NAME, KEY_CASHLESS, KEY_MANUFACTURER, KEY_STREET,
	KEY_CITY, KEY_PINCODE, KEY_STATE, KEY_CONTACT_PERSON,
	KEY_LANDLINE, KEY_MOBILE, KEY_EMAIL},
	null,
	null,
	null,
	null,
	null);
	}
	//---retrieves a particular title---
	public Cursor getGarage(long rowId) throws SQLException
	{
	Cursor mCursor =
	db.query(true, DATABASE_TABLE, new String[] {
			KEY_ROWID, KEY_NAME, KEY_CASHLESS, KEY_MANUFACTURER, KEY_STREET,
			KEY_CITY, KEY_PINCODE, KEY_STATE, KEY_CONTACT_PERSON,
			KEY_LANDLINE, KEY_MOBILE, KEY_EMAIL},
	KEY_ROWID + "=" + rowId,
	null,
	null,
	null,
	null,
	null);
	if (mCursor != null) {
	mCursor.moveToFirst();
	}
	return mCursor;
	}	 
	}