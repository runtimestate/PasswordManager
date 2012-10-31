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
package com.life.android.pwmanager.service;

import static android.provider.BaseColumns._ID;
import static com.life.android.pwmanager.entity.User.NAME;
import static com.life.android.pwmanager.entity.User.PASSWORD;
import static com.life.android.pwmanager.entity.User.TABLE_NAME;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.life.android.pwmanager.dao.SQLiteHelper;
import com.life.android.pwmanager.entity.User;

public class UserDataSource {

	private static String[] allColumns = { _ID, NAME, PASSWORD, };
	private static String ORDER_BY = NAME + " DESC";

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;

	public UserDataSource(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public User createUser(String name, String password) {
		User newUser = null;
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(NAME, name);
			values.put(PASSWORD, password);
			long insertId = database.insert(TABLE_NAME, null, values);
			Cursor cursor = database.query(TABLE_NAME, allColumns, _ID + " = "
					+ insertId, null, null, null, null);
			cursor.moveToFirst();
			newUser = cursorToUser(cursor);
			cursor.close();
		} finally {
			close();
		}
		return newUser;
	}

	public void deleteUser(User user) {
		try {
			open();
			long id = user.getId();
			database.delete(TABLE_NAME, _ID + " = " + id, null);
		} finally {
			close();
		}
	}

	public boolean updateUser(User user) {
		boolean flag = false;
		try {
			open();
			ContentValues values = new ContentValues();
			values.put(_ID, user.getId());
			values.put(NAME, user.getName());
			values.put(PASSWORD, user.getPassword());
			flag = database.update(TABLE_NAME, values,
					_ID + " = " + user.getId(), null) > 0;
		} finally {
			close();
		}
		return flag;
	}

	public User getUser(Long id) {
		User user = null;
		try {
			open();
			Cursor cursor = database.query(TABLE_NAME, allColumns, _ID + " = "
					+ id, null, null, null, null);
			cursor.moveToFirst();
			user = cursorToUser(cursor);
			cursor.close();
		} finally {
			close();
		}
		return user;
	}

	public User getUser(String password) {
		User user = null;
		try {
			open();
			Cursor cursor = database.query(TABLE_NAME, allColumns, PASSWORD
					+ " = '" + password + "'", null, null, null, null);
			cursor.moveToFirst();
			user = cursorToUser(cursor);
			cursor.close();
		} finally {
			close();
		}
		return user;
	}

	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		try {
			open();
			Cursor cursor = database.query(TABLE_NAME, allColumns, null, null,
					null, null, ORDER_BY);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				User user = cursorToUser(cursor);
				users.add(user);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
		} finally {
			close();
		}
		return users;
	}

	private User cursorToUser(Cursor cursor) {
		if (cursor.getCount() < 1) {
			return null;
		}
		User user = new User();
		user.setId(cursor.getLong(0));
		user.setName(cursor.getString(1));
		user.setPassword(cursor.getString(2));
		return user;
	}

}
