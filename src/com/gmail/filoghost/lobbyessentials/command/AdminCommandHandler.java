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
package com.gmail.filoghost.lobbyessentials.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.gmail.filoghost.lobbyessentials.LobbyEssentials;
import com.gmail.filoghost.lobbyessentials.Perms;
import com.google.common.base.Joiner;

public class AdminCommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!sender.hasPermission(Perms.ADMIN_COMMAND)) {
			sender.sendMessage("§cNon hai il permesso!");
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage("§6Comandi Lobby Essentials:");
			sender.sendMessage("§e/le setspawn");
			sender.sendMessage("§e/le setwarp <nome>");
			sender.sendMessage("§e/le warps <nome>");
			sender.sendMessage("§e/le weather <tempo>");
			return true;
		}
		
		if (sender instanceof Player == false) {
			sender.sendMessage("§cNon può essere eseguito dalla console.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args[0].equalsIgnoreCase("setspawn")) {
			FileConfiguration config = LobbyEssentials.plugin.getConfig();
			Location loc = player.getLocation();
			
			if (!config.isConfigurationSection("spawn")) {
				config.createSection("spawn");
			}
			LobbyEssentials.saveLocation(config.getConfigurationSection("spawn"), loc);
			LobbyEssentials.plugin.saveConfig();
			LobbyEssentials.setSpawn(loc);
			player.sendMessage("§aSpawn salvato.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("setwarp")) {
			
			if (args.length < 2) {
				player.sendMessage("/le setwarp <nome>");
				return true;
			}
			
			String name = args[1].toLowerCase();
			FileConfiguration config = LobbyEssentials.plugin.getConfig();
			Location loc = player.getLocation();
			
			if (!config.isConfigurationSection("warps." + name)) {
				config.createSection("warps." + name);
			}
			LobbyEssentials.saveLocation(config.getConfigurationSection("warps." + name), loc);
			LobbyEssentials.plugin.saveConfig();
			LobbyEssentials.loadWarps(config);
			player.sendMessage("§aWarp " + name + " salvato.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("weather")) {
			
			if (args.length < 2) {
				player.sendMessage("/le weather <rain|thunder>");
				return true;
			}
			
			
			if (args[1].equalsIgnoreCase("rain")) {
				LobbyEssentials.rain = !LobbyEssentials.rain;
				LobbyEssentials.plugin.getConfig().set("rain", LobbyEssentials.rain);
				
				if (LobbyEssentials.rain) {
					player.sendMessage("Pioggia attivata.");
				} else {
					player.sendMessage("Pioggia disattivata.");
				}
			} else if (args[1].equalsIgnoreCase("thunder")) {
				LobbyEssentials.thunder = !LobbyEssentials.thunder;
				LobbyEssentials.plugin.getConfig().set("thunder", LobbyEssentials.thunder);
				
				if (LobbyEssentials.thunder) {
					player.sendMessage("Tuoni attivati.");
				} else {
					player.sendMessage("Tuoni disattivati.");
				}
			} else {
				player.sendMessage("L'argomento può essere solo \"rain\" o \"thunder\".");
			}
			
			LobbyEssentials.world.setThundering(LobbyEssentials.thunder);
			LobbyEssentials.world.setStorm(LobbyEssentials.rain);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("warps")) {
			player.sendMessage("§eWarps: " + Joiner.on(", ").join(LobbyEssentials.warps.keySet()));
		}
		
		player.sendMessage("§cComando sconosciuto. Scrivi \"/le\" per i comandi.");
		return true;
	}

}
