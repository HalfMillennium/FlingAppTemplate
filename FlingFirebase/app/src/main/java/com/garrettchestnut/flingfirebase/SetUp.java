package com.garrettchestnut.flingfirebase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by glc22 on 7/2/2017.
 */

public class SetUp extends AppCompatActivity
{
	private static boolean filepathB;
	private Button busCard;
	private static int ensureOneIsComplete = 0;
	private static String facebook, phoneNum, linkedIn, webURL;
	public static String resLink;
	private StorageReference mStorage;
	static boolean fileRetrieved;
	static String cardName;
	boolean fbAvail, liAvail, psAvail, resAvail, webAvail, phAvail;
	String file_name = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		setTitle(null);

		busCard = (Button) findViewById(R.id.upload);

		DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

		mStorage = FirebaseStorage.getInstance().getReference();

		final EditText phoneAuto = (EditText) findViewById(R.id.phone);
		try {
			DatabaseReference phone = ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("phone");
			phone.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if(dataSnapshot != null)
						phoneAuto.setText(dataSnapshot.getValue(String.class));
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {}
			});
		} catch (Exception e) {
			Log.i("RNF", "1");
		}

		final EditText fbAuto = (EditText) findViewById(R.id.fb_page);
		try {
			DatabaseReference facebook = ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("facebook");
			facebook.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if(dataSnapshot != null)
						fbAuto.setText(dataSnapshot.getValue(String.class));
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {}
			});
		} catch (Exception e) {
			Log.i("RNF", "2");
		}

		final EditText liAuto = (EditText) findViewById(R.id.linkedIn);
		try {
			DatabaseReference linkedIn = ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("linkedIn");
			linkedIn.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if(dataSnapshot != null)
						liAuto.setText(dataSnapshot.getValue(String.class));
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {}
			});
		} catch (Exception e) {
			Log.i("RNF", "3");
		}

		final EditText resAuto = (EditText) findViewById(R.id.resLink);
		try {
			DatabaseReference resLink = ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("resume");
			resLink.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if(dataSnapshot != null)
						resAuto.setText(dataSnapshot.getValue(String.class));
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {}
			});
		} catch (Exception e) {
			Log.i("RNF", "RES");
		}

		// get business card data
		try {
			DatabaseReference bc = ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("BC");
			bc.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if(dataSnapshot.getValue(Uri.class) != null)
					{
						DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("bcName");
						ref.addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
								file_name = dataSnapshot.getValue(String.class);
							}

							@Override
							public void onCancelled(DatabaseError databaseError) {
								Log.i("UNABLE TO RET", "BC");
							}
						});
						busCard.setText("'" + file_name + "' Has Been Selected");
						busCard.setBackgroundColor(Color.parseColor("#67E6EC"));
						busCard.setTextColor(Color.BLACK);
						filepathB = true;
						fileRetrieved = false;
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {}
			});
		} catch (Exception e) {
			Log.i("RNF", "RES");
		}
	}

	public void onClickBC (View view)
	{
		Intent intent = UploadImage.makeIntent(SetUp.this);
		startActivity(intent);
	}
	public void clearRES(View view)
	{
		EditText resAuto = (EditText) findViewById(R.id.resLink);
		resAuto.setText(null);
	}

	public void clearBC(View view)
	{
		busCard.setText("Upload Your Digital Business Card (JPEG, PNG)");
		busCard.setBackgroundColor(Color.parseColor("#090909"));
		busCard.setTextColor(Color.WHITE);

		filepathB = false;
		cardName = null;
		fileRetrieved = false;
	}

	public void save (View view)
	{
		EditText fb = (EditText) findViewById(R.id.fb_page);
		EditText phone = (EditText) findViewById(R.id.phone);
		EditText li = (EditText) findViewById(R.id.linkedIn);
		EditText res = (EditText) findViewById(R.id.resLink);
		EditText web = (EditText) findViewById(R.id.webLink);

		facebook = fb.getText().toString();
		phoneNum = phone.getText().toString();
		linkedIn = li.getText().toString();
		resLink = res.getText().toString();
		webURL = web.getText().toString();

		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

		DatabaseReference uRef = FirebaseDatabase.getInstance().getReference();

		getAvailabilities();

		//// PSA --- If an 'Avail' boolean is true, it means it isn't there. I don't why I did this.
		if(!fbAvail)
		{
			ensureOneIsComplete++;

			uRef.child("users").child(user.getDisplayName()).child("facebook").setValue(facebook);

			uRef.push();
		} else { uRef.child("users").child(user.getDisplayName()).child("facebook").removeValue(); uRef.push(); fbAvail = true;}

		if(!phAvail)
		{
			ensureOneIsComplete++;

			DatabaseReference pRef = FirebaseDatabase.getInstance().getReference();
			pRef.child("users").child(user.getDisplayName()).child("phone").setValue(phoneNum);

			pRef.push();
		}	else { uRef.child("users").child(user.getDisplayName()).child("phone").removeValue(); uRef.push(); phAvail = true;}

		if(!liAvail)
		{
			ensureOneIsComplete++;

			DatabaseReference lRef = FirebaseDatabase.getInstance().getReference();
			lRef.child("users").child(user.getDisplayName()).child("linkedIn").setValue(linkedIn);

			lRef.push();
		}	else { uRef.child("users").child(user.getDisplayName()).child("linkedIn").removeValue(); uRef.push(); liAvail = true;}

		if(!resAvail)
		{
			ensureOneIsComplete++;

			DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
			FirebaseUser userToUpload = FirebaseAuth.getInstance().getCurrentUser();

			dbref.child("users").child(userToUpload.getDisplayName()).child("resume").setValue(resLink);
			dbref.push();
		}	else { uRef.child("users").child(user.getDisplayName()).child("resume").removeValue(); uRef.push(); resAvail = true;}

		if(!webAvail)
		{
			ensureOneIsComplete++;

			DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
			FirebaseUser userToUpload = FirebaseAuth.getInstance().getCurrentUser();

			dbref.child("users").child(userToUpload.getDisplayName()).child("weblink").setValue(webURL);
			dbref.push();
		}	else { uRef.child("users").child(user.getDisplayName()).child("weblink").removeValue(); uRef.push(); webAvail = true;}

		if(filepathB)
		{
			ensureOneIsComplete++;

			DatabaseReference base = FirebaseDatabase.getInstance().getReference();
			DatabaseReference ref = base.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("BC").child(UploadImage.file.getLastPathSegment());

			StorageReference store = mStorage.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("BC").child(UploadImage.file.getLastPathSegment());
			store.putFile(UploadImage.file)
					.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
						@Override
						public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
							Log.i("UPLOAD", "COMPLETE.");

							DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
							ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("bcName").setValue(cardName);
						}
					})
					.addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception exception) {
							Toast.makeText(SetUp.this, "Unable To Upload Business Card.", Toast.LENGTH_SHORT).show();
							Log.i("UNABLE TO UPLOAD FILE", "Error: " + exception.toString());
						}
					});
		}	else { uRef.child("users").child(user.getDisplayName()).child("BC").removeValue(); uRef.push(); }

		finish();
	}

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, SetUp.class);
	}

	public static String getFacebook() { return facebook; }
	public static  String getLinkedIn() { return linkedIn; }
	public static String getResLink() { return resLink; }

	@Override
	protected void onResume() {
		super.onResume();

		if(fileRetrieved) {
			busCard.setText("'" + cardName + "' Has Been Selected");
			busCard.setBackgroundColor(Color.parseColor("#ffffff"));
			busCard.setTextColor(Color.BLACK);

			filepathB = true;
			fileRetrieved = false;
		}
	}

	public void getStatement(View view)
	{
		Intent intent = StatementActivity.makeIntent(SetUp.this);
		startActivity(intent);
	}

	public void getAvailabilities()
	{
		try
		{
			char check = facebook.charAt(0);
		} catch (IndexOutOfBoundsException e) {
			fbAvail = true;
		}

		try
		{
			char check = linkedIn.charAt(0);
		} catch (IndexOutOfBoundsException e) {
			 liAvail = true;
		}

		try
		{
			char check = resLink.charAt(0);
		} catch (IndexOutOfBoundsException e) {
			resAvail = true;
		}

		try
		{
			char check = webURL.charAt(0);
		} catch (IndexOutOfBoundsException e) {
			webAvail = true;
		}

		try
		{
			char check = phoneNum.charAt(0);
		} catch (IndexOutOfBoundsException e) {
			phAvail = true;
		}
	}
}
