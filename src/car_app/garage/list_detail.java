package car_app.garage;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class list_detail extends Activity{
    /** Called when the activity is first created. */  
	String name, manu, addr, contct;
	TextView t1,t2,t3,t4;
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.list_details);
        Bundle bundle= getIntent().getExtras();
        long id = bundle.getLong("g");
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c =db.getGarage(id);
        if (c.moveToFirst()){
        String s;
		if(c.getString(2).equalsIgnoreCase("Yes"))
			s="Cashless";
		else s="Not Cashless";
		name = c.getString(1);
		manu = c.getString(3)+ "\n" +s;
		addr = c.getString(4)+ ", " +c.getString(5) +", "+c.getString(7)+"\n\nPincode: "+c.getString(6);
		contct = c.getString(8)+"\n"+c.getString(9)+"\n"+c.getString(10)+"\n"+c.getString(11);
		t1=(TextView)findViewById(R.id.Tv_Name);
		t1.setText(name);
		t2=(TextView)findViewById(R.id.Tv_Cashless);
		t2.setText(manu);
		t3=(TextView)findViewById(R.id.Tv_Address);
		t3.setText(addr);
		t4=(TextView)findViewById(R.id.Tv_contact);
		t4.setText(contct);
        }
        else
        Toast.makeText(this, "Garage not found!",Toast.LENGTH_SHORT).show(); 
        
	}
}