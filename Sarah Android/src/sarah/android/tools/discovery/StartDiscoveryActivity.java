package sarah.android.tools.discovery;

import sarah.android.R;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartDiscoveryActivity extends ActionBarActivity implements OnClickListener
{
	private Context ctxt;
	public final static String TAG = "MainActivity";
	public static final String PKG = "com.example.discoverytest";
	public static SharedPreferences prefs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inicializarAplicacao();
	}

	private void inicializarAplicacao()
	{
		// Default behaviors
		Button btnConnect = (Button) findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	public void onClick(View v)
	{
		Button clickedButton = (Button) v;

		switch (clickedButton.getId())
		{
		case R.id.btnConnect:
		{
			ctxt = this;
			startActivity(new Intent(ctxt, ActivityDiscovery.class));
			finish();
		}
		}

	}
}