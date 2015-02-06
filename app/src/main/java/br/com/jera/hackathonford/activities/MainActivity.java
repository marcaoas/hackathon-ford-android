package br.com.jera.hackathonford.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.jera.hackathonford.HackathonApplication;
import br.com.jera.hackathonford.R;
import br.com.jera.hackathonford.applink.AppLinkActivity;
import br.com.jera.hackathonford.model.User;
import br.com.jera.hackathonford.utils.Constants;
import br.com.jera.hackathonford.utils.Logger;
import butterknife.ButterKnife;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends AppLinkActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener {

    MapFragment mMapFragment;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private String[] mPlanetTitles;
    private ListView mDrawerList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
//        // Set the list's click listener
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        HackathonApplication app = HackathonApplication.getInstance();
        if (app != null) {
            app.startSyncProxyService();
        }
        HackathonApplication.setCurrentActivity(this);
        User test = User.getRandom();
        Toast.makeText(this, test.userName, Toast.LENGTH_SHORT).show();

        buildGoogleApiClient();
        createLocationRequest();
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            if(i!=null){
                String lat = i.getStringExtra(Constants.Extras.LAT);
                String lng = i.getStringExtra(Constants.Extras.LNG);
                String location = String.valueOf(lat) + "  --- " + String.valueOf(lng);
                Toast.makeText(MainActivity.this, "Location = " + location, Toast.LENGTH_LONG).show();
                Logger.d(location);
            }
        }
    };
    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(
                Constants.Receivers.COORDINATES_RECEIVED);
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("TESTE"));
        Log.d("HACKATON", "FOI");
    }


    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            Log.d("AAAA", "FOI");
        }
        Log.d("AAAA", "NEM FOI");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @OnClick(R.id.upgrade)
    public void upgradeApp(){

        Intent intent = new Intent(this, PurchaseActivity.class);
        startActivity(intent);
    }
}
