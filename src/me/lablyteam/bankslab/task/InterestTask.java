package me.lablyteam.bankslab.task;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.lablyteam.bankslab.BanksLabMain;
import me.lablyteam.bankslab.account.Account;

public class InterestTask implements Runnable {
	private BanksLabMain main;
	
	public InterestTask(BanksLabMain main) {
		this.main = main;
	}
	
	
	@Override
	public void run() {
		List<UUID> players = main.getCache().getInterestPlayers();
		try {
			main.getCache().getInterestPlayers().forEach((uuid) -> {
				Player player = Bukkit.getPlayer(uuid);
				if(player == null || !player.isOnline() || !main.getDatabase().hasAccount(player)) {
					players.remove(uuid);
					return;
				}
				
				Account account = main.getAccount(player);
				if(System.currentTimeMillis() > (account.loggedAt() + 1000)) {
					account.checkInterest();
					players.remove(uuid);
				}
			});
		}catch(NullPointerException|ConcurrentModificationException ex) {
			
		}
	}

}
