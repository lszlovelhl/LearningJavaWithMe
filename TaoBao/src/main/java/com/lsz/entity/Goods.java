package com.lsz.entity;

/**
 *@Author:lsz1310225074@iCloud.com
 *@Description: TODO
 *@DateTime: 2022/1/19
 *@Params:
 *@Return:
 */

import java.io.Serializable;

public class Goods implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String cname;
	private String cno;
	private int price;
	private int num;
	private boolean state;
	
	
	
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCno() {
		return cno;
	}
	public void setCno(String cno) {
		this.cno = cno;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "商品id：" + id + ", 商品名称：" + cname + ", 商品编码：" + cno + ", 商品价格：" + price + ", 商品数量：" + num
				+ ", 商品状态：" + state;
	}
	
	
}
