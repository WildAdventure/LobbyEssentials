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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

public class VanishUtils {

	private static Set<Player> whoCantSeeOthers = Sets.newHashSet();
	private static Set<Player> hiddenPlayers = Sets.newHashSet();
	private static Set<Player> vanished = Collections.synchronizedSet(new HashSet<Player>());
	private static Set<Player> canInteract = Collections.synchronizedSet(new HashSet<Player>());
	
	public static void hideEveryoneToPlayer(Player who) {
		whoCantSeeOthers.add(who);
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			who.hidePlayer(online);
		}
	}

	public static void showEveryoneToPlayer(Player who) {
		whoCantSeeOthers.remove(who);
		
		boolean canSeeVanished = canSeeVanished(who);
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (canBeSeen(online)) {
				if (canSeeVanished || !isVanished(online)) {
					who.showPlayer(online);
				}
			}
		}
	}
	
	public static void hidePlayerToEveryone(Player who) {
		hiddenPlayers.add(who);
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.hidePlayer(who);
		}
	}
	
	public static void showPlayerToEveryone(Player who) {
		hiddenPlayers.remove(who);
		
		if (isVanished(who)) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (canSeeVanished(online) && canSeeOthers(online)) {
					online.showPlayer(who);
				}
			}
			return;
		}
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (canSeeOthers(online)) {
				online.showPlayer(who);
			}
		}
	}
	
	public static void setVanished(Player who) {
		vanished.add(who);
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (!canSeeVanished(online)) {
				online.hidePlayer(who);
			}
		}
	}
	
	public static void removeVanished(Player who) {
		vanished.remove(who);
		
		if (canBeSeen(who)) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (canSeeOthers(online)) {
					online.showPlayer(who);
				}
			}
		}
	}
	
	public static boolean isVanished(Player who) {
		return vanished.contains(who);
	}
	
	public static void setCanInteract(Player who) {
		canInteract.add(who);
	}
	
	public static void removeCanInteract(Player who) {
		canInteract.remove(who);
	}
	
	public static boolean canInteract(Player who) {
		return canInteract.contains(who);
	}
	
	public static boolean canSeeVanished(Player who) {
		return who.hasPermission(Perms.VANISH);
	}
	
	public static boolean canSeeOthers(Player player) {
		return !whoCantSeeOthers.contains(player);
	}
	
	public static boolean canBeSeen(Player player) {
		return !hiddenPlayers.contains(player);
	}
	
	public static void onJoin(Player player) {
		
		for (Player whoCantSee : whoCantSeeOthers) {
			whoCantSee.hidePlayer(player);
		}
		
		for (Player whoCantBeSeen : hiddenPlayers) {
			player.hidePlayer(whoCantBeSeen);
		}
		
		if (!canSeeVanished(player)) {
			for (Player van : vanished) {
				player.hidePlayer(van);
			}
		}
	}
	
	public static void onQuit(Player player) {
		whoCantSeeOthers.remove(player);
		hiddenPlayers.remove(player);
		vanished.remove(player);
		canInteract.remove(player);
	}
	
}
