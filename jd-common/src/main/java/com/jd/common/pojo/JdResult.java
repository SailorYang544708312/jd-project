package com.jd.common.pojo;

import java.io.Serializable;

public class JdResult implements Serializable {

   private boolean success;
   private String message;
   private Object obj;

   public JdResult(boolean success, String message, Object obj) {
      this.success = success;
      this.message = message;
      this.obj = obj;
   }

   public JdResult(Object obj) {
      this.success = true;
      this.message = "OK";
      this.obj = obj;
   }
   public static JdResult ok(){
      return new JdResult(true,"OK",null);
   }

   public boolean isSuccess() {
      return success;
   }

   public void setSuccess(boolean success) {
      this.success = success;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Object getObj() {
      return obj;
   }

   public void setObj(Object obj) {
      this.obj = obj;
   }
}
