package org.nerve.ui.dialog;

import org.nerve.R;
import org.nerve.ui.dialog.DialogHelper.ConfirmListener;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * @项目名称 :nerveAndroid
 * @文件名称 :ConfirmDialog.java
 * @所在包 :org.nerve.ui.dialog
 * @功能描述 :
 *	确定对话框，显示标题，内容，还有确定和取消按钮
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：广西可诺科技有限公司(http://www.chonour.com/)
 * @创建日期 :2013-6-6
 * @修改记录 :
 */
public class ConfirmDialog {
	/**默认的对话框视图*/
	public static int DIALOG_UI = R.layout.nerve_dialog_main;
	
	/**默认的对话框占主屏幕多宽度的比例*/
	public static float WIDTH_SCALE = 0.8F;
	
	/**OK按钮被点击*/
	public static final int OK = 0;
	/**取消按钮点击*/
	public static final int CANCEL = 1;
	
	protected Context context;
	protected Dialog dialog;
	protected Button okBtn;
	protected Button cancelBtn;
	
	protected int id;
	protected String title;
	protected String message;
	protected String okBtnText,	//ok按钮的文字
					  cancelBtnText;//取消按钮的文字
	
	protected ConfirmListener listener;
	
	public ConfirmDialog(Context context){
		this.context = context;
		
		this.cancelBtnText = "取消";
		this.okBtnText = "确定";
	}
	public ConfirmDialog(Context context, String t, String m){
		this(context);
		this.title = t;
		this.message = m;
	}
	
	public void setTitle(String t){
		this.title = t;
	}
	public void setMessage(String m){
		this.message = m;
	}
	public void setConfirmListener(ConfirmListener listener){
		this.listener = listener;
	}
	
	protected void createDialog(){
		View view = View.inflate(context, getMainXML(), null);
		
		((TextView)view.findViewById(R.id.dialog_title)).setText(title);
		
		//如果message为null，不显示
		TextView messageTV = (TextView)view.findViewById(R.id.dialog_message);
		if(message == null){
			messageTV.setVisibility(View.GONE);
		}
		else{
			messageTV.setText(message);
		}
			
		
		dialog = new Dialog(context);
		dialog.show();
		Window win = dialog.getWindow();
		//将dialog的背景透明化
		win.setBackgroundDrawable(new ColorDrawable(0));
		win.setGravity(getGravity());
		
		LinearLayout ll = (LinearLayout)view.findViewById(R.id.dialog_live);
		View liveView = getLiveView();
		if(liveView != null){
			ll.addView(liveView);
		}
		
		win.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		win.setContentView(view);
		
		
		System.out.println("height = " + view.getHeight() + ", mheight = " + view.getMeasuredHeight());
		initButton(view);
	}
	
	public void show(){
		if(dialog == null)
			createDialog();
		else
			dialog.show();
	}
	
	/**
	 * @方法名称 :initButton
	 * @功能描述 :初始化按钮
	 * 	
	 * @param view
	 * @return :void
	 */
	protected void initButton(View view){
		if(isButtonShow()){
			okBtn = (Button)view.findViewById(R.id.dialog_ok);
			cancelBtn = (Button)view.findViewById(R.id.dialog_cannel);
			
			okBtn.setText(okBtnText);
			okBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					afterClickOK(OK);
				}
			});
			
			cancelBtn.setText(cancelBtnText);
			cancelBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					afterClickOK(CANCEL);
				}
			});
		}else{
			View v = view.findViewById(R.id.dialog_button_group);
			((ViewGroup)view).removeView(v);
		}
	}
	
	/**
	 * @方法名称 :isButtonShow
	 * @功能描述 :如果子类不需要显示按钮，可以重写这个方法。
	 * @return
	 * @return :boolean
	 */
	protected boolean isButtonShow(){
		return true;
	}
	
	/**
	 * @方法名称 :getMainXML
	 * @功能描述 :获得主视图id
	 * @return
	 * @return :int
	 */
	public int getMainXML(){
		return DIALOG_UI;
	}
	
	public int getGravity(){
		return Gravity.CENTER;
	}
	
	/**
	 * @方法名称 :afterClickOK
	 * @功能描述 :确认按钮点击后触发，子类可以重写这个方法达到不同的效果
	 * @return :void
	 */
	public void afterClickOK(int type){
		if(listener != null)
			listener.onConfirmClick(type, null);
	}
	/**
	 * @方法名称 :getLiveView
	 * @功能描述 :得到一个扩展的视图，可以产生不同组合的对话框，子类可以重写这个方法
	 * @return
	 * @return :View
	 */
	public View getLiveView(){
		return null;
	}
	
	/**
	 * 改变ok按钮的文字
	 *	@param text
	 */
	public void setOkButtonText(String text){
		this.okBtnText = text;
		if(this.okBtn != null)
			this.okBtn.setText(okBtnText);
	}
	
	/**
	 * 改变取消按钮的文字
	 *	@param text
	 */
	public void setCancelButtonText(String text){
		this.cancelBtnText = text;
		if(this.cancelBtn != null)
			this.cancelBtn.setText(cancelBtnText);
	}
}