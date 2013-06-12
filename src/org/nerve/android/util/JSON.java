package org.nerve.android.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

/**
 * @项目名称 :nerveAndroid
 * @文件名称 :JSON.java
 * @所在包 :org.nerve.android.util
 * @功能描述 :
 *	JSON范型处理类
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：IBM GDC (http://www.ibm.com/)
 * @创建日期 :2013-6-10
 * @修改记录 :
 */
public class JSON<T> {

	/**
	 * 泛型类型class
	 */
	private Class<T> entityClass;
	
	private String dateFormat;
	
	public JSON(Class<T> clazz){
		//entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		//对泛型进行具体的赋值
		
		entityClass = clazz;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@SuppressLint("SimpleDateFormat")
	public T parse(JSONObject obj){
		if(obj == null)
			return null;
		
		T bean = null;
		try {
			bean = entityClass.newInstance();
			String name = null;
			Field[] fields = bean.getClass().getDeclaredFields();
			for(Field f:fields){
				if(!obj.has(f.getName()))
					continue;
				
				if(!f.isAccessible())
					f.setAccessible(true);
				
				Class<?> ft = f.getType();
				name = ft.getName(); //属性的类名
				//System.out.println("解析属性： " + name + " @ " + f.getName());
				//基本类型的判断
				if(ft.isPrimitive()){
					
					if(name.equals("int") || name.equals("float"))
						f.setInt(bean, obj.getInt(f.getName()));
					else if(name.equals("double"))
						f.setDouble(bean, obj.getDouble(f.getName()));
					else if(name.equals("boolean"))
						f.setBoolean(bean, obj.getBoolean(f.getName()));
						
				}else{
					if(name.equals("java.lang.String")){
						//System.out.println("赋值String ：" + obj.getString(f.getName()));
						f.set(bean, obj.getString(f.getName()));
					}
					else if(name.equals("java.util.Date")){ 	//对日期类型赋值
						SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
						f.set(bean, sdf.parse(obj.getString(f.getName())));
					}
				}
			}
			
		} catch (IllegalAccessException e) {
			e.printStackTrace(System.out);
		} catch (InstantiationException e) {
			e.printStackTrace(System.out);
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		
		return bean;
	}
	

	/**
	 * 从JSONArray中生成相应的List<T>
	 *	@param array
	 *	@return
	 *	@throws JSONException
	 */
	private List<T> toList(JSONArray array) throws JSONException{
		List<T> list = new ArrayList<T>();
		if(array == null)
			return list;
		
		for(int i=0;i<array.length();i++){
			JSONObject obj = array.getJSONObject(i);
			
			T bean = parse(obj);
			System.out.println("解析：" + bean);
			if(bean != null){
				list.add(bean);
			}
		}
		
		return list;
	}
	
	/**
	 * 从相应的对象数组json数据中生成对象List，格式如下：
	 * [{},{},{}]
	 *	@param json
	 *	@return
	 *	@throws JSONException
	 */
	public List<T> parseToList(String json) throws JSONException{
		JSONArray array = new JSONArray(json);
		
		return toList(array);
	}
	
	/**
	 * 从相应的对象数组json数据中生成对象List，格式如下：
	 * {'key':[{},{},{}]}
	 * 
	 *	@param json
	 *	@param key
	 *	@return
	 *	@throws JSONException
	 */
	public List<T> parseToList(String json, String key) throws JSONException{
		JSONObject obj = new JSONObject(json);
		
		return toList(obj.getJSONArray(key));
	}
	
	/**
	 * 从json生成一个对象
	 *	@param json
	 *	@return
	 *	@throws JSONException
	 */
	public T parse(String json) throws JSONException{
		JSONObject obj = new JSONObject(json);
		
		return parse(obj);
	}
	
	/**
	 * 将对象转化成json格式的数据
	 *	@param bean
	 *	@return
	 *	@throws JSONException
	 */
	public String toJson(T bean) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(entityClass.getName(), bean);
		
		return obj.toString();
	}
}