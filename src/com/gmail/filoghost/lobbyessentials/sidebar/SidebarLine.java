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
package com.gmail.filoghost.lobbyessentials.sidebar;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import wild.api.WildCommons;

public class SidebarLine {
	
	@Getter
	private int score;
	private Team team;
	
	private String lastText;

	public SidebarLine(Objective obj, int score) {
		team = createSafeTeam(obj.getScoreboard(), "Line-" + score);
		String fakeEntry = ChatColor.values()[score].toString();
		team.addEntry(fakeEntry);
		obj.getScore(fakeEntry).setScore(score);
		this.score = score;
	}
	
	public SidebarLine setText(String text, boolean fast) {
		
		if (lastText != null && lastText.equals(text)) {
			return this;
		}
		
		lastText = text;
		
		String prefix;
		String suffix;
		
		if (text.length() > 16) {
			
			if (text.charAt(15) == ChatColor.COLOR_CHAR) {
				prefix = text.substring(0, 15);
				text = text.substring(15, text.length());
			} else {
				prefix = text.substring(0, 16);
				text = text.substring(16, text.length());
			}
			
			String lastColors = ChatColor.getLastColors(prefix);
			if (lastColors.isEmpty()) {
				lastColors = "§f";
			}
			
			suffix = lastColors + text;
			if (suffix.length() > 16) {
				suffix = suffix.substring(0, 16);
			}
			
		}  else {
			prefix = text;
			suffix = "";
		}
		
		if (fast) {
			if (!team.getPrefix().equals(prefix) || !team.getPrefix().equals(suffix)) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					try {
						WildCommons.Unsafe.sendTeamPrefixSuffixChangePacket(player, team, prefix, suffix);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			
			if (!team.getPrefix().equals(prefix)) {
				team.setPrefix(prefix);
			}
			
			if (!team.getSuffix().equals(suffix)) {
				team.setSuffix(suffix);
			}
		}
		
		return this;
	}
	
	public void sendPrefixSuffix(Player player, String fakeText) {
		if (player.getScoreboard().equals(team.getScoreboard())) {
			try {
				WildCommons.Unsafe.sendTeamPrefixSuffixChangePacket(player, team, fakeText, "");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


	private static Team createSafeTeam(Scoreboard scoreboard, String name) {
		if (scoreboard.getTeam(name) != null) {
			scoreboard.getTeam(name).unregister();
		}
		
		return scoreboard.registerNewTeam(name);
	}

}
