package com.example.lloyd.multifunctionbed.entity;

import java.io.Serializable;

/**
 * ����ʵ����
 * @author lloyd
 *
 */
public class EntityDevice implements Serializable{
	
	private String name;
	private String address;
	private int rssi;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
}
