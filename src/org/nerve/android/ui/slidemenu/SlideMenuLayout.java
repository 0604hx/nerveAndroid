package org.nerve.android.ui.slidemenu;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * @项目名称 :nerveAndroid
 * @文件名称 :SlideMenuLayout.java
 * @所在包 :org.nerve.android.ui.slidemenu
 * @功能描述 :
 *
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：IBM GDC (http://www.ibm.com/)
 * @创建日期 :2013-6-9
 * @修改记录 :
 */
public class SlideMenuLayout extends RelativeLayout{
	
	private static final String TAG = "nerverAndroid#SlideMenu";
	
	/**
	 * 默认的菜单宽度
	 */
	public static final int DEFAULT_WIDTH = 200;
	public static final int TITLEBAR_HEIGHT = 38; //androi系统的标题栏高度
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int TOP = 2;
	
	private boolean isOverTitle; //是否移动标题栏
	
	private boolean singleMenu; //是否可以同时撕开多个菜单，默认为false
	
	private ViewGroup[] menuArray;	//滑动菜单数组，默认是3个，分别是左，右，上
	private boolean[] menuOpen;	//记录了菜单是否打开
	
	private Activity activity;//需要增加滑动菜单的activity
	private View mainLayout;//主界面内容
	
	private int movietime = 250;//默认的移动时间间隔,单位为毫秒
	private int speed = 20;//默认的移动距离
	
	private boolean isMoving = false;//是否在动画中
	private int curPosition = -1;	//当前移动的菜单位置标识
	
	public SlideMenuLayout(Context context){
		super(context);
		
		this.menuArray = new ViewGroup[3];
		this.menuOpen = new boolean[3];
		
		this.singleMenu = true;
	}
	
	/**
	 * @构造方法:
	 * @类名:SlideMenuLayout.java
	 * @param context 需要添加菜单的activity
	 * @param isOverTitle	是否同时滚动titleBar，如果为false，那么标题栏会一直固定着
	 */
	public SlideMenuLayout(Activity context, boolean isOverTitle){
		this(context);
		this.activity = context;
		this.isOverTitle = isOverTitle;
		
		/*
		 * 思路
		 * 先得到
		 */
		ViewGroup decorView = (ViewGroup)activity.getWindow().getDecorView();
		View rootView = isOverTitle ? decorView.getChildAt(0) : decorView.findViewById(android.R.id.content);
		ViewGroup rootViewParent = (ViewGroup)rootView.getParent();
		
		this.mainLayout = rootView;
		rootViewParent.removeView(this.mainLayout);
		
		//将activit的要视图放到本布局中
		addView(rootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		
		//然后将本视图放到在activit视图容器中，这里就是 rootViewParent
		rootViewParent.addView(this);
	}
	
	/**
	 * 增加一个菜单，使用默认的菜单大小：200dp
	 *	@param layout
	 *	@param position
	 */
	public void addMenu(int layout, int position){
		addMenu(layout, position, DEFAULT_WIDTH);
	}
	
	/**
	 * 在指定的位置增加一个滑动菜单，如果之前已经设置过指定位置的菜单，这个方法会覆盖之前的菜单
	 *	@param layout	菜单的视图
	 *	@param position	位置
	 *	@param width	菜单大小，如果是左右菜单，这个值是宽度，如果是顶部菜单，这个指的是高度
	 */
	public void addMenu(int layout, int position, int width){
		if(layout > 0){
			ViewGroup menuView = (ViewGroup)View.inflate(activity, layout, null);
			
			addMenu(menuView, position, width);
		}
	}
	
	/**
	 * 在指定的位置增加一个滑动菜单，如果之前已经设置过指定位置的菜单，这个方法会覆盖之前的菜单
	 *	@param layout	菜单，必须是ViewGroup的子类
	 *	@param position	位置
	 *	@param width	菜单大小，如果是左右菜单，这个值是宽度，如果是顶部菜单，这个指的是高度
	 */
	public void addMenu(ViewGroup menuView, int position, int width){
		DisplayMetrics dm = getResources().getDisplayMetrics();
		
		if(position == TOP){
			menuView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, width));
		}else{
			menuView.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.FILL_PARENT));
		}
		addView(menuView);
		
		menuArray[position] = null;
		menuArray[position] = menuView;
		
		RelativeLayout.LayoutParams lp = (LayoutParams)menuView.getLayoutParams();
		
		/*
		 * 左边菜单位置主视图的左边
		 * 右边在主视图的右边
		 * 顶部的话就在上面嘛
		 */
		switch (position) {
		case LEFT:
			lp.leftMargin = -width;
			break;
		case RIGHT:
			lp.leftMargin = dm.widthPixels;
			lp.rightMargin = -lp.width;
			break;
		case TOP:
			lp.topMargin = -width;
			break;
		}
		
		/*
		 * 如果想要标题栏一起移动，那么要对菜单的topMagic进行相应的设置
		 */
		if(isOverTitle){
			lp.topMargin += TITLEBAR_HEIGHT;
		}
		menuView.setLayoutParams(lp);
		
		Log.d(TAG, "add menu to Position:" + position + " OK!");
	}
	
	
	/**
	 * 计算需要移动的距离
	 *	@return
	 */
	private int[] figureMoveDistance(){
		int[] distance = new int[2];
		switch (curPosition) {
		/*
		 * 左菜单的话，如果已经打开那么要左移，所以 speed 要为负数
		 */
		case LEFT:
			distance[0] = menuArray[curPosition].getLayoutParams().width;
			distance[1] = menuOpen[curPosition] ? -speed : speed;
			break;
			
		/*
		 * 右菜单，如果已经打开，那么要右移，speed为正
		 */
		case RIGHT:
			distance[0] = menuArray[curPosition].getLayoutParams().width;
			distance[1] = menuOpen[curPosition] ? speed : -speed;
			break;
		
		/*
		 * 
		 */
		case TOP:
			distance[0] = menuArray[curPosition].getLayoutParams().height;
			distance[1] = menuOpen[curPosition] ? -speed : speed;
			break;
		}
		
		return distance;
	}
	
	/**
	 * 打开或者隐藏相应的菜单
	 * 如果　isMoving　为true，不作任何动作
	 *	@param type		可以为 LEFT 或者 RIGHT 或者 TOP
	 */
	public void toggleMenu(int type){
		if(isMoving)
			return ;
		
		if(menuArray[type] != null){
			if(singleMenu && isOpen()){
				if(!menuOpen[type])
					return ;
			}
			
			curPosition = type;
			
			int params[] = figureMoveDistance();
			
			new SlideMenuWorker().execute(params[0], params[1]);
			menuOpen[type] = !menuOpen[type];
			
		}
	}
	
	/**
	 * 设置菜单是否只能同时打开一个
	 * 默认为true，如果为false，那么可以同时打开多个滑动菜单
	 *	@param s
	 */
	public void setSingleMenu(boolean s){
		this.singleMenu = s;
	}
	
	/**
	 * 设置菜单一次滑动的移动距离
	 *	@param speed
	 */
	public void setSpeed(int speed){
		this.speed = speed;
	}
	
	/**
	 * 完成两个菜单显示/隐藏所需的时间
	 *	@param time
	 */
	public void setDuration(int time){
		if(time <= 0)
			return ;
		this.movietime = time;
	}
	
	private void rollLayout(int margin){
		LayoutParams lp = (LayoutParams) mainLayout.getLayoutParams();
		
		/*
		 * 如果是顶部菜单，则是操作Topmargin
		 */
		if(curPosition == TOP){
			lp.topMargin += margin;
		}else{
			lp.leftMargin += margin;
			lp.rightMargin -= margin;
		}
		mainLayout.setLayoutParams(lp);
		
		switch(curPosition){
		case LEFT:
		case RIGHT:
			if(menuArray[curPosition] != null){
				lp = (LayoutParams) menuArray[curPosition].getLayoutParams();
				lp.leftMargin += margin;
				menuArray[curPosition].setLayoutParams(lp);
			}
			break;
		case TOP:
			if(menuArray[curPosition] != null){
				lp = (LayoutParams) menuArray[curPosition].getLayoutParams();
				lp.topMargin += margin;
				menuArray[curPosition].setLayoutParams(lp);
			}
			break;
		}
	}

	/**
	 * 判断是否有菜单打开
	 *	@return
	 */
	public boolean isOpen(){
		for(boolean b:menuOpen){
			if(b)
				return true;
		}
		return false;
	}
	
	
	/**
	 * 当返回键被按下时
	 */
	public void onBackKeyDown(){
		for(int i=0;i<menuOpen.length;i++){
			if(menuOpen[i]){
				toggleMenu(i);
				return ;
			}
		}
	}
	
	/**
	 *	左、右菜单滑出<br />
	 *	<br />
	 *	params[0]: 滑动距离<br />
	 *	params[1]: 滑动速度,带方向<br />
	 *	
	 *	@see 感谢cheng.yang 实现的移动的代码
	 *  @author cheng.yang
	 */
	public class SlideMenuWorker extends AsyncTask<Integer, Integer, Void>{
		
		/**
		 * 需要两个参数：
		 * 1，移动的距离
		 * 2，移动的速度，如果为-，则左移
		 */
		@Override
		protected Void doInBackground(Integer... params) {
			if(params.length != 2){
				return null;
			}

			isMoving = true;
			
			int times = params[0] / Math.abs(params[1]);
			if(params[0] % Math.abs(params[1]) != 0){
				times ++;
			}
			
			for(int i = 0; i < times; i++){
				this.publishProgress(params[0], params[1], i+1);
				try{
					Thread.sleep(movietime / times);
				}catch(Exception e){
					e.printStackTrace(System.out);
				}
			}
			
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if(values.length != 3){
				return ;
			}

			int distance = Math.abs(values[1]) * values[2];
			int delta = values[0] - distance;

			int leftMargin = 0;
			if(values[1] < 0){ // 左移
				leftMargin = (delta > 0 ? values[1] : -(Math.abs(values[1]) - Math.abs(delta)));
			}else{
				leftMargin = (delta > 0 ? values[1] : (Math.abs(values[1]) - Math.abs(delta)));
			}
			
			rollLayout(leftMargin);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			isMoving = false;
		}
	}
}
