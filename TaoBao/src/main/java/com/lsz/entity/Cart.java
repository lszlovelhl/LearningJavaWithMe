package com.lsz.entity;

/**
 *@Author:lsz1310225074@iCloud.com
 *@Description: TODO
 *@DateTime: 2022/1/19
 *@Params:
 *@Return:
 */

import java.io.Serializable;
import java.util.Date;

public class Cart implements Serializable{
	private int userid;
	private int goodsId;
	private String goodsName;
	private int goodsNum;
	private Date purchaseTime;

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public int getGoodsNum() {
		return goodsNum;
	}
	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}
	public Date getPurchaseTime() {
		return purchaseTime;
	}
	public void setPurchaseTime(Date purchaseTime) {
		this.purchaseTime = purchaseTime;
	}
	@Override
	public String toString() {
		return "用户ID" + userid + ",商品ID" + goodsId + ",商品名称：" + goodsName + ", 商品数量：" + goodsNum + ", 购买时间=" + purchaseTime;
	}
	
	
}
