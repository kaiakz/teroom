package sqlserver;

public class mess<a, b, c, d> {

    a t1;
    b t2;
    c t3;
    d t4;
    public mess(a t1, b t2, c t3, d t4){
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }
    public a getT1(){
        return t1;
    }
    public b getT2(){
        return t2;
    }
    public c getT3(){
        return t3;
    }
    public d getT4() { return t4; }
    //此为泛型类 可用于填装用户表
    //统一格式为 用户ID 姓名 用户密码 用户等级(教师/学生)
    //现在该泛型类还未启用
}
