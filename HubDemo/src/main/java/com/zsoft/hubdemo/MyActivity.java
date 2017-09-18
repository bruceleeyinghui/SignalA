package com.zsoft.hubdemo;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.hubs.IHubProxy;

public class MyActivity extends AppCompatActivity implements OnDisconnectionRequestedListener{

    private ConnectionFragment.OnConnectionRequestedListener mListener;
    private OnDisconnectionRequestedListener mDisconnectListener = null;
    private EditText mAddressTextBox = null;

    protected HubConnection con = null;
    protected IHubProxy hub = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mAddressTextBox = (EditText) findViewById(R.id.address);
        mAddressTextBox.setText("http://118.190.46.159:803");
        //mAddressTextBox.setText("http://192.168.1.70:8080");
        Button button = (Button) findViewById(R.id.btnConnect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestConnection();
            }
        });
        Button disconnectButton = (Button) findViewById(R.id.btnDisconnect);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisconnectListener.DisconnectionRequested();
            }
        });

    }
    protected void requestConnection() {
        Uri address = Uri.parse(mAddressTextBox.getText().toString());
        mListener.ConnectionRequested(address);
    }


    public interface OnConnectionRequestedListener
    {
        public void ConnectionRequested(Uri address);
    }
    @Override
    public void DisconnectionRequested() {
        if(con!=null)
        {
            con.Stop();
        }

    }
}
