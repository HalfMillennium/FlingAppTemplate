package com.garrettchestnut.flingfirebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by glc22 on 7/2/2017.
 */

public class SignIn extends AppCompatActivity
{
	private FirebaseAuth mAuth;
	private static final String TAG = "SignIn";
	private FirebaseAuth.AuthStateListener mAuthListener;
	private static boolean success = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in);

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
	}

	public void signIn(View view)
	{
		final MainActivity newMain = new MainActivity();

		EditText mail = (EditText) findViewById(R.id.email);
		EditText pass = (EditText) findViewById(R.id.password);

		String email = mail.getText().toString();
		String password = pass.getText().toString();

		if(email != null && password != null)
		{
			mAuth.signInWithEmailAndPassword(email, password)
					.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete(@NonNull Task<AuthResult> task) {
							Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

							if(task.isSuccessful()) {
								success = true;
								if(success)
									Log.i("SUCCESS", "SUCCESS");

								Toast.makeText(SignIn.this, "Log In Successful!", Toast.LENGTH_SHORT).show();

								final Button signIn = (Button) findViewById(R.id.sign_in);
								signIn.setText("SIGN OUT");
								signIn.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										signIn.setText("SIGN OUT");
										signIn.setOnClickListener(new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												mAuth.signOut();
												Toast.makeText(SignIn.this, "Logged Out!", Toast.LENGTH_SHORT).show();

												signIn.setText("SIGN IN");
												signIn.setOnClickListener(new View.OnClickListener() {
													@Override
													public void onClick(View v) {
														newMain.setSignOut(signIn);
													}
												});
											}
										});
									}
								});

								finish();
							}
							// If sign in fails, display a message to the user. If sign in succeeds
							// the auth state listener will be notified and logic to handle the
							// signed in user can be handled in the listener.
							if (!task.isSuccessful()) {
								Log.w(TAG, "signInWithEmail:failed", task.getException());

								Toast.makeText(SignIn.this, "Log In Failed. Username/Password Incorrect.", Toast.LENGTH_SHORT).show();
								Log.i("LOG IN FAILED", "Error: " + task.toString());
							}
						}
					});
		}
	}

	public static Intent makeIntent(Context context)
	{
		return new Intent(context, SignIn.class);
	}

	public void signUp (View view)
	{
		Intent intent = SignUp.makeIntent(SignIn.this);
		startActivity(intent);

		finish();
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

	public static boolean getSignInState()
	{
		return success;
	}
	public static void makeFalse()
	{
		success = false;
	}
}
