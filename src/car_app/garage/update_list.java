package car_app.garage;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class update_list extends Activity{
	EditText e1, e3, e4, e5, e6, e7, e8, e9, e10, e11;
	RadioButton rb1,rb2;
	DBAdapter db = new DBAdapter(this);
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.updat);
        e1=(EditText)findViewById(R.id.name);
        e3=(EditText)findViewById(R.id.manu);
        e4=(EditText)findViewById(R.id.street);
        e5=(EditText)findViewById(R.id.city);
        e6=(EditText)findViewById(R.id.pincode);
        e7=(EditText)findViewById(R.id.state);
        e8=(EditText)findViewById(R.id.contact);
        e9=(EditText)findViewById(R.id.landline);
        e10=(EditText)findViewById(R.id.mobile);
        e11=(EditText)findViewById(R.id.email);
        
        e1.setText(null);e3.setText(null);e4.setText(null);e5.setText(null);
        e6.setText(null);e7.setText(null);e8.setText(null);e9.setText(null);
        e10.setText(null);e11.setText(null);
       }
    public void onSubmit(View v) {
    	Geocoder fwd_gc = new Geocoder(this, Locale.getDefault());
    	List<Address> locations = null;
    	if(e1.getText().toString()==null || e3.getText().toString()==null|| 
        		e4.getText()==null|| e5.getText().toString()==null|| e6.getText().toString()==null||
        		e7.getText().toString()==null|| 
        		e8.getText().toString()==null)
    		Toast.makeText(this,
        		    "Please fill in all required fields!",Toast.LENGTH_LONG).show();
		else
			{
			try {
				locations=fwd_gc.getFromLocationName(e5.getText().toString(), 1);
				rb1=(RadioButton)findViewById(R.id.rbtn1);
		        rb2=(RadioButton)findViewById(R.id.rbtn2);
		        String s = "Yes";
		        if(rb1.isChecked())
		        	s="Yes";
		        else if(rb2.isChecked())
		        	s="No";
		    	db.open();
		        db.insertGarage(e1.getText().toString(), s, e3.getText().toString(), 
		        		e4.getText().toString(), e5.getText().toString(), e6.getText().toString(), e7.getText().toString(), 
		        		e8.getText().toString(), e9.getText().toString(), e10.getText().toString(), e11.getText().toString());
		        	Toast.makeText(this,
		        		    "Database Updated!",Toast.LENGTH_LONG).show();
		        db.close();		
				}	
			catch (IOException e) {
				Toast.makeText(this,
	        		    "Valid address required!",Toast.LENGTH_LONG).show();
			}
    }
    }
    
    public void onCancel(View v) {
    	finish();
    }
}










