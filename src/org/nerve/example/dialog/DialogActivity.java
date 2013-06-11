package org.nerve.example.dialog;

import org.nerve.R;
import org.nerve.android.NerveActivity;
import org.nerve.android.annotation.Acvitity;
import org.nerve.android.annotation.ViewOnId;
import org.nerve.android.ui.dialog.ConfirmDialog;
import org.nerve.android.ui.dialog.SingleInputDialog;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@Acvitity(layout=R.layout.dialog_demo)
public class DialogActivity extends NerveActivity implements OnClickListener{
	
	@ViewOnId(id=R.id.confirmB, clickListener="this")
	private Button confirmBtn;
	@ViewOnId(id=R.id.inputB, clickListener="this")
	private Button 	inputBtn;
	@ViewOnId(id=R.id.radioB, clickListener="this")
	private Button radioBtn;

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.confirmB:
			ConfirmDialog cfd = new ConfirmDialog(this);
			cfd.setTitle("确定删除");
			cfd.setMessage("你确定删除这个订单吗，删除后不能恢复！");
			cfd.show();
			break;
		case R.id.inputB:
			SingleInputDialog sid = new SingleInputDialog(this);
			sid.setDefaultText("http://");
			sid.setTitle("服务器地址");
			sid.setOkButtonText("确定");
			sid.setCancelButtonText("使用默认网址");
			sid.show();
			break;
		case R.id.radioB:
			break;
		}
	}
}