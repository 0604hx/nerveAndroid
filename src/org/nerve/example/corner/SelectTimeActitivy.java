package org.nerve.example.corner;

import java.util.ArrayList;
import java.util.List;

import org.nerve.R;
import org.nerve.ui.corner.CornerCell;
import org.nerve.ui.corner.CornerRowLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;

public class SelectTimeActitivy extends Activity {
	
	private CornerRowLayout cornerL;
	private List<CornerCell> timeList;
	
	/**
	 * 相对固定的省份列�?
	 */
	private static final String TITLE[] = {"全天","早上","下午","晚上"};
	private static final String TIME[] = {"06:00 - 23:59","06:00 - 12:00","12:00 - 18:00","18:00 - 23:59"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.corner_demo_province);
		initUI();
	}
	
	protected void initUI() {
		
		timeList = buildList();
		
		cornerL = (CornerRowLayout)findViewById(R.id.myCornerLayout);
		//cornerL.setOnRowClickListener(this);
		//cornerL.setShowValue(false);//不显示右边的内容
		cornerL.setCellList(timeList);
		
		//设置表头文字
		cornerL.setHeader("选择发车时间", Gravity.CENTER);
	}

	private List<CornerCell> buildList(){
		List<CornerCell> temp = new ArrayList<CornerCell>();
		
		for(int i=0;i<TITLE.length;i++){
			CornerCell cell = new CornerCell(TITLE[i], TIME[i], false);
			temp.add(cell);
		}
		
		return temp;
	}

}
