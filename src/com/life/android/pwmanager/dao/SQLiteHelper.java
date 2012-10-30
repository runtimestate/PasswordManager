/**
 * This file is part of ${project_name}.
 * 
 * ${project_name} is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * ${project_name} is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ${project_name}. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @see http://www.gnu.org/licenses/lgpl.txt
 * @author art <runtimestate@gmail.com>
 */
package com.life.android.pwmanager.dao;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.life.android.pwmanager.entity.Item;
import com.life.android.pwmanager.entity.User;

public class SQLiteHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "pwmanager.db";
	private static final int DATABASE_VERSION = 1;

	/** Create a helper object for the Events database */
	public SQLiteHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Item.TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + Item.TITLE
				+ " TEXT NOT NULL, " + Item.CONTENT + " TEXT NOT NULL, "
				+ Item.SITE + " TEXT NOT NULL, " + Item.MEMO
				+ " TEXT NOT NULL);");
		db.execSQL("CREATE TABLE " + User.TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + User.NAME
				+ " TEXT NOT NULL, " + User.PASSWORD + " TEXT NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Item.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
		onCreate(db);
	}
}