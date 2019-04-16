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

public class garageFinder extends Activity{
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
	double lat = 0, lng = 0;
	
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
        b1 = (Button) findViewById(R.id.Button01);
        db.open();
        Cursor cursors = db.getAllGarages();
		if (cursors.moveToNext()) {
			b1.setVisibility(View.GONE);
		} else {
			b1.setVisibility(View.VISIBLE);
		}
		db.close();
		
        String location_context= Context.LOCATION_SERVICE;
    	locationManager = (LocationManager)getSystemService(location_context);
    	GPSLocation();
    	lv = (ListView)findViewById(R.id.myList);   
    	list_cap = new ArrayList<String>();
    	list_desc = new ArrayList<String>();
    	list_id= new ArrayList<String>();
    	getList();
    	String[] captionArray = (String[]) list_cap.toArray(
				new String[list_cap.size()]);
    	final ItemsAdapter itemsAdapter = new ItemsAdapter(
    	    	   garageFinder.this, R.layout.list_item,
    	    	    captionArray);
    	lv.setAdapter(itemsAdapter);
    	lv.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position,
    		long id) {
    			listIntent = new Intent(garageFinder.this,list_detail.class);
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
	    	searchInt = new Intent(garageFinder.this,search_list.class);
	        startActivity(searchInt);
	        break;
	    case REGISTER:
	    	regIntent= new Intent(garageFinder.this,register_claim.class);
	        //regIntent = new Intent(Intent.ACTION_VIEW,
	            	//Uri.parse("http://www.internfair.internshala.com/internFiles/AppDesign/RegisterClaim.html"));
	            	startActivity(regIntent);
	        break;
	    case UPDATE:
	    	updateIntent = new Intent(garageFinder.this,update_list.class);
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
        				Location.distanceBetween(lat, lng, lat2, lng2, results);
        				return results[0];
        			} catch (IOException e) {
        				return -1;
        			}
	}
	
	//button to populate
	public void handleClick(View v) {
		insert();
		thisIntent=new Intent(garageFinder.this,garageFinder.class);
        startActivity(thisIntent);
	}
	
	//populates the db
	private void insert(){
		db.open();
	    //long id;
	    db.insertGarage(
	    "Advaith Motors", "Yes","Hyundai",
	    "No 1to5, bannerghatta road","Bangalore","560029","Karnataka",
	    "Prakash","080-22966204","9663374813","viresh.aradhya@relianceada.com");
	    
	    db.insertGarage(
	    "Jubilant enpro pvt.ltd.","Yes","Audi",
	    "No.65-6/1,beratena agrahara grama,15th km main, hosur road","Bangalore","560068","Karnataka",
	    "Anup N Ramesh ","80-28521551 ","9740031202","");

	    db.insertGarage(
	    "Harpreet ford- moti nagar","No","Ford",
	    "69/1a moti nagar crossing najafgraph road","New Delhi","110015 ","Delhi",
	    "Rajesh saraswat","11-25153955","9871198519","ravis@thesachdevgroup.com");

	    db.insertGarage(
	    "Ambition auto pvt ltd","Yes","Mahindra and Mahindra",
	    "3/2 woodllen textiles industries, 2 lake road, bhandup (west)","Mumbai","400078","Maharashtra",
	    "Jude","022-25943334","9663374513","am@vsnl.net");

	    db.insertGarage(
	    "Automax Honda","No","Maruti",
	    "A-17, Mayapuri - 1","New Delhi","110064","Delhi",
	    "Ramesh ","011-45690000 ","9740021302","");

	    db.insertGarage(
	    "Car Nation Auto Pvt. Ltd","Yes","All Models",
	    "19/2 Narayan Swamy Bldg., Kadbisanahalli, Outer Ring Road,Marathalli","Bangalore","560087 ","Karnataka",
	    "Vicky Singh","080-25639973","9891198519","vicky@rediffmail.com");

	    db.insertGarage(
	    "CarNation Auto Services","Yes","Tata",
	    "B-20, I Cross, Main Road, Ambattur Indl. Estate, Ambattur","Chennai","600058","Tamilnadu",
	    "Sethuraman","044-22576204","9403374813","sethubuddy@yahoo.co.in");

	    db.insertGarage(
	    "Maa Ambe Motors pvt.ltd.","No","Ford",
	    "10/57, Kirti Nagar Indl. Area, opp. SD Public School","New Delhi","110015","Delhi",
	    "Jagdeep Bakshi ","011-45074501","9450031002","");

	    db.insertGarage(
	    "ABC Hyundai","Yes","All Models",
	    "7, Jamshedji Tata road, Churchgate","Mumbai","400005 ","Maharashtra",
	    "Tejas Mange","022-22047171 ","","");

	    db.insertGarage(
	    "Auto Carriage Pvt Limited","Yes","Hyundai",
	    "Jagatipota, Kalikapur,E.M. by Pass, Garia","Kolkata","700099","West Bengal",
	    "Partha","033-32556826","9233374813","nilikh@gmail.com");

	    db.insertGarage(
	    "Khivraj Motors Pvt Ltd","No","All Models",
	    "623, MOUNT ROAD","Chennai","600006","Tamilnadu",
	    "Karthik ","044-28571551 ","9040031202","");

	    db.insertGarage(
	    "Osmania Technology Works","Yes","Ford",
	    "H.NO.12-1-498, FATHULLA GUDA VILLAGE, NAGOLE, R.R.DIST.","Hyderabad","500068 ","Andhra Pradesh",
	    "Ankur Jain","040-24223333","9871198819","ankur@osmaniatech.com");

	    db.close();
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

