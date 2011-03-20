package org.seniorProject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import java.net.URI;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class Pet extends Activity {
	private String api_key;
	private String email ="coutermarsh.mike@gmail.com";
	public static final String PREFS_NAME = "LocalPetData";
	private TextView text_age,text_hunger,text_mood,text_cleanliness;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Restore pet data
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		api_key = settings.getString("key", null);
		//email = settings.getString("email", null);

		// Setup Textviews
		text_age = new TextView(this);
		text_hunger = new TextView(this);
		text_mood = new TextView(this);
		text_cleanliness = new TextView(this);

		text_age = (TextView) findViewById(R.id.age);
		text_hunger = (TextView) findViewById(R.id.hunger);
		text_cleanliness = (TextView) findViewById(R.id.cleanliness);
		text_mood = (TextView) findViewById(R.id.mood);
		
		// Setup buttons
		final Button button_feed = (Button) findViewById(R.id.feed);
		final Button button_play = (Button) findViewById(R.id.play);
		final Button button_clean = (Button) findViewById(R.id.clean);
		
		 button_feed.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	try {
						executeHttpPost(api_key, "feed", email, null);
						updatePetData();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	        });
		 button_play.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	try {
						executeHttpPost(api_key, "play", email, null);
						updatePetData();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	        });
		 button_clean.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	try {
						executeHttpPost(api_key, "clean", email, null);
						updatePetData();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	        });

		// if no key... create new pet
		if (api_key == null) {
			try {
				email = "coutermarsh.mike@gmail.com";
				String name = "Zinkus";
				String color = "Blue";

				api_key = executeHttpPost(null, "",email, "name=" + name + "&color=" + color);
				// Save API Key
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("key", api_key);

				// Commit the edits!
				editor.commit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Update pet data
		updatePetData();
	}
	
	// update text boxes with pet data
	public void updatePetData() {
		try {

			text_age.setText("Age: " + executeHttpGet(api_key, "age"));
			text_hunger.setText("Hunger: " + executeHttpGet(api_key, "hunger"));
			text_cleanliness.setText("Cleanliness: "
					+ executeHttpGet(api_key, "cleanliness"));
			text_mood.setText("Mood: " + executeHttpGet(api_key, "mood"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// get Data for specific pet (key)
	public String executeHttpGet(String Key, String action) throws Exception {
		BufferedReader in = null;
		try {
			String URI = "http://mcqu.heroku.com/pet";
			if (Key != null) {
				URI = URI + "/" + Key;
			}
			if (action != null) {
				URI = URI + "/" + action;
			}

			InputStream content = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(URI);
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			return getResponseBody(response);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// execute post
	public String executeHttpPost(String Key, String action, String email, String params)
			throws Exception {
		BufferedReader in = null;
		try {
			String URI = "http://mcqu.heroku.com/pet";
			if (Key != null) {
				URI = URI + "/" + Key;
			}
			if (action != null){
				URI = URI + "/" + action;
			}
			if (email != null) {
				URI = URI + "?email=" + email;
			}
			if (params != null) {
				URI = URI + "&" + params;
			}

			InputStream content = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URI);
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httpPost);
			return getResponseBody(response);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// helper, convert response to a string
	public static String getResponseBody(HttpResponse response) {

		String response_text = null;

		HttpEntity entity = null;

		try {

			entity = response.getEntity();

			response_text = _getResponseBody(entity);

		} catch (ParseException e) {

			e.printStackTrace();

		} catch (IOException e) {

			if (entity != null) {

				try {

					entity.consumeContent();

				} catch (IOException e1) {

				}

			}

		}

		return response_text;

	}

	public static String _getResponseBody(final HttpEntity entity)
			throws IOException, ParseException {

		if (entity == null) {
			throw new IllegalArgumentException("HTTP entity may not be null");
		}

		InputStream instream = entity.getContent();

		if (instream == null) {
			return "";
		}

		if (entity.getContentLength() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(

			"HTTP entity too large to be buffered in memory");
		}

		String charset = getContentCharSet(entity);

		if (charset == null) {

			charset = HTTP.DEFAULT_CONTENT_CHARSET;

		}

		Reader reader = new InputStreamReader(instream, charset);

		StringBuilder buffer = new StringBuilder();

		try {

			char[] tmp = new char[1024];

			int l;

			while ((l = reader.read(tmp)) != -1) {

				buffer.append(tmp, 0, l);

			}

		} finally {

			reader.close();

		}

		return buffer.toString();

	}

	public static String getContentCharSet(final HttpEntity entity)
			throws ParseException {

		if (entity == null) {
			throw new IllegalArgumentException("HTTP entity may not be null");
		}

		String charset = null;

		if (entity.getContentType() != null) {

			HeaderElement values[] = entity.getContentType().getElements();

			if (values.length > 0) {

				NameValuePair param = values[0].getParameterByName("charset");

				if (param != null) {

					charset = param.getValue();

				}

			}

		}

		return charset;

	}
}
