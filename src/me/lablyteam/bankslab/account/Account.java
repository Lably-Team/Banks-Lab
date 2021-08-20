package me.lablyteam.bankslab.account;

import org.bukkit.entity.Player;

import me.lablyteam.bankslab.BanksLabMain;
import me.lablyteam.bankslab.account.logging.AccountLogs;
import me.lablyteam.bankslab.bank.Bank;

public interface Account {
	static Account initAccount(BanksLabMain main, Player player) {
		return new AccountImpl(main, player);
	}
	
	static Account createAccount(BanksLabMain main, Player player, Bank bank) {
		return main.getDatabase().createAccount(player, bank, 0);
	}
	
	static Account createAccount(BanksLabMain main, Player player) {
		return Account.createAccount(main, player, main.getCache().getBanks().get("default"));
	}
	
	Player getOwner();
	double getMoney();
	AccountLogs getLogs();
	Bank getBank();
	void save();
	long getLastInterest();
	long getNextInterest();
	long loggedAt();
	boolean checkInterest();
	
	void setLastInterest(long interest);
	
	void addMoney(double amount);
	void removeMoney(double amount);
	boolean setMoney(double amount);
	
	boolean withdraw(double amount);
	boolean deposit(double amount);
	
}
