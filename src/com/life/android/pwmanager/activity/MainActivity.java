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

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler {

		public static final int REFRESH = 1;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH:
				sortValues();
				adapter.notifyDataSetChanged();
				break;
			}
		}
	}

	private UIHandler uiHandler;

	private static final int DELETE_ID = Menu.FIRST + 1;

	public static final int ADD_OR_UPDATE = 101;
	public static final int RESET_PASSWORD = 102;

	private String password;
	private ItemDataSource itemDataSource;
	private ArrayAdapter<Item> adapter;
	private List<Item> values;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHandler = new UIHandler();
		itemDataSource = new ItemDataSource(this);

		password = getIntent().getStringExtra("password");
		if (password == null) {
			return;
		}

		values = itemDataSource.getAllItems();

		sortValues();
		adapter = new ArrayAdapter<Item>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);

		registerForContextMenu(this.getListView());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Item item = (Item) l.getItemAtPosition(position);
		Intent intent = new Intent();
		intent.putExtra("id", item.getId());
		intent.putExtra("password", password);
		intent.setClass(this, ItemActivity.class);
		startActivityForResult(intent, ADD_OR_UPDATE);
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
				sortValues();
				adapter.notifyDataSetChanged();
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
			startActivityForResult(itemIntent, ADD_OR_UPDATE);
			return true;
		case R.id.resetPasswordMenu:
			Intent resetPasswordIntent = new Intent();
			resetPasswordIntent.putExtra("password", password);
			resetPasswordIntent.setClass(this, ResetPasswordActivity.class);
			startActivityForResult(resetPasswordIntent, RESET_PASSWORD);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case ADD_OR_UPDATE:
			password = data.getStringExtra("password");
			Item item = ((Item) data.getSerializableExtra("item"));

			for (int i = values.size(); i > 0; i--) {
				Item temp = values.get(i - 1);
				if (item.getId().equals(temp.getId())) {
					values.remove(temp);
				}
			}
			values.add(item);
			break;
		case RESET_PASSWORD:
			password = data.getStringExtra("password");
			break;
		}

		uiHandler.sendEmptyMessage(UIHandler.REFRESH);
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.exit_prompt)
				.setMessage(R.string.exit_message)
				.setNegativeButton(R.string.cancel_button_label,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						})
				.setPositiveButton(R.string.confirm_button_label,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
								finish();
							}
						}).show();
	}

	// TODO 
	private void sortValues() {
		final Collator chinaCollator = Collator
				.getInstance(java.util.Locale.CHINA);
		Collections.sort(values, new Comparator<Item>() {
			@Override
			public int compare(Item item1, Item item2) {
				String title1 = item1.getTitle();
				String title2 = item1.getTitle();
				return chinaCollator.compare(title1, title2);
			}
		});
	}

}
