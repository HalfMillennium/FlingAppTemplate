/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.garrettchestnut.flingfirebase;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.garrettchestnut.flingfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

	View main;
	boolean loggedIn, signedUp;

	Button signIn;
	TextView user;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		main = getLayoutInflater().inflate(R.layout.activity_main, null);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(main);

		signIn = (Button) findViewById(R.id.sign_in);

		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

		if(user != null)
		{
			loggedIn = true;
			signedUp = true;
			Toast.makeText(this, user.getDisplayName() + " is signed in!", Toast.LENGTH_SHORT).show();
			setSignOut(signIn);
		}
	}

	//set up
	public void getSetup(View view)
	{
		if(FirebaseAuth.getInstance().getCurrentUser() != null) {
			Intent intent = SetUp.makeIntent(MainActivity.this);
			startActivity(intent);
		} else
		{
			Toast.makeText(this, "Log in or Sign up!", Toast.LENGTH_SHORT).show();
		}
	}

	// launch
	public void getLaunch(View view)
	{
		if(FirebaseAuth.getInstance().getCurrentUser() != null) {
			Intent intent = FlingIt.makeIntent(MainActivity.this);
			startActivity(intent);
		} else {
			Toast.makeText(this, "Log in or Sign up!", Toast.LENGTH_SHORT).show();
		}
	}

	// sign in
	public void getSignIn (View view)
	{
		Intent intent = SignIn.makeIntent(MainActivity.this);
		startActivity(intent);
	}


	// test user list

	public void getUserList(View view)
	{
		//Intent intent = UserList.makeIntent(MainActivity.this);
		//startActivity(intent);
	}

	public void getContacts(View view)
	{
		if(FirebaseAuth.getInstance().getCurrentUser() != null)
		{
			Intent intent = ViewContactsActivity.makeIntent(MainActivity.this);
			startActivity(intent);
		}
		else
			Toast.makeText(this, "Log in or Sign up!", Toast.LENGTH_SHORT).show();
	}

	public void setSignOut(final Button button)
	{
			Toast.makeText(this, "POINT REACHED", Toast.LENGTH_SHORT).show();
			button.setText("SIGN OUT");
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FirebaseAuth.getInstance().signOut();

					if(FirebaseAuth.getInstance().getCurrentUser() == null) {
						Toast.makeText(MainActivity.this, "Logged Out!", Toast.LENGTH_SHORT).show();
						button.setText("SIGN IN");
					}

					button.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							getSignIn(v);
						}
					});
				}
			});
		}

	public void onResume()
	{
		super.onResume();

		if(FirebaseAuth.getInstance().getCurrentUser() != null) {
			setSignOut(signIn);
			Log.i("SUCCESS", "SIGN IN SUCCESS");
			SignIn.makeFalse();
		}
	}
}

