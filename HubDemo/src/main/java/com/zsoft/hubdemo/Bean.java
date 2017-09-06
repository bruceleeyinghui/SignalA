package com.zsoft.hubdemo;

/**
 * Created by liyh on 2017/9/6.
 *
 * 测试类实体测试用
 */

public class Bean implements java.io.Serializable{

    private int Cmd ;
    private int SeqId;
    private String Data;

    public Bean(int cmd, int seqId, String data) {
        Cmd = cmd;
        SeqId = seqId;
        Data = data;
    }
}
