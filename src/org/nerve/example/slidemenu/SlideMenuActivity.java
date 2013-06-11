package org.nerve.example.slidemenu;

import org.nerve.R;
import org.nerve.android.NerveActivity;
import org.nerve.android.annotation.Acvitity;
import org.nerve.android.annotation.ViewOnId;
import org.nerve.android.ui.slidemenu.SlideMenuLayout;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

@Acvitity(layout=R.layout.sildemenu_demo)
public class SlideMenuActivity extends NerveActivity implements OnClickListener{
	
	@ViewOnId(id=R.id.openmenu, clickListener="this")
	protected Button menuBtn;
	
	@ViewOnId(id=R.id.openmenuRight, clickListener="this")
	protected Button rightBtn;

	@ViewOnId(id=R.id.openmenuTop, clickListener="this")
	protected Button topBtn;
	
	@ViewOnId(layout=R.layout.sildemenu_top_demo, clickListener="this")
	protected LinearLayout topMenuLayout;
	@ViewOnId(id=R.id.menu_home, parent="topMenuLayout",clickListener="this")
	protected ImageView topmenuHome;
	@ViewOnId(id=R.id.menu_member, parent="topMenuLayout", clickListener="this")
	protected ImageView topmenuMember;
	
	private SlideMenuLayout slideMenu;
	
	@Override
	protected void initUI() {
		//设置三个方向的菜单
		slideMenu = new SlideMenuLayout(this, true);
		slideMenu.addMenu(R.layout.sildemenu_right_demo, SlideMenuLayout.RIGHT, 100);
		slideMenu.addMenu(R.layout.sildemenu_left_demo, SlideMenuLayout.LEFT);
		slideMenu.addMenu(topMenuLayout, SlideMenuLayout.TOP, 42);
	}
	
	@Override
	public void onClick(View v) {
		System.out.println(v.getId());
		switch (v.getId()) {
		case R.id.openmenu:
			slideMenu.toggleMenu(SlideMenuLayout.LEFT);
			break;
		case R.id.openmenuRight:
			slideMenu.toggleMenu(SlideMenuLayout.RIGHT);
			break;
		case R.id.openmenuTop:
			slideMenu.toggleMenu(SlideMenuLayout.TOP);
			break;
		
		case R.id.menu_home:
			toast("top menu_home be clicked!");
			break;
		case R.id.menu_member:
			toast("top menu_member be clicked!");
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			if(slideMenu.isOpen()){
				slideMenu.onBackKeyDown();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}