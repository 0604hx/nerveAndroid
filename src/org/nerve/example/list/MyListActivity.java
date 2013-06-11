package org.nerve.example.list;

import java.util.ArrayList;
import java.util.List;

import org.nerve.R;
import org.nerve.android.NerveActivity;
import org.nerve.android.annotation.Acvitity;
import org.nerve.android.annotation.ViewOnId;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

@Acvitity(layout=R.layout.list_demo)
public class MyListActivity extends NerveActivity implements 
		OnClickListener, OnFocusChangeListener{

	@ViewOnId(id=R.id.addBtn, clickListener="this")
	private Button addBtn;
	@ViewOnId(id=R.id.listView)
	private ListView listView;

	private List<User> userList;
	
	private UserListAdapter adapter;
	
	@Override
	public void onClick(View v) {
		userList.add(new User("" + System.currentTimeMillis()));
		
		adapter.notifyDataSetChanged();
		
		System.out.println("当前listview childCount=" + listView.getChildCount() + ", userlist size= " + userList.size());
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		EditText et = (EditText)v;
		System.out.println("view 的值为：" + et.getText().toString());
		
		int index = Integer.valueOf(et.getTag().toString());
		userList.get(index).password = et.getText().toString();
		
		System.out.println("当前listview childCount=" + listView.getChildCount() + ", userlist size= " + userList.size());
	}
	
	@Override
	protected void initData() {
		userList = new ArrayList<User>();
	}
	
	@Override
	protected void initUI() {
		adapter = new UserListAdapter(this);
		listView.setAdapter(adapter);
	}
	
	
	
	public class UserListAdapter extends BaseAdapter{
		protected LayoutInflater layoutInf;
    	
    	public UserListAdapter(Context context){
    		layoutInf = LayoutInflater.from(context);
    	}
    	
		@Override
		public int getCount() {
			return userList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return userList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			User user = userList.get(arg0);
			
			if(convertView == null)
				convertView = layoutInf.inflate(R.layout.list_demo_item, null);
			
			((TextView)convertView.findViewById(R.id.name)).setText(user.name);
			EditText et = (EditText)convertView.findViewById(R.id.edit);
			et.setText(user.password);
			et.setTag(arg0);
			et.setOnFocusChangeListener(MyListActivity.this);
			
			return convertView;
		}
		
	}

}
