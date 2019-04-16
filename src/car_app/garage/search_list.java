package car_app.garage;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class search_list extends Activity{
	TextView desc_tv, caption_tv;
	Intent home;
	DBAdapter db;
	EditText et;
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.search_list);
        et= (EditText)findViewById(R.id.et_search);
	}
	
	public void onSearch(View v){
		home=new Intent(search_list.this,garageFinder.class);
		home.putExtra("str",et.getText().toString());
		startActivity(home);
	}
}