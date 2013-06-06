package org.nerve.ui.corner;

import android.view.View;

public interface OnRowClickListener {
	
	/**
	 * 当列表中的一行被点击时被调用
	 *	@param v	被点击的行View
	 *	@param index	点击行所在的行数
	 */
	public void onRowClick(View v, int index);
}
