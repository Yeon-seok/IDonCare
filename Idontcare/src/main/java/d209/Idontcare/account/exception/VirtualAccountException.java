package d209.Idontcare.account.exception;

import d209.Idontcare.common.exception.CommonException;

public class VirtualAccountException extends CommonException{

    public final static String CODE = "400";
    public final static String DESCRIPTION = "로그인 되지 않은 접근";

    public VirtualAccountException(String message){
        this.code = 402;
        this.message = message;
    }
}
