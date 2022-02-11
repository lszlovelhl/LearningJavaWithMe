package com.lsz.client;

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
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import com.lsz.entity.Cart;
import com.lsz.entity.Goods;
import com.lsz.utils.Constants;

public class ShoppingClient {

    public static void main(String[] args) {
        System.out.println("********************************");
        System.out.println("*****                      *****");
        System.out.println("*****                      *****");
        System.out.println("*****   欢迎光临淘宝购物商城   *****");
        System.out.println("*****                      *****");
        System.out.println("*****                      *****");
        System.out.println("*****                      *****");
        System.out.println("********************************");

        try {
            while (true) {
                System.out.println("请输入： 登录(A)  注册(R)  退出购物(T)");
                Scanner in = new Scanner(System.in);
                String line = in.nextLine();

                //实现用户登录功能
                if ("a".equalsIgnoreCase(line)) {

                    int role = -1; //用户角色
                    while (true) {
                        System.out.print("请输入用户姓名：");
                        String userName = in.nextLine();
                        System.out.print("请输入用户密码：");
                        String userPsw = in.nextLine();
                        System.out.print("请输入私钥");
                        String pswKey = in.nextLine();

                        role = login(userName, userPsw, pswKey);    //获取返回的用户角色

                        if (role != -1) {
                            while (true) {        //根据用户角色不同，进入不同的菜单选项
                                if (role == 1) {
                                    System.out.println("欢迎买家，您好！请输入： 查看商品(L)  购买商品(P)  查看购物车(C)  返回上一级(B)");
                                    line = in.nextLine();
                                    if ("l".equalsIgnoreCase(line)) {        //查看商品
                                        while (true) {
                                            System.out.println("按条件查看(O)  查看全部(A)  返回上一级(B)");
                                            line = in.nextLine();
                                            if ("o".equalsIgnoreCase(line)) {        //按条件查看
                                                while (true) {
                                                    System.out.println("请选择查看条件：按名称查看(N)  按编号查看(P)  返回上一级(B)");
                                                    line = in.nextLine();
                                                    if ("n".equalsIgnoreCase(line)) {        //按名称查看
                                                        System.out.print("请输入商品名称：");
                                                        String cname = in.nextLine();
                                                        findOrderGoods("cname", cname);
                                                    } else if ("p".equalsIgnoreCase(line)) {
                                                        System.out.print("请输入商品编号：");
                                                        String cno = in.nextLine();
                                                        findOrderGoods("cno", cno);

                                                    } else if ("b".equalsIgnoreCase(line)) {            //返回上一级
                                                        break;
                                                    }
                                                }


                                            }
                                            if ("a".equalsIgnoreCase(line)) {        //查看全部
                                                findAllGoods();
                                            }
                                            if ("b".equalsIgnoreCase(line)) {        //返回上一级
                                                break;
                                            }
                                        }

                                    }

                                    if ("p".equalsIgnoreCase(line)) {        //购买商品
                                        while (true) {
                                            System.out.print("请输入您想购买的商品(结束购买请按B)： ");
                                            String cname = in.nextLine();

                                            if (!"b".equalsIgnoreCase(cname)) {
                                                System.out.print("请输入购买的数量：");
                                                String cnum = in.nextLine();

                                                Purchase(cname, cnum);
                                            } else {
                                                break;
                                            }

                                        }


                                    }

                                    if ("c".equalsIgnoreCase(line)) {        //查看购物车
                                        checkCart();
                                    }
                                    if ("b".equalsIgnoreCase(line)) {        //返回上一级
                                        break;
                                    }


                                    //卖家的增删改查商品
                                } else if (role == 2) {

                                    System.out.println("欢迎卖家，您好，请输入： 增加商品(A)  删除商品(D)  修改商品(U)  查看商品(C)  返回上一级(B)");
                                    line = in.nextLine();

                                    if ("a".equalsIgnoreCase(line)) {        //增加商品
                                        System.out.print("请输入要添加的商品编号：");
                                        String cNo = in.nextLine();
                                        System.out.print("请输入要添加的商品名称：");
                                        String cName = in.nextLine();
                                        System.out.print("请输入添加商品的价格：");
                                        String cPrice = in.nextLine();
                                        System.out.print("请输入添加商品的库存：");
                                        String cNum = in.nextLine();

                                        //添加成功为1，不成功为-1

                                        int isadd = addGoods(cNo, cName, cPrice, cNum);

                                        if (isadd == -1) {
                                            System.out.println("商品已经存在，请勿重复添加！！！");
                                        } else {
                                            System.out.println("添加商品成功☺");
                                        }
                                    }

                                    if ("d".equalsIgnoreCase(line)) {        //删除商品
                                        System.out.println("请输入：按编号删除(B)  按商品名称删除(N)");
                                        line = in.nextLine();
                                        if ("b".equalsIgnoreCase(line)) {
                                            System.out.println("请输入要删除的商品编号：");
                                            line = in.nextLine();
                                            delGoods("cNo", line);
                                        }
                                        if ("n".equalsIgnoreCase(line)) {
                                            System.out.println("请输入要删除的商品名称：");
                                            line = in.nextLine();
                                            delGoods("cName", line);
                                        }

                                    }

                                    if ("u".equalsIgnoreCase(line)) {        //修改商品信息
                                        System.out.print("请输入要修改的商品名：");
                                        String cname = in.nextLine();
                                        System.out.println("请输入：修改编号(B)  修改名称(N)  修改价格(P)  修改数量(M)");
                                        line = in.nextLine();
                                        if ("b".equalsIgnoreCase(line)) {
                                            System.out.print("请输入新的编号：");
                                            String newCNo = in.nextLine();
                                            updateGoods(cname, "cno", newCNo);
                                        }
                                        if ("n".equalsIgnoreCase(line)) {
                                            System.out.print("请输入新的名称：");
                                            String newCName = in.nextLine();
                                            updateGoods(cname, "cname", newCName);
                                        }
                                        if ("p".equalsIgnoreCase(line)) {
                                            System.out.print("请输入新的价格：");
                                            String newCPrice = in.nextLine();
                                            updateGoods(cname, "price", newCPrice);
                                        }
                                        if ("m".equalsIgnoreCase(line)) {
                                            System.out.print("请输入新的数量：");
                                            String newCNum = in.nextLine();
                                            updateGoods(cname, "num", newCNum);
                                        }
                                        continue;
                                    }

                                    if ("c".equalsIgnoreCase(line)) {        //查看商品信息
                                        findAllGoods();
                                    }

                                    if ("b".equalsIgnoreCase(line)) {        //返回上一级
                                        break;
                                    }

                                }

                            }

                        }
                        break;
                    }
                }

                //实现用户注册功能
                int repeat = -1;
                if ("r".equalsIgnoreCase(line)) {
                    System.out.print("请输入昵称：");
                    String userName = in.nextLine();
                    System.out.print("请输入密码：");
                    String userPsw = in.nextLine();
                    System.out.print("请输入您的私钥，请牢记");
                    String pswKey = in.nextLine();
                    System.out.print("请输入用户类型(1.买家2.卖家)");
                    int userRole = in.nextInt();

                    repeat = regist(userName, userPsw, pswKey, userRole);

                    //System.out.println("repeat:" + repeat);//测试
                    if (repeat != 0) {
                        System.out.println("昵称重复，请重新注册！！！");
                    } else {
                        System.out.println("注册成功☺，请登录！");
                    }

                }

                //实现退出系统功能
                if ("t".equalsIgnoreCase(line)) {
                    System.out.println("祝您购物愉快，欢迎您下次光临！！！");
                    clearState();//清空数据库的购买商品状态state，重置为0

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("客户端错误");
        }

    }

    //卖家修改商品
    private static void updateGoods(String cname, String filedName, String filedValue) throws Exception {
        try (
                Socket socket = new Socket(InetAddress.getLocalHost(), Constants.SERVER_PORT);
        ) {
            String data = Constants.OPER_UPDATEGOODS + "#@" + cname + "#@" + filedName + "#@" + filedValue;
            writeToServer(socket, data);
            String str = acceptServer(socket);
            if ("1".equals(str)) {
                System.out.println("商品修改成功☺");
            } else {
                System.out.println("商品修改失败！！！");
            }

        }


    }

    //卖家删除商品
    private static void delGoods(String filedName, String filedValue) throws Exception {

        try (
                Socket socket = new Socket(InetAddress.getLocalHost(), Constants.SERVER_PORT);
        ) {
            String data = Constants.OPER_DELGOODS + "#@" + filedName + "#@" + filedValue;
            writeToServer(socket, data);
            String feedback = acceptServer(socket);
            if ("0".equals(feedback)) {
                System.out.println("该商品不存在！！！");
            } else if ("1".equals(feedback) || "2".equals(feedback)) {
                System.out.println("商品删除成功☺");
            }

        }
    }


    //卖家增加商品
    private static int addGoods(String cNo, String cName, String cPrice, String cNum) throws Exception {
        try (
                Socket socket = new Socket(InetAddress.getLocalHost(), Constants.SERVER_PORT);
        ) {
            String data = Constants.OPER_ADDGOODS + "#@" + cNo + "#@" + cName + "#@" + cPrice + "#@" + cNum;

            writeToServer(socket, data);
            //接收服务端传过来的数据，添加成功返回1，不成功返回-1
            String isadd = acceptServer(socket);
            return Integer.parseInt(isadd);
        }
    }

    //重置商品的state为0
    private static void clearState() throws Exception {
        try (
                Socket socket = new Socket(InetAddress.getLocalHost(), Constants.SERVER_PORT);
        ) {
            String date = Constants.OPER_CLEARSTATE + "#@";

            writeToServer(socket, date);

        }
    }


    //查看购物车
    private static void checkCart() throws Exception {
        try (
                Socket socket = new Socket(InetAddress.getLocalHost(), Constants.SERVER_PORT);
        ) {
            String data = Constants.OPER_CHECKCART + "#@";
            writeToServer(socket, data);

            System.out.println("用户ID\t\t\t商品ID\t\t\t商品名称\t\t\t商品数量\t\t\t购买时间");
            InputStream in = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            Object o = ois.readObject();
            List<Cart> carts = (List<Cart>) o;
            for (Cart cart : carts) {
                System.out.println(cart.getUserid() + "\t\t\t" + cart.getGoodsId() + "\t\t\t" + cart.getGoodsName() + "\t\t\t"
                        + cart.getGoodsNum() + " \t\t\t" + cart.getPurchaseTime());
            }
        }
    }

    //购买商品
    private static void Purchase(String cname, String cnum) throws Exception {
        try (
                Socket socket = new Socket(InetAddress.getLocalHost(), Constants.SERVER_PORT);
        ) {
            String time = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(Calendar.getInstance().getTime());//获取当前时间
            String data = Constants.OPER_Purchase + "#@" + cname + "#@" + cnum + "#@" + time;

            writeToServer(socket, data);

            acceptServer(socket);

        }
    }

    //按商品条件查找
    private static void findOrderGoods(String filedName, String filedValue) throws Exception {
        try (
                Socket socket = new Socket(InetAddress.getLocalHost(), Constants.SERVER_PORT);

        ) {
            String data = Constants.OPER_QUERYNAME + "#@" + filedName + "#@" + filedValue;

            writeToServer(socket, data);
            InputStream in = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            Object o = ois.readObject();
            System.out.println("商品编号\t\t商品名称\t\t商品价格\t\t商品数量\t\t商品状态");
            if (o != null) {
                goods((List<Goods>) o);
            }
        }
    }

    //查看所有商品
    private static void findAllGoods() throws Exception {
        try (
                Socket socket = new Socket(InetAddress.getLocalHost(), Constants.SERVER_PORT);

        ) {
            String data = Constants.OPER_ALLGOODS + "#@";
            writeToServer(socket, data);
            findGoods(socket);

        }
    }

    //在查看所有商品的基础上按价格来排序
    private static void findGoods(Socket socket) throws Exception {

        InputStream inputStream = socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        Object o = ois.readObject();
        System.out.println("商品编号\t\t商品名称\t\t商品价格\t\t商品数量\t\t商品状态");
        //接收服务端发送过来的所有商品信息
        if (o != null) {
            goods((List<Goods>) o);
            //再根据价格进行排序
            while (true) {
                System.out.println("请选择排序方式：价格由低到高(A)  价格由高到低(D)  返回上一级(B)");        //按价格高低排序
                Scanner in = new Scanner(System.in);
                String line = in.nextLine();

                if ("a".equalsIgnoreCase(line)) {            //价格由低到高
                    String data = Constants.OPER_ALLGOODS + "#@price#@asc";
                    writeToServer(socket, data);
                    orderByPrice(socket);
                } else if ("d".equalsIgnoreCase(line)) {        //价格由高到低
                    String data = Constants.OPER_ALLGOODS + "#@price#@desc";
                    writeToServer(socket, data);
                    orderByPrice(socket);

                } else if ("b".equalsIgnoreCase(line)) {        //返回上一级
                    break;
                }

            }

        }

    }

    private static void goods(List<Goods> o) {
        List<Goods> goods = o;
        for (Goods good : goods) {
            System.out.println(good.getCno() + "\t\t" + good.getCname() +
                    "\t\t" + good.getPrice() + "\t\t" + good.getNum() + "\t\t" + (good.isState() ? "买过" : "没买过"));
        }
    }

    private static void orderByPrice(Socket socket) throws Exception {
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        Object o = ois.readObject();
        System.out.println("商品编号\t\t商品名称\t\t商品价格\t\t商品数量\t\t商品状态");
        if (o != null) {
            goods((List<Goods>) o);
        }
    }


    //用户注册
    private static int regist(String userName, String userPsw, String pswKey, int userRole) throws Exception {
        try (
                Socket socket = new Socket(InetAddress.getLocalHost(), Constants.SERVER_PORT);

        ) {
            String data = Constants.OPER_REGIST + "#@" + userName + "#@" + userPsw + "#@" + pswKey + "#@" + userRole;
            writeToServer(socket, data);
			
			/*InputStream in = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String repeat = br.readLine();*/

            String repeat = acceptServer(socket);
			
			/*if(!"0".equals(repeat)) {
				System.out.println("昵称重复，注册失败！");
			}*/
            return Integer.parseInt(repeat);
        }
    }


    //用户登录，返回用户的角色，-1代表登录失败
    private static int login(String userName, String userPsw, String pswKey) throws Exception {
        try (
                Socket socket = new Socket(InetAddress.getLocalHost(), Constants.SERVER_PORT);

        ) {
            String data = Constants.OPER_LOGIN + "#@" + userName + "#@" + userPsw + "#@" + pswKey;
            writeToServer(socket, data);        //把拼接信息发送给服务端

            String role = acceptServer(socket);

            if ("-1".equals(role)) {
                System.out.println("用户名或密码不正确");
            }
            //System.out.println("role从服务端获取的值："+ role);	//测试
            return Integer.parseInt(role);
        }


    }

    //接受服务端的数据
    private static String acceptServer(Socket socket) throws Exception {
        InputStream in = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        return line;
    }


    //向服务端发送数据
    private static void writeToServer(Socket socket, String content) throws IOException {
        OutputStream out = socket.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

        if (!"".equals(content)) {
            bw.write(content);
        }
        bw.write("\n");
        bw.flush();
    }
}
