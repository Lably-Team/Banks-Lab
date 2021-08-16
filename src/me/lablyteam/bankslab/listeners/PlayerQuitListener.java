package me.lablyteam.bankslab.listeners;

import java.util.Map;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.lablyteam.bankslab.BanksLabMain;
import me.lablyteam.bankslab.account.Account;

public class PlayerQuitListener implements Listener {
	
	private BanksLabMain main;
	
	public PlayerQuitListener(BanksLabMain main) {
		this.main = main;
	}
	
	@EventHandler
	public void removeBank(PlayerQuitEvent event) {
		Map<UUID, Account> accounts = main.getCache().getAccounts();
		if(!accounts.containsKey(event.getPlayer().getUniqueId()))return;
		Account account = accounts.get(event.getPlayer().getUniqueId());
		account.save();
		accounts.remove(event.getPlayer().getUniqueId());
	}
	
	
}
