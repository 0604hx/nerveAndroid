package org.nerve.example.net;

import org.nerve.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GoodsRow extends LinearLayout{

	private CheckBox checkBox; 	//是否选中
	private TextView codeTV;	//编号
	private TextView nameTV;	//名称
	
	private static Drawable rightArrow;
	
	public GoodsRow(Context context,AttributeSet attributeSet){
		super(context,attributeSet);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		this.checkBox = (CheckBox)findViewById(R.id.checkBox);
		this.codeTV = (TextView)findViewById(R.id.goodsCode);
		this.nameTV = (TextView)findViewById(R.id.goodsName);
	}
	
	/**
	 * 
	 *	@param g	需要显示的商品
	 *	@param canSelect	是否显示选中框
	 */
	public void setGoods(Goods g, boolean canSelect){
		if(g != null){
			codeTV.setText(g.UserCode);
			nameTV.setText(g.FullName);
			
			if(g.sonnum > 0){
				if(rightArrow == null){
					rightArrow = getResources().getDrawable(R.drawable.arrow);
					rightArrow.setBounds(0, 0, rightArrow.getMinimumWidth()/2 + 6, rightArrow.getMinimumHeight()/2 + 6);
				}
				
				nameTV.setCompoundDrawables(null, null, rightArrow, null);
			}else{
				nameTV.setCompoundDrawables(null, null, null, null);
			}
				
		}
		checkBox.setChecked(g.select);
		if(canSelect)
			checkBox.setVisibility(View.VISIBLE);
		else
			checkBox.setVisibility(View.GONE);
	}
	
	public boolean isCheck(){
		return checkBox.isChecked();
	}
	
	public void setChecked(boolean check){
		checkBox.setChecked(check);
	}
}
