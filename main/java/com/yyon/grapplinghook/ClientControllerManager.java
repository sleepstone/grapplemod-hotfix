package com.yyon.grapplinghook;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import com.yyon.grapplinghook.controllers.airfrictionController;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.controllers.repelController;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.launcherItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class ClientControllerManager {
	public static ClientControllerManager instance;

	public ClientControllerManager() {
		instance = this;
	}
	
	public HashMap<Integer, Long> enderlaunchtimer = new HashMap<Integer, Long>();
	
	public double rocketFuel = 1.0;
	public double rocketIncreaseTick = 0.0;
	public double rocketDecreaseTick = 0.0;
	
	public void onClientTick(PlayerEntity player) {
		if (this.iswallrunning(player, vec.motionvec(player))) {
			if (!grapplemod.controllers.containsKey(player.getId())) {
				grappleController controller = this.createControl(grapplemod.AIRID, -1, player.getId(), player.level, new vec(0,0,0), null, null);
				if (controller.getwalldirection() == null) {
					controller.unattach();
				}
			}
			
			if (grapplemod.controllers.containsKey(player.getId())) {
				tickssincelastonground = 0;
				alreadyuseddoublejump = false;
			}
		}
		
		this.checkdoublejump();
		
		this.checkslide(player);
		
		this.rocketFuel += this.rocketIncreaseTick;
		
		try {
			Collection<grappleController> controllers = grapplemod.controllers.values();
			for (grappleController controller : controllers) {
				controller.doClientTick();
			}
		} catch (ConcurrentModificationException e) {
			System.out.println("ConcurrentModificationException caught");
		}

		if (this.rocketFuel > 1) {this.rocketFuel = 1;}
		
		if (player.isOnGround()) {
			if (enderlaunchtimer.containsKey(player.getId())) {
				long timer = grapplemod.getTime(player.level) - enderlaunchtimer.get(player.getId());
				if (timer > 10) {
					this.resetlaunchertime(player.getId());
				}
			}
		}
	}

	public void checkslide(PlayerEntity player) {
		if (ClientEventHandlers.key_slide.isDown() && !grapplemod.controllers.containsKey(player.getId()) && this.issliding(player, vec.motionvec(player))) {
			this.createControl(grapplemod.AIRID, -1, player.getId(), player.level, new vec(0,0,0), null, null);
		}
	}

	public void launchplayer(PlayerEntity player) {
		long prevtime;
		if (enderlaunchtimer.containsKey(player.getId())) {
			prevtime = enderlaunchtimer.get(player.getId());
		} else {
			prevtime = 0;
		}
		long timer = grapplemod.getTime(player.level) - prevtime;
		if (timer > GrappleConfig.getconf().enderstaff.ender_staff_recharge) {
			if ((player.getItemInHand(Hand.MAIN_HAND)!=null && (player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof launcherItem || player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof grappleBow)) || (player.getItemInHand(Hand.OFF_HAND)!=null && (player.getItemInHand(Hand.OFF_HAND).getItem() instanceof launcherItem || player.getItemInHand(Hand.OFF_HAND).getItem() instanceof grappleBow))) {
				enderlaunchtimer.put(player.getId(), grapplemod.getTime(player.level));
				
	        	vec facing = vec.lookvec(player);
	        	
	        	GrappleCustomization custom = null;
	        	if (player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof grappleBow) {
	        		custom = ((grappleBow) player.getItemInHand(Hand.MAIN_HAND).getItem()).getCustomization(player.getItemInHand(Hand.MAIN_HAND));
	        	} else if (player.getItemInHand(Hand.OFF_HAND).getItem() instanceof grappleBow) {
	        		custom = ((grappleBow) player.getItemInHand(Hand.OFF_HAND).getItem()).getCustomization(player.getItemInHand(Hand.OFF_HAND));
	        	}
	        	
				if (!grapplemod.controllers.containsKey(player.getId())) {
					player.setOnGround(false);
					this.createControl(grapplemod.AIRID, -1, player.getId(), player.level, new vec(0,0,0), null, custom);
				}
				facing.mult_ip(GrappleConfig.getconf().enderstaff.ender_staff_strength);
				grapplemod.receiveEnderLaunch(player.getId(), facing.x, facing.y, facing.z);
			}
		}
	}
	
	public void resetlaunchertime(int playerid) {
		if (enderlaunchtimer.containsKey(playerid)) {
			enderlaunchtimer.put(playerid, (long) 0);
		}
	}

	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
		this.rocketDecreaseTick = 0.05 / 2.0 / rocket_active_time;
		this.rocketIncreaseTick = 0.05 / 2.0 / rocket_active_time / rocket_refuel_ratio;
	}
	

	public double getRocketFunctioning() {
		this.rocketFuel -= this.rocketIncreaseTick;
		this.rocketFuel -= this.rocketDecreaseTick;
		if (this.rocketFuel >= 0) {
			return 1;
		} else {
			this.rocketFuel = 0;
			return this.rocketIncreaseTick / this.rocketDecreaseTick / 2.0;
		}
	}
	
	public boolean iswallrunning(Entity entity, vec motion) {
		if (entity.horizontalCollision && !entity.isOnGround() && !entity.isCrouching()) {
			for (ItemStack stack : entity.getArmorSlots()) {
				if (stack != null) {
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
					if (enchantments.containsKey(grapplemod.wallrunenchantment)) {
						if (enchantments.get(grapplemod.wallrunenchantment) >= 1) {
							if (!ClientEventHandlers.key_jumpanddetach.isDown() && !Minecraft.getInstance().options.keyJump.isDown()) {
								BlockRayTraceResult raytraceresult = grapplemod.rayTraceBlocks(entity.level, vec.positionvec(entity), vec.positionvec(entity).add(new vec(0, -1, 0)));
								if (raytraceresult == null) {
									double current_speed = Math.sqrt(Math.pow(motion.x, 2) + Math.pow(motion.z,  2));
									if (current_speed >= GrappleConfig.getconf().enchantments.wallrun.wallrun_min_speed) {
										return true;
									}
								}
							}
						}
						break;
					}
				}
			}
		}
		return false;
	}

	boolean prevjumpbutton = false;
	int tickssincelastonground = 0;
	boolean alreadyuseddoublejump = false;
	
	public void checkdoublejump() {
		PlayerEntity player = Minecraft.getInstance().player;
		
		if (player.isOnGround()) {
			tickssincelastonground = 0;
			alreadyuseddoublejump = false;
		} else {
			tickssincelastonground++;
		}
		
		boolean isjumpbuttondown = Minecraft.getInstance().options.keyJump.isDown();
		
		if (isjumpbuttondown && !prevjumpbutton && !player.isInWater()) {
			
			if (tickssincelastonground > 3) {
				if (!alreadyuseddoublejump) {
					if (wearingdoublejumpenchant(player)) {
						if (!grapplemod.controllers.containsKey(player.getId()) || grapplemod.controllers.get(player.getId()) instanceof airfrictionController) {
							if (!grapplemod.controllers.containsKey(player.getId())) {
								this.createControl(grapplemod.AIRID, -1, player.getId(), player.level, new vec(0,0,0), null, null);
							}
							grappleController controller = grapplemod.controllers.get(player.getId());
							if (controller instanceof airfrictionController) {
								alreadyuseddoublejump = true;
								controller.doublejump();
							}
							grapplemod.proxy.playDoubleJumpSound(controller.entity);
						}
					}
				}
			}
		}
		
		prevjumpbutton = isjumpbuttondown;
		
	}

	public boolean wearingdoublejumpenchant(Entity entity) {
		if (entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.flying) {
			return false;
		}
		
		for (ItemStack stack : entity.getArmorSlots()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
				if (enchantments.containsKey(grapplemod.doublejumpenchantment)) {
					if (enchantments.get(grapplemod.doublejumpenchantment) >= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean isWearingSlidingEnchant(Entity entity) {
		for (ItemStack stack : entity.getArmorSlots()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
				if (enchantments.containsKey(grapplemod.slidingenchantment)) {
					if (enchantments.get(grapplemod.slidingenchantment) >= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean issliding(Entity entity, vec motion) {
		if (entity.isInWater()) {return false;}
		
		if (entity.isOnGround() && ClientEventHandlers.key_slide.isDown()) {
			if (isWearingSlidingEnchant(entity)) {
				boolean was_sliding = false;
				int id = entity.getId();
				if (grapplemod.controllers.containsKey(id)) {
					grappleController controller = grapplemod.controllers.get(id);
					if (controller instanceof airfrictionController) {
						airfrictionController afc = (airfrictionController) controller;
						if (afc.was_sliding) {
							was_sliding = true;
						}
					}
				}
				double speed = motion.removealong(new vec (0,1,0)).length();
				if (speed > GrappleConfig.getconf().enchantments.slide.sliding_end_min_speed && (was_sliding || speed > GrappleConfig.getconf().enchantments.slide.sliding_min_speed)) {
					return true;
				}
			}
		}
		
		return false;
	}


	public grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom) {
		grappleArrow arrow = null;
		Entity arrowentity = world.getEntity(arrowid);
		if (arrowentity != null && arrowentity instanceof grappleArrow) {
			arrow = (grappleArrow) arrowentity;
		}
		
		boolean multi = (custom != null) && (custom.doublehook);
		
		grappleController currentcontroller = grapplemod.controllers.get(entityid);
		if (currentcontroller != null && !(multi && currentcontroller.custom != null && currentcontroller.custom.doublehook)) {
			currentcontroller.unattach();
		}
		
//		System.out.println(blockpos);
		
		grappleController control = null;
		if (id == grapplemod.GRAPPLEID) {
			if (!multi) {
				control = new grappleController(arrowid, entityid, world, pos, id, custom);
			} else {
				control = grapplemod.controllers.get(entityid);
				boolean created = false;
				if (control != null && control.getClass().equals(grappleController.class)) {
					grappleController c = (grappleController) control;
					if (control.custom.doublehook) {
						if (arrow != null && arrow instanceof grappleArrow) {
							grappleArrow multiarrow = (grappleArrow) arrowentity;
							created = true;
							c.addArrow(multiarrow);
						}
					}
				}
				if (!created) {
					control = new grappleController(arrowid, entityid, world, pos, id, custom);
				}
			}
		} else if (id == grapplemod.REPELID) {
			control = new repelController(arrowid, entityid, world, pos, id);
		} else if (id == grapplemod.AIRID) {
			control = new airfrictionController(arrowid, entityid, world, pos, id, custom);
		} else {
			return null;
		}
		if (blockpos != null && control != null) {
			grapplemod.controllerpos.put(blockpos, control);
		}
		
		Entity e = world.getEntity(entityid);
		if (e != null && e instanceof ClientPlayerEntity) {
			ClientPlayerEntity p = (ClientPlayerEntity) e;
			control.receivePlayerMovementMessage(p.input.leftImpulse, p.input.forwardImpulse, p.input.jumping, p.input.shiftKeyDown);
		}
		
		return control;
	}
}