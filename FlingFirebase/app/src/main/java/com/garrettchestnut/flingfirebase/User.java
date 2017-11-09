package com.garrettchestnut.flingfirebase;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User extends AppCompatActivity {

	private FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;
	private final static String TAG = "User";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAuth = FirebaseAuth.getInstance();

		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					// User is signed in

					Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
				} else {
					// User is signed out
					Log.d(TAG, "onAuthStateChanged:signed_out");
				}
			}
		};

		mAuth.createUserWithEmailAndPassword(getIntent().getExtras().getString("email"), getIntent().getExtras().getString("password"))
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d("INFO", "createUserWithEmail:success");
							FirebaseUser user = mAuth.getCurrentUser();
							String username = getIntent().getExtras().getString("username");
							String occupation = getIntent().getExtras().getString("occupation");
							String name = getIntent().getExtras().getString("name");

							UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
									.setDisplayName(username).build();

							user.updateProfile(profileUpdates);

							DatabaseReference base = FirebaseDatabase.getInstance().getReference();
							Log.i("Error for Name", username);
							base.child("users").child(username).setValue(username);
							base.child("users").child(username).child("occupation").setValue(occupation);
							base.child("users").child(username).child("name").setValue(name);
							base.child("users").child(username).child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());

							base.push();
							finish();
						} else {
							// If sign in fails, display a message to the user.
							Log.w("INFO", "createUserWithEmail:failure", task.getException());
							Toast.makeText(User.this, "Sign In Failed!", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	@Override
	public void onStart() {
		super.onStart();
		mAuth.addAuthStateListener(mAuthListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mAuthListener != null) {
			mAuth.removeAuthStateListener(mAuthListener);
		}
	}

	public static Intent makeIntent(Context context) { return new Intent(context, User.class); }
}
