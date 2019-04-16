package car_app.garage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.database.Cursor;

public class garageFinder2 extends Activity{
    /** Called when the activity is first created. */

	LocationManager locationManager;
	TextView t1;
	Button b1;
	TextView desc_tv, caption_tv;
	
	Intent regIntent;
	Intent updateIntent;
	Intent listIntent;
	Intent thisIntent;
	Intent searchInt;
	
	MenuItem menuItem1;
	MenuItem menuItem2;
	MenuItem menuItem3;
	MenuItem menuItem4;
	
	Location location;
	double lat = 0, lng = 0, lat1=0, lng1=0;
	
	ListView lv;
	DBAdapter db = new DBAdapter(this);
	private ArrayList<String> list_cap = new ArrayList<String>();
	private ArrayList<String> list_desc = new ArrayList<String>();
	private ArrayList<String> list_id = new ArrayList<String>();
	
	static final private int SEARCH = Menu.FIRST;
	static final private int REGISTER = Menu.FIRST +1;
	static final private int UPDATE = Menu.FIRST +2;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        t1=(TextView)findViewById(R.id.TextView01);		
        String location_context= Context.LOCATION_SERVICE;
    	locationManager = (LocationManager)getSystemService(location_context);
    	GPSLocation();
    	Geocoder gc=new Geocoder(this, Locale.getDefault());
    	Bundle bndl=getIntent().getExtras();
        String pos1 = bndl.getString("str");
        List<Address> loc = null;			
		try {
				loc = gc.getFromLocationName(pos1, 1);
				lat1= loc.get(0).getLatitude();
				lng1= loc.get(0).getLongitude();
			}
		catch (IOException e) {
			lat1=-1; lng1=-1;
		}
    	lv = (ListView)findViewById(R.id.myList);   
    	list_cap = new ArrayList<String>();
    	list_desc = new ArrayList<String>();
    	list_id= new ArrayList<String>();
    	getList();
    	String[] captionArray = (String[]) list_cap.toArray(
				new String[list_cap.size()]);
    	final ItemsAdapter itemsAdapter = new ItemsAdapter(
    	    	   garageFinder2.this, R.layout.list_item,
    	    	    captionArray);
    	lv.setAdapter(itemsAdapter);
    	lv.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position,
    		long id) {
    			listIntent = new Intent(garageFinder2.this,list_detail.class);
    			long i=Long.valueOf(list_id.get(position));
    			listIntent.putExtra("g",i);
    			startActivity(listIntent);
    		}
    		});
    	
    }
    	
	//Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	int groupId = 0;
	menuItem1 = menu.add(groupId, SEARCH, Menu.NONE,"Search");
	menuItem1.setIcon(R.drawable.search1);
	menuItem3 = menu.add(groupId, REGISTER, Menu.NONE,"Register Claim");
	menuItem3.setIcon(R.drawable.register);
	menuItem4 = menu.add(groupId, UPDATE, Menu.NONE, "Update");
	menuItem4.setIcon(R.drawable.update);
	return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
	    case SEARCH:
	    	searchInt = new Intent(garageFinder2.this,search_list.class);
	        startActivity(searchInt);
	        break;
	    case REGISTER:
	        regIntent = new Intent(Intent.ACTION_VIEW,
	            	Uri.parse("http://www.internfair.internshala.com/internFiles/AppDesign/RegisterClaim.html"));
	            	startActivity(regIntent);
	        break;
	    case UPDATE:
	    	updateIntent = new Intent(garageFinder2.this,update_list.class);
	        startActivity(updateIntent);
	        break;
	    default:
	        return super.onOptionsItemSelected(item);
	    }

	    return true;
	}
	
	//GPS
	public void GPSLocation(){
		TextView tv = (TextView)findViewById(R.id.myTextView);
		Geocoder gc;
		StringBuilder sb = new StringBuilder();
		locationManager.requestLocationUpdates("network", 1000, 0,
		new LocationListener() {
		public void onLocationChanged(Location location) {}
		public void onProviderDisabled(String provider){}
		public void onProviderEnabled(String provider){}
		public void onStatusChanged(String provider, int status,
		Bundle extras){}
		});
		location = locationManager.getLastKnownLocation("network");
		if (location != null) {
		lat = location.getLatitude();
		lng = location.getLongitude();
		gc = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> myList =gc.getFromLocation(lat, lng, 1);
			if(myList.size()>0){
			Address address = myList.get(0);
			for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
				sb.append(address.getAddressLine(i)).append("\n");
			}
			else{
			sb.append("Location details not found");
			}
		}catch (IOException e) {
			sb.append("Unable to display Location Details\nNetwork connection required!\n");
		}
		} 
		else
		sb.append("No Location");
		tv.setText(sb);
		}
	
	//sort by proximity
	public float sort(Cursor c)
	{
    	Geocoder fwd_gc = new Geocoder(this, Locale.getDefault());
    	String pos=c.getString(5);
        List<Address> locations = null;			
        		try {
        				locations = fwd_gc.getFromLocationName(pos, 1);
        				double lat2= locations.get(0).getLatitude();
        				double lng2= locations.get(0).getLongitude();
        				float results[] = new float[3];
        				if(lat1!=-1 &&lng1!=-1){
        				Location.distanceBetween(lat1, lng1, lat2, lng2, results);
        				return results[0];
        				}
        				else return -1;
        			} catch (IOException e) {
        				return -1;
        			}
	}
	
	 public void display(Cursor c)
	    { 
		 String s;
		if(c.getString(2).equalsIgnoreCase("Yes"))
			s="\n"+"Cashless";
		else s="";
		String temp_id = c.getString(0);
		String temp_caption = c.getString(1);
		String temp_description = c.getString(4)+ ", " +c.getString(5)+s;
		float d=sort(c);
		if(d!=-1 && d<=300000)
		{
		list_cap.add(temp_caption);
		list_desc.add(temp_description);
		list_id.add(temp_id);
	    }
	  }

	 public void getList(){
		    db.open();
	        Cursor c = db.getAllGarages();
	        if (c.moveToFirst())
	        {
	        do {
	        display(c);
	        } while (c.moveToNext());
	        }
	        db.close();
	 }

	 //custom adapter for list view
	 private class ItemsAdapter extends BaseAdapter {
	  String[] items;

	  public ItemsAdapter(Context context, int textViewResourceId,
	    String[] items) {
	   this.items = items;
	  }

	  @Override
	  public View getView(final int position, View convertView,
	    ViewGroup parent) {
	   View view = convertView;
	   if (view == null) {
	    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    view = vi.inflate(R.layout.list_item, null);
	   }
	   caption_tv = (TextView) view.findViewById(R.id.caption);
	   desc_tv= (TextView)view.findViewById(R.id.description);
	   caption_tv.setText(list_cap.get(position));
	   desc_tv.setText(list_desc.get(position));
	   return view;
	  }

	  public int getCount() {
	   return items.length;
	  }

	  public Object getItem(int position) {
	   return position;
	  }

	  public long getItemId(int position) {
	   return position;
	  }
	 }
	}

