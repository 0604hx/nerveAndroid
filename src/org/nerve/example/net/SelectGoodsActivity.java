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

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
@Acvitity(layout=R.layout.goods_select)
public class SelectGoodsActivity extends NerveActivity implements OnClickListener{
	
	public static final String TAG = "SelectGoodsActivity";
	
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
	private String curTypeId = "00000";
	
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
	@ViewOnId(id=R.id.parentBtn, clickListener="this")
	protected Button parentBtn;
	@ViewOnId(id=R.id.selectAllBtn, clickListener="this")
	protected Button selectAllBtn;
	@ViewOnId(id=R.id.selectNoBtn, clickListener="this")
	protected Button selectNoBtn;
	@ViewOnId(id=R.id.okBtn, clickListener="this")
	protected Button okBtn;
	
	
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
	
	private GoodsWorker worker;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GoodsWorker.NONET:
				toast("当前网络连接不可用，请稍后再试");
				break;
			case GoodsWorker.WORKING:
				showProgressDialog(null, "查询商品中...");
				break;
			case GoodsWorker.FAILED:
				toast("获取商品列表时出错了...");
				break;
			case GoodsWorker.OK:
				hideProgressDialog();
				curGoodsList = worker.getList();
				System.out.println("获取成功：" + curGoodsList.size());
				System.out.println(curGoodsList.get(0));
				adapter.notifyDataSetChanged(); //更新列表
				break;
			}
		}
	};
	
	@Override
	protected void initData() {
		Log.d(TAG, "初始化数据");
		
		familyMap = new HashMap<String, String>();
		goodsMap = new HashMap<String, List<Goods>>();
		
		curGoodsList = new ArrayList<Goods>();
		selectGoodsList = new ArrayList<Goods>();
	}
	
	@Override
	protected void initUI() {
		initSpinner();
		
		adapter = new GoodsAdapter();
		listView.setAdapter(adapter);
		
		worker = new GoodsWorker();
		worker.execute(GoodsWorker.CATEGORY);
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
			
			break;
		}
	}
	
	
	public class GoodsAdapter extends BaseAdapter{

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
			TextView v = new TextView(SelectGoodsActivity.this);
			v.setText(curGoodsList.get(position).toString());
			return v;
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
		public static final String GOODS_URL = "http://10.0.0.14/jxc/chonourDV/getPtype.asp?TypeID=%1$s&sessionId=%2$s&keyWord=%3$s";
		
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
		
		private List<Goods> resultList = new ArrayList<Goods>();
		
		public List<Goods> getList(){
			return resultList;
		}
		
		@Override
		protected Integer doInBackground(Integer... params) {
			if(params.length < 1)
				return FAILED;
			int action = params[0];
			
			/*
			 * 查询子类的话，先判断在 goodsMap 中是否有以 curTypeId 为key的value
			 * 没有，就从网络中获取
			 */
			if(action == CATEGORY){
				if(goodsMap.containsKey(curTypeId)){
					resultList = goodsMap.get(curTypeId);
					return OK;
				}
			}
			
			handler.sendEmptyMessage(WORKING);
			
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
				resultList = goodsJSON.parseToList(result, "LIST");
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