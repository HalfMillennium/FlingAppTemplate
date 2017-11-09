package com.garrettchestnut.flingfirebase;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatementActivity extends AppCompatActivity {

	static String pStatement;
	static boolean statementAct;
	EditText field;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statement);

		field = (EditText) findViewById(R.id.content);

		DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
		DatabaseReference getContent;

		getContent = ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("statement");
		getContent.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				field.setText(dataSnapshot.getValue(String.class));
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});
	}

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, StatementActivity.class);
	}

	public void saveContent(View view)
	{
		field = (EditText) findViewById(R.id.content);
		String content = field.getText().toString();

		if(content == null)
		{
			Toast.makeText(this, "Nothing To Save!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Personal Statement Saved!", Toast.LENGTH_SHORT).show();
			statementAct = true;
			pStatement = content;

			FirebaseUser activeUser = FirebaseAuth.getInstance().getCurrentUser();
			Log.i("statement", pStatement);

			DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
			ref.child("users").child(activeUser.getDisplayName()).child("statement").setValue(pStatement);
			ref.push();

			StatementActivity.statementAct = false;

			finish();
		}
	}

	public void clearText(View view)
	{
		field = (EditText) findViewById(R.id.content);
		field.setText(null);
	}
}
