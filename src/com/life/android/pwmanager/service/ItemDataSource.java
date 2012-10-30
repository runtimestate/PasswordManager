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
import static com.life.android.pwmanager.entity.Item.CONTENT;
import static com.life.android.pwmanager.entity.Item.MEMO;
import static com.life.android.pwmanager.entity.Item.SITE;
import static com.life.android.pwmanager.entity.Item.TABLE_NAME;
import static com.life.android.pwmanager.entity.Item.TITLE;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.life.android.pwmanager.dao.SQLiteHelper;
import com.life.android.pwmanager.entity.Item;

public class ItemDataSource {

	private static String[] allColumns = { _ID, TITLE, CONTENT, SITE, MEMO, };
	private static String ORDER_BY = TITLE + " DESC";

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;

	public ItemDataSource(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Item createItem(String title, String content, String site,
			String memo) {
		ContentValues values = new ContentValues();
		values.put(TITLE, title);
		values.put(CONTENT, content);
		values.put(SITE, site);
		values.put(MEMO, memo);
		long insertId = database.insert(TABLE_NAME, null, values);
		Cursor cursor = database.query(TABLE_NAME, allColumns, _ID + " = "
				+ insertId, null, null, null, null);
		cursor.moveToFirst();
		Item newItem = cursorToItem(cursor);
		cursor.close();
		return newItem;
	}

	public void deleteItem(Item item) {
		long id = item.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(TABLE_NAME, _ID + " = " + id, null);
	}

	public boolean updateItem(Item item) {
		ContentValues values = new ContentValues();
		values.put(_ID, item.getId());
		values.put(TITLE, item.getTitle());
		values.put(CONTENT, item.getContent());
		values.put(SITE, item.getSite());
		values.put(MEMO, item.getMemo());
		return database.update(TABLE_NAME, values, _ID + " = " + item.getId(),
				null) > 0;
	}

	public Item getItem(Long id) {
		Cursor cursor = database.query(TABLE_NAME, allColumns,
				_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		Item item = cursorToItem(cursor);
		cursor.close();
		return item;
	}

	public List<Item> getAllItems() {
		List<Item> items = new ArrayList<Item>();

		Cursor cursor = database.query(TABLE_NAME, allColumns, null, null,
				null, null, ORDER_BY);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Item item = cursorToItem(cursor);
			items.add(item);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return items;
	}

	private Item cursorToItem(Cursor cursor) {
		if (cursor.getCount() < 1) {
			return null;
		}
		Item item = new Item();
		item.setId(cursor.getLong(0));
		item.setTitle(cursor.getString(1));
		item.setContent(cursor.getString(2));
		item.setSite(cursor.getString(3));
		item.setMemo(cursor.getString(4));
		return item;
	}

}
