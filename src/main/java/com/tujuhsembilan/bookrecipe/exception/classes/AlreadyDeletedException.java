package com.tujuhsembilan.bookrecipe.exception.classes;

public class AlreadyDeletedException extends RuntimeException{
	public AlreadyDeletedException(String message) {
		super(message);
	}
}
