package org.nerve.example.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nerve.R;
import org.nerve.android.NerveActivity;
import org.nerve.android.annotation.Acvitity;
import org.nerve.android.annotation.ViewOnId;
import org.nerve.android.net.NetWorker;
import org.nerve.android.util.JSON;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @项目名称 :nerveAndroid
 * @文件名称 :SelectGoodsActivity.java
 * @所在包 :org.nerve.example.net
 * @功能描述 :
 *	选择商品的activity
 *	在一开始，显示顶部的商品分类
 *	当用户输入关键字搜索商品时，显示“搜索辅助的LinearLayout”
 *	在搜索的时候，可以多选。
 *	以isSearch 作为是否搜索的标识
 *
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：IBM GDC (http://www.ibm.com/)
 * @创建日期 :2013-6-10
 * @修改记录 :
 */
@SuppressLint("HandlerLeak")
@Acvitity(layout=R.layout.goods_select)
public class SelectGoodsActivity extends NerveActivity implements OnClickListener, OnItemClickListener{
	
	public static final String TAG = "SelectGoodsActivity";
	
	/**
	 * 默认的根分类id
	 */
	private static final String ROOTID = "00000";
	
	/**
	 * 记录了商品分类之间的父子关系
	 * 
	 * key 为商品分类的 typeid
	 * value 为商品父类的typerid
	 * 
	 * 如果value为空，那么就是顶层的分类
	 * 这个主要是方便转到上一层分类
	 */
	private Map<String, String> familyMap;
	
	/**
	 * 放置了已经从网络中获取过来的商品数据
	 */
	private Map<String, List<Goods>> goodsMap;
	
	/**
	 * 当前显示的商品列表
	 */
	private List<Goods> curGoodsList;
	
	/**
	 * 应该选择的商品列表，在此activity结束时，会返回这个值
	 */
	private List<Goods> selectGoodsList;
	
	private boolean isSearch;
	
	/**
	 * 当前显示的分类id，默认为 00000
	 */
	private String curTypeId;
	
	/**
	 * -----------------------------------------------------------------------------
	 * BEGIN 布局相关
	 * ----------------------------------------------------------------------------- 
	 */
	
	/*
	 * 搜索辅助的布局
	 */
	@ViewOnId(id=R.id.search_div)
	private LinearLayout searchLayout;
	@ViewOnId(id=R.id.category_div)
	private LinearLayout categoryLayout;
	@ViewOnId(id=R.id.parentBtn, clickListener="this")
	protected Button parentBtn;	//上一层按钮
	@ViewOnId(id=R.id.selectAllBtn, clickListener="this")
	protected Button selectAllBtn;	//全选按钮
	@ViewOnId(id=R.id.selectNoBtn, clickListener="this")
	protected Button selectNoBtn;	//全不选按钮
	@ViewOnId(id=R.id.okBtn, clickListener="this")
	protected Button okBtn;	//确定按钮
	@ViewOnId(id=R.id.cancelBtn, clickListener="this")
	protected Button cancelBtn;
	@ViewOnId(id=R.id.searchSize)
	protected TextView sizeTV;
	
	@ViewOnId(id=R.id.searchBtn, clickListener="this")
	protected Button searchBtn;	//搜索按钮
	@ViewOnId(id=R.id.keyword)
	protected EditText keywordET;	//关键字输入框
	@ViewOnId(id=R.id.spinner1)
	protected Spinner typeSpinner;	//搜索类型选择器
	@ViewOnId(id=R.id.navBackBtn, clickListener="this")
	protected Button backBtn;	//返回按钮
	
	@ViewOnId(id=R.id.goods_list)
	protected ListView listView;
	
	/**
	 * -----------------------------------------------------------------------------
	 * END 布局相关
	 * ----------------------------------------------------------------------------- 
	 */
	
	private GoodsAdapter adapter;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GoodsWorker.NONET:
				toast("当前网络连接不可用，请稍后再试");
				break;
			case GoodsWorker.FAILED:
				toast("获取商品列表时出错了...");
				hideProgressDialog();
				break;
			case GoodsWorker.OK:
				hideProgressDialog();
				System.out.println("获取成功：" + curGoodsList.size());
				adapter.notifyDataSetChanged(); //更新列表
				
				//如果是分类获取，保存相应的分类列表到goodsMap中
				//同时保存分类的父子关系到family中
				if(!isSearch){
					goodsMap.put(curTypeId, curGoodsList);
					for(Goods g:curGoodsList){
						familyMap.put(g.typeId, g.ParId);
					}
					
					System.out.println("保存了key=" + curTypeId +" 的商品列表");
				}else{
					sizeTV.setText("共 " + curGoodsList.size() + " 条");
				}
				break;
			}
		}
	};
	
	@Override
	protected void initData() {
		Log.d(TAG, "初始化数据");
		
		familyMap = new HashMap<String, String>();
		goodsMap = new HashMap<String, List<Goods>>();
		
		curTypeId = ROOTID;
		
		curGoodsList = new ArrayList<Goods>();
		selectGoodsList = new ArrayList<Goods>();
	}
	
	@Override
	protected void initUI() {
		initSpinner();
		
		adapter = new GoodsAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		searchLayout.setVisibility(View.GONE);
		
		loadData();
	}
	
	private void initSpinner(){
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.search_type, android.R.layout.simple_spinner_item);
		typeSpinner.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchBtn:
			isSearch = true;
			toggleBottomDiv();
			loadData();
			break;
			
		case R.id.selectAllBtn:
			for(int i=0;i<curGoodsList.size();i++){
				curGoodsList.get(i).select = true;
			}
			adapter.notifyDataSetChanged();
			break;
			
		case R.id.selectNoBtn:
			for(int i=0;i<curGoodsList.size();i++){
				curGoodsList.get(i).select = false;
			}
			adapter.notifyDataSetChanged();
			break;
			
		case R.id.cancelBtn:
			isSearch = false;
			toggleBottomDiv();
			loadData();
			break;
			
		case R.id.okBtn:
			for(int i=0;i<curGoodsList.size();i++){
				Goods g = curGoodsList.get(i);
				
				if(g.select){
					selectGoods(curGoodsList.get(i));
				}
			}
			endSelect();
			break;
			
		/*
		 * 先判断当前typeId 为否为00000
		 * 是：显示不能进入上一层
		 * 否：从familyMap中找curtypeid的父类id
		 */
		case R.id.parentBtn:
			if(curTypeId.equals(ROOTID)){
				toast("已经是最顶层分类了");
			}else{
				System.out.println(familyMap);
				String newId = familyMap.get(curTypeId);
				if(newId != null){
					curTypeId = newId;
					loadData();
				}
			}
			break;
		}
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Goods g = curGoodsList.get(arg2);
		if(isSearch){
			g.select = !g.select;
			GoodsRow row = (GoodsRow)arg1;
			row.setChecked(g.select);
		}else{
			
			if(g.sonnum == 0){
				selectGoods(g);
				endSelect();
			}else{
				curTypeId = g.typeId;
				loadData();
			}
		}
	}
	
	/**
	 * 切换试问的按钮组
	 */
	private void toggleBottomDiv(){
		searchLayout.setVisibility(isSearch ? View.VISIBLE : View.GONE);
		categoryLayout.setVisibility(isSearch ? View.GONE : View.VISIBLE);
	}
	
	private void loadData(){
		if(isSearch){
			showProgressDialog(null, typeSpinner.getSelectedItem() + " 方式搜索商品中...");
			if(typeSpinner.getSelectedItemPosition() == 0){
				new GoodsWorker().execute(GoodsWorker.BYNAME);
			}else{
				new GoodsWorker().execute(GoodsWorker.BYCODE);
			}
		}else{
			
			/*
			 * 查询子类的话，先判断在 goodsMap 中是否有以 curTypeId 为key的value
			 * 没有，就从网络中获取
			 */
			if(goodsMap.containsKey(curTypeId)){
				curGoodsList = goodsMap.get(curTypeId);
				adapter.notifyDataSetChanged();
			}else{
				showProgressDialog(null, "查看下一层分类中...");
				new GoodsWorker().execute(GoodsWorker.CATEGORY);
			}
			
		}
	}
	
	private void selectGoods(Goods g){
		selectGoodsList.add(g);
	}
	
	/**
	 * 结束选择
	 */
	private void endSelect(){
		if(isSearch){
			
		}else{
			
		}
		
		System.out.println("选择的goodsList：");
		for(Goods g:selectGoodsList)
			System.out.println(g);
	}
	
	
	/**
	 * @项目名称 :nerveAndroid
	 * @文件名称 :SelectGoodsActivity.java
	 * @所在包 :org.nerve.example.net
	 * @功能描述 :
	 *	商品列表的适配器
	 * @创建者 :集成显卡	1053214511@qq.com
	 * @公司：IBM GDC (http://www.ibm.com/)
	 * @创建日期 :2013-6-12
	 * @修改记录 :
	 */
	public class GoodsAdapter extends BaseAdapter{
		private LayoutInflater mInflater;

		public GoodsAdapter(Context context){
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			if(curGoodsList != null)
				return curGoodsList.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Goods g = curGoodsList.get(position);
			if(convertView == null){
				//System.out.println("0----------000000--------------00");
				convertView = mInflater.inflate(R.layout.goods_select_item, null);
			}
			GoodsRow row = (GoodsRow)convertView;
			row.setGoods(g, isSearch);
			
			return row;
		}
		
	}
	
	
	/**
	 * @项目名称 :nerveAndroid
	 * @文件名称 :SelectGoodsActivity.java
	 * @所在包 :org.nerve.example.net
	 * @功能描述 :
	 *	网络查询的工作者线程
	 *	传递参数：
	 *		0	直接获取指定分类下商品
	 *		1	按照命名搜索商品
	 *		2	按照编号搜索商品	
	 *
	 * @创建者 :集成显卡	1053214511@qq.com
	 * @公司：IBM GDC (http://www.ibm.com/)
	 * @创建日期 :2013-6-10
	 * @修改记录 :
	 */
	public class GoodsWorker extends AsyncTask<Integer, Void, Integer>{
		
		/**
		 * 获取商品信息的地址
		 */
		public static final String GOODS_URL = "http://jxc.chonour.com/chonourDV/getPtype.asp?TypeID=%1$s&sessionId=%2$s&keyWord=%3$s";
		
		public static final int FAILED = 0;
		public static final int OK = 1;
		public static final int NONET = 2;
		public static final int WORKING = 3;
		
		/**搜索分类*/
		public static final int CATEGORY = 10;
		/**以命名搜索*/
		public static final int BYNAME = 11;
		/**以编号搜索*/
		public static final int BYCODE = 12;
		
		private NetWorker net = new NetWorker("utf-8");
		
		@Override
		protected Integer doInBackground(Integer... params) {
			if(params.length < 1)
				return FAILED;
			int action = params[0];
			
			//从网络中获取商品
			String url = buildUrl(action);
			String result = net.getData(url);
			System.out.println("url=" + url);
			if(result.equals(NetWorker.NO_NET)){
				return NONET;
			}
			
			//删除返回值前面的 {'LIST':
			System.out.println("result : " + result);
			JSON<Goods> goodsJSON = new JSON<Goods>(Goods.class);
			try{
				curGoodsList = goodsJSON.parseToList(result, "LIST");
			}catch(Exception e){
				e.printStackTrace(System.out);
				
				return FAILED;
			}
			
			return OK;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			handler.sendEmptyMessage(result);
		}
		
		/**
		 * 根据查询条件构建网址
		 *	@return
		 */
		private String buildUrl(int action){
			String typeId = null,		//父类id
				   sessionId = "1",	//查询类形
				   key = null;			//关键字
			switch(action){
			case CATEGORY:
				typeId = curTypeId;
				break;
			case BYCODE:
				sessionId = "2";
				break;
			case BYNAME:
				sessionId = "3";
				break;
			}
			
			key = keywordET.getText().toString();
			
			return String.format(GOODS_URL, typeId, sessionId, key);
		}
	}

}