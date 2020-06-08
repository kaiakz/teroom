package sqlserver;

public class mes<a, b, c> {

    a t1;
    b t2;
    c t3;
    public mes(a t1, b t2, c t3){
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
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
    //此为泛型类 可用于填装聊天记录、签到时间等
    //统一格式为 姓名、聊天消息、发送时间 或 ID、姓名、签到时间
}
