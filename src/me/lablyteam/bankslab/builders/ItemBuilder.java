package me.lablyteam.bankslab.builders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemBuilder {
	private ItemStack item;
	private String name;
	private List<String> lore;
	private List<ItemFlag> flags;
	private Map<Enchantment, Integer> enchantments;
	private boolean unbrkbl;
	public final char COLOR_CHAR = ChatColor.COLOR_CHAR;

	public ItemBuilder(ItemStack item) {
		this.item = item;
		this.enchantments = new HashMap<>();
		this.lore = new ArrayList<>();
		this.flags = new ArrayList<>();
		this.unbrkbl = false;
	}

	public ItemBuilder(Material mat, int amount, short data) {
		this(new ItemStack(mat, amount, data));
	}

	public ItemBuilder(Material mat, int amount) {
		this(mat, amount, (short) 0);
	}

	public ItemBuilder(Material mat) {
		this(mat, 1, (short) 0);
	}

	public ItemBuilder(Material mat, short data) {
		this(mat, 1, data);
	}

	public Material getMaterial() {
		return this.item.getType();
	}

	public int getAmount() {
		return this.item.getAmount();
	}

	public String getName() {
		return this.name;
	}

	public List<String> getLore() {
		return this.lore;
	}

	public List<ItemFlag> getItemFlags() {
		return this.flags;
	}

	public Map<Enchantment, Integer> getEnchants() {
		return this.enchantments;
	}

	public ItemStack getItemStack() {
		return item;
	}

	public boolean isUnbreakable() {
		return this.unbrkbl;
	}

	public ItemBuilder setName(String text) {
		this.name = ChatColor.translateAlternateColorCodes('&', text);
		return this;
	}

	public ItemBuilder setLore(List<String> lore) {
		setLore(lore.stream().toArray(String[]::new));
		return this;
	}
	
	public ItemBuilder setLore(String...lore) {
		this.lore.clear();
		for(String line : lore) {
			this.lore.add(ChatColor.translateAlternateColorCodes('&', "&f"+line));
		}
		return this;
	}
	
	public ItemBuilder addEnchant(Enchantment enchant, int level) {
		enchantments.put(enchant, level);
		return this;
	}

	public ItemBuilder addFlag(ItemFlag flag) {
		this.flags.add(flag);
		return this;
	}

	public ItemBuilder addFlags(ItemFlag[] flags) {
		for (int i = 0; i < flags.length; i++) {
			this.flags.add(flags[i]);
		}
		return this;
	}

	public ItemBuilder setUnbreakable(boolean x) {
		this.unbrkbl = x;
		return this;
	}

	public static String toBase64(ItemStack item) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeObject(item);
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stack", e);
		}
	}

	public static ItemStack fromBase64(String from) {
		ItemStack item = null;
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(from));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			item = (ItemStack) dataInput.readObject();
			dataInput.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return item;
	}

	public ItemStack build() {
		return convert();
	}

	public ItemStack convert() {
		ItemStack i = this.item;
		ItemMeta m = i.getItemMeta();
		if (this.name != null)
			m.setDisplayName(this.name);
		if (!this.lore.isEmpty())
			m.setLore(this.lore);
		if (!enchantments.isEmpty())
			enchantments.forEach((enchant, level) -> {
				m.addEnchant(enchant, level, true);
			});
		if (!this.flags.isEmpty())
			this.flags.forEach(flag -> {
				m.addItemFlags(flag);
			});
		if (unbrkbl)
			m.setUnbreakable(unbrkbl);
		i.setItemMeta(m);
		return i;
	}
}
