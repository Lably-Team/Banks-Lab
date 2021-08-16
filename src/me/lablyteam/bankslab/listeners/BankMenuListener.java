package me.lablyteam.bankslab.listeners;

import java.util.Arrays;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.lablyteam.bankslab.BanksLabMain;
import me.lablyteam.bankslab.account.Account;
import me.lablyteam.bankslab.lib.SignMenuFactory;

public class BankMenuListener implements Listener {
	
	private BanksLabMain main;
	
	public BankMenuListener(BanksLabMain main) {
		this.main = main;
	}
	
	@EventHandler
	public void manageAction(InventoryClickEvent event) {
		if(event.getClickedInventory() == null || event.getView() == null || event.getInventory() == null || event.getCurrentItem() == null) {
			return;
		}
		if(!ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase("Your bank account")) {
			return;
		}
		Player player = (Player)event.getWhoClicked();
		Account account = main.getAccount(player);
		event.setCancelled(true);
		if(event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
			return;
		}
		String clickedItem = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
		switch(clickedItem) {
		case "Deposit":
			if(event.isRightClick()) {
				double depositBalance = main.getEconomy().getBalance(player);
				account.deposit(depositBalance);
				player.sendMessage("Depositado: "+depositBalance);
				main.getBankMenu().openMenu(player);
			} else {
				execute(player, account, (amount) -> {
					boolean success = account.deposit(amount);
					if(!success) {
						player.sendMessage("§cYou can't affort that.");
						return;
					}
					player.sendMessage("§aDeposited: "+amount+"$");
					main.getBankMenu().openMenu(player);
				}, "", "^^^^^^^^^^^^^^^", "Insert amount to", "deposit");
			}
			break;
		case "Withdraw":
			if(event.isRightClick()) {
				double withdrawBalance = account.getMoney();
				account.withdraw(withdrawBalance);
				player.sendMessage("Sacado: "+withdrawBalance);
				main.getBankMenu().openMenu(player);
			} else {
				execute(player, account, (amount) -> {
					boolean success = account.withdraw(amount);
					if(!success) {
						player.sendMessage("§cYou can't affort that.");
						return;
					}
					player.sendMessage("§aWithdrawed: "+amount+"$");
					main.getBankMenu().openMenu(player);
				}, "", "^^^^^^^^^^^^^^^", "Insert amount to", "withdraw");
			}
			break;
		case "Close":
			player.closeInventory();
			break;
		}
	}
	
	private void execute(Player player, Account account, Consumer<Integer> predicate, String...signLines) {
		SignMenuFactory.Menu signFactory = new SignMenuFactory(main).newMenu(Arrays.asList(signLines));
		signFactory.reopenIfFail(false);
		signFactory.response((target, lines) -> {
			if(!isInt(lines[0])) {
				player.sendMessage("§cInvalid number");
				return false;
			}
			int amount = Integer.valueOf(lines[0]);
			predicate.accept(amount);
			return true;
		});
		player.closeInventory();
		signFactory.open(player);
	}
	
	private boolean isInt(String str) {
		try {
			Integer.valueOf(str);
			return true;
		} catch(NumberFormatException ex) {
			return false;
		}
	}
}
