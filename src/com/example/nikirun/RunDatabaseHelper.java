package com.example.nikirun;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class RunDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "myruns.sqlite";
	private static int VERSION = 1;
	
	private static final String TABLE_RUN = "run";
	private static final String COLUMN_RUN_START_DATE = "start_date";
	private static final String COLUMN_RUN_END_DATE = "end_date";
	private static final String COLUMN_RUN_TRACK_DATA = "track_data";
	
	public RunDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS run (_id integer primary key autoincrement, start_date integer, end_date integer, track_data text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public long insertRunData(String HistoryRunData){
		ContentValues cValues = new ContentValues();
		cValues.put(COLUMN_RUN_TRACK_DATA, HistoryRunData);
		 
		return getWritableDatabase().insert(TABLE_RUN, null, cValues);
	}
	
	public List<HistoryTrackData> queryRuns() {
		ArrayList<HistoryTrackData> trackDatas = new ArrayList<HistoryTrackData>();
		RunCursor cursor = queryRunsCursor();
//		cursor.moveToFirst();
		while (cursor.moveToNext()) {
			String historyTrack = cursor.getString(cursor.getColumnIndex(COLUMN_RUN_TRACK_DATA));
			HistoryTrackData trackData = GsonService.parseJson(historyTrack, 
					HistoryTrackData.class);
			trackDatas.add(trackData);
		}
		cursor.close();
		return trackDatas;
	}
	
	
	public RunCursor queryRunsCursor() {
		Cursor wrapped = getReadableDatabase().query(TABLE_RUN, 
				null, null, null, null, null, COLUMN_RUN_START_DATE + " asc");
		return new RunCursor(wrapped);
	}
	
	public int deleteRunCursor(String id) {
		int count = getReadableDatabase().delete(TABLE_RUN,"start_date = ?", new String[]{id});
		Log.i(MainActivity.TAG, "count ==="+count);
		return count;
		
	}
	public static class RunCursor extends CursorWrapper {
		
		public RunCursor(Cursor cursor) {
			// TODO Auto-generated constructor stub
			super(cursor);
		}
		
		public HistoryTrackData getHistoryData() {
			 if( isBeforeFirst() || isAfterLast())
				 return null;
			 HistoryTrackData historyTrackData = new HistoryTrackData();
			 String historyTrack = getString(getColumnIndex(COLUMN_RUN_TRACK_DATA));
			 historyTrackData = GsonService.parseJson(historyTrack, 
						HistoryTrackData.class);
			 return historyTrackData;
		}
		
		public String getStartDate() {
			moveToFirst();
			String startDate = getString(getColumnIndex(COLUMN_RUN_START_DATE));
			return startDate;
		}
	}
	

}
