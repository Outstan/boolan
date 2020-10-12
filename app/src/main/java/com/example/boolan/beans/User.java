package com.example.boolan.beans;

import java.io.Serializable;

public class User implements Serializable {
	
	private String u_id;//id
	private String u_name;//登录名
	private String u_password;//密码
	private String u_phone;//电话
	private String u_sex;//性别
	private String u_age;//年龄
	private String u_email;//邮箱
	private String u_img;//头像
	private String u_hometown;//地址
	private String u_nickname;//昵称
	private String u_birthday;//生日
	private String u_synopsis;//简介
	private String u_localtion;//所在地
	private String follow;

	public String getFollow() {
		return follow;
	}

	public void setFollow(String follow) {
		this.follow = follow;
	}

	public String getU_hometown() {
		return u_hometown;
	}

	public void setU_hometown(String u_hometown) {
		this.u_hometown = u_hometown;
	}

	public String getU_localtion() {
		return u_localtion;
	}

	public void setU_localtion(String u_localtion) {
		this.u_localtion = u_localtion;
	}

	public String getU_nickname() {
		return u_nickname;
	}

	public void setU_nickname(String u_nickname) {
		this.u_nickname = u_nickname;
	}

	public String getU_birthday() {
		return u_birthday;
	}

	public void setU_birthday(String u_biryhday) {
		this.u_birthday = u_biryhday;
	}

	public String getU_synopsis() {
		return u_synopsis;
	}

	public void setU_synopsis(String u_synopsis) {
		this.u_synopsis = u_synopsis;
	}

	public String getU_id() {
		return u_id;
	}


	public void setU_id(String u_id) {
		this.u_id = u_id;
	}


	public String getU_name() {
		return u_name;
	}


	public void setU_name(String u_name) {
		this.u_name = u_name;
	}


	public String getU_password() {
		return u_password;
	}


	public void setU_password(String u_password) {
		this.u_password = u_password;
	}


	public String getU_phone() {
		return u_phone;
	}


	public void setU_phone(String u_phone) {
		this.u_phone = u_phone;
	}


	public String getU_sex() {
		return u_sex;
	}


	public void setU_sex(String u_sex) {
		this.u_sex = u_sex;
	}


	public String getU_age() {
		return u_age;
	}


	public void setU_age(String u_age) {
		this.u_age = u_age;
	}


	public String getU_email() {
		return u_email;
	}


	public void setU_email(String u_email) {
		this.u_email = u_email;
	}

	public String getU_img() {
		return u_img;
	}


	public void setU_img(String u_img) {
		this.u_img = u_img;
	}


	public User() {

	}
	

	public User(String id, String phone, String sex, String age, String email, String hometown, String nickname, String birthday, String synopsis,String localtion,String u_img) {
		this.u_id = id;
		this.u_phone = phone;
		this.u_sex = sex;
		this.u_age = age;
		this.u_email = email;
		this.u_hometown = hometown;
		this.u_nickname = nickname;
		this.u_birthday = birthday;
		this.u_synopsis = synopsis;
		this.u_localtion = localtion;
		this.u_img = u_img;
	}
}
