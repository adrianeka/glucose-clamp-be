package com.tujuhsembilan.bookrecipe.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private int id;
	private String type = "Bearer";
	private String username;
    private List<String> role;

    public JwtResponse(String token, int id, String username, List<String> role) {
	    this.token = token;
	    this.id = id;
	    this.username = username;
	    this.role = role;
	}
}