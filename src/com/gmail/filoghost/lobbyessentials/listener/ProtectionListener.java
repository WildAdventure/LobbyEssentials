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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.gmail.filoghost.lobbyessentials.VanishUtils;

public class ProtectionListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event) {

		Material type = event.getBlock().getType();

		if (type == Material.WALL_SIGN ||
			type == Material.IRON_DOOR_BLOCK ||
			type == Material.SNOW ||
			type == Material.TORCH ||
			event.getChangedType() == Material.VINE) {
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPortalSpawn(CreatureSpawnEvent event) {
		switch (event.getSpawnReason()) {
			case BUILD_IRONGOLEM:
			case BUILD_WITHER:
			case BUILD_SNOWMAN:
			case SPAWNER_EGG:
				// Allowed
			
			default:
				event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDecay(LeavesDecayEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onFade(BlockFadeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onForm(BlockFormEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!canChangeWorld(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!canChangeWorld(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		if (!canChangeWorld(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBucketFill(PlayerBucketFillEvent event) {
		if (!canChangeWorld(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingPlace(HangingPlaceEvent event) {
		if (!canChangeWorld(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingBreak(HangingBreakEvent event) {

		if (!(event instanceof HangingBreakByEntityEvent)) {
			event.setCancelled(true);
			return;
		}

		HangingBreakByEntityEvent breakByEntityEvent = (HangingBreakByEntityEvent) event;

		if (breakByEntityEvent.getRemover().getType() == EntityType.PLAYER) {
			Player remover = (Player) breakByEntityEvent.getRemover();
			if (!canChangeWorld(remover)) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteractItemFrame(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
        	if (!canChangeWorld(event.getPlayer())) {
				event.setCancelled(true);
			}
        }
    }

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (!canChangeWorld(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (!canChangeWorld(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onExplode(EntityExplodeEvent event) {
		event.blockList().clear();
	}

	@EventHandler(ignoreCancelled = true)
	public void onGrow(BlockGrowEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onSpread(BlockIgniteEvent event) {
		// Fuoco solo manuale
		if (event.getCause() != IgniteCause.FLINT_AND_STEEL) {
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onMobTarget(EntityTargetEvent event) {
		// I mob non attaccano nessuno
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void food(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	public static boolean canChangeWorld(Player player) {
		return canChangeWorld(player, false);
	}
	
	public static boolean canChangeWorld(Player player, boolean silent) {
		if (player.hasPermission("lobbyessentials.edit")) {
			if (VanishUtils.isVanished(player)) {
				if (VanishUtils.canInteract(player)) {
					return true;
				} else {
					if (!silent) {
						player.sendMessage(ChatColor.DARK_AQUA + "Sei in vanish, scrivi \"/ni\" per interagire.");
					}
				}
			} else {
				return true;
			}
		}
		
		return false;
	}

}
