package com.garrettchestnut.flingfirebase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by glc22 on 7/1/2017.
 */

public class FlingIt extends AppCompatActivity {
	private StorageReference mStorage;
	boolean getRes, getBC, getFB, getLI, getPS;
	boolean userExists = false;
	String fbItem = null, liItem = null, phItem = null, resItem = null, stateItem = null;
	Uri uri = null;
	String linkedIn, state, res_link;
	boolean facebook;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.fling_it);
		mStorage = FirebaseStorage.getInstance().getReference();

		getToggles();
		/*
		if () {
			Switch swi = (Switch) findViewById(R.id.res_inc);
			((ViewManager) swi.getParent()).removeView(swi);
		}

		if (getFileData() == null) {
			ToggleButton swi = (ToggleButton) findViewById(R.id.bc_inc);
			((ViewManager) swi.getParent()).removeView(swi);
		}

		DatabaseReference base_fb = FirebaseDatabase.getInstance().getReference();
		DatabaseReference fbBase = base_fb.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("facebook");
		fbBase.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					String face = dataSnapshot.getValue(String.class);

					if(face.equals(null))
						Log.i("Um,", "Sir");
				} else {
					removeView(R.id.fb_inc);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});

		if (!facebook) {
			ToggleButton swi = (ToggleButton) findViewById(R.id.fb_inc);
			//((ViewManager) swi.getParent()).removeView(swi);
		}

		if (getLinkedInData() == null) {
			Switch swi = (Switch) findViewById(R.id.li_inc);
			((ViewManager) swi.getParent()).removeView(swi);
		}

		if (getStatementData() == null)
		{
			Switch swi = (Switch) findViewById(R.id.ps_inc);
			((ViewManager) swi.getParent()).removeView(swi);
		}
		*/

		getFacebookData();
		getLinkedInData();
		getStatementData();
		getResumeData();
	}

	public static Intent makeIntent(Context context) {
		return new Intent(context, FlingIt.class);
	}

	public void select(View view) {
		ToggleButton tog = (ToggleButton) view;

		if (tog.getId() == R.id.res_inc)
			getRes = true;

		if (tog.getId() == R.id.bc_inc)
			getBC = true;

		if (tog.getId() == R.id.fb_inc)
			getFB = true;

		if (tog.getId() == R.id.li_inc)
			getLI = true;

		if (tog.getId() == R.id.ps_inc)
			getPS = true;
	}

	public void launch(View view) {
		final FirebaseUser flingUser = FirebaseAuth.getInstance().getCurrentUser();
		EditText recipient = (EditText) findViewById(R.id.target);
		final String target = recipient.getText().toString();

		if(!(target.equals(flingUser.getDisplayName()))) {
			DatabaseReference flingDB;

			FirebaseDatabase dBase = FirebaseDatabase.getInstance();
			flingDB = dBase.getReference("users").child(target);
			flingDB.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if(dataSnapshot != null)
					{
						userExists = true;
						Log.i("SUCCESS", "MY MAN");
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});

			if(userExists)
			{
				flingDB.child("users").child(flingUser.getDisplayName()).child("fling").child("recipient: " + target);
				DatabaseReference ref = flingDB.child("users").child(flingUser.getDisplayName()).child("fling").child("recipient: " + target);

				if(getRes)
				{
					ref.child("resume").setValue(1);
				} else { ref.child("resume").setValue(0); }

				if(getFB)
				{
					ref.child("facebook").setValue(1);
				} else { ref.child("facebook").setValue(0); }

				if(getLI)
				{
					ref.child("linkedIn").setValue(1);
				} else { ref.child("linkedIn").setValue(0); }

				if(getBC)
				{
					ref.child("BC").setValue(1);
				} else { ref.child("BC").setValue(0); }
				flingDB.push();
			}
			finish();
			Toast.makeText(this, "Fling Successful!", Toast.LENGTH_SHORT).show();
			userExists = false;
		} else {
			Toast.makeText(this, "That's you!", Toast.LENGTH_SHORT).show();
		}
	}

	public void getFileData()
	{
		DatabaseReference base = FirebaseDatabase.getInstance().getReference();
		DatabaseReference bc = base.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("BC");
		bc.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					uri = dataSnapshot.getValue(Uri.class);
				} else {
					removeView(R.id.bc_inc);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});
	}

	public void getFacebookData() {
		DatabaseReference base_fb = FirebaseDatabase.getInstance().getReference();
		DatabaseReference fbBase = base_fb.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("facebook");
		fbBase.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					facebook = true;
				} else {
					removeView(R.id.fb_inc);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});

	}

	public void getLinkedInData() {
		DatabaseReference base_li = FirebaseDatabase.getInstance().getReference();
		DatabaseReference liBase = base_li.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("linkedIn");
		liBase.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					linkedIn = dataSnapshot.getValue(String.class);
				} else {
					removeView(R.id.li_inc);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});
	}

	public void getResumeData()
	{
		DatabaseReference base_res = FirebaseDatabase.getInstance().getReference();
		DatabaseReference resBase = base_res.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("resume");
		resBase.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists()) {
					res_link = dataSnapshot.getValue(String.class);
				} else {
					removeView(R.id.res_inc);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});
	}

	public void getStatementData()
	{
		DatabaseReference base_ps = FirebaseDatabase.getInstance().getReference();
		DatabaseReference psBase = base_ps.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("statement");
		psBase.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				if(dataSnapshot.exists()) {
					state = dataSnapshot.getValue(String.class);
				} else {
					removeView(R.id.ps_inc);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});
	}

	public void getWebLinkData()
	{
		DatabaseReference base_ps = FirebaseDatabase.getInstance().getReference();
		DatabaseReference psBase = base_ps.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("statement");
		psBase.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				if(dataSnapshot.exists()) {
					Log.i("WEB", dataSnapshot.getValue(String.class));
				} else {
					removeView(R.id.ps_inc);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});
	}

	public void removeView(int id)
	{
		Switch swi = (Switch) findViewById(id);
		((ViewManager) swi.getParent()).removeView(swi);
	}

	public void getToggles()
	{
		final ToggleButton res = (ToggleButton) findViewById(R.id.res_inc);
		res.setChecked(false);
		res.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					res.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.select_sel));
				} else {
					res.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.select_def));
				}
			}
		});

		final ToggleButton fb = (ToggleButton) findViewById(R.id.fb_inc);
		fb.setChecked(false);
		fb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					fb.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.select_sel));
				} else {
					fb.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.select_def));
				}
			}
		});

		final ToggleButton li = (ToggleButton) findViewById(R.id.li_inc);
		li.setChecked(false);
		li.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					li.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.select_sel));
				} else {
					li.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.select_def));
				}
			}
		});

		final ToggleButton ps = (ToggleButton) findViewById(R.id.ps_inc);
		ps.setChecked(false);
		ps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					ps.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.select_sel));
				} else {
					ps.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.select_def));
				}
			}
		});

		final ToggleButton web = (ToggleButton) findViewById(R.id.web_inc);
		web.setChecked(false);
		web.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					web.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.select_sel));
				} else {
					web.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.select_def));
				}
			}
		});
	}
}
