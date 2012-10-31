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
package com.life.android.pwmanager.activity;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.life.android.pwmanager.R;
import com.life.android.pwmanager.entity.Item;
import com.life.android.pwmanager.service.ItemDataSource;

public class MainActivity extends ListActivity {

	private static final int DELETE_ID = Menu.FIRST + 1;
	private String password;
	private ItemDataSource itemDataSource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		itemDataSource = new ItemDataSource(this);

		password = getIntent().getStringExtra("password");
		if (password == null) {
			return;
		}

		registerForContextMenu(this.getListView());

		List<Item> values = itemDataSource.getAllItems();
		ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Item item = (Item) l.getItemAtPosition(position);
		Intent intent = new Intent();
		intent.putExtra("id", item.getId());
		intent.putExtra("password", password);
		intent.setClass(this, ItemActivity.class);
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		menu.add(0, DELETE_ID, 0, R.string.delete_menu_label);
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<Item> adapter = (ArrayAdapter<Item>) getListAdapter();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuItem
				.getMenuInfo();
		Item item = null;
		switch (menuItem.getItemId()) {
		case DELETE_ID:
			if (getListAdapter().getCount() > 0) {
				item = (Item) adapter.getItem(info.position);
				itemDataSource.deleteItem(item);
				adapter.remove(item);
			}
			break;
		default:
			break;
		}
		return super.onContextItemSelected(menuItem);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.createMenu:
			Intent itemIntent = new Intent();
			itemIntent.putExtra("password", password);
			itemIntent.setClass(this, ItemActivity.class);
			startActivity(itemIntent);
			return true;
		case R.id.resetPasswordMenu:
			Intent resetPasswordIntent = new Intent();
			resetPasswordIntent.putExtra("password", password);
			resetPasswordIntent.setClass(this, ResetPasswordActivity.class);
			startActivity(resetPasswordIntent);
			return true;
		case R.id.aboutMenu:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.exitMenu:
			finish();
			return true;
		}
		return false;
	}

}
