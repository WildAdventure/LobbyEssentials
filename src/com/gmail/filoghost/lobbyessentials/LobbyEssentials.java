/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.lobbyessentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import wild.api.config.PluginConfig;
import wild.api.item.ItemBuilder;

import com.gmail.filoghost.lobbyessentials.command.AdminCommandHandler;
import com.gmail.filoghost.lobbyessentials.command.LobbyCommandHandler;
import com.gmail.filoghost.lobbyessentials.command.NointeractCommand;
import com.gmail.filoghost.lobbyessentials.command.VanishCommand;
import com.gmail.filoghost.lobbyessentials.listener.BungeeListener;
import com.gmail.filoghost.lobbyessentials.listener.EventListener;
import com.gmail.filoghost.lobbyessentials.listener.ProtectionListener;
import com.gmail.filoghost.lobbyessentials.sidebar.SidebarManager;
import com.google.common.collect.Maps;

public class LobbyEssentials extends JavaPlugin {
	
	private static Location spawn;
	public static World world;
	public static LobbyEssentials plugin;
	public static ItemStack book;
	public static ItemStack hideGadgetOn;
	public static ItemStack hideGadgetOff;
	public static ItemStack navigator;
	public static ItemStack hubSelector;
	public static ItemStack speedGadget;
	
	public static boolean rain;
	public static boolean thunder;
	
	public static Map<String, Location> warps;
	
	@Override
	public void onEnable() {
		if (!Bukkit.getPluginManager().isPluginEnabled("WildCommons")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + this.getName() + "] Richiesto WildCommons!");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) { }
			Bukkit.shutdown();
			return;
		}
		
		plugin = this;
		
		saveDefaultConfig();
		
		world = Bukkit.getWorld("world");
		FileConfiguration config = getConfig();
		
		if (config.isConfigurationSection("spawn")) {
			spawn = readLocation(config.getConfigurationSection("spawn"));
		} else {
			spawn = world.getSpawnLocation();
		}
		
		String serverName = null;
		
		try {
			PluginConfig serverConfig = new PluginConfig(this, "server.yml");
			if (!serverConfig.isSet("server-name")) {
				serverConfig.set("server-name", "Default");
				serverConfig.save();
			}
			
			serverName = serverConfig.getString("server-name");
			
		} catch (Exception ex) {
			getLogger().severe("Cannot load server.yml");
			ex.printStackTrace();
		}
		
		rain = config.getBoolean("rain", false);
		thunder = config.getBoolean("thunder", false);
		
		if (serverName == null) {
			serverName = "Default";
		}
		SidebarManager.initialize(serverName);
		
		loadWarps(config);
		
		try {
			readBook();
		} catch (Exception ex) {
			ex.printStackTrace();
			getLogger().severe("Impossibile leggere il libro!");
		}
		
		speedGadget = ItemBuilder.of(Material.SUGAR).name("§bAumenta velocità §7(Click destro)").lore("§7Clicca con il tasto destro per", "§7aumentare la tua velocità.").build();
		
		hideGadgetOn = ItemBuilder.of(Material.INK_SACK).durability(10).name("§cNascondi giocatori §7(Click destro)").lore("§7Clicca con il tasto destro per", "§7nascondere tutti i giocatori.").build();
		hideGadgetOff = ItemBuilder.of(Material.INK_SACK).durability(8).name("§aMostra giocatori §7(Click destro)").lore("§7Clicca con il tasto destro per", "§7mostrare tutti i giocatori.").build();
		
		hubSelector = ItemBuilder.of(Material.NETHER_STAR).name("§aCambia hub §7(Click destro)").lore("§7Apre il menù degli hub disponibili.").build();
		navigator = ItemBuilder.of(Material.COMPASS).name("§aScegli un server §7(Click destro)").lore("§7Apre il menù dei server disponibili.").build();
		
		
		getCommand("lobbyessentials").setExecutor(new AdminCommandHandler());
		getCommand("spawn").setExecutor(new LobbyCommandHandler());
		getCommand("vanish").setExecutor(new VanishCommand());
		getCommand("ni").setExecutor(new NointeractCommand());
		
		world.setThundering(thunder);
		world.setStorm(rain);
		
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginManager().registerEvents(new ProtectionListener(), this);

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener());
	}
	
	public static void loadWarps(FileConfiguration config) {
		warps = Maps.newHashMap();
		
		if (config.isConfigurationSection("warps")) {
			ConfigurationSection warpSection = config.getConfigurationSection("warps");
			Set<String> warpNames = warpSection.getKeys(false);
			
			for (String warpName : warpNames) {
				if (warpSection.isConfigurationSection(warpName)) {
					warps.put(warpName.toLowerCase(), readLocation(warpSection.getConfigurationSection(warpName)));
				}
			}
		}
	}

	public static void setSpawn(Location loc) {
		spawn = loc;
	}
	
	public static Location getSpawn() {
		if (spawn == null) {
			return world.getSpawnLocation();
		} else {
			return spawn;
		}
	}
	
	public static Location readLocation(ConfigurationSection section) {
		return new Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"), (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
	}
	
	public static void saveLocation(ConfigurationSection section, Location loc) {
		section.set("x", loc.getX());
		section.set("y", loc.getY());
		section.set("z", loc.getZ());
		section.set("yaw", loc.getYaw());
		section.set("pitch", loc.getPitch());
	}
	
	private void readBook() {
		book = new ItemStack(Material.WRITTEN_BOOK);
		List<String> content = Utils.loadFile("book.yml").getStringList("content");
		BookMeta bm = (BookMeta) book.getItemMeta();
		bm.setTitle("§aTutorial");
		bm.setAuthor("Wild Adventure");
		
		List<String> pages = new ArrayList<String>();
		pages.add("");
		
		for(String line : content)  {
			if (line.equals("<newpage>")) {
				pages.add("");
			} else {
				line = line.replace("&", "§").replace("->", "➡");
				int index = pages.size() - 1;
				pages.set(index, pages.get(index) + line + "\n");
			}
		}
		
		bm.setPages(pages);
		book.setItemMeta(bm);
	}
	
}
