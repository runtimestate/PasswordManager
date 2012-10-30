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
import com.life.android.pwmanager.entity.User;
import com.life.android.pwmanager.service.ItemDataSource;
import com.life.android.pwmanager.service.UserDataSource;

public class ResetPasswordActivity extends Activity implements OnClickListener {

	private String password;

	private User user;
	private UserDataSource userDataSource;
	private ItemDataSource itemDataSource;

	private EditText oldPasswordEdit;
	private EditText newPasswordEdit;
	private Button resetPasswordButton;
	private Button cancelButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reset_password);
		userDataSource = new UserDataSource(this);
		userDataSource.open();
		itemDataSource = new ItemDataSource(this);
		itemDataSource.open();

		password = getIntent().getStringExtra("password");

		oldPasswordEdit = (EditText) findViewById(R.id.oldPasswordEdit);
		newPasswordEdit = (EditText) findViewById(R.id.newPasswordEdit);

		resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);
		resetPasswordButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View paramView) {
		switch (paramView.getId()) {
		case R.id.resetPasswordButton:
			String oldPassword = oldPasswordEdit.getText().toString();
			String newPassword = newPasswordEdit.getText().toString();
			if (!oldPassword.equals(oldPassword) || newPassword == null) {
				Intent messageIntent = new Intent();
				messageIntent.putExtra("message",
						this.getString(R.string.wrong_password_message));
				messageIntent.setClass(this, MessageActivity.class);
				startActivity(messageIntent);
			}
			String encryptedPassword = CryptoHelper.encrypt(password);
			String newEncryptedPassword = CryptoHelper.encrypt(newPassword);
			// Update user
			user = userDataSource.getUser(encryptedPassword);
			user.setPassword(newEncryptedPassword);
			userDataSource.updateUser(user);
			// Update items
			List<Item> items = itemDataSource.getAllItems();
			for (Item item : items) {
				String content = CryptoHelper.decrypt(item.getContent(),
						password);
				item.setContent(CryptoHelper.encrypt(content, newPassword));
				itemDataSource.updateItem(item);
			}

			Intent mainIntent = new Intent();
			mainIntent.putExtra("password", newPassword);
			mainIntent.setClass(this, MainActivity.class);
			startActivity(mainIntent);
			break;
		case R.id.cancelButton:
			finish();
			break;
		}

	}
}