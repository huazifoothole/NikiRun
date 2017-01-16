package com.example.nikirun;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class UserSoicalInfo extends BmobObject {
	private BmobRelation friends;
	private RunUser runner;

	public BmobRelation getFriends() {
		return friends;
	}

	public void setFriends(BmobRelation friends) {
		this.friends = friends;
	}

	public RunUser getRunner() {
		return runner;
	}

	public void setRunner(RunUser runner) {
		this.runner = runner;
	}
}
