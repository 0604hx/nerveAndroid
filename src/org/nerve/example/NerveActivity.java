package org.nerve.example;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.nerve.annotation.Acvitity;
import org.nerve.annotation.ViewOnId;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class NerveActivity extends Activity{
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
		View v = View.inflate(this, id, null);
        setContentView(v);
	}
	
	/**
	 * 解析Class层的注解
	 */
	protected void parseAnnotationClass(){
		org.nerve.annotation.Acvitity activity = this.getClass()
				.getAnnotation(org.nerve.annotation.Acvitity.class);
		if(activity != null){
			System.out.println("layout to：" + activity.layout());
			this.loadView(activity.layout());
		}
		
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
			System.out.println("解析：" + field.getName() + " viewOnId=" +voi);
			if(voi != null){
				try{
					if(!field.isAccessible()){
						field.setAccessible(true);
					}
					System.out.println("自动绑定id：" + field.getType().cast(findViewById(voi.id())));
					field.set(this, field.getType().cast(findViewById(voi.id())));
					name = voi.clickListener();
					if(name.length() > 0){
						Method method = field.getType().getMethod("setOnClickListener", OnClickListener.class);
						System.out.println("method:" + method);
						if(voi.clickListener().equals("this")){
							System.out.println(this + " , " + field.getName()+ "= " + field.get(this));
							method.invoke(field.get(this), this);
						}else{
							Field listener = this.getClass().getField(voi.clickListener());
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
