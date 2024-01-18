package com.tujuhsembilan.bookrecipe.exception.classes;

public class DataAccessException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5920633207715501150L;

	public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
