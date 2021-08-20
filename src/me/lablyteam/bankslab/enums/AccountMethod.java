package me.lablyteam.bankslab.enums;

public enum AccountMethod {
	WITHDRAW(),
	DEPOSIT();
	
	public String getName() {
		return name().toLowerCase();
	}
	
	public String getDisplayName() {
		return String.valueOf(name().charAt(0)).toUpperCase()+name().toLowerCase().substring(1);
	}
}
