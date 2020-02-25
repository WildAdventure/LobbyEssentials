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
package com.gmail.filoghost.lobbyessentials.listener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import wild.api.WildCommons;

import com.gmail.filoghost.lobbyessentials.HideGadget;
import com.gmail.filoghost.lobbyessentials.LobbyEssentials;
import com.gmail.filoghost.lobbyessentials.Perms;
import com.gmail.filoghost.lobbyessentials.VanishUtils;
import com.gmail.filoghost.lobbyessentials.sidebar.SidebarManager;
import com.gmail.filoghost.lobbyessentials.task.ApplyVectorTask;

public class EventListener implements Listener {
	
	private static Map<Player, Long> storeCooldown = new HashMap<Player, Long>();
	private static Map<Player, Long> plateCooldown = new HashMap<Player, Long>();
	
	/************
	 * JOIN
	 ************/
	@EventHandler(priority = EventPriority.LOW)
	public void onJoinLow(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		player.teleport(LobbyEssentials.getSpawn());
		
		if (player.hasPermission(Perms.VANISH)) {
			VanishUtils.setVanished(player);
			player.sendMessage("§3Sei entrato invisibile.");
		}
		
		VanishUtils.onJoin(player);

		player.setFoodLevel(20);
		player.setSaturation(20.0F);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setLevel(0);
		player.setExp(0f);
		player.getInventory().setHeldItemSlot(0);

		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

		if (LobbyEssentials.navigator != null) {
			player.getInventory().setItem(0, LobbyEssentials.navigator);
		}
		
		if (LobbyEssentials.book != null) {
			player.getInventory().setItem(1, LobbyEssentials.book);
		}
		
//		if (LobbyEssentials.hubSelector != null) {
//			player.getInventory().setItem(4, LobbyEssentials.hubSelector);
//		}

		if (LobbyEssentials.speedGadget != null) {
			player.getInventory().setItem(7, LobbyEssentials.speedGadget);
		}
		
		if (LobbyEssentials.hideGadgetOn != null) {
			player.getInventory().setItem(8, LobbyEssentials.hideGadgetOn);
		}

		player.setWalkSpeed(0.3f);

		if (player.hasPermission(Perms.CREATIVE_ON_JOIN)) {
			player.setGameMode(GameMode.CREATIVE);
		} else {
			player.setAllowFlight(false);
		}
		
		SidebarManager.setScoreboard(player);
		
		new BukkitRunnable() {
			
			int count = 0;
			
			@Override
			public void run() {
				WildCommons.sendActionBar(player, "" + ChatColor.WHITE + ChatColor.BOLD + "Per chiedere aiuto usa " + ChatColor.GREEN + ChatColor.BOLD + "/ticket new <messaggio>");

				if (count++ >= 10 || !player.isOnline()) {
					cancel();
				}
			}
			
		}.runTaskTimer(LobbyEssentials.plugin, 0, 40);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onQuit(PlayerQuitEvent event) {
		event.getPlayer().teleport(LobbyEssentials.getSpawn());
		HideGadget.onQuit(event.getPlayer());
		VanishUtils.onQuit(event.getPlayer());
		storeCooldown.remove(event.getPlayer());
		plateCooldown.remove(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onKick(PlayerKickEvent event) {
		if (event.getReason().contains("Flying is not enabled on this server")) {
			Location kickLoc = event.getPlayer().getLocation();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission(Perms.SEE_FLY_KICK)) {
					player.sendMessage("§4[§cFlyKick§4] §c" + event.getPlayer().getName() + " è stato kickato per fly (o lag).");
					WildCommons.fancyMessage("§4[§cFlyKick§4]").then(" X: " + kickLoc.getBlockX() + ", Y: " + kickLoc.getBlockY() + ", Z: " + kickLoc.getBlockZ() + " ").color(ChatColor.RED).then("[TPPOS]").color(ChatColor.GOLD).style(ChatColor.BOLD).tooltip("Clicca per teletrasportarti").command("/tppos " + kickLoc.getBlockX() + " " + kickLoc.getBlockY() + " " + kickLoc.getBlockZ()).send(player);
				}
			}
		}
	}
	
	

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {

		if (event.getEntityType() == EntityType.PLAYER) {
			switch (event.getCause()) {
				case VOID:
					event.getEntity().teleport(LobbyEssentials.getSpawn());
					break;
				case FIRE:
				case FIRE_TICK:
					event.getEntity().setFireTicks(0);
					break;
				default:
					break;
			}
			
		} else {
			// Mob o altro che viene danneggiato
			if (event instanceof EntityDamageByEntityEvent) {
				Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
				if (damager.getType() == EntityType.PLAYER && ProtectionListener.canChangeWorld((Player) damager, true)) {
					// Non cancellare l'evento
					return;
				}
			}
		}
		
		event.setCancelled(true); // Di default cancella
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		
		if (VanishUtils.isVanished(event.getPlayer()) && !VanishUtils.canInteract(event.getPlayer())) {
			event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Sei in vanish, scrivi \"/ni\" per chattare.");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSignComplete(SignChangeEvent event) {
		if (event.getPlayer().hasPermission(Perms.SET_WARP_SIGNS)) {
			String[] lines = event.getLines();
			for (int i = 0; i < lines.length; i++) {
				event.setLine(i, lines[i] != null ? ChatColor.translateAlternateColorCodes('&', lines[i]) : null);
			}
			
			if (event.getLine(0).equalsIgnoreCase("[warp]")) {
				event.setLine(0, "§1" + event.getLine(0));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractPlayer(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player && !event.getPlayer().isInsideVehicle() && event.getPlayer().hasPermission(Perms.RIDE_PLAYERS)) {
			Entity topEntity = event.getRightClicked();
			while (topEntity.getPassenger() != null) {
				topEntity = topEntity.getPassenger();
			}
			
			topEntity.setPassenger(event.getPlayer());
		}
	}
	

	@EventHandler(priority = EventPriority.HIGH)
	public void onInteract(final PlayerInteractEvent event) {

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.hasItem()) {
				if (event.getItem().isSimilar(LobbyEssentials.hideGadgetOff) || event.getItem().isSimilar(LobbyEssentials.hideGadgetOn)) {
					event.setCancelled(true);
					HideGadget.onClick(event.getPlayer());
					return;
				} else if (event.getItem().isSimilar(LobbyEssentials.speedGadget)) {
					event.setCancelled(true);
					int amplifier = 6;
					
					Collection<PotionEffect> effects = event.getPlayer().getActivePotionEffects();
					for (PotionEffect effect : effects) {
						if (effect.getType().equals(PotionEffectType.SPEED) && effect.getAmplifier() == amplifier) {
							event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
							event.getPlayer().sendMessage("§7Effetto §bvelocità §7disattivato.");
							return;
						}
					}
					
					event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, amplifier, true), true);
					event.getPlayer().sendMessage("§7Effetto §bvelocità §7attivato.");
					return;
				}
			}
			
			if (event.hasBlock() && isSign(event.getClickedBlock().getType())) {
				
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (sign.getLine(0).equalsIgnoreCase("§1[warp]") && !sign.getLine(1).isEmpty()) {
					Location warp = LobbyEssentials.warps.get(sign.getLine(1).toLowerCase());
					if (warp != null) {
						event.getPlayer().teleport(warp, TeleportCause.PLUGIN);
					} else {
						event.getPlayer().sendMessage("§cWarp non trovato.");
					}
					return;
				}
			}
			
			if (event.hasBlock() && isRedstoneComponent(event.getClickedBlock().getType())) {
				return;
			}
		}

		if (event.getAction() == Action.PHYSICAL && event.hasBlock()) {
			
			if (event.getClickedBlock().getType() == Material.STONE_PLATE) {
				
				event.setCancelled(true);
				
				Material twoBlocksUnderMaterial = event.getClickedBlock().getRelative(BlockFace.DOWN, 2).getType();
				
				if (twoBlocksUnderMaterial == Material.REDSTONE_BLOCK) {
					
					Long oldCooldown = plateCooldown.get(event.getPlayer());
					if (oldCooldown == null || System.currentTimeMillis() - oldCooldown.longValue() > 700) {
						plateCooldown.put(event.getPlayer(), System.currentTimeMillis());
						event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 2.0F, 1.5F);
						Vector dir = event.getPlayer().getLocation().getDirection().setY(0.0).normalize().multiply(3.5D).setY(0.6D);
						Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyEssentials.plugin, new ApplyVectorTask(event.getPlayer(), dir));
						Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyEssentials.plugin, new ApplyVectorTask(event.getPlayer(), dir), 2L);
					}
					
				} else if (twoBlocksUnderMaterial == Material.EMERALD_BLOCK) {
					
					Long oldCooldown = plateCooldown.get(event.getPlayer());
					if (oldCooldown == null || System.currentTimeMillis() - oldCooldown.longValue() > 700) {
						plateCooldown.put(event.getPlayer(), System.currentTimeMillis());
						event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2.0F, 1.3F);
						Vector dir = event.getPlayer().getLocation().getDirection().setY(0.0).normalize().multiply(0.05D).setY(1.5D);
						Bukkit.getScheduler().scheduleSyncDelayedTask(LobbyEssentials.plugin, new ApplyVectorTask(event.getPlayer(), dir));
					}
					
				}
			} else if (event.getClickedBlock().getType() == Material.SOIL) {
				event.setCancelled(true);
			}
			
			return;
		}
		
		if (!ProtectionListener.canChangeWorld(event.getPlayer(), true)) {
			event.setUseInteractedBlock(Result.DENY);
		}
		
//		if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP && event.getClickedBlock().getType() == Material.NETHERRACK) {
//			Block fire = event.getClickedBlock().getRelative(event.getBlockFace());
//			if (fire.getType() == Material.FIRE && !event.getPlayer().hasPermission(Perms.EDIT)) {
//				event.setCancelled(true);
//			}
//		}
	}

	private boolean isRedstoneComponent(Material type) {
		return type == Material.LEVER || type == Material.STONE_BUTTON || type == Material.WOOD_BUTTON;
	}

	@EventHandler(ignoreCancelled = true)
	public void weather(WeatherChangeEvent event) {
		if (event.toWeatherState() != LobbyEssentials.rain) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void thunder(ThunderChangeEvent event) {
		if (event.toThunderState() != LobbyEssentials.thunder) {
			event.setCancelled(true);
		}
	}
	
	private boolean isSign(Material mat) {
		return mat == Material.SIGN_POST || mat == Material.WALL_SIGN;
	}
}
