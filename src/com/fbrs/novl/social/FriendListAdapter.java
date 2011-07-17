package com.fbrs.novl.social;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class FriendListAdapter extends SimpleAdapter{

	private static final String[] textmap = {"Text"};
	private static final int[] textm = {R.id.itemText};
	ArrayList<String> items;

	private HashMap<String, Integer> pics;

	public FriendListAdapter(Context context, int textViewResourceId,
			String[] objects, ArrayList<HashMap<String,String>> groupData) {	
		super(context, groupData, textViewResourceId, textmap,  textm );
		//ArrayList<HashMap<String,String>> groupData = new ArrayList<HashMap<String,String>>();
		items = new ArrayList<String>();
		for(String o:objects)
		{
			items.add(o);
			//HashMap<String,String> temp = new HashMap<String,String>();
			//temp.put("Text",o);
			//groupData.add(temp);
		}

		pics = new HashMap<String, Integer>();
		pics.put("busy", R.drawable.busy);
		pics.put("ingame", R.drawable.ingame);
		pics.put("offline", R.drawable.offline);
		pics.put("online", R.drawable.online);
		pics.put("onphone", R.drawable.onphone);
		pics.put("ok", R.drawable.ok);
	}
	@Override
	public View getView(int groupPosition, View convertView, ViewGroup parent)
	{
		View v = super.getView(groupPosition, convertView, parent);
		String friend = items.get(groupPosition);
		String[] imgs = friend.split("-");
		TextView t = (TextView) v.findViewById(R.id.itemText);
		ImageView i1 =  (ImageView) v.findViewById(R.id.itemImg);
		ImageView i2 =  (ImageView) v.findViewById(R.id.itemImg2);
		t.setText(imgs[0]);
		if(imgs.length >= 3) {
			if(!imgs[2].equals("null") && !imgs[2].equals(""))
				i1.setImageResource(pics.get(imgs[2]));
			if(!imgs[1].equals("null") && !imgs[1].equals(""))
				i2.setImageResource(pics.get(imgs[1]));
		}
		return v;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}


}
