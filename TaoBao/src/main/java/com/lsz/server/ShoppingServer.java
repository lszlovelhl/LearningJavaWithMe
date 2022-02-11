package com.lsz.server;

/**
 *@Author:lsz1310225074@iCloud.com
 *@Description: TODO
 *@DateTime: 2022/1/19
 *@Params:
 *@Return:
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.lsz.entity.Cart;
import com.lsz.entity.Goods;
import com.lsz.utils.Constants;
import com.mchange.v2.c3p0.ComboPooledDataSource;

class ServerTask implements Runnable {

	private Socket socket;
	
	static QueryRunner qr = new QueryRunner(new ComboPooledDataSource());
	
	public ServerTask(Socket socket) {
		this.socket = socket;
	}
	
	
	@Override
	public void run() {
		try {
			//处理客户端发送过来的数据(接受的数据包含：指令类型 + 业务数据)
			while(true) {
				InputStream in = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line = br.readLine();	//接收了客户端发送过来的拼接信息

				if(line != null) {
					String[] data = line.split("#@");
					String oper = data[0];			//读取第一个操作看是什么操作指令
					
					switch(Integer.parseInt(oper)) {
					case Constants.OPER_LOGIN:		//登录指令
						if(data.length != 4) {
							writeToClient("参数个数不正确");
							
						}else {
							String userName = data[1];
							String userPsw = data[2];
							String pswKey = data[3];
							String sql = "select role from user where username = ? and userpass = AES_ENCRYPT(?,?)";
//							String sql = "select role from user where username =' " + "?" + "'and userpass = AES_ENCRYPT(?,?)";
							try {
								Object query = qr.query(sql, new ScalarHandler(), new Object[] {userName, userPsw, pswKey});
								
								if(query==null) {
									writeToClient("-1");
								}else {
									writeToClient(query+"");
								}
								//writeToClient(query == null ? "-1" : query+"");		//将查询结果返回给客户端
								
							} catch (SQLException e) {
								e.printStackTrace();
								System.out.println("sql语句有错，登录查询失败");
							}
						}
						break;
						
					//注册指令,返回-1，则昵称重复，重新注册，返回0则注册成功	
					case Constants.OPER_REGIST:		
						
						String userName = data[1];
						String userPsw = data[2];
						String pswKey = data[3];
						String userRole = data[4];

						String str = "SELECT COUNT(*) FROM USER WHERE username = ?";
//						String str = "SELECT COUNT(*) FROM USER WHERE username = '" + "?'";	//查看是否有名字相同的，若返回count＞0，则不给注册，0则注册
						try {
							
							long query = (long) qr.query(str, new ScalarHandler(), new Object[] {userName});
							//System.out.println("querycount: " + query);
							if(query != 0) {
								
								writeToClient("-1");
								//System.out.println("昵称重复，请重新注册");
							}else {
								String sql = "insert into user(username, userpass, role) values(?, AES_ENCRYPT(?,?),?)";
								try {
									qr.update(sql, new Object[] {userName, userPsw, pswKey, userRole});
									
									writeToClient(query+"");		//返回给客户端查询结果0，表示昵称未重复，注册成功
									
								} catch (SQLException e) {
									e.printStackTrace();
									System.out.println("sql语句有错，注册失败");
								}
							}
							
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						break;
						
					//查看所有商品指令	
					case Constants.OPER_ALLGOODS:		
						String sql = "select * from commodity";
						if(data.length == 3) {
							String price = data[1];		
							String order = data[2];		
							sql += " order by " + price + " " + order;
						}
						//查看所有商品
						try {
							List<Goods> list = (List<Goods>)qr.query(sql, new BeanListHandler(Goods.class));
							writeToClient(list);
							
						} catch (SQLException e) {
							e.printStackTrace();
							writeToClient("查询失败");
						}
						
						
						break;
						
					//按商品名称查找指令
					case Constants.OPER_QUERYNAME: 			
						try {
							sql = "select * from commodity";
							String fieldName = data[1];
							String fieldValue = data[2];
							
							if("cname".equals(fieldName)) {
								sql += " where cname like ?";
							} else if("cno".equals(fieldName)) {
								sql += " where cno like ?";
							}
							List<Goods> list = (List<Goods>) qr.query(sql, new BeanListHandler(Goods.class)
									, new Object[] {"%" + fieldValue + "%"});

							writeToClient(list);
							
						} catch (SQLException e) {
							e.printStackTrace();
							writeToClient("查询失败");
						}
						break;	
						
					//商品购买指令	
					case Constants.OPER_Purchase:		
						if(data.length != 4) {
							writeToClient("输入的参数个数不对");
						}else {
							String cname = data[1];
							String cnum = data[2];
							String time = data[3];
							String delSql = "UPDATE commodity SET num=num-? ,state = 1 WHERE cname = ?";	//购买时先删除goods里的数量，并且设置state为1，表示已购买过
							String addSql = "INSERT INTO shoppingcart "
									+ "(goodsName, goodsNum, purchaseTime) "
									+ "VALUES(?,  ?, ?);";	//购买时添加购物车里某物品的信息，若物品存在则，数量加1
							
							//String stateSql = "UPDATE goods SET state = 1 WHERE cname = ?";//设置state为1，表示已购买过
							try {
								int update = qr.update(delSql, new Object[] {cnum, cname});
								int update2 = qr.update(addSql,new Object[] {cname, cnum, time});
							//	int update3 = qr.update(delSql, new Object[] {cname});
								//System.out.println("测试statesql：" + update3);
								writeToClient("");	//给客户端一个响应，没有意义
							} catch (SQLException e) {
								e.printStackTrace();
								System.out.println("购买失败");
							}
						}
						break;
						
					//查看购物车
					case Constants.OPER_CHECKCART:			
						sql = "select * from shoppingcart";
						try {
							List<Cart> list = (List<Cart>)qr.query(sql, new BeanListHandler(Cart.class));
							writeToClient(list);
							
						} catch (SQLException e) {
							e.printStackTrace();
							writeToClient("查询失败");
						}
						
						break;
					
					//重置商品state为0 
					case Constants.OPER_CLEARSTATE:
						sql = "UPDATE commodity SET state = 0";
						try {
							qr.update(sql);
							writeToClient("");
							
							socket.close();	// 不传数据了 就把客户端的给关了
							//ss.close  // 暂时不把服务器关了，以免重复启动
						} catch (SQLException e) {
							e.printStackTrace();
							System.out.println("重置商品为0，失败~~~");
						}
						break;
					
					//卖家添加商品
					case Constants.OPER_ADDGOODS:
						if(data.length != 5) {
							writeToClient("传入的参数个数不正确");
						}else {
							String cNo = data[1];
							String cName = data[2];
							String cPrice = data[3];
							String cNum = data[4];
							
							String checkSql = "SELECT COUNT(*) FROM commodity WHERE cName = ?";
							try {
								long query = (long) qr.query(checkSql, new ScalarHandler(), new Object[] {cName});
								
								if(query != 0) {
									writeToClient("-1");//商品已经存在，不可以重复添加
								}else {
									String addSql = "INSERT INTO commodity (cno, cname, price, num) VALUES(?, ?, ?, ?)";//sql添加商品
									String update = qr.update(addSql,new Object[] {cNo, cName, cPrice, cNum}) + "";
									
									writeToClient(update);//告诉客户端，已经添加成功，返回影响行数update=1
								}
								
								
							} catch (SQLException e) {
								e.printStackTrace();
								System.out.println("添加商品失败");
							}
						}
						break;
						
					//卖家删除商品
					case Constants.OPER_DELGOODS:
						String filedName = data[1];
						String filedValue = data[2];

						if(data.length != 3) {
							writeToClient("传入参数个数不正确");
						}else {
							/*sql = "SELECT cno,cname FROM goods";
							try {
								List<Goods> goods = (List<Goods>) qr.query(sql, new BeanListHandler(Goods.class));
								for (Goods good : goods) {
									System.out.println(good.getCno() +"，名称："+ good.getCname());//测试
									
									if(!(good.getCno().equals(filedValue)) || (good.getCname().equals(filedValue))) {
										writeToClient("0");	
									}else {*/
										if("cNo".equals(filedName)) {	//按编码删除
											sql = "DELETE FROM commodity WHERE cno = ?";
											try {
												qr.update(sql,new Object[] {filedValue});
												writeToClient("1");
											} catch (SQLException e) {
												e.printStackTrace();
												System.out.println("按编码删除失败！！！");
											}
											
										}else if("cName".equals(filedName)) {	//按商品名删除
											sql = "DELETE FROM commodity WHERE cname = ?";
											try {
												qr.update(sql,new Object[] {filedValue});
												writeToClient("2");
											} catch (SQLException e) {
												e.printStackTrace();
												System.out.println("按商品名删除失败！！！");
											}
										}
									/*}
								}
								
							} catch (SQLException e1) {
								e1.printStackTrace();
								System.out.println("查询cno失败！！！");
							}*/
							
							/*
							if(!("cNo".equals(filedValue) || "cName".equals(filedValue) )) {	//判断该商品是不是存在的
								//System.out.println("该商品不存在！！！");
								writeToClient("0");	
								
							}else {
								if("cNo".equals(filedName)) {	//按编码删除
									sql = "DELETE FROM goods WHERE cno = ?";
									try {
										qr.update(sql,new Object[] {filedValue});
										writeToClient("1");
									} catch (SQLException e) {
										e.printStackTrace();
										System.out.println("按编码删除失败！！！");
									}
									
								}else if("cName".equals(filedName)) {	//按商品名删除
									sql = "DELETE FROM goods WHERE cname = ?";
									try {
										qr.update(sql,new Object[] {filedValue});
										writeToClient("2");
									} catch (SQLException e) {
										e.printStackTrace();
										System.out.println("按商品名删除失败！！！");
									}
								}
							}*/
						}

						break;
						
					//卖家修改商品
					case Constants.OPER_UPDATEGOODS:
						if(data.length != 4) {
							writeToClient("传入的参数个数不对");
						}else {
							String cname = data[1];		//要修改的商品名
							filedName = data[2];		//要修改的字段名
							filedValue = data[3];		//修改的新值
							sql = "update commodity set " + filedName + " = ? where cname = ?";
							try {
								int update = qr.update(sql, new Object[] {filedValue, cname});
								writeToClient("1");		//给客户端发送响应1，表示修改成功
								/*if(update == 0) {
									throw new CnameNotFoundException();		//手动抛自定义异常
								}else {
									
								}
								*/
							} catch (SQLException e) {
								e.printStackTrace();
								System.out.println("修改商品失败！！！");
							}/* catch (CnameNotFoundException e) {
								e.printStackTrace();
								writeToClient("该商品不存在！！！");
							}*/
						}
						
					break;		
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("客户端推出或服务端操作失败");
		}
				
	}
	
	
	
	
	//向客户端发送数据
	private void writeToClient(String content) throws IOException {
		OutputStream out = socket.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		
		if(!"".equals(content)) {
			bw.write(content);
		}
		bw.write("\n");
		bw.flush();
	}
	
	private void writeToClient(Object content) throws IOException {
		OutputStream out = socket.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		
		oos.writeObject(content);
		
	}
}





//服务端主方法
public class ShoppingServer {

	static ExecutorService threadPool = Executors.newFixedThreadPool(20);
	public static void main(String[] args) {
		try {
			System.out.println("服务器启动成功");
			ServerSocket ss = new ServerSocket(Constants.SERVER_PORT);
			
			while(true) {
				Socket socket = ss.accept();
				ServerTask task = new ServerTask(socket);
				threadPool.submit(task);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("服务器启动失败");
		}
	}

}
