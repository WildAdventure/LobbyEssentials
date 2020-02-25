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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HideGadget {

	private static Map<Player, Long> cooldown = new HashMap<Player, Long>();
	
	public static void onQuit(Player player) {
		cooldown.remove(player);
	}
	
	public static void onClick(Player player) {
		Long oldCooldown = cooldown.get(player);
		if (oldCooldown == null || System.currentTimeMillis() - oldCooldown.longValue() > 5000) {
			cooldown.put(player, System.currentTimeMillis());
			
			if (!VanishUtils.canSeeOthers(player)) {
				// Non vedeva i player, quindi ora li mostra
				VanishUtils.showEveryoneToPlayer(player);
				player.sendMessage(ChatColor.GRAY + "Ora puoi " + ChatColor.GREEN + "vedere" + ChatColor.GRAY + " di nuovo i giocatori.");
				player.setItemInHand(LobbyEssentials.hideGadgetOn);
				
			} else {
				// Vedeva i player, quindi ora li nasconde
				VanishUtils.hideEveryoneToPlayer(player);
				player.sendMessage(ChatColor.GRAY + "Hai " + ChatColor.RED + "nascosto" + ChatColor.GRAY + " gli altri giocatori.");
				player.setItemInHand(LobbyEssentials.hideGadgetOff);
			}
		} else {
			player.sendMessage(ChatColor.GRAY + "Devi aspettare 5 secondi prima di usare questo oggetto.");
		}
	}
}
