package com.garrettchestnut.flingfirebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by glc22 on 7/2/2017.
 */

public class SignUp extends AppCompatActivity
{
	private boolean username_free = true;
	private boolean email_unused = true;
	private static boolean success = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);

		setTitle(null);
	}

	public void confirm(View view)
	{
		EditText user = (EditText) findViewById(R.id.username);
		EditText pass = (EditText) findViewById(R.id.password);
		EditText eAddress = (EditText) findViewById(R.id.email);
		EditText name = (EditText) findViewById(R.id.fullname);
		EditText occ = (EditText) findViewById(R.id.occupation);

		final String username = user.getText().toString();
		final String password = pass.getText().toString();
		final String email = eAddress.getText().toString();
		final String fName = name.getText().toString();
		final String pos = occ.getText().toString();

		if(username != null && password != null && email != null & fName != null)
		{
			DatabaseReference signUpRef = FirebaseDatabase.getInstance().getReference();

			Query queryUsername = signUpRef.orderByChild("users").equalTo(username);
				queryUsername.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						if(dataSnapshot.exists())
						{
							username_free = false;
							Toast.makeText(SignUp.this, "Username Unavailable!", Toast.LENGTH_SHORT).show();
						} else {
							username_free = true;
						}
					}
					@Override
					public void onCancelled(DatabaseError databaseError) {}
				});

			Query queryEmail = signUpRef.child("users").orderByChild("email").equalTo(email);
			queryEmail.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if(dataSnapshot.exists())
					{
						email_unused = false;
						Toast.makeText(SignUp.this, "Email Already In Use!", Toast.LENGTH_SHORT).show();
						Log.i("TEST", dataSnapshot.getValue(String.class));
					} else {
						email_unused = true;
					}
				}
				@Override
				public void onCancelled(DatabaseError databaseError) {}
			});

			if(username_free && email_unused)
			{
				Intent intent = User.makeIntent(SignUp.this);
				intent.putExtra("username", username);
				intent.putExtra("password", password);
				intent.putExtra("name", fName);
				intent.putExtra("occupation", pos.toUpperCase());
				intent.putExtra("email", email);

				startActivity(intent);

				finish();
			}
		}
	}

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, SignUp.class);
	}

	public static boolean signInState()
	{
		return success;
	}
}
