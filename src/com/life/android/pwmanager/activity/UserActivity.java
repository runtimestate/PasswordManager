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
import com.life.android.pwmanager.entity.User;
import com.life.android.pwmanager.service.UserDataSource;

public class UserActivity extends Activity implements OnClickListener {

	private User user;
	private UserDataSource userDataSource;

	private EditText passwordEdit;
	private Button loginButton;
	private Button cancelButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		userDataSource = new UserDataSource(this);

		passwordEdit = (EditText) findViewById(R.id.passwordEdit);

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View paramView) {
		switch (paramView.getId()) {
		case R.id.loginButton:
			String password = passwordEdit.getText().toString();
			if (password == null) {
				Intent messageIntent = new Intent();
				messageIntent.putExtra("message",
						this.getString(R.string.wrong_password_message));
				messageIntent.setClass(this, MessageActivity.class);
				startActivity(messageIntent);
				break;
			}
			String encryptedPassword = CryptoHelper.encrypt(password);

			List<User> users = userDataSource.getAllUsers();
			if (users.size() == 0) {
				user = userDataSource.createUser("", encryptedPassword);
			}

			user = userDataSource.getUser(encryptedPassword);
			if (user != null) {
				Intent mainIntent = new Intent();
				mainIntent.putExtra("password", password);
				mainIntent.setClass(this, MainActivity.class);
				startActivity(mainIntent);
			} else {
				Intent messageIntent = new Intent();
				messageIntent.putExtra("message",
						this.getString(R.string.wrong_password_message));
				messageIntent.setClass(this, MessageActivity.class);
				startActivity(messageIntent);
			}
			break;
		case R.id.cancelButton:
			finish();
			break;
		}

	}
}