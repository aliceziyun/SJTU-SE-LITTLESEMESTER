package com.example.myapplication.db.entity;

import java.util.ArrayList;
import java.util.List;
import com.amap.api.location.AMapLocation;

/**
 * 用于记录一条轨迹，包括起点、终点、轨迹中间点、距离、耗时、平均速度、时间、所属用户
 */
public class PathRecord {
	private AMapLocation startPoint;
	private AMapLocation endPoint;
	private List<AMapLocation> pathLinePoints = new ArrayList<AMapLocation>();
	private String date;
	private int id = 0;
	private String userId;

	public PathRecord() {

	}

	public int getId() {return id;}

	public void setId(int id){this.id = id;}

	public String getUserId(){return userId;}

	public void setUserId(String userId) {this.userId = userId;}

	public AMapLocation getStartpoint() {
		return startPoint;
	}

	public void setStartpoint(AMapLocation startpoint) {
		this.startPoint = startpoint;
	}

	public AMapLocation getEndpoint() {
		return endPoint;
	}

	public void setEndpoint(AMapLocation endpoint) {
		this.endPoint = endpoint;
	}

	public List<AMapLocation> getPathline() {
		return pathLinePoints;
	}

	public void setPathline(List<AMapLocation> pathline) {
		this.pathLinePoints = pathline;
	}


	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void addpoint(AMapLocation point) {
		pathLinePoints.add(point);
	}

	@Override
	public String toString() {
		StringBuilder record = new StringBuilder();
		record.append("recordSize:" + getPathline().size() + ", ");
		return record.toString();
	}
}
