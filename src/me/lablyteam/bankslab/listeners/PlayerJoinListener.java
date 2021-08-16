package me.lablyteam.bankslab.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.lablyteam.bankslab.BanksLabMain;
import me.lablyteam.bankslab.account.Account;

public class PlayerJoinListener implements Listener {
	
	private BanksLabMain main;
	
	public PlayerJoinListener(BanksLabMain main) {
		this.main = main;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void initBank(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(!main.getDatabase().hasAccount(player))return;
		Account account = Account.initAccount(main, player);
		main.getCache().getAccounts().put(player.getUniqueId(), account);
		main.getCache().getInterestPlayers().add(player.getUniqueId());
	}
	
	
	
}
