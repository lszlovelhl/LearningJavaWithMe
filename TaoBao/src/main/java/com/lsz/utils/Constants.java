package com.lsz.utils;

/*
 	买家： 登录，注册，购买商品，添加进购物车，查看购物车
 	卖家： 增删改查商品
 * */
public class Constants {
	public static final int SERVER_PORT = 8000;		//端口号
	public static final int OPER_LOGIN = 1;			//登录指令
	public static final int OPER_REGIST = 2;		//注册指令
	public static final int OPER_QUERYNAME = 3;		//按名称查找指令
	public static final int OPER_ALLGOODS = 4;		//查看所有商品指令	
	public static final int OPER_Purchase = 5;		//购买指令	
	public static final int OPER_CHECKCART = 6;		//查看购物车指令	
	public static final int OPER_CLEARSTATE = 7;	//重置商品state为0指令	
	
	public static final int OPER_ADDGOODS = 8;		//卖家增加商品
	public static final int OPER_DELGOODS = 9;		//卖家删除商品
	public static final int OPER_UPDATEGOODS = 10;		//卖家修改商品
}
