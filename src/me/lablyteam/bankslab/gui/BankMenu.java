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
import me.lablyteam.bankslab.account.logging.AccountLog;
import me.lablyteam.bankslab.builders.ItemBuilder;
import me.lablyteam.bankslab.builders.SkullItemBuilder;
import me.lablyteam.bankslab.enums.AccountMethod;
import me.lablyteam.bankslab.lib.Utility;
import me.lablyteam.bankslab.lib.YamlFile;

public class BankMenu {
	
	private BanksLabMain main;
	private YamlFile itemsFile;
	
	private ItemBuilder withdrawItem;
	private ItemBuilder depositItem;
	private ItemBuilder closeItem;
	private ItemStack glass;
	
	public BankMenu(BanksLabMain main) {
		this.main = main;
		this.itemsFile = main.getFiles().getItems();
		
		// In a future i can upgrade this code, please don't fuck me with that code :c
		this.closeItem = new ItemBuilder(Material.matchMaterial(itemsFile.getString("items.close.material")))
				.addFlags(ItemFlag.values())
				.setName(Utility.colorize(itemsFile.getString("items.close.name", "&cClose")));
		
		this.withdrawItem = new ItemBuilder(Material.matchMaterial(itemsFile.getString("items.withdraw.material")))
				.addFlags(ItemFlag.values())
				.setName(Utility.colorize(itemsFile.getString("items.withdraw.name", "&cWithdraw")))
				.setLore(colorizeLore(itemsFile.getStringList("items.withdraw.lore")));
		
		this.depositItem = new ItemBuilder(Material.matchMaterial(itemsFile.getString("items.deposit.material")))
				.addFlags(ItemFlag.values())
				.setName(Utility.colorize(itemsFile.getString("items.deposit.name", "&aDeposit")))
				.setLore(colorizeLore(itemsFile.getStringList("items.deposit.lore")));
		
		this.glass = new ItemBuilder(Material.STAINED_GLASS_PANE, (short)itemsFile.getInt("items.glass.data_id"))
				.addFlags(ItemFlag.values())
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
		bankStatus.setName(Utility.colorize(itemsFile.getString("items.bank_status.name", "&bBank status")));
		bankStatus.setLore("", currentBalance,"§7Bank: §b"+account.getBank().getDisplayName());
		bankStatus.setOwner(player.getName());
		
		for(int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, glass);
		}
		String noLogs = Utility.colorize(main.getFiles().getLang().getString("no-logs"));
		
		ItemBuilder logItem = new ItemBuilder(Material.matchMaterial(itemsFile.getString("items.transactions.material")))
				.setName(Utility.colorize(itemsFile.getString("items.transactions.name")))
				.addFlags(ItemFlag.values());
		if(account.getLogs().size() < 1) {
			logItem.setLore(noLogs);
			inventory.setItem(24, logItem.build());
		} else {
			List<String> lore = new ArrayList<>();
			int max = main.getConfig().getInt("max-transactions-log", 15);
			for(int i = account.getLogs().size() - 1; i >= 0; i--) { // AccountLog log : account.getLogs().parseAll()
				if(lore.size() > max || i < 0) {
					break;
				}
				if(account.getLogs().getAllLog().get(i) == null || account.getLogs().getAllLog().get(i).isEmpty()) {
					continue;
				}
				AccountLog log = account.getLogs().getLog(i);
				String plus = (log.getMethod() == AccountMethod.DEPOSIT ? "§a+" : "§c-") + "§r";
				String name = log.getMethod().getName() + "ed";
				String amount = Utility.formatCurrency(log.getAmount()).replace(',', '.');
				//String target = log.getPlayer().getName();
				long createdAt = log.createdAt();
				String replaced = itemsFile.getString("items.transactions.log-format", "%plus% &6%money%, &7%method_name% &9%time% ago")
						.replace("%plus%", plus)
						.replace("%money%", String.valueOf(log.getAmount()))
						.replace("%formatted_money%", amount)
						.replace("%method_name%", name)
						.replace("%time%", formatDate(createdAt));
				lore.add(replaced);
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

	public String formatDate(long time) {
		long date = time - System.currentTimeMillis();
		float inMinutes = (date / (1000 * 60));
		float inHours = (date / (1000 * 60));
		
		if(inMinutes < 1) {
			return "few moments";
		}
		
		
		
		return inHours < 1 ? inMinutes + " minutes" : inHours + " hours";
	}
	
	public List<String> colorizeLore(List<String> lore) {
		List<String> newLore = new ArrayList<>();
		for(String line : lore) {
			newLore.add(Utility.colorize(line));
		}
		return newLore;
	}
}
