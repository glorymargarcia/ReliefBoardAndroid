package com.stratpoint.reliefboard.listener;

import com.stratpoint.reliefboard.model.PostObjectPOJO;


public interface PostActionListener {
	public void onResponseClick(PostObjectPOJO post, int position);
}
