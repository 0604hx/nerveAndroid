package org.nerve.example.net;

import java.io.Serializable;

/**
 * @项目名称 :nerveAndroid
 * @文件名称 :Goods.java
 * @所在包 :org.nerve.example.net
 * @功能描述 :
 *	商品Bean
 *	这里只有一些使用到的属性
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：IBM GDC (http://www.ibm.com/)
 * @创建日期 :2013-6-10
 * @修改记录 :
 */
@SuppressWarnings("serial")
public class Goods implements Serializable{
	public boolean select;
	public String typeId;
	public String ParId;
	public int sonnum;
	public String UserCode;
	public String FullName;
	
	@Override
	public String toString() {
		return String.format("[typeid=%1$s, code=%2$s, name=%3$s, sonnum=%4$d]", typeId, UserCode, FullName, sonnum);
	}
}
