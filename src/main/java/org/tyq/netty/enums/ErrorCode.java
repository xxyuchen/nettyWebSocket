package org.tyq.netty.enums;

/**
 * Created by Administrator on 2018/5/4 0004.
 */
public enum ErrorCode {
    A(5000, "无解析结果"),
    B(6000, "暂不支持该功能"),
    C(4000, "请求参数格式错误"),
    D(4001, "加密方式错误"),
    E(4002, "无功能权限"),
    F(4003, "该apikey没有可用请求次数"),
    G(4005, "无功能权限"),
    H(4007, "apikey不合法"),
    I(4100, "userid获取失败"),
    J(4200, "上传格式错误"),
    K(4300, "批量操作超过限制"),
    L(4400, "没有上传合法userid"),
    M(4500, "userid申请个数超过限制"),
    N(4600, "输入内容为空"),
    O(4602, "输入文本内容超长(上限150)"),
    P(7002, "上传信息失败"),
    Q(8008, "服务器错误");

    private Integer code;
    private String value;

    ErrorCode(int key, String value) {
        this.code = key;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static ErrorCode valueOf(Integer code) {
        ErrorCode[] enums = ErrorCode.values();
        for (ErrorCode e : enums) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
    public static boolean isHave(Integer code){
        ErrorCode[] enums = ErrorCode.values();
        for (ErrorCode e : enums) {
            if (e.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
