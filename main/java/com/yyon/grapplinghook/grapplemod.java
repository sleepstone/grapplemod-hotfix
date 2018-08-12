package com.yyon.grapplinghook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.yyon.grapplinghook.blocks.BlockGrappleModifier;
import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.airfrictionController;
import com.yyon.grapplinghook.controllers.enderController;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.controllers.hookControl;
import com.yyon.grapplinghook.controllers.magnetController;
import com.yyon.grapplinghook.controllers.multihookController;
import com.yyon.grapplinghook.controllers.repelController;
import com.yyon.grapplinghook.controllers.smartHookControl;
import com.yyon.grapplinghook.entities.enderArrow;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.entities.hookArrow;
import com.yyon.grapplinghook.entities.magnetArrow;
import com.yyon.grapplinghook.entities.multihookArrow;
import com.yyon.grapplinghook.entities.smartHookArrow;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.items.enderBow;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.hookBow;
import com.yyon.grapplinghook.items.launcherItem;
import com.yyon.grapplinghook.items.magnetBow;
import com.yyon.grapplinghook.items.multiBow;
import com.yyon.grapplinghook.items.repeller;
import com.yyon.grapplinghook.items.smartHookBow;
import com.yyon.grapplinghook.items.upgrades.BaseUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.DoubleUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.ForcefieldUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.MagnetUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.MotorUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.RopeUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.StaffUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.SwingUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.ThrowUpgradeItem;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;
import com.yyon.grapplinghook.network.GrappleClickMessage;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.GrappleModifierMessage;
import com.yyon.grapplinghook.network.MultiHookMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;
import com.yyon.grapplinghook.network.SegmentMessage;
import com.yyon.grapplinghook.network.ToolConfigMessage;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

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

//TODO
// Pull mobs
// Attach 2 things together

@Mod(modid = grapplemod.MODID, version = grapplemod.VERSION)
public class grapplemod {

	public grapplemod(){}

    public static final String MODID = "grapplemod";
    
    public static final String VERSION = "1.12.2-v10";

    public static Item grapplebowitem;
    public static Item hookshotitem;
    public static Item smarthookitem;
    public static Item enderhookitem;
    public static Item launcheritem;
    public static Item longfallboots;
    public static Item magnetbowitem;
    public static Item repelleritem;
    public static Item multihookitem;

    public static Item baseupgradeitem;
    public static Item doubleupgradeitem;
    public static Item forcefieldupgradeitem;
    public static Item magnetupgradeitem;
    public static Item motorupgradeitem;
    public static Item ropeupgradeitem;
    public static Item staffupgradeitem;
    public static Item swingupgradeitem;
    public static Item throwupgradeitem;

	public static Object instance;
	
	public static SimpleNetworkWrapper network;
	
	public static HashMap<Integer, grappleController> controllers = new HashMap<Integer, grappleController>(); // client side
	public static HashMap<BlockPos, grappleController> controllerpos = new HashMap<BlockPos, grappleController>();
	public static HashSet<Integer> attached = new HashSet<Integer>(); // server side
	
	private static int controllerid = 0;
	public static int GRAPPLEID = controllerid++;
	public static int ENDERID = controllerid++;
	public static int HOOKID = controllerid++;
	public static int MAGNETID = controllerid++;
	public static int REPELID = controllerid++;
	public static int MULTIID = controllerid++;
	public static int MULTISUBID = controllerid++;
	public static int AIRID = controllerid++;
	public static int SMARTHOOKID = controllerid++;
	
	public static int REPELCONFIGS = 0;
//	public static int REPELSPEED = REPELCONFIGS++;
	public static int REPELSTRONG = REPELCONFIGS++;
	public static int REPELWEAK = REPELCONFIGS++;
	public static int REPELNONE = REPELCONFIGS++;
	
	public static int grapplingLength = 0;
	public static boolean anyblocks = true;
	public static ArrayList<Block> grapplingblocks;
	public static boolean removeblocks = false;
	
	public static Block blockGrappleModifier;
	public static ItemBlock itemBlockGrappleModifier;
	
	public ResourceLocation resourceLocation;
	
	public enum upgradeCategories {
		ROPE ("Rope"), 
		THROW ("Hook Thrower"), 
		MOTOR ("Motor"), 
		SWING ("Swing Speed"), 
		STAFF ("Ender Staff"), 
		FORCEFIELD ("Forcefield"), 
		MAGNET ("Hook Magnet"), 
		DOUBLE ("Double Hook");
		
		public String description;
		private upgradeCategories(String desc) {
			this.description = desc;
		}
		
		public static upgradeCategories fromInt(int i) {
			return upgradeCategories.values()[i];
		}
		public int toInt() {
			for (int i = 0; i < size(); i++) {
				if (upgradeCategories.values()[i] == this) {
					return i;
				}
			}
			return -1;
		}
		public static int size() {
			return upgradeCategories.values().length;
		}
		public Item getItem() {
			if (this == upgradeCategories.ROPE) {
				return ropeupgradeitem;
			} else if (this == upgradeCategories.THROW) {
				return throwupgradeitem;
			} else if (this == upgradeCategories.MOTOR) {
				return motorupgradeitem;
			} else if (this == upgradeCategories.SWING) {
				return swingupgradeitem;
			} else if (this == upgradeCategories.STAFF) {
				return staffupgradeitem;
			} else if (this == upgradeCategories.FORCEFIELD) {
				return forcefieldupgradeitem;
			} else if (this == upgradeCategories.MAGNET) {
				return magnetupgradeitem;
			} else if (this == upgradeCategories.DOUBLE) {
				return doubleupgradeitem;
			}
			return null;
		}
	};
	
	@SidedProxy(clientSide="com.yyon.grapplinghook.ClientProxyClass", serverSide="com.yyon.grapplinghook.ServerProxyClass")
	public static CommonProxyClass proxy;
	
	@EventHandler
	public void load(FMLInitializationEvent event){
	}

	public void registerRenderers(){
	}
	public void generateNether(World world, Random random, int chunkX, int chunkZ){}
	public void generateSurface(World world, Random random, int chunkX, int chunkZ){}
	public int addFuel(ItemStack fuel){
		return 0;
	}

	public void serverLoad(FMLServerStartingEvent event){
		event.getServer().worlds[0].getGameRules().addGameRule("grapplingLength", "0", GameRules.ValueType.NUMERICAL_VALUE);
		event.getServer().worlds[0].getGameRules().addGameRule("grapplingBlocks", "any", GameRules.ValueType.ANY_VALUE);
		event.getServer().worlds[0].getGameRules().addGameRule("grapplingNonBlocks", "none", GameRules.ValueType.ANY_VALUE);
	}
	
	public static void updateMaxLen(World world) {
		grapplemod.grapplingLength = world.getMinecraftServer().worlds[0].getGameRules().getInt("grapplingLength");
	}
	
	public static void updateGrapplingBlocks(World world) {
		String s = world.getMinecraftServer().worlds[0].getGameRules().getString("grapplingBlocks");
		if (s.equals("any") || s.equals("")) {
			s = world.getMinecraftServer().worlds[0].getGameRules().getString("grapplingNonBlocks");
			if (s.equals("none") || s.equals("")) {
				anyblocks = true;
			} else {
				anyblocks = false;
				removeblocks = true;
			}
		} else {
			anyblocks = false;
			removeblocks = false;
		}
	
		if (!anyblocks) {
			String[] blockstr = s.split(",");
			
			grapplingblocks = new ArrayList<Block>();
			
		    for(String str:blockstr){
		    	str = str.trim();
		    	String modid;
		    	String name;
		    	if (str.contains(":")) {
		    		String[] splitstr = str.split(":");
		    		modid = splitstr[0];
		    		name = splitstr[1];
		    	} else {
		    		modid = "minecraft";
		    		name = str;
		    	}
		    	
		    	Block b = Block.REGISTRY.getObject(new ResourceLocation(modid, name));
		    	
		        grapplingblocks.add(b);
		    }
		}
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
//		System.out.println("REGISTERING ITEMS");
//		System.out.println(grapplebowitem);
	    event.getRegistry().registerAll(grapplebowitem, hookshotitem, smarthookitem, launcheritem, longfallboots, enderhookitem, magnetbowitem, repelleritem, multihookitem, baseupgradeitem, doubleupgradeitem, forcefieldupgradeitem, magnetupgradeitem, motorupgradeitem, ropeupgradeitem, staffupgradeitem, swingupgradeitem, throwupgradeitem);

	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
//		System.out.println("PREINIT!!!");
		grapplebowitem = new grappleBow();
		grapplebowitem.setRegistryName("grapplinghook");
		hookshotitem = new hookBow();
		hookshotitem.setRegistryName("hookshot");
		smarthookitem = new smartHookBow();
		smarthookitem.setRegistryName("smarthook");
		launcheritem = new launcherItem();
		launcheritem.setRegistryName("launcheritem");
		longfallboots = new LongFallBoots(ItemArmor.ArmorMaterial.DIAMOND, 3);
		longfallboots.setRegistryName("longfallboots");
		enderhookitem = new enderBow();
		enderhookitem.setRegistryName("enderhook");
		magnetbowitem = new magnetBow();
		magnetbowitem.setRegistryName("magnetbow");
		repelleritem = new repeller();
		repelleritem.setRegistryName("repeller");
		multihookitem = new multiBow();
		multihookitem.setRegistryName("multihook");
	    baseupgradeitem = new BaseUpgradeItem();
	    baseupgradeitem.setRegistryName("baseupgradeitem");
	    doubleupgradeitem = new DoubleUpgradeItem();
	    doubleupgradeitem.setRegistryName("doubleupgradeitem");
	    forcefieldupgradeitem = new ForcefieldUpgradeItem();
	    forcefieldupgradeitem.setRegistryName("forcefieldupgradeitem");
	    magnetupgradeitem = new MagnetUpgradeItem();
	    magnetupgradeitem.setRegistryName("magnetupgradeitem");
	    motorupgradeitem = new MotorUpgradeItem();
	    motorupgradeitem.setRegistryName("motorupgradeitem");
	    ropeupgradeitem = new RopeUpgradeItem();
	    ropeupgradeitem.setRegistryName("ropeupgradeitem");
	    staffupgradeitem = new StaffUpgradeItem();
	    staffupgradeitem.setRegistryName("staffupgradeitem");
	    swingupgradeitem = new SwingUpgradeItem();
	    swingupgradeitem.setRegistryName("swingupgradeitem");
	    throwupgradeitem = new ThrowUpgradeItem();
	    throwupgradeitem.setRegistryName("throwupgradeitem");
	    
//		System.out.println(grapplebowitem);
		
		resourceLocation = new ResourceLocation(grapplemod.MODID, "grapplemod");
		
		registerEntity(grappleArrow.class, "grappleArrow");
		registerEntity(enderArrow.class, "enderArrow");
		registerEntity(hookArrow.class, "hookArrow");
		registerEntity(magnetArrow.class, "magnetArrow");
		registerEntity(multihookArrow.class, "multihookArrow");
		registerEntity(smartHookArrow.class, "smartHookArrow");
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel("grapplemodchannel");
		byte id = 0;
		network.registerMessage(PlayerMovementMessage.Handler.class, PlayerMovementMessage.class, id++, Side.SERVER);
		network.registerMessage(GrappleAttachMessage.Handler.class, GrappleAttachMessage.class, id++, Side.CLIENT);
		network.registerMessage(GrappleEndMessage.Handler.class, GrappleEndMessage.class, id++, Side.SERVER);
		network.registerMessage(GrappleClickMessage.Handler.class, GrappleClickMessage.class, id++, Side.CLIENT);
		network.registerMessage(GrappleAttachPosMessage.Handler.class, GrappleAttachPosMessage.class, id++, Side.CLIENT);
		network.registerMessage(MultiHookMessage.Handler.class, MultiHookMessage.class, id++, Side.SERVER);
		network.registerMessage(ToolConfigMessage.Handler.class, ToolConfigMessage.class, id++, Side.SERVER);
		network.registerMessage(SegmentMessage.Handler.class, SegmentMessage.class, id++, Side.CLIENT);
		network.registerMessage(GrappleModifierMessage.Handler.class, GrappleModifierMessage.class, id++, Side.SERVER);
		
		blockGrappleModifier = (BlockGrappleModifier)(new BlockGrappleModifier().setUnlocalizedName("block_grapple_modifier"));
		blockGrappleModifier.setHardness(10F);
		blockGrappleModifier.setRegistryName("block_grapple_modifier");
	    ForgeRegistries.BLOCKS.register(blockGrappleModifier);

	    itemBlockGrappleModifier = new ItemBlock(blockGrappleModifier);
	    itemBlockGrappleModifier.setRegistryName(blockGrappleModifier.getRegistryName());
	    ForgeRegistries.ITEMS.register(itemBlockGrappleModifier);

	    // Each of your tile entities needs to be registered with a name that is unique to your mod.
		GameRegistry.registerTileEntity(TileEntityGrappleModifier.class, "tile_entity_grapple_modifier");
	
	    MinecraftForge.EVENT_BUS.register(this);
	    
		proxy.preInit(event);
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		proxy.init(event, this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	
	int entityID = 0;
	public void registerEntity(Class<? extends Entity> entityClass, String name)
	{
		EntityRegistry.registerModEntity(resourceLocation, entityClass, name, entityID++, this, 900, 1, true);
	}
	
	public static void registerController(int entityId, grappleController controller) {
		if (controllers.containsKey(entityId)) {
			controllers.get(entityId).unattach();
		}
		
		controllers.put(entityId, controller);
	}
	
	public static void unregisterController(int entityId) {
		controllers.remove(entityId);
	}

	public static void receiveGrappleClick(int id,
			boolean leftclick) {
		grappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveGrappleClick(leftclick);
		} else {
			System.out.println("Couldn't find controller");
		}
	}

	public static void receiveEnderLaunch(int id, double x, double y, double z) {
		grappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveEnderLaunch(x, y, z);
		} else {
			System.out.println("Couldn't find controller");
		}
	}
	
	public static void sendtocorrectclient(IMessage message, int playerid, World w) {
		Entity entity = w.getEntityByID(playerid);
		if (entity instanceof EntityPlayerMP) {
			grapplemod.network.sendTo(message, (EntityPlayerMP) entity);
		} else {
			System.out.println("ERROR! couldn't find player");
		}
	}
	
	public static grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, double maxlen, BlockPos blockpos) {

		grappleArrow arrow = null;
		Entity arrowentity = world.getEntityByID(arrowid);
		if (arrowentity != null && arrowentity instanceof grappleArrow) {
			arrow = (grappleArrow) arrowentity;
		}
		
		if (id != MULTISUBID) {
			grappleController currentcontroller = controllers.get(entityid);
			if (currentcontroller != null) {
				currentcontroller.unattach();
			}
		}
		
//		System.out.println(blockpos);
		
		grappleController control = null;
		if (id == GRAPPLEID) {
			control = new grappleController(arrowid, entityid, world, pos, maxlen, id);
		} else if (id == ENDERID) {
			control = new enderController(arrowid, entityid, world, pos, maxlen, id);
		} else if (id == HOOKID) {
			control = new hookControl(arrowid, entityid, world, pos, maxlen, id);
		} else if (id == SMARTHOOKID) {
			boolean slow = false;
			if (arrow != null && arrow instanceof smartHookArrow) {
				slow = ((smartHookArrow) arrow).slow;
			}
			control = new smartHookControl(arrowid, entityid, world, pos, maxlen, id, slow);
		} else if (id == MAGNETID) {
			int repelconf = 0;
			if (arrow != null && arrow instanceof magnetArrow) {
				repelconf = ((magnetArrow) arrow).repelconf;
			}
			control = new magnetController(arrowid, entityid, world, pos, maxlen, id, repelconf);
		} else if (id == REPELID) {
			control = new repelController(arrowid, entityid, world, pos, maxlen, id);
		} else if (id == MULTIID) {
			control = new multihookController(arrowid, entityid, world, pos, maxlen, id);
		} else if (id == MULTISUBID) {
			control = grapplemod.controllers.get(entityid);
			boolean created = false;
			if (control instanceof multihookController) {
				multihookController c = (multihookController) control;
				if (arrow != null && arrow instanceof multihookArrow) {
					multihookArrow multiarrow = (multihookArrow) arrowentity;
					created = true;
					c.addArrow(multiarrow, pos);
				}
			}
			if (!created) {
				System.out.println("Couldn't create");
				grapplemod.removesubarrow(arrowid);
			}
		} else if (id == AIRID) {
//			System.out.println("AIR FRICTION CONTROLLER");
			control = new airfrictionController(arrowid, entityid, world, pos, maxlen, id);
		}
		if (blockpos != null && control != null) {
			grapplemod.controllerpos.put(blockpos, control);
		}
		
		return control;
	}
	
	public static void removesubarrow(int id) {
		grapplemod.network.sendToServer(new GrappleEndMessage(-1, id));
	}

	public static void receiveGrappleEnd(int id, World world, int arrowid) {
		if (grapplemod.attached.contains(id)) {
			grapplemod.attached.remove(new Integer
					(id));
		} else {
		}
		
		if (arrowid != -1) {
	      	Entity grapple = world.getEntityByID(arrowid);
	  		if (grapple instanceof grappleArrow) {
	  			((grappleArrow) grapple).removeServer();
	  		} else {
	
	  		}
		}
  		
  		Entity entity = world.getEntityByID(id);
  		if (entity != null) {
      		entity.fallDistance = 0;
  		}
  		
  		grapplemod.removeallmultihookarrows();
	}

	public static HashSet<multihookArrow> multihookarrows = new HashSet<multihookArrow>();
	public static void receiveMultihookMessage(int id, World w, boolean sneaking) {
      	Entity e = w.getEntityByID(id);
      	if (e != null && e instanceof EntityLivingBase) {
      		EntityLivingBase player = (EntityLivingBase) e;
      		
      		float angle = multiBow.getAngle(player);
      		
      		//vec look = new vec(player.getLookVec());
      		
      		//System.out.println(player.rotationPitch);
      		//System.out.println(player.rotationYaw);
      		
      		/*
      		grappleArrow arrow = new grappleArrow(w, player, false);
      		arrow.setHeadingFromThrower(player, (float)look.getPitch(), (float)look.getYaw(), 0.0F, arrow.getVelocity(), 0.0F);
			w.spawnEntityInWorld(arrow);
			*/
      		
      		vec anglevec = new vec(0,0,1).rotate_yaw(Math.toRadians(-angle));
      		anglevec = anglevec.rotate_pitch(Math.toRadians(-player.rotationPitch));
      		anglevec = anglevec.rotate_yaw(Math.toRadians(player.rotationYaw));
			multihookArrow entityarrow = new multihookArrow(w, player, false);
            entityarrow.shoot(player, (float) anglevec.getPitch(), (float)anglevec.getYaw(), 0.0F, entityarrow.getVelocity(), 0.0F);
            
            /*
            vec pos = vec.positionvec(entityarrow);
            pos.add_ip(new vec(0.36, -0.175, 0.45).rotate_yaw(Math.toRadians(player.rotationYaw)));
            entityarrow.setPosition(pos.x, pos.y, pos.z);
            */
            
			w.spawnEntity(entityarrow);
			multihookarrows.add(entityarrow);
			
			
      		anglevec = new vec(0,0,1).rotate_yaw(Math.toRadians(angle));
      		anglevec = anglevec.rotate_pitch(Math.toRadians(-player.rotationPitch));
      		anglevec = anglevec.rotate_yaw(Math.toRadians(player.rotationYaw));
			entityarrow = new multihookArrow(w, player, true);
            entityarrow.shoot(player, (float) anglevec.getPitch(), (float)anglevec.getYaw(), 0.0F, entityarrow.getVelocity(), 0.0F);
            
            /*
            pos = vec.positionvec(entityarrow);
            pos.add_ip(new vec(-0.36, -0.175, 0.45).rotate_yaw(Math.toRadians(player.rotationYaw)));
            entityarrow.setPosition(pos.x, pos.y, pos.z);
            */
            
			w.spawnEntity(entityarrow);
			multihookarrows.add(entityarrow);
      	}
	}
		
	public static void removeallmultihookarrows() {
		for (multihookArrow arrow : multihookarrows) {
			if (arrow != null && !arrow.isDead) {
				arrow.removeServer();
			}
		}
	}
	

	public static NBTTagCompound getstackcompound(ItemStack stack, String key) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound basecompound = stack.getTagCompound();
        if (basecompound.hasKey(key, 10))
        {
            return basecompound.getCompoundTag(key);
        }
        else
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            stack.setTagInfo(key, nbttagcompound);
            return nbttagcompound;
        }
	}

	public static void receiveToolConfigMessage(int id, World w) {
      	Entity e = w.getEntityByID(id);
      	if (e != null && e instanceof EntityLivingBase) {
      		EntityLivingBase player = (EntityLivingBase) e;
      		
      		ItemStack stack = player.getHeldItemMainhand();
      		Item item = stack.getItem();
      		if (item instanceof multiBow) {
      			NBTTagCompound compound = grapplemod.getstackcompound(stack, "grapplemod");
    			boolean slow = compound.getBoolean("slow");
    			slow = !slow;
    			compound.setBoolean("slow", slow);
    			
    			if (slow) {
    				player.sendMessage(new TextComponentString("Set to slow mode"));
    			} else {
    				player.sendMessage(new TextComponentString("Set to fast mode"));
    			}
      		} else if (item instanceof magnetBow) {
      			NBTTagCompound compound = grapplemod.getstackcompound(stack, "grapplemod");
    			int repelconf = compound.getInteger("repelconf");
    			repelconf++;
    			if (repelconf >= REPELCONFIGS) {
    				repelconf = 0;
    			}
    			compound.setInteger("repelconf", repelconf);
    			
//    			if (repelconf == REPELSPEED) {
//    				player.addChatMessage(new TextComponentString("Repel force set to speed based"));
    			if (repelconf == REPELSTRONG) {
    				player.sendMessage(new TextComponentString("Repel force set to strong"));
    			} else if (repelconf == REPELWEAK) {
    				player.sendMessage(new TextComponentString("Repel force set to weak"));
    			} else if (repelconf == REPELNONE) {
    				player.sendMessage(new TextComponentString("Repel force set to off"));
    			}
      		} else if (item instanceof smartHookBow) {
      			NBTTagCompound compound = grapplemod.getstackcompound(stack, "grapplemod");
    			boolean slow = compound.getBoolean("slow");
    			slow = !slow;
    			compound.setBoolean("slow", slow);
    			
    			if (slow) {
    				player.sendMessage(new TextComponentString("Set to slow mode"));
    			} else {
    				player.sendMessage(new TextComponentString("Set to fast mode"));
    			}
      		}
      	}
	}
}
