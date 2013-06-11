package org.nerve.android.ui.dialog;

import java.util.ArrayList;

import org.nerve.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


/**
 * @项目名称 :CellNote
 * @文件名称 :SelectDialog.java
 * @所在包 :org.nerve.cellnote.view.dialog
 * @功能描述 :
 *	基于RadioButton的选择对话框，提供一系列的选项给用户
 *	默认使用的布局是 R.layout.dialog_select_backup
 *	默认的 radioGroup id 是  R.id.select_radio_group
 *
 * @创建者 :集成显卡	1053214511@qq.com
 * @创建日期 :2013-2-2
 * @修改记录 :
 */
public class RadioSelectDialog extends ConfirmDialog implements OnCheckedChangeListener{

	private int selectLayout = -1;
	private int radioGroup = -1;
	
	/**如果设置了这个值，会动态生成radiobutton列表*/
	private ArrayList<String> buttonList;
	
	/**
	 * @构造方法:
	 * 	默认使用的布局是 R.layout.dialog_select_backup<br />
	 *	默认的 radioGroup id 是  R.id.select_radio_group<br />
	 * @类名:RadioSelectDialog.java
	 * @param context
	 */
	public RadioSelectDialog(Context context) {
		super(context);
		radioGroup = R.id.select_radio_group;
		selectLayout = R.layout.nerve_dialog_select_radio;
	}
	
	/**
	 * @方法名称 :setLayoutId
	 * @功能描述 :设置需要显示的布局
	 * @返回值类型 :void
	 *	@param id
	 */
	public void setLayoutId(int id){
		this.selectLayout = id;
	}
	
	@Override
	public View getLiveView() {
		if(selectLayout > 0){
			View lv = View.inflate(context, selectLayout, null);
			
			RadioGroup rg = (RadioGroup)lv.findViewById(radioGroup);
			if(rg != null){
				rg.setOnCheckedChangeListener(this);
				
				//如果buttonList不为空
				//先清空原有的子view，然后动态添加子RadioButton
				if(buttonList != null){
					rg.removeAllViews();
					
					addRadioButton(rg);
				}
			}
				
				
			
			Log.i("CellNote", "RadioSelectDialog: 返回选项视图 = " + selectLayout);
			return lv;
		}else{
			return null;
		}
		
	}
	
	private void addRadioButton(RadioGroup rg){
		for(int i=0;i<buttonList.size();i++){
			RadioButton rb = (RadioButton)View.inflate(
					context, 
					R.layout.nerve_dialog_select_radiobutton, 
					null);
			rb.setId(i);
			rb.setText(buttonList.get(i));
			
			rg.addView(rb);
		}
		Log.i("CellNote", "RadioSelectDialog:添加radioButton " + buttonList.size() + "个");
	}
	
	@Override
	protected boolean isButtonShow() {
		return false;
	}
	
	/**
	 * @方法名称 :setSelectLayout
	 * @功能描述 :设置选项的布局id
	 * @param layoutId
	 * @return :void
	 */
	public void setSelectLayout(int layoutId){
		this.selectLayout = layoutId;
	}
	
	public void setRadioGroup(int radioGroup){
		this.radioGroup = radioGroup;
	}

	/**
	 * @方法名称 :setButtonList
	 * @功能描述 :自定义选项的列表，如果定义了
	 * 	那么当用户点击其中一个时，调用的 onConfirmClick 方法中
	 * 		参数1：radioButton id
	 * 		参数2：被点击的radioBUtton 的文字
	 * 
	 * @param list
	 * @return :void
	 */
	public void setButtonList(ArrayList<String> list){
		this.buttonList = list;
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.i("CellNote", "RadioSelectDialog: 点击 = " + checkedId);
		if(listener != null){
			if(buttonList != null){
				listener.onConfirmClick(checkedId, buttonList.get(checkedId));
			}else{
				RadioButton rb = (RadioButton)group.findViewById(checkedId);
				System.out.println(rb + " ,text = "+rb.getText() + ",tag=" + rb.getTag());
				listener.onConfirmClick(checkedId, rb.getText());
			}
		}
		dialog.dismiss();
	}
}