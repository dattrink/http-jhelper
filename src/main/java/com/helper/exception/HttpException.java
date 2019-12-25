package com.helper.exception;

public class HttpException extends Exception {
	
	private static final long serialVersionUID = -427593770749514414L;
	
	public HttpException() {
		super("Lấy dữ liệu liên thông thất bại");
	}

	public HttpException(String message) {
		super(message);
	}
}
