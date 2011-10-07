package ar.com.qiwi.Quiosco;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Map extends QiwiActivity {

	WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadMap(getIntent().getExtras().getInt("map"));
	}

	private void loadMap(int id) {
		setContentView(R.layout.main);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new AppWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		switch (id) {
		case R.id.button0:
			mWebView.loadUrl((String) getResources().getText(R.string.buenosaires));
			break;
		case R.id.button1:
			mWebView.loadUrl((String) getResources().getText(R.string.laplata));
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private class AppWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
