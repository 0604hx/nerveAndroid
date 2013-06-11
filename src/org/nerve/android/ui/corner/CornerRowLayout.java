package org.nerve.android.ui.corner;

import java.util.List;

import org.nerve.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @项目名称 :missingEn
 * @文件名称 :CornerRowLayout.java
 * @所在包 :org.nerve.ui.corner
 * @功能描述 :
 *	类似iphone设置界面中的圆角列表布局
 *	
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：IBM GDC(http://www.ibm.com/)
 * @创建日期 :2013-5-23
 * @修改记录 :
 */
public class CornerRowLayout extends LinearLayout implements OnClickListener{

	private OnRowClickListener listener;
	
	private LinearLayout contentLy;//装东西的容器
	private TextView headerTX;//表头标题
	private TextView footerTX;//表的落款
	
	private boolean isShowValue;//是否显示右边的值
	
	public CornerRowLayout(Context context) {
		super(context);
	}
	
	
	public CornerRowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.isShowValue = true;
		
		contentLy = new LinearLayout(context, attrs);
		contentLy.setBackgroundResource(R.drawable.nerve_shape_corner_list_background);
		contentLy.setOrientation(LinearLayout.VERTICAL);
		
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		headerTX = new TextView(getContext());
		headerTX.setLayoutParams(lp);
		
		footerTX = new TextView(getContext());
		footerTX.setLayoutParams(lp);
		footerTX.setGravity(Gravity.RIGHT);
		footerTX.setTextSize(13);
		
		//设置为垂直布局
		this.setOrientation(LinearLayout.VERTICAL);
		this.addView(headerTX);
		this.addView(contentLy);
		this.addView(footerTX);
	}
	
	/**
	 * 设置表格头的文字
	 *	@param header
	 */
	public void setHeader(String header){
		headerTX.setText(header);
		headerTX.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 
	 *	@param header	表格头文字
	 *	@param position	文字位置，使用 Gravity.LEFT, Gravity.RIGHT, Gravity.CENTER ，分别代表左，右，中
	 */
	public void setHeader(String header, int position){
		setHeader(header);
		headerTX.setGravity(position);
	}
	
	/**
	 * 隐藏表格头
	 */
	public void hideHeader(){
		headerTX.setVisibility(View.GONE);
	}
	
	/**
	 * 
	 *	@param footer	表格落款文字
	 */
	public void setFooter(String footer){
		footerTX.setText(footer);
		footerTX.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏表格落款
	 */
	public void hideFooter(){
		footerTX.setVisibility(View.GONE);
	}

	/**
	 *	统一设置整个表格行中的右边内容是否显示
	 *	注意，这个必须在setCellList 之前调用，不然不会有效果的
	 *	@param isShowValue
	 */
	public void setShowValue(boolean isShowValue) {
		this.isShowValue = isShowValue;
	}


	/**
	 * 设置这个表格的数据，会直接重新渲染整个表格
	 *	@param cells
	 */
	public void setCellList(List<CornerCell> cells){
		contentLy.removeAllViews();
		
		for(int i=0;i<cells.size();i++){
			CornerCell cell = cells.get(i);
			
			//如果 CornerCell 已经有自定义的视图，就用自定义的视图
			View cellView = cell.getView() == null ?
					View.inflate(getContext(), R.layout.nerve_corner_cell, null)
					:
					cell.getView();
			
			if(cellView == null)
				continue;
			
			System.out.println(cell);
			
			/*
			 * 对头，中，尾进行分组
			 */
			if(i == 0)
				cellView.setBackgroundResource(R.drawable.nerve_corner_list_top);
			else{
				//设置顶部的margin为1，就会出现一条细线
				LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 1, 0, 0);
				cellView.setLayoutParams(lp);
				
				if(i == cells.size() - 1)
					cellView.setBackgroundResource(R.drawable.nerve_corner_list_bottom);
				else
					cellView.setBackgroundResource(R.drawable.nerve_corner_list_middle);
			}
				
			
			//设置可以点击，不然按住时不会有效果
			//cellView.setClickable(true);
			//cellView.setPadding(5, 8, 5, 8);
			
			((TextView)cellView.findViewById(R.id.cell_title)).setText(cell.getTitle());
			if(isShowValue)
				((TextView)cellView.findViewById(R.id.cell_value)).setText(cell.getValue());
			
			cellView.findViewById(R.id.cell_arrow)
				.setVisibility(cell.isArrow() ? View.VISIBLE : View.GONE);
			
			cellView.setOnClickListener(this);
			cellView.setTag(i);
			//将这个view添加到本地容器
			contentLy.addView(cellView);
		}
		
		resetAll();
	}
	
	
	/**
	 * 更新指定行的数据值。就是右边的值
	 *	@param index
	 *	@param value
	 */
	public void updateCellValue(int index, String value){
		TextView view = (TextView)contentLy.getChildAt(index).findViewById(R.id.cell_value);
		view.setText(value);
	}
	
	/**
	 * 更新指定行的数据标题，就是左边的值
	 *	@param index
	 *	@param title
	 */
	public void updateCellTitle(int index, String title){
		TextView view = (TextView)contentLy.getChildAt(index).findViewById(R.id.cell_title);
		view.setText(title);
	}
	
	/**
	 * 更新指定行的箭头是否显示
	 *	@param index
	 *	@param isShow
	 */
	public void updateCellArrow(int index, boolean isShow){
		contentLy.getChildAt(index).findViewById(R.id.cell_arrow)
			.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}
	
	/**
	 * 当表格数据填充完后（即在setCellList（）方法后）
	 * 这个方法会被调用，用于显示或者隐藏表格头尾
	 * 如果头，尾的文字为空，会被强制隐藏
	 */
	public void resetAll(){
		if(headerTX.getText() == null 
				|| headerTX.getText().toString().trim().length() == 0)
			hideHeader();
		if(footerTX.getText() == null 
				|| footerTX.getText().toString().trim().length() == 0)
			hideFooter();
	}
	
	/**
	 * 绑定一个选择事件
	 * 这个绑定的是默认的表格行，如果是自定义的视图，请自己编写点击事件
	 * 如 CornerCell 中设置了View
	 *	@param listener
	 */
	public void setOnRowClickListener(OnRowClickListener listener){
		this.listener = listener;
	}


	@Override
	public void onClick(View v) {
		if(listener != null)
			listener.onRowClick(v, Integer.valueOf(v.getTag().toString()));
	}
}