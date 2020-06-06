import java.sql.*;
import java.util.Date;
import java.util.Vector;

import sqlserver.*;

/*
此文档为sqlserver的基本使用方法示例，
 */


public class Test {
    public static void main(String args[]){
        Date date = new Date();

        // 使用 toString() 函数显示日期时间
        System.out.println(date.toString());
       try{
           Sqlserver sql = new Sqlserver();


         sql.putMessage("M001","This is the Second Message");
           /*
           聊天记录写入数据库，后台会自动写入时间，不需额外添加。请注意M001为ID号，不是姓名。
           */


           System.out.println("此处为聊天记录提取");
           Vector<mes> rs1;
           rs1 = sql.getMeseage();
           for(int i = 0; i < rs1.size(); i++){
               System.out.println(rs1.elementAt(i).getT1() + " " + rs1.elementAt(i).getT2() + " " + rs1.elementAt(i).getT3());
           }

           System.out.println("下列为签到记录提取");
           Vector<mes> rs2;
           rs2 = sql.getSignTime();
           for(int i = 0; i < rs2.size(); i++){
               System.out.println(rs2.elementAt(i).getT1() + " " + rs2.elementAt(i).getT2() + " " + rs2.elementAt(i).getT3());
           }

           /*
           getT1 getT2 getT3分别得到泛型向量中的三个内容
           若为聊天记录 统一格式为 姓名、聊天消息、发送时间
           若为签到时间 统一格式为 ID、姓名、签到时间
            */

       } catch (Exception e) {
           System.err.println((e.getClass().getName()+":"+e.getMessage()));
           System.exit(0);
       }

    }
}
