package sarah.android;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreenActivity extends ActionBarActivity
{
	private boolean backButtonPressed;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Cacher la ActionBar et faire la reference a l'actionbar correcte
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// set content view AFTER ABOVE sequence (to avoid crash)
		this.setContentView(R.layout.activity_splash_screen);

		Handler handlerNewPage = new Handler();
		handlerNewPage.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				if (!backButtonPressed)
				{
					Intent intent = new Intent(SplashScreenActivity.this, StartDiscoveryActivity.class);
					SplashScreenActivity.this.startActivity(intent);
				}
				finish();
			}

		}, 1000);// The waiting time in miliseconds
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		backButtonPressed = true;
		super.onBackPressed();
	}
}
