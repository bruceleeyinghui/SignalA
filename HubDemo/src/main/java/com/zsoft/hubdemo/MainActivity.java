package com.zsoft.hubdemo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zsoft.hubdemo.utils.MyDesUtils;
import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnDisconnectionRequestedListener,
	ConnectionFragment.OnConnectionRequestedListener,
	CalculatorFragment.ShowAllListener,
	CalculatorFragment.OnCalculationRequestedListener
{
	protected static final String TAG_CONNECTION_FRAGMENT = "connection";	
	protected static final String TAG_CALCULATION_FRAGMENT = "calculation";
	
	protected HubConnection con = null;
	protected IHubProxy hub = null;
	protected TextView tvStatus = null;
	private Boolean mShowAll = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvStatus = (TextView) findViewById(R.id.connection_status);
		
		ChangeFragment(new ConnectionFragment(), false, TAG_CONNECTION_FRAGMENT);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void ConnectionRequested(Uri address) {
		
		con = new HubConnection(address.toString(), this, new LongPollingTransport())
		{
			@Override
			public void OnStateChanged(StateBase oldState, StateBase newState) {
				tvStatus.setText(oldState.getState() + " -> " + newState.getState());
				
				switch(newState.getState())
				{
					case Connected:
						CalculatorFragment fragment = new CalculatorFragment();
						ChangeFragment(fragment, true, TAG_CALCULATION_FRAGMENT);
						break;
					case Disconnected:
						Fragment f = getSupportFragmentManager().findFragmentByTag(TAG_CALCULATION_FRAGMENT);
						if (f!=null && f.isVisible()) {
							getSupportFragmentManager().popBackStackImmediate();
						}
						break;
					default:
						break;
				}
			}
				
			@Override
			public void OnError(Exception exception) {
	            Toast.makeText(MainActivity.this, "On error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
			}

		};
		
		try {
			//代理 名称需要跟服务器一致
			hub = con.CreateHubProxy("MyHub");
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//第一个参数需要跟服务器一致
		hub.On("addMessage", new HubOnDataCallback()
		{
			@Override
			public void OnReceived(JSONArray args) {
				if(mShowAll)
				{
					for(int i=0; i<args.length(); i++)
					{
						String s = args.opt(i).toString();
						Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
		
		con.Start();
	}

	@Override
	public void calculate(int value1, int value2, String operator) {
//		int answer = operator.equalsIgnoreCase("Plus") ? value1+value2 : value1-value2;
//			
//		StringBuilder sb = new StringBuilder();
//		sb.append(value1);
//		sb.append(operator=="plus" ? "+":"-");
//		sb.append(value2);
//		sb.append(" = ");
//		sb.append(answer);

		/*con.Send("{ \"Cmd\": 90021010101010, \"SeqId\": 1,  \"Data\":\"\" }", new SendCallback() {
			public void OnError(Exception ex)
			{
				Toast.makeText(MainActivity.this, "Error when sending: " + ex.getMessage(), Toast.LENGTH_LONG).show();
			}
			public void OnSent(CharSequence message)
			{
				Toast.makeText(MainActivity.this, "Sent: " + message, Toast.LENGTH_SHORT).show();
			}

		});*/

		Intent intent = new Intent(this,WebViewActivity.class);
        startActivity(intent);


        try {
			String utf = java.net.URLDecoder.decode("SuS0sJtW69diVmaWBrcl9X6XtbSZ2ZQYOhHcPqWRWHD1ypjloYCEMRXBGFXVIoeivpC62PjNbxcsvImUkP8eQA%3d%3d",   "utf-8");
            String s = MyDesUtils.desEncrypt(utf);
            Log.e("jiemi",s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HubInvokeCallback callback = new HubInvokeCallback() {
			@Override
			public void OnResult(boolean succeeded, String response) {
				Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void OnError(Exception ex) {
				Toast.makeText(MainActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
			}
		};
		
		List<JSONObject> args = new ArrayList<JSONObject>();
		Bean bean = new Bean(9002,1,"");
		Gson gson2=new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
		String sobj2=gson2.toJson(bean);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(sobj2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		args.add(jsonObject);
//		args.add(value2);
		hub.Invoke( "send", args, callback);
		
	}

	protected void ChangeFragment(Fragment fragment, Boolean addToBackstack, String tag)
	{
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.replace(R.id.fragment_container, fragment, tag);
		if(addToBackstack)
			trans.addToBackStack(null);
		trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		trans.commit();
		
	}

	@Override
	public void setShowAll(Boolean value) {
		mShowAll = value;
	}

	@Override
	public Boolean getShowAll() {
		return mShowAll;
	}

	@Override
	public void DisconnectionRequested() {
		if(con!=null)
		{
			con.Stop();
		}
		
	}

}



