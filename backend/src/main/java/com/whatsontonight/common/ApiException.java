package com.whatsontonight.common;

/** 會轉成 { "isSuccess": false, "error": message } 的錯誤。status 為 HTTP 狀態碼。 */
public class ApiException extends RuntimeException {
    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(400, message);
    }

    public static ApiException tooManyRequests() {
        return new ApiException(429, "操作太頻繁，請稍後再試");
    }
}
