package com.barbara.social;

import java.util.HashMap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.Toast;
import cn.sharesdk.douban.Douban;
import cn.sharesdk.evernote.Evernote;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.kaixin.KaiXin;
import cn.sharesdk.netease.microblog.NetEaseMicroBlog;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.sohu.microblog.SohuMicroBlog;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.youdao.YouDao;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.barbara.dowhat.R;
import com.umeng.analytics.MobclickAgent;

public class SocialActivity extends SherlockActivity implements
		PlatformActionListener, Callback, OnClickListener {
	protected Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		handler = new Handler(this);

		findViewById(R.id.ctvSw).setOnClickListener(this);
		this.findViewById(R.id.ctvTc).setOnClickListener(this);
		this.findViewById(R.id.ctvRr).setOnClickListener(this);
		this.findViewById(R.id.ctvQz).setOnClickListener(this);
		this.findViewById(R.id.ctvDb).setOnClickListener(this);
		this.findViewById(R.id.ctvNemb).setOnClickListener(this);
		this.findViewById(R.id.ctvEn).setOnClickListener(this);
		this.findViewById(R.id.ctvSoHu).setOnClickListener(this);
		this.findViewById(R.id.ctvKaiXin).setOnClickListener(this);
		this.findViewById(R.id.ctvYouDao).setOnClickListener(this);

		Platform[] platforms = ShareSDK.getPlatformList(this);
		for (Platform plat : platforms) {
			if (!plat.isValid()) {
				continue;
			}

			CheckedTextView ctv = getView(plat);
			if (ctv != null) {
				ctv.setChecked(true);
				String userName = plat.getDb().get("nickname"); // getAuthedUserName();
				if (userName == null || userName.length() <= 0
						|| "null".equals(userName)) {
					// 如果平台已经授权却没有拿到帐号名称，则自动获取用户资料，以获取名称
					userName = getPlatformName(plat);
					plat.setPlatformActionListener(this);
					plat.showUser(null);
				}
				ctv.setText(userName);
			}
		}

	}
	
	// 使用快捷分享完成分享（请务必仔细阅读位于SDK解压目录下Docs文件夹中OnekeyShare类的JavaDoc）
		private void showShare(boolean silent, String platform) {
			OnekeyShare oks = new OnekeyShare();
			// 分享时Notification的图标和文字
			oks.setNotification(R.drawable.note48, "分享");
			// address是接收人地址，仅在信息和邮件使用
//			oks.setAddress("12345678901");
			// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
			oks.setTitle("doWhat状态同步&分享");
			// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
			oks.setTitleUrl("http://myguet.duapp.com/home.html");
			// text是分享文本，所有平台都需要这个字段
			oks.setText("测试测试");
			// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//			oks.setImagePath(getApplicationContext().getResources().get ("@drawable/note"));
			// imageUrl是图片的网络路径，新浪微博、人人网、QQ空间、
			// 微信的两个平台、Linked-In支持此字段
//			oks.setImageUrl("http://sharesdk.cn/ rest.png");
			// url仅在微信（包括好友和朋友圈）中使用
//			oks.setUrl("http://sharesdk.cn");
			// appPath是待分享应用程序的本地路劲，仅在微信中使用
//			oks.setAppPath(MainActivity.TEST_IMAGE);
			// comment是我对这条分享的评论，仅在人人网和QQ空间使用
			oks.setComment("来自doWhat");
			// site是分享此内容的网站名称，仅在QQ空间使用
			oks.setSite(getApplicationContext().getResources().getString(R.string.app_name));
			// siteUrl是分享此内容的网站地址，仅在QQ空间使用
//			oks.setSiteUrl("http://sharesdk.cn");
			// venueName是分享社区名称，仅在Foursquare使用
//			oks.setVenueName("Southeast in China");
			// venueDescription是分享社区描述，仅在Foursquare使用
//			oks.setVenueDescription("This is a beautiful place!");
			// latitude是维度数据，仅在新浪微博、腾讯微博和Foursquare使用
//			oks.setLatitude(23.122619f);
			// longitude是经度数据，仅在新浪微博、腾讯微博和Foursquare使用
//			oks.setLongitude(113.372338f);
			// 是否直接分享（true则直接分享）
			oks.setSilent(silent);
			// 指定分享平台，和slient一起使用可以直接分享到指定的平台
			if (platform != null) {
			        oks.setPlatform(platform);
			}
			// 去除注释可通过OneKeyShareCallback来捕获快捷分享的处理结果
			// oks.setCallback(new OneKeyShareCallback());
			//通过OneKeyShareCallback来修改不同平台分享的内容
//			oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());

			oks.show(getApplicationContext());
		}

	public void onClick(View v) {

		Platform plat = getPlatform(v.getId());
		CheckedTextView ctv = (CheckedTextView) v;
		if (plat == null) {
			ctv.setChecked(false);
			ctv.setText(R.string.not_yet_authorized);
			return;
		}

		if (plat.isValid()) {//有效，
			plat.removeAccount();
			ctv.setChecked(false);
			ctv.setText(R.string.not_yet_authorized);
			return;
		}

		plat.setPlatformActionListener(this);
		plat.showUser(null);
	}

	private Platform getPlatform(int vid) {
		String name = null;
		switch (vid) {
		case R.id.ctvSw:
			name = SinaWeibo.NAME;
			break;
		case R.id.ctvTc:
			name = TencentWeibo.NAME;
			break;
		case R.id.ctvRr:
			name = Renren.NAME;
			break;
		case R.id.ctvQz:
			name = QZone.NAME;
			break;
		case R.id.ctvDb:
			name = Douban.NAME;
			break;
		case R.id.ctvEn:
			name = Evernote.NAME;
			break;
		case R.id.ctvNemb:
			name = NetEaseMicroBlog.NAME;
			break;
		case R.id.ctvSoHu:
			name = SohuMicroBlog.NAME;
			break;
		case R.id.ctvKaiXin:
			name = KaiXin.NAME;
			break;
		case R.id.ctvYouDao:
			name = YouDao.NAME;
			break;
		}

		if (name != null) {
			return ShareSDK.getPlatform(getApplicationContext(), name);
		}
		return null;
	}

	private CheckedTextView getView(Platform plat) {
		if (plat == null) {
			return null;
		}

		String name = plat.getName();
		if (name == null) {
			return null;
		}

		View v = null;
		if (SinaWeibo.NAME.equals(name)) {
			v = this.findViewById(R.id.ctvSw);
		} else if (TencentWeibo.NAME.equals(name)) {
			v = this.findViewById(R.id.ctvTc);
		} else if (Renren.NAME.equals(name)) {
			v = this.findViewById(R.id.ctvRr);
		} else if (QZone.NAME.equals(name)) {
			v = this.findViewById(R.id.ctvQz);
		} else if (Douban.NAME.equals(name)) {
			v = this.findViewById(R.id.ctvDb);
		} else if (Evernote.NAME.equals(name)) {
			v = this.findViewById(R.id.ctvEn);
		} else if (NetEaseMicroBlog.NAME.equals(name)) {
			v = this.findViewById(R.id.ctvNemb);
		} else if (SohuMicroBlog.NAME.equals(name)) {
			v = this.findViewById(R.id.ctvSoHu);
		} else if (KaiXin.NAME.equals(name)) {
			v = this.findViewById(R.id.ctvKaiXin);
		} else if (YouDao.NAME.equals(name)) {
			v = this.findViewById(R.id.ctvYouDao);
		} 
		if (v == null) {
			return null;
		}

		if (!(v instanceof CheckedTextView)) {
			return null;
		}

		return (CheckedTextView) v;
	}

	private String getPlatformName(Platform plat) {
		if (plat == null) {
			return null;
		}

		String name = plat.getName();
		if (name == null) {
			return null;
		}

		int res = 0;
		if (SinaWeibo.NAME.equals(name)) {
			res = R.string.sinaweibo;
		} else if (TencentWeibo.NAME.equals(name)) {
			res = R.string.tencentweibo;
		} else if (Renren.NAME.equals(name)) {
			res = R.string.renren;
		} else if (QZone.NAME.equals(name)) {
			res = R.string.qzone;
		} else if (Douban.NAME.equals(name)) {
			res = R.string.douban;
		} else if (Evernote.NAME.equals(name)) {
			res = R.string.evernote;
		} else if (NetEaseMicroBlog.NAME.equals(name)) {
			res = R.string.neteasemicroblog;
		} else if (SohuMicroBlog.NAME.equals(name)) {
			res = R.string.sohumicroblog;
		} else if (KaiXin.NAME.equals(name)) {
			res = R.string.kaixin;
		} else if (YouDao.NAME.equals(name)) {
			res = R.string.youdao;
		} 
		if (res == 0) {
			return name;
		}

		return this.getResources().getString(res);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_social, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.social_send:
			showShare(false, null);
			break;
			
		}
		return true;
	}

	
	public void onComplete(Platform plat, int action,
			HashMap<String, Object> res) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = plat;
		handler.sendMessage(msg);
	}

	public void onError(Platform plat, int action, Throwable t) {
		t.printStackTrace();

		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = plat;
		handler.sendMessage(msg);
	}

	public void onCancel(Platform plat, int action) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = plat;
		handler.sendMessage(msg);
	}

	@Override
	public boolean handleMessage(Message msg) {

		Platform plat = (Platform) msg.obj;
		String text = actionToString(msg.arg2);
		switch (msg.arg1) {
		case 1: { // 成功
			text = plat.getName() + " completed at " + text;
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();
		}
			break;
		case 2: { // 失败
			text = plat.getName() + " caught error at " + text;
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		case 3: { // 取消
			text = plat.getName() + " canceled at " + text;
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		}

		CheckedTextView ctv = getView(plat);
		if (ctv != null) {
			ctv.setChecked(true);
			String userName = plat.getDb().get("nickname"); // getAuthedUserName();
			if (userName == null || userName.length() <= 0
					|| "null".equals(userName)) {
				userName = getPlatformName(plat);
			}
			ctv.setText(userName);
		}
		return false;
	}

	public static String actionToString(int action) {
		switch (action) {
		case Platform.ACTION_AUTHORIZING:
			return "ACTION_AUTHORIZING";// 认证
		case Platform.ACTION_GETTING_FRIEND_LIST:
			return "ACTION_GETTING_FRIEND_LIST";
		case Platform.ACTION_FOLLOWING_USER:
			return "ACTION_FOLLOWING_USER";
		case Platform.ACTION_SENDING_DIRECT_MESSAGE:
			return "ACTION_SENDING_DIRECT_MESSAGE";
		case Platform.ACTION_TIMELINE:
			return "ACTION_TIMELINE";
		case Platform.ACTION_USER_INFOR:
			return "ACTION_USER_INFOR";
		case Platform.ACTION_SHARE:
			return "ACTION_SHARE";
		default: {
			return "UNKNOWN";
		}
		}
	}


}
