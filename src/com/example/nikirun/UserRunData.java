package com.example.nikirun;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class UserRunData extends BmobObject {
 
	double runDistance;
	private String  runTime;//跑步花费时间
	private String 	runDate;//跑步日期
	private String  runData;
	private RunUser runUser;
	
	
	public double getRunDistance() {
		return runDistance;
	}
	public void setRunDistance(double runDistance) {
		this.runDistance = runDistance;
	}
	public String getRunTime() {
		return runTime;
	}
	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}
	public String getRunDate() {
		return runDate;
	}
	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}
	public String getRunData() {
		return runData;
	}
	public void setRunData(String runData) {
		this.runData = runData;
	}
	 
	public RunUser getRunUser() {
		return runUser;
	}
	public void setRunUser(RunUser runUser) {
		this.runUser = runUser;
	}
 

}
