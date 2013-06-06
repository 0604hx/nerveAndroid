package org.nerve.ui.dialog;

import org.nerve.R;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

/**
 * @项目名称 :CellNote
 * @文件名称 :SingleInputDialog.java
 * @所在包 :org.nerve.cellnote.view.dialog
 * @功能描述 :
 *	只有一个输入的对话框，在这里，message 被设置为了：请输入
 * @创建者 :集成显卡	1053214511@qq.com
 * @创建日期 :2013-1-28
 * @修改记录 :
 */
public class SingleInputDialog extends ConfirmDialog{

	protected EditText singleInput;
	
	protected String defaultText;
	
	public SingleInputDialog(Context context) {
		super(context);
	}
	
	@Override
	public View getLiveView() {
		View lv = View.inflate(context, R.layout.nerve_dialog_single_input, null);
		this.singleInput = (EditText)lv.findViewById(R.id.dialog_single_input);
		this.singleInput.setText(defaultText);
		return lv;
	}
	
	@Override
	public void afterClickOK(int type) {
		if(listener != null)
			listener.onConfirmClick(type, singleInput.getText().toString());
	}
	
	public void setDefaultText(String t){
		this.defaultText = t;
	}
}