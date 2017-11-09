package com.garrettchestnut.flingfirebase;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewContactsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_contents);

		final ListView list = (ListView) findViewById(R.id.contactList);
		final ArrayList<String> contacts = new ArrayList<>();

		final ArrayAdapter adapt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, contacts);

		DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

		Query query = ref.orderByChild("fling").equalTo(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists())
				{
					for(DataSnapshot snap : dataSnapshot.getChildren())
					{
						contacts.add(snap.child("sender").getValue(String.class));
					}

					list.setAdapter(adapt);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = ProfileActivity.makeIntent(ViewContactsActivity.this);
				intent.putExtra("username", contacts.get(position));

				startActivity(intent);
			}
		});
	}

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, ViewContactsActivity.class);
	}
}

