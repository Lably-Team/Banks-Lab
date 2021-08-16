package me.lablyteam.bankslab.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import me.lablyteam.bankslab.BanksLabMain;
import me.lablyteam.bankslab.account.Account;
import me.lablyteam.bankslab.account.AccountLog;
import me.lablyteam.bankslab.builders.ItemBuilder;
import me.lablyteam.bankslab.builders.SkullItemBuilder;
import me.lablyteam.bankslab.lib.Utility;

public class BankMenu {
	
	private BanksLabMain main;
	
	private ItemBuilder withdrawItem;
	private ItemBuilder depositItem;
	private ItemBuilder closeItem;
	private ItemStack glass;
	
	public BankMenu(BanksLabMain main) {
		this.main = main;
		
		this.closeItem = new ItemBuilder(Material.BARRIER)
				.setName("§cClose");
		this.withdrawItem = new ItemBuilder(Material.DISPENSER)
				.setName("§cWithdraw")
				.setLore("", "§7Withdraw money from your bank","","§e§lCLICK RIGHT§7 for withdraw all");
		this.depositItem = new ItemBuilder(Material.CHEST)
				.setName("§aDeposit")
				.setLore("", "§7Deposit money to your bank","","§e§lCLICK RIGHT§7 for deposit all");
		this.glass = new ItemBuilder(Material.STAINED_GLASS_PANE, (short)7)
				.setName("§7")
				.build();
	}
	
	
	public void openMenu(Player player) {
		if(!main.getDatabase().hasAccount(player)) {
			return;
		}
		Inventory inventory = Bukkit.createInventory(null, 45, "Your bank account");
		Account account = main.getAccount(player);
		String balance = Utility.formatCurrency(account.getMoney());
		
		String currentBalance = " §7Current balance: §6"+balance;
		
		SkullItemBuilder bankStatus = new SkullItemBuilder();
		bankStatus.setName("§bBank status");
		bankStatus.setLore("", currentBalance,"§7Bank: §b"+account.getBank().getDisplayName());
		bankStatus.setOwner(player.getName());
		
		for(int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, glass);
		}
		String noLogs = "§7No logs found.";
		
		ItemBuilder logItem = new ItemBuilder(Material.MAP)
				.setName("§aRecent transactions")
				.addFlags(ItemFlag.values());
		if(account.getLogs().size() < 1) {
			logItem.setLore(noLogs);
			inventory.setItem(24, logItem.build());
		} else {
			List<String> lore = new ArrayList<>();
			int max = main.getConfig().getInt("max-transactions-log", 15);
			for(int i = account.getLogs().size() - 1; i >= 0; i--) { // AccountLog log : account.getLogs().parseAll()
				AccountLog log = account.getLogs().getLog(i);
				if(lore.size() > max) {
					break;
				}
				
				String name = log.getMethod().getName() + "ed";
				String amount = Utility.formatCurrency(log.getAmount());
				String target = log.getPlayer().getName();
				lore.add("§c"+target+" §7"+name+" §6"+amount);
			}
			if(lore.size() < 1) {
				logItem.setLore(noLogs);
				inventory.setItem(24, logItem.build());
			} else {
				logItem.setLore(lore);
				inventory.setItem(24, logItem.build());
			}
		}
		
		inventory.setItem(20, depositItem.build());
		inventory.setItem(22, withdrawItem.build());
		
		inventory.setItem(40, closeItem.build());
		inventory.setItem(4, bankStatus.build());
		
		player.openInventory(inventory);
	}
}
