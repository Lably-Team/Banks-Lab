package me.lablyteam.bankslab.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.lablyteam.bankslab.account.Account;
import me.lablyteam.bankslab.bank.Bank;

public class Cache {
	private Map<UUID, Account> accounts;
	private Map<String, Bank> banks;
	private List<UUID> interestPlayers;
	
	public Cache() {
		this.accounts = new HashMap<>();
		this.banks = new HashMap<>();
		this.interestPlayers = new ArrayList<>();
	}
		
	public Map<UUID, Account> getAccounts() {
		return this.accounts;
	}
	
	public Map<String, Bank> getBanks() {
		return this.banks;
	}
	
	public List<UUID> getInterestPlayers() {
		return interestPlayers;
	}
}
