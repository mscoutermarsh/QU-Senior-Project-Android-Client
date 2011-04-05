package org.seniorProject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Pet extends Activity {

	private TextView text_age, text_hunger, text_mood, text_cleanliness;
	private String api_key = "ddda6ea9eefd";
	private String email = "mike@mike.com";
	private boolean alive;
	public static final String PREFS_NAME = "LocalPetData";
	private SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	private SharedPreferences.Editor editor = settings.edit();
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Restore pet data
		api_key = settings.getString("key", null);
		email = settings.getString("email", null);

		text_age = (TextView) findViewById(R.id.age);
		text_hunger = (TextView) findViewById(R.id.hunger);
		text_mood = (TextView) findViewById(R.id.mood);
		text_cleanliness = (TextView) findViewById(R.id.cleanliness);

		// Setup buttons
		final Button button_feed = (Button) findViewById(R.id.feed);
		final Button button_play = (Button) findViewById(R.id.play);
		final Button button_clean = (Button) findViewById(R.id.clean);
		final Button button_stats = (Button) findViewById(R.id.stats);

		button_feed.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					new HttpPostTask().execute("http://mcqu.heroku.com/pet/"
							+ api_key + "/feed/?email=" + email);
					// updatePetData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		button_play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					new HttpPostTask().execute("http://mcqu.heroku.com/pet/"
							+ api_key + "/play/?email=" + email);
					// updatePetData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		button_clean.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					new HttpPostTask().execute("http://mcqu.heroku.com/pet/"
							+ api_key + "/clean/?email=" + email);
					// updatePetData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		button_stats.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					new HttpUpdatePetData()
							.execute("http://mcqu.heroku.com/pet/" + api_key);
					// updatePetData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public final class HttpUpdatePetData
			extends
			AsyncTask<String/* Param */, Void /* Progress */, String /* Result */> {

		@Override
		protected String doInBackground(String... params) {
			BufferedReader in = null;
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				request.setURI(new URI(params[0].toString()));
				HttpResponse response = client.execute(request);
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();
				String result = sb.toString();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "error";

		}

		@Override
		protected void onPostExecute(String result) {
			// publishProgess(false);
			// Do something with result in your activity
			System.out.println(result);

			JSONObject petObject;
			try {
				petObject = new JSONObject(result);

				int mood = petObject.getInt("mood");
				int cleanliness = petObject.getInt("cleanliness");
				int hunger = petObject.getInt("hunger");
				alive = petObject.getBoolean("alive");

				text_mood.setText("Mood: " + mood);
				text_cleanliness.setText("Cleanliness: " + cleanliness);
				text_hunger.setText("Hunger: " + hunger);
				
				editor.putInt("mood", mood);
				editor.putInt("cleanliness", cleanliness);
				editor.putInt("hunger", hunger);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public final class HttpCreatePet
			extends
			AsyncTask<String/* Param */, Void /* Progress */, String /* Result */> {

		@Override
		protected String doInBackground(String... params) {
			BufferedReader in = null;
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(params[0].toString());
				HttpResponse response = client.execute(request);
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();
				String result = sb.toString();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "error";

		}

		@Override
		protected void onPostExecute(String result) {
			// publishProgess(false);
			// Do something with result in your activity
			System.out.println(result);
			api_key = result;
			editor.putString("key", api_key);
		}
	}

	public final class HttpPostTask
			extends
			AsyncTask<String/* Param */, Void /* Progress */, String /* Result */> {

		@Override
		protected String doInBackground(String... params) {
			BufferedReader in = null;
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(params[0].toString());
				HttpResponse response = client.execute(request);
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();
				String result = sb.toString();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "error";

		}

		@Override
		protected void onPostExecute(String result) {
			// Do something with result in your activity
			System.out.println(result);
		}
	}
}