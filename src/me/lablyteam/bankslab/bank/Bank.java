package me.lablyteam.bankslab.bank;

public interface Bank {
	String getDisplayName();
	String getId();
	double getInterestTax();
	int getInterestTimestamp();
}
