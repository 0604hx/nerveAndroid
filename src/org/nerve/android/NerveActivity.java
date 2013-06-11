package org.nerve.android;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.nerve.android.annotation.ViewOnId;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * @项目名称 :nerveAndroid
 * @文件名称 :NerveActivity.java
 * @所在包 :org.nerve.example
 * @功能描述 :
 *	实现了注解绑定View的Activity抽象类
 *
 *	在oncreate 方法中，调用的顺序为：<br />
 *		1.initData()			初始化数据<br />
 *		2.parseAnnotation()		解析类中的注解（只解析了 org.nerve.android.annotation 中的注解类）<br />
 *		3.initUI();				初始化UI的显示<br />
 *
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：IBM GDC (http://www.ibm.com/)
 * @创建日期 :2013-6-8
 * @修改记录 :
 */
public abstract class NerveActivity extends Activity{
	
	protected ProgressDialog progressDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initData();
        
        parseAnnotation();
        
        initUI();
    }
	
	protected void initData(){}
	
	protected void initUI(){}
	
	protected void loadView(int id){
		//View v = View.inflate(this, id, null);
        setContentView(id);
	}
	
	/**
	 * 解析Class层的注解
	 */
	protected void parseAnnotationClass(){
		org.nerve.android.annotation.Acvitity activity = this.getClass()
				.getAnnotation(org.nerve.android.annotation.Acvitity.class);
		if(activity != null){
			System.out.println("layout to：" + activity.layout());
			this.loadView(activity.layout());
		}
		
	}
	
	protected void toast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 显示进度条
	 *	@param title
	 *	@param message
	 */
	protected void showProgressDialog(String title, String message){
		if(this.progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			
			progressDialog.setIndeterminate(false);//不明确的进度条
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
				}
			});
		}
		if(title != null)
			progressDialog.setTitle(title);
		if(message != null)
			progressDialog.setMessage(message);
		progressDialog.show();
	}
	
	/**
	 * 隐藏进度框
	 */
	protected void hideProgressDialog(){
		if(progressDialog != null)
			progressDialog.dismiss();
	}
	
	/**
	 * 解析view注解
	 */
	protected void parseAnnotation(){
		parseAnnotationClass();
		
		Field[] fields = this.getClass().getDeclaredFields();
		String name = null;
		for(Field field:fields){
			ViewOnId voi = field.getAnnotation(ViewOnId.class);
			if(voi != null){
				try{
					if(!field.isAccessible()){
						field.setAccessible(true);
					}
					
					//优先检查id
					if(voi.id() > 0){
						//设置了父视图，那么会从这个parent中调用 findViewById
						if(voi.parent().length() > 0){
							Field parent = this.getClass().getDeclaredField(voi.parent());
							parent.setAccessible(true);
							
							if(parent.get(this) != null){
								View parentV = (View)parent.get(this);
								field.set(this, field.getType().cast(parentV.findViewById(voi.id())));
							}
						}else{
							field.set(this, field.getType().cast(findViewById(voi.id())));
						}
					}
					//再判断是否指定了layout
					else if(voi.layout() > 0){
						field.set(this, field.getType().cast(View.inflate(this, voi.layout(), null)));
					}
					//如果id 跟layout 都没有配置，这个view的绑定取消
					else{
						continue;
					}
					name = voi.clickListener();
					if(name.length() > 0){
						Method method = field.getType().getMethod("setOnClickListener", OnClickListener.class);
						if(voi.clickListener().equals("this")){
							method.invoke(field.get(this), this);
						}else{
							Field listener = this.getClass().getDeclaredField(voi.clickListener());
							listener.setAccessible(true);
							if(listener.getClass().equals(OnClickListener.class)){
								method.invoke(field.get(this), listener.get(this));
							}
						}
					}
					
					System.out.println(field.getName() + " init ok.id=" + voi.id() +", clickListener=" + voi.clickListener());
				}catch(Exception e){
					System.err.println("error on bind view '" + field.getName() +"':" + e.getMessage());
					e.printStackTrace(System.out);
				}
			}
		}
	}
}
