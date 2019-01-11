package mmp.im.auth.model;

public class Response {
    private boolean success;
    private Object data;

    public static Response build() {
        return new Response();
    }


    public static Response success() {
        return new Response().setSuccess(true);
    }

    public static Response fail() {
        return new Response().setSuccess(false);
    }

    public boolean isSuccess() {
        return success;
    }

    public Response setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Response setData(Object data) {
        this.data = data;
        return this;
    }
}
