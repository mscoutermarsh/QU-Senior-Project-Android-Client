package org.seniorProject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

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
	private String email;
	public static final String PREFS_NAME = "LocalPetData";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Restore pet data
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		api_key = settings.getString("key", null);
		email = settings.getString("email", null);

		// if no key... create new pet
		if (api_key == null) {
			try {
				email = "coutermarsh.mike@gmail.com";
				String name = "Zinkus";
				String color = "Blue";

				api_key = executeHttpPost(null, "", "email=" + email + "&name="
						+ name + "&color=" + color);
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
	}

	// get Data for specific pet (key)
	public String executeHttpGet(String key, String action) throws Exception {
		BufferedReader in = null;
		String URI = "mcqu.heroku.com/pet/" + key + "/action";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(URI));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			return sb.toString();
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
	public String executeHttpPost(String Key, String action, String params)
			throws Exception {
		BufferedReader in = null;
		try {
			String URI = "http://mcqu.heroku.com/pet";
			if (Key != null) {
				URI = URI + Key;
			}
			if (params != null) {
				URI = URI + "?" + params;
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
