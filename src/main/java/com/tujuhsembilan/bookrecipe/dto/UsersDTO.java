package com.tujuhsembilan.bookrecipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

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
