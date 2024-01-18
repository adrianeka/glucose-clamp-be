package com.tujuhsembilan.bookrecipe.exception.classes;

public class UnauthorizedUserException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8842902557746924866L;

	public UnauthorizedUserException(String message){
        super(message);
    }
}
