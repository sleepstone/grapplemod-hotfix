package com.yyon.grapplinghook.items;

import java.util.List;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.ClientProxyClass;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class grappleBow extends Item implements KeypressItem {
//	public static HashMap<Entity, grappleArrow> grapplearrows1 = new HashMap<Entity, grappleArrow>();
//	public static HashMap<Entity, grappleArrow> grapplearrows2 = new HashMap<Entity, grappleArrow>();
	
	public grappleBow() {
		super(new Item.Properties().stacksTo(1).tab(grapplemod.tabGrapplemod));
//		maxStackSize = 1;
//		setFull3D();
//		setUnlocalizedName("grapplinghook");
//		
//		this.setMaxDamage(GrappleConfig.getconf().default_durability);
//		
//		setCreativeTab(grapplemod.tabGrapplemod);
		
//		MinecraftForge.EVENT_BUS.register(this);
	}

	/*
    @Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}
	
	public boolean hasArrow(Entity entity) {
		grappleArrow arrow1 = getArrowLeft(entity);
		grappleArrow arrow2 = getArrowRight(entity);
		return (arrow1 != null) || (arrow2 != null);
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = new ItemStack(Items.LEATHER, 1);
        if (mat != null && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true;
        return super.getIsRepairable(toRepair, repair);
	}

	public void setArrowLeft(Entity entity, grappleArrow arrow) {
		grappleBow.grapplearrows1.put(entity, arrow);
	}
	public void setArrowRight(Entity entity, grappleArrow arrow) {
		grappleBow.grapplearrows2.put(entity, arrow);
	}
	public grappleArrow getArrowLeft(Entity entity) {
		if (grappleBow.grapplearrows1.containsKey(entity)) {
			grappleArrow arrow = grappleBow.grapplearrows1.get(entity);
			if (arrow != null && !arrow.isDead) {
				return arrow;
			}
		}
		return null;
	}
	public grappleArrow getArrowRight(Entity entity) {
		if (grappleBow.grapplearrows2.containsKey(entity)) {
			grappleArrow arrow = grappleBow.grapplearrows2.get(entity);
			if (arrow != null && !arrow.isDead) {
				return arrow;
			}
		}
		return null;
	}	
	
	public void dorightclick(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
        if (!worldIn.isRemote) {
    	}
	}
	
	public void throwBoth(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
		grappleArrow arrow_left = getArrowLeft(entityLiving);
		grappleArrow arrow_right = getArrowRight(entityLiving);

		if (arrow_left != null || arrow_right != null) {
			detachBoth(entityLiving);
    		return;
		}
		
    	GrappleCustomization custom = this.getCustomization(stack);
  		double angle = custom.angle;
  		double verticalangle = custom.verticalthrowangle;
  		if (entityLiving.isSneaking()) {
  			angle = custom.sneakingangle;
  			verticalangle = custom.sneakingverticalthrowangle;
  		}

    	if (!(!custom.doublehook || angle == 0)) {
    		throwLeft(stack, worldIn, entityLiving, righthand);
    	}
		throwRight(stack, worldIn, entityLiving, righthand);

		stack.damageItem(1, entityLiving);
        worldIn.playSound((EntityPlayer)null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
	}
	
	public boolean throwLeft(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	
  		double angle = custom.angle;
  		double verticalangle = custom.verticalthrowangle;
  		
  		if (entityLiving.isSneaking()) {
  			angle = custom.sneakingangle;
  			verticalangle = custom.sneakingverticalthrowangle;
  		}
  		
  		EntityLivingBase player = entityLiving;
  		
  		vec anglevec = new vec(0,0,1).rotate_yaw(Math.toRadians(-angle)).rotate_pitch(Math.toRadians(verticalangle));
  		anglevec = anglevec.rotate_pitch(Math.toRadians(-player.rotationPitch));
  		anglevec = anglevec.rotate_yaw(Math.toRadians(player.rotationYaw));
        float velx = -MathHelper.sin((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
        float vely = -MathHelper.sin((float) anglevec.getPitch() * 0.017453292F);
        float velz = MathHelper.cos((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
		grappleArrow entityarrow = this.createarrow(stack, worldIn, entityLiving, false, true);// new grappleArrow(worldIn, player, false);
        float extravelocity = (float) vec.motionvec(entityLiving).dist_along(new vec(velx, vely, velz));
        if (extravelocity < 0) { extravelocity = 0; }
        entityarrow.shoot((double) velx, (double) vely, (double) velz, entityarrow.getVelocity() + extravelocity, 0.0F);
        
		worldIn.spawnEntity(entityarrow);
		setArrowLeft(entityLiving, entityarrow);    			
		
		return true;
	}
	
	public void throwRight(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	
  		double angle = custom.angle;
  		double verticalangle = custom.verticalthrowangle;
  		if (entityLiving.isSneaking()) {
  			angle = custom.sneakingangle;
  			verticalangle = custom.sneakingverticalthrowangle;
  		}
  		
    	if (!custom.doublehook || angle == 0) {
			grappleArrow entityarrow = this.createarrow(stack, worldIn, entityLiving, righthand, false);
      		vec anglevec = new vec(0,0,1).rotate_pitch(Math.toRadians(verticalangle));
      		anglevec = anglevec.rotate_pitch(Math.toRadians(-entityLiving.rotationPitch));
      		anglevec = anglevec.rotate_yaw(Math.toRadians(entityLiving.rotationYaw));
	        float velx = -MathHelper.sin((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
	        float vely = -MathHelper.sin((float) anglevec.getPitch() * 0.017453292F);
	        float velz = MathHelper.cos((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
	        float extravelocity = (float) vec.motionvec(entityLiving).dist_along(new vec(velx, vely, velz));
	        if (extravelocity < 0) { extravelocity = 0; }
	        entityarrow.shoot((double) velx, (double) vely, (double) velz, entityarrow.getVelocity() + extravelocity, 0.0F);
			setArrowRight(entityLiving, entityarrow);
			worldIn.spawnEntity(entityarrow);
    	} else {
      		EntityLivingBase player = entityLiving;
      		
      		vec anglevec = new vec(0,0,1).rotate_yaw(Math.toRadians(angle)).rotate_pitch(Math.toRadians(verticalangle));
      		anglevec = anglevec.rotate_pitch(Math.toRadians(-player.rotationPitch));
      		anglevec = anglevec.rotate_yaw(Math.toRadians(player.rotationYaw));
	        float velx = -MathHelper.sin((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
	        float vely = -MathHelper.sin((float) anglevec.getPitch() * 0.017453292F);
	        float velz = MathHelper.cos((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
			grappleArrow entityarrow = this.createarrow(stack, worldIn, entityLiving, true, true);//new grappleArrow(worldIn, player, true);
//            entityarrow.shoot(player, (float) anglevec.getPitch(), (float)anglevec.getYaw(), 0.0F, entityarrow.getVelocity(), 0.0F);
	        float extravelocity = (float) vec.motionvec(entityLiving).dist_along(new vec(velx, vely, velz));
	        if (extravelocity < 0) { extravelocity = 0; }
	        entityarrow.shoot((double) velx, (double) vely, (double) velz, entityarrow.getVelocity() + extravelocity, 0.0F);
            
			worldIn.spawnEntity(entityarrow);
			setArrowRight(entityLiving, entityarrow);
		}
	}
	
	public void detachBoth(EntityLivingBase entityLiving) {
		grappleArrow arrow1 = getArrowLeft(entityLiving);
		grappleArrow arrow2 = getArrowRight(entityLiving);

		setArrowLeft(entityLiving, null);
		setArrowRight(entityLiving, null);
		
		if (arrow1 != null) {
			arrow1.removeServer();
		}
		if (arrow2 != null) {
			arrow2.removeServer();
		}

		int id = entityLiving.getEntityId();
		grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, entityLiving.world);

		if (grapplemod.attached.contains(id)) {
			grapplemod.attached.remove(new Integer(id));
		}
	}
	
	public void detachLeft(EntityLivingBase entityLiving) {
		grappleArrow arrow1 = getArrowLeft(entityLiving);
		
		setArrowLeft(entityLiving, null);
		
		if (arrow1 != null) {
			arrow1.removeServer();
		}
		
		int id = entityLiving.getEntityId();
		
		// remove controller if hook is attached
		if (getArrowRight(entityLiving) == null) {
			grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, entityLiving.world);
		} else {
			grapplemod.sendtocorrectclient(new DetachSingleHookMessage(id, arrow1.getEntityId()), id, entityLiving.world);
		}
		
		if (grapplemod.attached.contains(id)) {
			grapplemod.attached.remove(new Integer(id));
		}
	}
	
	public void detachRight(EntityLivingBase entityLiving) {
		grappleArrow arrow2 = getArrowRight(entityLiving);
		
		setArrowRight(entityLiving, null);
		
		if (arrow2 != null) {
			arrow2.removeServer();
		}
		
		int id = entityLiving.getEntityId();
		
		// remove controller if hook is attached
		if (getArrowLeft(entityLiving) == null) {
			grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, entityLiving.world);
		} else {
			grapplemod.sendtocorrectclient(new DetachSingleHookMessage(id, arrow2.getEntityId()), id, entityLiving.world);
		}
		
		if (grapplemod.attached.contains(id)) {
			grapplemod.attached.remove(new Integer(id));
		}
	}
	
    public double getAngle(EntityLivingBase entity, ItemStack stack) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	if (entity.isSneaking()) {
    		return custom.sneakingangle;
    	} else {
    		return custom.angle;
    	}
    }
	
	public grappleArrow createarrow(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand, boolean isdouble) {
		grappleArrow arrow = new grappleArrow(worldIn, entityLiving, righthand, this.getCustomization(stack), isdouble);
		grapplemod.addarrow(entityLiving.getEntityId(), arrow);
		return arrow;
	}
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer entityLiving, EnumHand hand)
    {
    	ItemStack stack = entityLiving.getHeldItem(hand);
        if (!worldIn.isRemote) {
	        this.dorightclick(stack, worldIn, entityLiving, hand == EnumHand.MAIN_HAND);
        }
        entityLiving.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
	

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn,
			EntityLivingBase entityLiving, int timeLeft) {
		if (!worldIn.isRemote) {
//			stack.getSubCompound("grapplemod", true).setBoolean("extended", (this.getArrow(entityLiving, worldIn) != null));
		}
		super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
	}
	*/

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	/*
    @Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.NONE;
	}

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
    	return true;
    }
    */
    
	@Override
	public void onCustomKeyDown(ItemStack stack, PlayerEntity player, KeypressItem.Keys key, boolean ismainhand) {
/*		if (player.world.isRemote) {
			if (key == KeypressItem.Keys.LAUNCHER) {
				if (this.getCustomization(stack).enderstaff) {
					grapplemod.proxy.launchplayer(player);
				}
			} else if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
				grapplemod.network.sendToServer(new KeypressMessage(key, true));
			} else if (key == KeypressItem.Keys.ROCKET) {
				GrappleCustomization custom = this.getCustomization(stack);
				if (custom.rocket) {
					grapplemod.proxy.startrocket(player, custom);
				}
			}
		} else {
			if (key == KeypressItem.Keys.THROWBOTH) {
	        	throwBoth(stack, player.world, player, ismainhand);
			} else if (key == KeypressItem.Keys.THROWLEFT) {
				grappleArrow arrow1 = getArrowLeft(player);

	    		if (arrow1 != null) {
	    			detachLeft(player);
		    		return;
				}
				
				boolean threw = throwLeft(stack, player.world, player, ismainhand);

				if (threw) {
					stack.damageItem(1, player);
			        player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
				}
			} else if (key == KeypressItem.Keys.THROWRIGHT) {
				grappleArrow arrow2 = getArrowRight(player);

	    		if (arrow2 != null) {
	    			detachRight(player);
		    		return;
				}
				
				throwRight(stack, player.world, player, ismainhand);

				stack.damageItem(1, player);
		        player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
			}
		}*/
	}
	
	@Override
	public void onCustomKeyUp(ItemStack stack, PlayerEntity player, KeypressItem.Keys key, boolean ismainhand) {
/*		if (player.world.isRemote) {
			if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
				grapplemod.network.sendToServer(new KeypressMessage(key, false));
			}
		} else {
	    	GrappleCustomization custom = this.getCustomization(stack);
	    	
	    	if (custom.detachonkeyrelease) {
	    		grappleArrow arrow_left = getArrowLeft(player);
	    		grappleArrow arrow_right = getArrowRight(player);
	    		
				if (key == KeypressItem.Keys.THROWBOTH) {
					detachBoth(player);
				} else if (key == KeypressItem.Keys.THROWLEFT) {
		    		if (arrow_left != null) detachLeft(player);
				} else if (key == KeypressItem.Keys.THROWRIGHT) {
		    		if (arrow_right != null) detachRight(player);
				}
	    	}
		}*/
	}
   
	/*
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
    	return true;
    }
   
    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos k, EntityPlayer player)
    {
      return true;
    }
    */
    
    public GrappleCustomization getCustomization(ItemStack itemstack) {
    	if (itemstack.hasTag()) {
        	GrappleCustomization custom = new GrappleCustomization();
    		custom.loadNBT(itemstack.getTag());
        	return custom;
    	} else {
    		GrappleCustomization custom = this.getDefaultCustomization();

			CompoundNBT nbt = custom.writeNBT();
			
			itemstack.setTag(nbt);
    		
    		return custom;
    	}
    }
    
    public GrappleCustomization getDefaultCustomization() {
    	return new GrappleCustomization();
    }
    
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag par4) {
		GrappleCustomization custom = getCustomization(stack);
		
		if (Screen.hasControlDown()) {
			if (!custom.detachonkeyrelease) {
				list.add(new StringTextComponent(ClientProxyClass.key_boththrow.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.throw.desc")));
				list.add(new StringTextComponent(ClientProxyClass.key_boththrow.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.release.desc")));
				list.add(new StringTextComponent(grapplemod.proxy.localize("grappletooltip.double.desc") + ClientProxyClass.key_boththrow.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.releaseandthrow.desc")));
			} else {
				list.add(new StringTextComponent(ClientProxyClass.key_boththrow.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.throwhold.desc")));
			}
			list.add(new StringTextComponent(grapplemod.proxy.getkeyname(grapplemod.keys.keyBindForward) + ", " +
					grapplemod.proxy.getkeyname(grapplemod.keys.keyBindLeft) + ", " +
					grapplemod.proxy.getkeyname(grapplemod.keys.keyBindBack) + ", " +
					grapplemod.proxy.getkeyname(grapplemod.keys.keyBindRight) +
					" " + grapplemod.proxy.localize("grappletooltip.swing.desc")));
			list.add(new StringTextComponent(ClientProxyClass.key_jumpanddetach.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.jump.desc")));
			list.add(new StringTextComponent(ClientProxyClass.key_slow.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.slow.desc")));
			list.add(new StringTextComponent((custom.climbkey ? ClientProxyClass.key_climb.getTranslatedKeyMessage().getString() + " + " : "") +
					ClientProxyClass.key_climbup.getTranslatedKeyMessage().getString() + 
					" " + grapplemod.proxy.localize("grappletooltip.climbup.desc")));
			list.add(new StringTextComponent((custom.climbkey ? ClientProxyClass.key_climb.getTranslatedKeyMessage().getString() + " + " : "") +
					ClientProxyClass.key_climbdown.getTranslatedKeyMessage().getString() + 
					" " + grapplemod.proxy.localize("grappletooltip.climbdown.desc")));
			if (custom.enderstaff) {
				list.add(new StringTextComponent(ClientProxyClass.key_enderlaunch.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.enderlaunch.desc")));
			}
			if (custom.rocket) {
				list.add(new StringTextComponent(ClientProxyClass.key_rocket.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.rocket.desc")));
			}
			if (custom.motor) {
				if (custom.motorwhencrouching && !custom.motorwhennotcrouching) {
					list.add(new StringTextComponent(ClientProxyClass.key_motoronoff.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.motoron.desc")));
				}
				else if (!custom.motorwhencrouching && custom.motorwhennotcrouching) {
					list.add(new StringTextComponent(ClientProxyClass.key_motoronoff.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.motoroff.desc")));
				}
			}
			if (custom.doublehook) {
				if (!custom.detachonkeyrelease) {
					list.add(new StringTextComponent(ClientProxyClass.key_leftthrow.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.throwleft.desc")));
					list.add(new StringTextComponent(ClientProxyClass.key_rightthrow.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.throwright.desc")));
				} else {
					list.add(new StringTextComponent(ClientProxyClass.key_leftthrow.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.throwlefthold.desc")));
					list.add(new StringTextComponent(ClientProxyClass.key_rightthrow.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.throwrighthold.desc")));
				}
			} else {
				list.add(new StringTextComponent(ClientProxyClass.key_rightthrow.getTranslatedKeyMessage().getString() + " " + grapplemod.proxy.localize("grappletooltip.throwalt.desc")));
			}
		} else {
			if (Screen.hasShiftDown()) {
				for (String option : GrappleCustomization.booleanoptions) {
					if (custom.isoptionvalid(option) && custom.getBoolean(option)) {
						list.add(new StringTextComponent(grapplemod.proxy.localize(custom.getName(option))));
					}
				}
				for (String option : GrappleCustomization.doubleoptions) {
					if (custom.isoptionvalid(option)) {
						list.add(new StringTextComponent(grapplemod.proxy.localize(custom.getName(option)) + ": " + Math.floor(custom.getDouble(option) * 100) / 100));
					}
				}
			} else {
				if (custom.doublehook) {
					list.add(new StringTextComponent(grapplemod.proxy.localize(custom.getName("doublehook"))));
				}
				if (custom.motor) {
					if (custom.smartmotor) {
						list.add(new StringTextComponent(grapplemod.proxy.localize(custom.getName("smartmotor"))));
					} else {
						list.add(new StringTextComponent(grapplemod.proxy.localize(custom.getName("motor"))));
					}
				}
				if (custom.enderstaff) {
					list.add(new StringTextComponent(grapplemod.proxy.localize(custom.getName("enderstaff"))));
				}
				if (custom.rocket) {
					list.add(new StringTextComponent(grapplemod.proxy.localize(custom.getName("rocket"))));
				}
				if (custom.attract) {
					list.add(new StringTextComponent(grapplemod.proxy.localize(custom.getName("attract"))));
				}
				if (custom.repel) {
					list.add(new StringTextComponent(grapplemod.proxy.localize(custom.getName("repel"))));
				}
				
				list.add(new StringTextComponent(""));
				list.add(new StringTextComponent(grapplemod.proxy.localize("grappletooltip.shiftcontrols.desc")));
				list.add(new StringTextComponent(grapplemod.proxy.localize("grappletooltip.controlconfiguration.desc")));
			}
		}
	}

	
    /*
	@Override
    @SideOnly(Side.CLIENT)
    public ItemStack getDefaultInstance()
    {
        ItemStack stack = new ItemStack(this);
        this.getCustomization(stack);
        return stack;
    }
	
	@Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
        	ItemStack stack = new ItemStack(this);
        	this.getCustomization(stack);
            items.add(stack);
        }
    }
	
	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		int id = player.getEntityId();
		grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, player.world);
		
		if (grapplemod.attached.contains(id)) {
			grapplemod.attached.remove(id);
		}
		
		if (grapplearrows1.containsKey(player)) {
			grappleArrow arrow1 = grapplearrows1.get(player);
			setArrowLeft(player, null);
			if (arrow1 != null) {
				arrow1.removeServer();
			}
		}
		
		if (grapplearrows2.containsKey(player)) {
			grappleArrow arrow2 = grapplearrows2.get(player);
			setArrowLeft(player, null);
			if (arrow2 != null) {
				arrow2.removeServer();
			}
		}
		
		return super.onDroppedByPlayer(item, player);
	}
	*/
}
