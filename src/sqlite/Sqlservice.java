package sqlite;
import java.sql.*;
import java.util.Vector;




public class Sqlservice {

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







    public void putMessage(String name, String message){ //消息记录写入函数
        String str = "insert into messageLog values(\"" + name + "\",\"" + message +"\",datetime(\"now\",\"+8 hour\"));";
        try{
            int a;
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

//datetime("now","+8 hour")
    public boolean teacherSign(String ID, String psw){ //教师登录函数 如果查询到ID与password均正确，则返回true，反之false
        String str = "select * from password where ID =\"" + ID + "\" and psw =\"" + psw +"\" and rank = \"teacher\";";
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



    public Vector<me2> getLogin(){
        String str = "select ID, name from login;";
        Vector<me2> rt = new Vector<me2>();
        try{
            sql sq = new sql();
            ResultSet rs = sq.stmt.executeQuery(str);
            while(rs.next()){
                String rs1 = rs.getString("ID");
                String rs2 = rs.getString("name");
                me2 temp = new me2(rs1, rs2);
                rt.add(temp);
            }
            sq.over();
            rs.close();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            return rt;
        }
        return rt;
    }

    public boolean stuLogin(String ID, String name){ //学生登录函数 如果查询到ID与name均正确，则返回true，反之false
        String str = "select * from password where ID =\"" + ID + "\" and name =\"" + name +"\" and rank = \"student\";";
        try{
            ResultSet rs;
            sql sq = new sql();
            rs = sq.stmt.executeQuery(str);
            String sttt = rs.getString("ID");
            sq.over();
            if(sttt.equals(ID)){
                String str1 = "insert into login values(\"" + ID +"\",\"" + name +"\");";
                sql sq1 =new sql();
                sq1.stmt.executeUpdate(str1);
                sq1.c.commit();
                sq1.over();
                return true;
            }
            else return false;
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            return false;
        }
    }

    public void setQuestion(String question){
        String str = "insert into question values(NULL,\"" + question + "\");";
        try{
            sql sq = new sql();
            sq.stmt.executeUpdate(str);
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
        }
    }

    public void setAnswer(String ID,String name, String answer){
        try{
            sql sq = new sql();
            int qid = this.getQID();
            String str = "insert into answer values(\"" + ID + "\",\"" + name + "\",\"" + answer + "\");";
            System.out.println(str);
            sq.stmt.executeUpdate(str);
            sq.c.commit();
            sq.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
        }
    }

    public void clearAnswer(){
        try{
            sql sq = new sql();
            String str = "delete from answer;";
            sq.stmt.executeUpdate(str);
            sq.c.commit();
            sq.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
        }
    }

    public void clearLogin(){
        try{
            sql sq = new sql();
            String str = "delete from login;";
            sq.stmt.executeUpdate(str);
            sq.c.commit();
            sq.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
        }
    }

    public Vector<mes> getAnswer(){
        Vector<mes> rt = new Vector<mes>();
        try{
            sql sq = new sql();
            ResultSet rs = sq.stmt.executeQuery("select ID, name, answer from answer;");
            while(rs.next()){
                String rs1 = rs.getString("ID");
                String rs2 = rs.getString("name");
                String rs3 = rs.getString("answer");
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

    public int getQID(){
        String str = "select quesID from question order by quesID desc";
        int rt = 0;
        try{
            sql sq = new sql();
            ResultSet rs = sq.stmt.executeQuery(str);
            rt = Integer.parseInt(rs.getString("quesID").toString());
            sq.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
        }
        return  rt;
    }



    public Vector<mes> getMeseage(){
        Vector<mes> rt = new Vector<mes>();
        try{
            sql sq = new sql();
            ResultSet rs = sq.stmt.executeQuery("select name, text, mes_time from messageLog;");
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


    public void refleshLogin(){ //重置签到(签入)表
        String str = "delete from login";
        try{
            int a;
            sql sq = new sql();
            a = sq.stmt.executeUpdate(str);
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
            String str = "delete from password where ID =\"" + ID + "\";";
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
