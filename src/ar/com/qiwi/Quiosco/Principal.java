package ar.com.qiwi.Quiosco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Principal extends QiwiActivity {

	private HashMap<String, Integer> cities = new HashMap<String, Integer>();
	private Integer selectedCity = -1;
	ProgressDialog progress;
	AlertDialog alert;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Usted debe estar conectado").setCancelable(false).setPositiveButton("salir", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Principal.this.finish();
			}
		});
		AlertDialog alert = builder.create();

		cities.put("Buenos Aires", R.id.button0);
		cities.put("La Plata", R.id.button1);
		progress = ProgressDialog.show(this, "", "Cargando su ubicación...", true);
		if (!isOnline(this)) {
			alert.show();
		} else {
			runOnUiThread(new Runnable() {
				public void run() {
					getLocation();
				}
			});
		}
	}

	private void buildButtons() {
		setContentView(R.layout.cities);
		Object[] citiesList = cities.keySet().toArray();
		for (int t = 0; t < citiesList.length; t++) {
			final Button button = (Button) findViewById(cities.get(citiesList[t]));
			if (cities.get(citiesList[t]).equals(selectedCity)) {
				highlight(button);
			}
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), Map.class);

					Bundle bundle = new Bundle();
					bundle.putInt("map", button.getId());

					intent.putExtras(bundle);
					startActivityForResult(intent, 0);
				}
			});
		}
	}

	private void highlight(Button button) {
		button.setTextScaleX(2);
		button.setSelected(true);
		button.setHighlightColor(Color.BLUE);
	}

	private void getLocation() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				try {
					String city = getCity(location);
					progress.dismiss();
					selectedCity = cities.get(city);
					buildButtons();
				} catch (Exception e) {
					alert.show();
					e.printStackTrace();
				}
			}

			@Override
			public void onProviderDisabled(String arg0) {
				progress.dismiss();
				buildButtons();
				Log.e("location", "OFF");
			}

			@Override
			public void onProviderEnabled(String provider) {
				Log.e("location", "ON");
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

		};
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	protected String getCity(Location location) throws URISyntaxException, ClientProtocolException, IOException, JSONException {
		String url = "http://maps.google.com/maps/api/geocode/json?latlng=" + location.getLatitude() + "," + location.getLongitude() + "&sensor=false";
		BufferedReader in = null;

		HttpGet httpGet = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(httpGet);

		in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer sb = new StringBuffer("");
		String line = "";
		String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null) {
			sb.append(line + NL);
		}
		in.close();
		return new JSONObject(sb.toString()).getJSONArray("results").getJSONObject(4).getJSONArray("address_components").getJSONObject(0).get("long_name").toString();
	}
}