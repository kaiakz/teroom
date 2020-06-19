package sqlserver;
import java.sql.*;
import java.util.Vector;




public class Sqlserver {

    class sql {
        Connection c = null;
        Statement stmt = null;
        public sql(){
            try{
                Class.forName("org.sqlite.JDBC");
                this.c = DriverManager.getConnection("jdbc:sqlite:testDB.db");

                this.stmt = this.c.createStatement();
                this.c.setAutoCommit(false);
            } catch (Exception e) {
                System.err.println((e.getClass().getName()+":"+e.getMessage()));
                System.exit(0);
            }
        }
        public void over(){
            try{
                this.c.close();
                this.stmt.close();
            }catch (Exception e) {
                System.err.println((e.getClass().getName()+":"+e.getMessage()));
                System.exit(0);
            }
        }
    }







    public void putMessage(String ID, String message){ //消息记录写入函数
        String str = "insert into messageLog values(\"" + ID + "\",\"" + message +"\",datetime(\"now\",\"+8 hour\"));";
        System.out.println(str);
        try{
            int a;
            sql sq = new sql();
            a = sq.stmt.executeUpdate(str);
//            System.out.println(a);
            sq.c.commit();
            sq.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            System.exit(0);
        }

    }

//datetime("now","+8 hour")
    public boolean teacherSign(String ID, String psw){ //教师登录函数 如果查询到ID与password均正确，则返回true，反之false
        String str = "select * from password where ID =\"" + ID + "\" and psw =\"" + psw +"\" and rank = \"teacher\";";
        System.out.println(str);
        try{
            ResultSet rs;
            sql sq = new sql();
            rs = sq.stmt.executeQuery(str);
            String sttt = rs.getString("ID");
            sq.over();
            if(sttt.equals(ID)) return true;
            else return false;
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            return false;
        }
    }

    public boolean stuLogin(String ID, String name){ //学生登录函数 如果查询到ID与name均正确，则返回true，反之false
        String str = "select * from password where ID =\"" + ID + "\" and name =\"" + name +"\" and rank = \"student\";";
        System.out.println(str);
        try{
            ResultSet rs;
            sql sq = new sql();
            rs = sq.stmt.executeQuery(str);
            String sttt = rs.getString("ID");
            sq.over();
            if(sttt.equals(ID)) return true;
            else return false;
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            return false;
        }
    }

    public void putNet(String ID, String classID, String ip, String port){ //签到写入函数 不是登录
        String str = "insert into net values(\"" + ID + "\",\"" + classID + "\",\""+ ip + "\",\"" + port + "\",datetime(\"now\",\"+8 hour\"));";
        System.out.println(str);
        try{
            int a;
            sql sq = new sql();
            a = sq.stmt.executeUpdate(str);
//            System.out.println(a);
            sq.c.commit();
            sq.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            System.exit(0);
        }

    }

    public Vector<mes> getMeseage(){
        Vector<mes> rt = new Vector<mes>();
        try{
            sql sq = new sql();
//            System.out.println("OK");
            ResultSet rs = sq.stmt.executeQuery("select password.name, messageLog.text, mes_time from password,messageLog where password.ID = messageLog.ID;");
//            System.out.println("OK");
            while(rs.next()){
                String rs1 = rs.getString("name");
                String rs2 = rs.getString("text");
                String rs3 = rs.getString("mes_time");
                mes temp = new mes(rs1, rs2, rs3);
                rt.add(temp);
            }
            sq.over();
            rs.close();


        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.exit(0);
        }
        return rt;
    }

    public Vector<mess> getUser(){
        Vector<mess> rt = new Vector<mess>();
        try{
            sql sq = new sql();
            ResultSet rs = sq.stmt.executeQuery("select * from password;");
            while(rs.next()){
                String rs1 = rs.getString("ID");
                String rs2 = rs.getString("name");
                String rs3 = rs.getString("psw");
                String rs4 = rs.getString("rank");
                mess temp = new mess(rs1, rs2, rs3, rs4);
                rt.add(temp);
            }
            sq.over();
            rs.close();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.exit(0);
        }
        return rt;
    }

    public Vector<mes> getSignTime(){
        Vector<mes> rt = new Vector<mes>();
        try{
            sql sq = new sql();
//            System.out.println("OK");
            ResultSet rs = sq.stmt.executeQuery("select password.ID, password.name, signTime from password,net where password.ID = net.ID;");
//            System.out.println("OK");
            while(rs.next()){
                String rs1 = rs.getString("ID");
                String rs2 = rs.getString("name");
                String rs3 = rs.getString("signTime");
                mes temp = new mes(rs1, rs2, rs3);
                rt.add(temp);
            }
            sq.over();
            rs.close();


        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.exit(0);
        }
        return rt;
    }

    public void refleshNet(){ //重置签到(签入)表
        String str = "delete from net";
        System.out.println(str);
        try{
            int a;
            sql sq = new sql();
            a = sq.stmt.executeUpdate(str);
//            System.out.println(a);
            sq.c.commit();
            sq.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            System.exit(0);
        }

    }

    public String getName(String ID){
        String str = "select name from password where ID =\"" + ID + "\";";
        System.out.println(str);
        try{
            ResultSet rs;
            sql sq = new sql();
            rs = sq.stmt.executeQuery(str);
            sq.over();
            try{
                rs.next();
                String rtStr = rs.getString("name");
                rs.close();
                return rtStr;
            }catch (Exception ex){
                System.err.println((ex.getClass().getName()+":"+ex.getMessage()));
                System.out.println("error");
                System.exit(0);
            }
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            System.exit(0);
        }
        return "";
    }

    public void putUser(String ID, String name, String psw, String rank){
        String str = "insert into password values(\"" + ID + "\",\"" + name + "\",\"" + psw + "\",\"" + rank + "\");";
        System.out.println(str);
        try{
            sql sq = new sql();
            sq.stmt.executeUpdate(str);
            sq.c.commit();
            sq.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            System.exit(0);
        }
    }

    public void delUser(String ID){//手动触发器 因为表不多
        try{
            String str = "delete from net where ID =\"" + ID + "\";";
            System.out.println(str);
            sql sq1 = new sql();
            sq1.stmt.executeUpdate(str);
            sq1.c.commit();
            sq1.over();
            str = "delete from password where ID =\"" + ID + "\";";
            System.out.println(str);
            sql sq2 = new sql();
            sq2.stmt.executeUpdate(str);
            sq2.c.commit();
            sq2.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            System.exit(0);
        }
    }

    public void updUser(String ID, String name, String psw, String rank){
        try{
            sql sq = new sql();
            String str = "update password set name = \"" + name + "\" , psw = \"" + psw + "\" , rank = \"" + rank + "\" where ID =\"" + ID + "\";";
            System.out.println(str);
            sq.stmt.executeUpdate(str);
            sq.c.commit();
            sq.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            System.exit(0);
        }
    }



}
