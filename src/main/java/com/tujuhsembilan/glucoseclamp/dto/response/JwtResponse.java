package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
	private int id;
    private String token;
	private String type = "Bearer";
	private String username;
	private String name;
    private String role;

    public JwtResponse(String token, int id, String username,String name, String role) {
	    this.token = token;
	    this.id = id;
	    this.username = username;
		this.name = name;
	    this.role = role;
	}
}