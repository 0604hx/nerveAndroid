package org.nerve.example.corner;

import java.util.ArrayList;
import java.util.List;

import org.nerve.R;
import org.nerve.android.ui.corner.CornerCell;
import org.nerve.android.ui.corner.CornerRowLayout;
import org.nerve.android.ui.corner.OnRowClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ConrnerActivity extends Activity implements OnRowClickListener{

	private static final int PROVINCE = 0;
	
	private CornerRowLayout cornerL;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.corner_demo);
		initUI();
	}
	
	protected void initUI() {
		
		cornerL = (CornerRowLayout)findViewById(R.id.myCornerLayout);
		
		List<CornerCell> cells = new ArrayList<CornerCell>();
		cells.add(new CornerCell("姓名", "集成显卡", true));
		cells.add(new CornerCell("年龄", "18岁", true));
		cells.add(new CornerCell("地区", "广西壮族自治区", true));
		
		cornerL.setCellList(cells);
		cornerL.setOnRowClickListener(this);
		
		cornerL.setHeader("以下信息我们会绝对保密");
		cornerL.setFooter("2013-5-24");
	}

	@Override
	public void onRowClick(View v, int index) {
		if(index == 2){
			Intent intent = new Intent(ConrnerActivity.this, SelectProvinceActitivy.class);
			startActivityForResult(intent, PROVINCE);
		}else if(index == 1){
			Intent intent = new Intent(ConrnerActivity.this, SelectTimeActitivy.class);
			startActivityForResult(intent, PROVINCE);
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case PROVINCE:
			if(data != null){
				String name = data.getExtras().getString("PROVINCE");
				String value = data.getExtras().getString("VALUE");
				System.out.println("get " + name + ", " + value);
				
				cornerL.updateCellValue(2, name);
			}
			break;
		}
	}
	
}
