package com.garrettchestnut.flingfirebase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

	ArrayList<String> names = new ArrayList<>();
	String fN = "", lN = "";
	View main;
	String emailAddress;
	String fullName = "";
	String phone, linkedin, facebook, email, personalStatement, occupation;
	String linkToRes = "";
	String value;
	DatabaseReference genRef = FirebaseDatabase.getInstance().getReference();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = getLayoutInflater().inflate(R.layout.activity_profile, null);
		main.setVisibility(View.GONE);
		setContentView(main);

		final String username = getIntent().getExtras().getString("username");
		String pre_name;

		DatabaseReference base = FirebaseDatabase.getInstance().getReference();
		DatabaseReference user = base.child("users").child(getIntent().getExtras().getString("username"));
		DatabaseReference name = user.child("name");

		name.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists())
				{
					fullName = dataSnapshot.getValue(String.class);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});

		names.add(fullName);
		Log.i("NAMES", fullName);

		for(int count = 0; count < names.get(0).length(); count++) {
			String let = "" + names.get(0).charAt(count);
			if (let.equals(" ")) {
				fN = names.get(0).substring(0, count);
				lN = names.get(0).substring(count, names.get(0).length());

				TextView firstName = (TextView) findViewById(R.id.first_name);
				TextView lastName = (TextView) findViewById(R.id.last_name);
				firstName.setText(fN.toUpperCase());
				lastName.setText(lN.toUpperCase());

				// after title set, continue with the rest of build
				DatabaseReference db = FirebaseDatabase.getInstance().getReference();
				final DatabaseReference target = db.child("users");
				final DatabaseReference flingDB = db.child("fling").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("sender");
				flingDB.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						phone = getData(target.child(dataSnapshot.getValue(String.class)).child("phone"));
						linkedin = getData(target.child(dataSnapshot.getValue(String.class)).child("linkedIn"));
						facebook = getData(target.child(dataSnapshot.getValue(String.class)).child("facebook"));
						email = getData(target.child(dataSnapshot.getValue(String.class)).child("email"));
						personalStatement = getData(target.child(dataSnapshot.getValue(String.class)).child("statement"));
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {}
				});

				db.child("users").child(username).child("occupation");
				db.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						if(dataSnapshot.exists())
						{
							occupation = dataSnapshot.getValue(String.class);
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {}
				});
				TextView pos = (TextView) findViewById(R.id.occupation);
				pos.setText(occupation);

				TextView statement = (TextView) findViewById(R.id.statement);
				statement.setText(personalStatement);

				TextView phoneNum = (TextView) findViewById(R.id.phoneNum);
				String phoneTemp = phone;

				phoneTemp = phoneTemp.substring(0, 3) + "-" + phoneTemp.substring(3, 6) + "-" + phoneTemp.substring(6, 10);
				phoneNum.setText(phoneTemp);

				TextView emailAdd = (TextView) findViewById(R.id.emailView);
				emailAdd.setText(email);

				Log.i("EMAIL", email);

				if(facebook.equals(null))
				{
					TextView fb = (TextView) findViewById(R.id.fb_tag);
					((ViewManager) fb.getParent()).removeView(fb);

					TextView fb_link = (TextView) findViewById(R.id.fb_link);
					((ViewManager) fb_link.getParent()).removeView(fb_link);
				} else {
					TextView fb_link = (TextView) findViewById(R.id.fb_link);
					fb_link.setText(facebook);
				}

				if(linkedin.equals(null))
				{
					TextView li = (TextView) findViewById(R.id.li_tag);
					((ViewManager) li.getParent()).removeView(li);

					TextView li_link = (TextView) findViewById(R.id.li_link);
					((ViewManager) li_link.getParent()).removeView(li_link);
				} else {
					TextView li_link = (TextView) findViewById(R.id.li_link);
					li_link.setText(linkedin);
				}

				if(db.child("users").child(username).child("resume").equals(null))
				{
					Button res = (Button) findViewById(R.id.res_inc);
					Button bc = (Button) findViewById(R.id.bc_inc);

					((ViewManager) res.getParent()).removeView(res);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bc.getLayoutParams();
					params.weight = 2.0f;
					bc.setLayoutParams(params);
				}

				DatabaseReference dbRes = db.child("users").child(username).child("BC");
				dbRes.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						if(!dataSnapshot.exists())
						{
							Button bc = (Button) findViewById(R.id.bc_inc);
							Button res = (Button) findViewById(R.id.res_inc);

							((ViewManager) bc.getParent()).removeView(bc);
							LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) res.getLayoutParams();
							params.weight = 2.0f;
							res.setLayoutParams(params);
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {}
				});


				main.setVisibility(View.VISIBLE);

			}
		}
	}

	public static Intent makeIntent(Context context) { return new Intent(context, ProfileActivity.class); }

	public void selRes(View view)
	{
		DatabaseReference rBase;
		rBase = genRef.child("users").child(getIntent().getExtras().getString("username")).child("resume");
		rBase.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists())
				{
					linkToRes = dataSnapshot.getValue(String.class);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});

		if(!(linkToRes.equals(null)))
		{
			if(!(linkToRes.substring(0, 7).equals("http://")))
			{
				linkToRes = "http://" + linkToRes;
			}

			Uri uri = Uri.parse(linkToRes); // missing 'http://' will cause crashed
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

	public void selBC(View view)
	{
		DatabaseReference bBase;
		bBase = genRef.child("users").child(getIntent().getExtras().getString("username")).child("BC");
		bBase.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Uri card = dataSnapshot.getValue(Uri.class);

				// then do something with the Uri
				Toast.makeText(ProfileActivity.this, "Currently Unable To Retrieve Business Card", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});
	}

	public String getData(DatabaseReference ref)
	{
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				value = dataSnapshot.getValue(String.class);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

		return value;
	}
}