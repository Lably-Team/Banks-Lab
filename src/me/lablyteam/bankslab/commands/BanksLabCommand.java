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
import me.lablyteam.bankslab.lib.YamlFile;

@Command(names = {
		"bankslab", "blp"
}, desc = "BanksLab principal command")
public class BanksLabCommand implements CommandClass {
	
	private BanksLabMain main;
	private YamlFile lang;
	
	public BanksLabCommand(BanksLabMain main) {
		this.main = main;
		this.lang = main.getFiles().getLang();
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
			player.sendMessage(Utility.colorize(lang.getString("no-account")));
			return;
		}
		main.getBankMenu().openMenu(player);
	}
	
	@Command(names = "create")
	public void createAccount(@Sender Player player) {
		if(main.getDatabase().hasAccount(player)) {
			player.sendMessage(Utility.colorize(lang.getString("already-has-account")));
			return;
		}
		Account newAccount = Account.createAccount(main, player);
		Bank bank = newAccount.getBank();
		String format = String.format(Utility.colorize(lang.getString("created-account")), bank.getDisplayName());
		player.sendMessage(format);
	}
	
	@Command(names = "set-money")
	public void setMoney(@Sender Player player, @OptArg("-1") String amount) {
		if(!main.getDatabase().hasAccount(player)) {
			player.sendMessage(Utility.colorize(lang.getString("no-account")));
			return;
		}
		String insertNumber = Utility.colorize(lang.getString("insert-number"));
		if(!isInt(amount)) {
			player.sendMessage(insertNumber);
			return;
		}
		int money = Integer.valueOf(amount);
		if(money < 0) {
			player.sendMessage(insertNumber);
			return;
		}
			
		Account account = main.getAccount(player);
		account.setMoney(money);
		player.sendMessage(String.format(Utility.colorize(lang.getString("set-money")), Utility.formatCurrency(money)));
	}
	
	@Command(names = "check-interest")
	public void checkInterest(@Sender Player player) {
		if(!main.getDatabase().hasAccount(player)) {
			player.sendMessage(Utility.colorize(lang.getString("no-account")));
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
