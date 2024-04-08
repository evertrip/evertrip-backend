package com.evertrip.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@NoArgsConstructor
public class NaverUser {
    public String id;
    public String age;
    public String gender;
    public String email;
    public String mobile;
    public String mobile_e164;
    public String name;
    public String birthday;
    public String birthyear;
}
