package org.nerve.example.corner;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.nerve.R;
import org.nerve.android.ui.corner.CornerCell;
import org.nerve.android.ui.corner.CornerRowLayout;
import org.nerve.android.ui.corner.OnRowClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

public class SelectProvinceActitivy extends Activity implements OnRowClickListener{
	private static final String TAG = "SelectProvinceActitivy";
	
	private CornerRowLayout cornerL;
	private List<CornerCell> provinceList;
	
	/**
	 * 相对固定的省份列�?
	 */
	private static final String PROVINCE_DATA = 
			"{'Province2':[{'2':'北京'},{'3':'安徽'},{'4':'福建'},{'5':'甘肃'}," +
			"{'6':'广东'},{'7':'广西壮族自治区'},{'8':'贵州'},{'9':'海南'},{'10':'河北'},{'11':'河南'},{'12':'黑龙江'}," +
			"{'13':'湖北'},{'14':'湖南'},{'15':'吉林'},{'16':'江苏'},{'17':'江西'},{'18':'辽宁'},{'19':'内蒙古'}," +
			"{'20':'宁夏'},{'21':'青海'},{'22':'山东'},{'23':'山西'},{'24':'陕西'},{'25':'上海'},{'26':'四川'}," +
			"{'27':'天津'},{'28':'西藏'},{'29':'新疆'},{'30':'云南'},{'31':'浙江'},{'32':'重庆'},{'33':'香港'}," +
			"{'34':'澳门'},{'35':'台湾'},{'3439':'钓鱼岛'}]}";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.corner_demo_province);
		initUI();
	}
	
	protected void initUI() {
		
		provinceList = JSONToList(PROVINCE_DATA, "Province2");
		
		cornerL = (CornerRowLayout)findViewById(R.id.myCornerLayout);
		cornerL.setOnRowClickListener(this);
		cornerL.setShowValue(false);//不显示右边的内容
		cornerL.setCellList(provinceList);
		
		//设置表头文字
		cornerL.setHeader("选择省份", Gravity.CENTER);
	}

	private List<CornerCell> JSONToList(String json, String key){
		List<CornerCell> temp = new ArrayList<CornerCell>();
		try{
			JSONObject obj = new JSONObject(json);
			JSONArray jarr = obj.getJSONArray(key);
			
			CornerCell c = null;
			for(int i=0;i<jarr.length();i++){
				obj = jarr.getJSONObject(i);
				
				String id = (String)obj.keys().next();
				c = new CornerCell(obj.getString(id), id, true);
				temp.add(c);
			}
			
		}catch(Exception e){
			Log.e(TAG, "解析json出错:"+e.getMessage());
		}
		
		return temp;
	}

	@Override
	public void onRowClick(View v, int index) {
		String name = provinceList.get(index).getTitle();
		String value = provinceList.get(index).getValue();
		System.out.println("选择的是：" + name);
		
		Intent intent = new Intent();
		intent.putExtra("PROVINCE", name);
		intent.putExtra("VALUE", value);
		setResult(1, intent);
		
		this.finish();
	}
	
	
}
