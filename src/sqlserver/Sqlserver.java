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







    public void putMessage(String ID, String message){
        String str = "insert into meslog values(\"" + ID + "\",\"" + message +"\",datetime(\"now\",\"+8 hour\"));";
        System.out.println(str);
        try{
            int a;
            sql sq = new sql();
            a = sq.stmt.executeUpdate(str);
            System.out.println(a);
            sq.c.commit();
            sq.over();
        }catch (Exception e){
            System.err.println((e.getClass().getName()+":"+e.getMessage()));
            System.out.println("error");
            System.exit(0);
        }

    }

    public void putSign(String ID, String name){
        String str = "insert into sign values(\"" + ID + "\",\"" + name +"\",datetime(\"now\",\"+8 hour\"));";
        System.out.println(str);
        try{
            int a;
            sql sq = new sql();
            a = sq.stmt.executeUpdate(str);
            System.out.println(a);
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
            ResultSet rs = sq.stmt.executeQuery("select user.name, meslog.text, mes_time from user,meslog where user.ID = meslog.ID;");
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

    public Vector<mes> getSignTime(){
        Vector<mes> rt = new Vector<mes>();
        try{
            sql sq = new sql();
//            System.out.println("OK");
            ResultSet rs = sq.stmt.executeQuery("select user.ID, user.name, sign_time from user,sign where user.ID = sign.ID;");
//            System.out.println("OK");
            while(rs.next()){
                String rs1 = rs.getString("ID");
                String rs2 = rs.getString("name");
                String rs3 = rs.getString("sign_time");
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


}
