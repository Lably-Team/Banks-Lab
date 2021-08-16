package me.lablyteam.bankslab.builders;

import java.lang.reflect.Field;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class SkullItemBuilder extends ItemBuilder {

	private String owner;
	private String url;

	public SkullItemBuilder() {
		super(Material.SKULL_ITEM, 1, (short) 3);
	}

	public SkullItemBuilder setOwner(String username) {
		this.owner = username;
		return this;
	}

	public SkullItemBuilder setTexture(String url) {
		this.url = url;
		return this;
	}

	public String getOwner() {
		return this.owner;
	}

	public ItemStack build() {
		ItemStack i = this.convert();
		if (url == null || url.isEmpty()) {
			SkullMeta m = (SkullMeta) i.getItemMeta();
			m.setOwner(this.owner == null ? "MHF_Question" : this.owner);
			i.setItemMeta(m);
			return i;
		} else {
			ItemMeta skullMeta = i.getItemMeta();
			GameProfile profile = new GameProfile(UUID.randomUUID(), null);
			byte[] encodedData = Base64
					.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", this.url).getBytes());
			profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
			Field profileField = null;
			try {
				profileField = skullMeta.getClass().getDeclaredField("profile");
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
			profileField.setAccessible(true);
			try {
				profileField.set(skullMeta, profile);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			i.setItemMeta(skullMeta);
			return i;
		}

	}
}
