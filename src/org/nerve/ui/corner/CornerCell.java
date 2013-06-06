package org.nerve.ui.corner;

import android.view.View;

/**
 * @项目名称 :missingEn
 * @文件名称 :CornerCell.java
 * @所在包 :org.nerve.ui.corner
 * @功能描述 :
 *	圆角表格中的一行。
 *	具有以下属性：
 *		title	左边显示的标题
 *		value	内容
 *		isArrow	是否显示右边的箭头
 *		view 	自定义的视图，如果为null，则使用默认的
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：IBM GDC(http://www.ibm.com/)
 * @创建日期 :2013-5-23
 * @修改记录 :
 */
public class CornerCell {
	
	private String title;
	private String value;
	private boolean isArrow;
	private View view;
	
	public CornerCell(String title){
		this(title, null, false);
	}
	
	public CornerCell(String title, boolean isArrow){
		this(title, null, isArrow);
	}
	
	public CornerCell(String title, String value, boolean isArrow){
		this.title = title;
		this.value = value;
		this.isArrow = isArrow;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isArrow() {
		return isArrow;
	}
	public void setArrow(boolean isArrow) {
		this.isArrow = isArrow;
	}
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	
	@Override
	public String toString() {
		return String.format(
				"[CornerCell: title=%1$s, value=%2$s, isArrow=%3$s]", 
				title, 
				value, 
				isArrow
				);
	}
}
