package com.tujuhsembilan.bookrecipe.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UsersDTO {
    private int userId;
	private String username;
	private String fullname;
	private String password;
	private String role;
	private Boolean isDeleted;
	private String createdBy;
	private Timestamp createdTime;
	private String modifiedBy;
	private Timestamp modifiedTime;
}
