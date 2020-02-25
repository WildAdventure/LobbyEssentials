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

import java.util.List;

import lombok.Getter;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import wild.api.WildCommons;

import com.gmail.filoghost.lobbyessentials.LobbyEssentials;
import com.google.common.collect.Lists;

public class SidebarManager {
	
	private static final String TITLE_COLORS = "" + ChatColor.GREEN + ChatColor.BOLD;
	private static final String CONTENT_COLORS = "" + ChatColor.WHITE;

	private static Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	private static Objective side;
	
	private static CyclicIterator<String> title1Frames;
	private static CyclicIterator<String> title2Frames;
	private static CyclicIterator<String> contactsTitleFrames;
	private static CyclicIterator<String> contactsContentsFrames;
	
	
	private static SidebarLine title1Line;
	private static SidebarLine title2Line;
	private static SidebarLine contactsContent;
	private static SidebarLine contactsTitle;
	
	@Getter
	private static SidebarLine onlinePlayersLine;
	
	static {
		String title1SpaceBefore = "           ";
		List<String> title1States = Lists.newArrayList();
		
		title1States.addAll(repeat(title1SpaceBefore + "&f&lWILD", 40));
		
		title1States.add(title1SpaceBefore + "&f&lWILD");
		title1States.add(title1SpaceBefore + "&f&lWILD");
		title1States.add(title1SpaceBefore + "&f&lWILD");
		title1States.add(title1SpaceBefore + "&e&lW&f&lILD");
		title1States.add(title1SpaceBefore + "&6&lW&e&lI&f&lLD");
		title1States.add(title1SpaceBefore + "&6&lWI&e&lL&f&lD");
		title1States.add(title1SpaceBefore + "&6&lWIL&e&lD");
		title1States.add(title1SpaceBefore + "&6&lWILD");
		title1States.add(title1SpaceBefore + "&6&lWILD");
		
		title1States.addAll(repeat(title1SpaceBefore + "&6&lWILD", 40));
		
		title1States.add(title1SpaceBefore + "&6&lWILD");
		title1States.add(title1SpaceBefore + "&6&lWILD");
		title1States.add(title1SpaceBefore + "&6&lWILD");
		title1States.add(title1SpaceBefore + "&e&lW&6&lILD");
		title1States.add(title1SpaceBefore + "&f&lW&e&lI&6&lLD");
		title1States.add(title1SpaceBefore + "&f&lWI&e&lL&6&lD");
		title1States.add(title1SpaceBefore + "&f&lWIL&e&lD");
		title1States.add(title1SpaceBefore + "&f&lWILD");
		title1States.add(title1SpaceBefore + "&f&lWILD");
		
		for (int i = 0; i < title1States.size(); i++) {
			title1States.set(i, WildCommons.color(title1States.get(i)));
		}
		
		String title2SpaceBefore = "      ";
		List<String> title2States = Lists.newArrayList();
		
		title2States.addAll(repeat(title2SpaceBefore + "&f&lADVENTURE", 40));
		
		title2States.add(title2SpaceBefore + "&e&lA&f&lDVENTURE");
		title2States.add(title2SpaceBefore + "&6&lA&e&lD&f&lVENTURE");
		title2States.add(title2SpaceBefore + "&6&lAD&e&lV&f&lENTURE");
		title2States.add(title2SpaceBefore + "&6&lADV&e&lE&f&lNTURE");
		title2States.add(title2SpaceBefore + "&6&lADVE&e&lN&f&lTURE");
		title2States.add(title2SpaceBefore + "&6&lADVEN&e&lT&f&lURE");
		title2States.add(title2SpaceBefore + "&6&lADVENT&e&lU&f&lRE");
		title2States.add(title2SpaceBefore + "&6&lADVENTU&e&lR&f&lE");
		title2States.add(title2SpaceBefore + "&6&lADVENTUR&e&lE");
		
		title2States.addAll(repeat(title2SpaceBefore + "&6&lADVENTURE", 40));

		title2States.add(title2SpaceBefore + "&e&lA&6&lDVENTURE");
		title2States.add(title2SpaceBefore + "&f&lA&e&lD&6&lVENTURE");
		title2States.add(title2SpaceBefore + "&f&lAD&e&lV&6&lENTURE");
		title2States.add(title2SpaceBefore + "&f&lADV&e&lE&6&lNTURE");
		title2States.add(title2SpaceBefore + "&f&lADVE&e&lN&6&lTURE");
		title2States.add(title2SpaceBefore + "&f&lADVEN&e&lT&6&lURE");
		title2States.add(title2SpaceBefore + "&f&lADVENT&e&lU&6&lRE");
		title2States.add(title2SpaceBefore + "&f&lADVENTU&e&lR&6&lE");
		title2States.add(title2SpaceBefore + "&f&lADVENTUR&e&lE");
		
		for (int i = 0; i < title2States.size(); i++) {
			title2States.set(i, WildCommons.color(title2States.get(i)));
		}
		
		title1Frames = new CyclicIterator<>(title1States.toArray(new String[0]));
		title2Frames = new CyclicIterator<>(title2States.toArray(new String[0]));
		
		SidebarTransitions transitions = new SidebarTransitions(80, 5, TITLE_COLORS, CONTENT_COLORS);
		transitions.addPair("Sito Web", "WildAdventure.it");
		transitions.addPair("Forum", "forum.WildAdventure.it");
		transitions.addPair("Discord", "discord.WildAdventure.it");
		transitions.addPair("Telegram", "tg.WildAdventure.it");
		Pair<List<String>, List<String>> lists = transitions.makeLists();
		
		contactsTitleFrames = new CyclicIterator<>(lists.getLeft().toArray(new String[0]));
		contactsContentsFrames = new CyclicIterator<>(lists.getRight().toArray(new String[0]));
	}
	
	public static void initialize(String serverName) {
		
		// Rimuove gli obiettivi precedenti
		safeRemoveObjective(scoreboard.getObjective(DisplaySlot.SIDEBAR));
		safeRemoveObjective(scoreboard.getObjective("sidebar"));
		
		side = scoreboard.registerNewObjective("sidebar", "dummy");
		side.setDisplayName("                              ");
		side.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		new SidebarLine(side, 11).setText(" §7§m-*---+---*---+---*-", false);
		
		title1Line = new SidebarLine(side, 10);
		title2Line = new SidebarLine(side, 9);
		
		title1Line.setText(title1Frames.next(), false);
		title2Line.setText(title2Frames.next(), false);

		new SidebarLine(side, 8).setText(" §7§m-*---+---*---+---*-", false);
		
		new SidebarLine(side, 7);
		
		new SidebarLine(side, 6).setText(TITLE_COLORS + "Giocatori online", false);
		onlinePlayersLine = new SidebarLine(side, 5).setText("0", false);
		
		new SidebarLine(side, 4);
		
		contactsTitle = new SidebarLine(side, 3);
		contactsContent = new SidebarLine(side, 2);
		
//		new SidebarLine(side, 4);
//		
//		new SidebarLine(side, 3).setText(TITLE_COLORS + "Server", false);
//		new SidebarLine(side, 2).setText(serverName, false);

		new SidebarLine(side, 1);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(LobbyEssentials.plugin, new Runnable() {
			
			int ticks;
			
			@Override
			public void run() {
				
				if (ticks++ % 2 == 0) {
					title1Line.setText(title1Frames.next(), true);
					title2Line.setText(title2Frames.next(), true);
				}
				
				contactsTitle.setText(contactsTitleFrames.next(), false);
				contactsContent.setText(contactsContentsFrames.next(), false);

			}
		}, 1, 1);


	}
	
	
	public static List<String> repeat(String elem, int times) {
		List<String> list = Lists.newArrayList();
		
		while (times > 0) {
			list.add(elem);
			times--;
		}
		
		return list;
	}
	
	private static void safeRemoveObjective(Objective o) {
		if (o != null) {
			o.unregister();
		}
	}

	public static void setScoreboard(Player player) {
		player.setScoreboard(scoreboard);
	}

}
