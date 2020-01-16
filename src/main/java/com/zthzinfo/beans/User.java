package com.zthzinfo.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@NoArgsConstructor
public class User {
	private String name;
	private String mail;
	private String webhookUserid;
	private Webhook webhook;
}
