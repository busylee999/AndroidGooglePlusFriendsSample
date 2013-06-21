package com.example.plustest;

import java.util.Iterator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnPeopleLoadedListener;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

public class LoginGLActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener,
		OnPeopleLoadedListener {

	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private static final int REQUEST_CODE_SHARE = 9001;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPlusClient = new PlusClient.Builder(this, this, this).build();

		// Progress bar to be displayed if the connection failure is not
		// resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!mPlusClient.isConnected())
			mPlusClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				mPlusClient.connect();
			}
		}
		// Save the result and resolve the connection failure upon a user click.
		mConnectionResult = result;
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}

		if (requestCode == REQUEST_CODE_SHARE) {
			mConnectionResult = null;
			Log.i("", "result = " + responseCode);
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		String accountName = mPlusClient.getAccountName();
		//
		mPlusClient.loadPeople(LoginGLActivity.this, Person.Collection.VISIBLE);

		Toast.makeText(this, accountName + " is connected. ", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisconnected() {
		Log.d("", "disconnected");
	}

	@Override
	public void onPeopleLoaded(ConnectionResult status, PersonBuffer personBuffer, String nextPageToken) {
		Log.i("", "persons loaded result = " + status.toString() + ", personsCount = " + personBuffer.getCount()
				+ ", token = " + nextPageToken);
		if (status.isSuccess()) {
			Iterator<Person> itP = personBuffer.iterator();
			while (itP.hasNext()) {
				Person person = itP.next();
				Log.i("", person.getDisplayName());
				// put some you actions here
			}
		}

	}

}
