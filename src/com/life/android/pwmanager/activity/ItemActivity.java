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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.life.android.pwmanager.R;
import com.life.android.pwmanager.dao.CryptoHelper;
import com.life.android.pwmanager.entity.Item;
import com.life.android.pwmanager.service.ItemDataSource;

public class ItemActivity extends Activity implements OnClickListener {

	private String password;

	private Item item;
	private ItemDataSource itemDataSource;

	private EditText titleEdit;
	private EditText contentEdit;
	private EditText siteEdit;
	private EditText memoEdit;
	private Button saveButton;
	private Button cancelButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item);
		itemDataSource = new ItemDataSource(this);

		long id = getIntent().getLongExtra("id", 0l);
		password = getIntent().getStringExtra("password");
		if (id == 0l) {
			item = new Item();
		} else {
			item = itemDataSource.getItem(id);
		}

		String title = item.getTitle();
		String content = item.getContent();
		if (content != null) {
			content = CryptoHelper.decrypt(content, password);
		}
		String site = item.getSite();
		String memo = item.getMemo();

		titleEdit = (EditText) findViewById(R.id.titleEdit);
		titleEdit.setText(title);
		contentEdit = (EditText) findViewById(R.id.contentEdit);
		contentEdit.setText(content);
		siteEdit = (EditText) findViewById(R.id.siteEdit);
		siteEdit.setText(site);
		memoEdit = (EditText) findViewById(R.id.memoEdit);
		memoEdit.setText(memo);

		saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View paramView) {
		switch (paramView.getId()) {
		case R.id.saveButton:
			String title = titleEdit.getText().toString();
			String content = contentEdit.getText().toString();
			if (title == null || content == null) {
				Intent messageIntent = new Intent();
				messageIntent.putExtra("message",
						this.getString(R.string.wrong_password_message));
				messageIntent.setClass(this, MessageActivity.class);
				startActivity(messageIntent);
				break;
			}
			String encryptedPassword = CryptoHelper.encrypt(content, password);
			String site = siteEdit.getText().toString();
			String memo = memoEdit.getText().toString();
			if (item.getId() == null) {
				item = itemDataSource.createItem(title, encryptedPassword,
						site, memo);
			} else {
				item.setTitle(title);
				item.setContent(encryptedPassword);
				item.setSite(site);
				item.setMemo(memo);
				itemDataSource.updateItem(item);
			}

			Intent intent = new Intent();
			intent.putExtra("password", password);
			intent.putExtra("item", item);
			setResult(MainActivity.ADD_OR_UPDATE, intent);
			finish();
			break;
		case R.id.cancelButton:
			finish();
			break;
		}

	}
}