package hulva.luva.wxx.platform.puzzle.backend.controller;

public abstract class AbstractBaseController {
    public Response<Void> SUCCESS() {
        Response<Void> response = new Response<>();
        response.setStatus(true);
        response.setSuccess(true);
        return response;
    }

    public <T> Response<T> SUCCESS(T data) {
        Response<T> response = new Response<>();
        response.setStatus(true);
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public <T> Response<T> SUCCESS(T data, String message) {
        Response<T> response = SUCCESS(data);
        response.setMessage(message);
        return response;
    }

    public <T> Response<T> FAIL(String message) {
        Response<T> response = new Response<T>();
        response.setStatus(false);
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    public <T> Response<T> FAIL(T data) {
        Response<T> response = new Response<T>();
        response.setStatus(false);
        response.setSuccess(false);
        response.setData(data);
        return response;
    }

    public <T> Response<T> FAIL(T data, String message) {
        Response<T> response = new Response<T>();
        response.setStatus(false);
        response.setSuccess(false);
        response.setData(data);
        response.setMessage(message);
        return response;
    }

    public <T> Response<T> FAIL(Exception e) {
        Response<T> response = new Response<T>();
        response.setStatus(false);
        response.setSuccess(false);
        response.setError(e);
        return response;
    }

    public <T> Response<T> FAIL(String message, Exception e) {
        Response<T> response = new Response<T>();
        response.setStatus(false);
        response.setSuccess(false);
        response.setMessage(message);
        response.setError(e);
        return response;
    }
}
