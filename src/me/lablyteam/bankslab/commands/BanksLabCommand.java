package me.lablyteam.bankslab.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import me.lablyteam.bankslab.BanksLabMain;
import me.lablyteam.bankslab.account.Account;
import me.lablyteam.bankslab.bank.Bank;
import me.lablyteam.bankslab.lib.Utility;

@Command(names = {
		"bankslab", "blp"
}, desc = "BanksLab principal command")
public class BanksLabCommand implements CommandClass {
	
	private BanksLabMain main;
	
	public BanksLabCommand(BanksLabMain main) {
		this.main = main;
	}
	
	@Command(names = "")
	public void helpMessage(CommandSender sender) {
		sender.sendMessage("bankslab");
	}
	
	@Command(names = "reload", desc = "Reload all files")
	public void reloadSubCommand(CommandSender sender) {
		main.getConfig().reload();
		sender.sendMessage("Files reloaded");
	}
	
	@Command(names = "bank", desc = "Open bank menu")
	public void bankCommand(@Sender Player player) {
		if(!main.getDatabase().hasAccount(player)) {
			player.sendMessage("§cNo tienes una cuenta.");
			return;
		}
		main.getBankMenu().openMenu(player);
	}
	
	@Command(names = "create")
	public void createAccount(@Sender Player player) {
		if(main.getDatabase().hasAccount(player)) {
			player.sendMessage("§cYa tienes una cuenta.");
			return;
		}
		Account newAccount = Account.createAccount(main, player);
		Bank bank = newAccount.getBank();
		player.sendMessage("§aNueva cuenta de banco creada en: "+bank.getDisplayName()+" §7["+bank.getId()+"]");
	}
	
	@Command(names = "set-money")
	public void setMoney(@Sender Player player, @OptArg("-1") String amount) {
		if(!main.getDatabase().hasAccount(player)) {
			player.sendMessage("§cNo tienes una cuenta.");
			return;
		}
		if(!isInt(amount)) {
			player.sendMessage("§cDebes insertar un número.");
			return;
		}
		int money = Integer.valueOf(amount);
		if(money < 0) {
			player.sendMessage("§cDebes insertar un número.");
			return;
		}
			
		Account account = main.getAccount(player);
		account.setMoney(money);
		player.sendMessage("§aEstableciste tu dinero a §6"+Utility.formatCurrency(money));
	}
	
	@Command(names = "check-interest")
	public void checkInterest(@Sender Player player) {
		if(!main.getDatabase().hasAccount(player)) {
			player.sendMessage("§cNo tienes una cuenta.");
			return;
		}
		
		Account account = main.getAccount(player);
		if(account.checkInterest()) {
			player.sendMessage("Sí!");
		} else {
			player.sendMessage("No!");
		}
	}
	
	private boolean isInt(String number) {
		try {
			Integer.valueOf(number);
			return true;
		}catch(NumberFormatException ex) {
			return false;
		}
	}
	
}
