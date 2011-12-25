package terranetworkorg.CraftingRestriction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class CraftingRestriction extends JavaPlugin {
	
	public final static Logger log = Logger.getLogger("Minecraft");
	public static final String logprefix = "[CraftingRestriction 1.0.0]";
	
	private final CraftingRestrictionCraftListener craftListener = new CraftingRestrictionCraftListener(this);
	
	private final CraftingRestrictionUtility utils = new CraftingRestrictionUtility(this);
	
	File configFile;
	File languageFile;
	
	static Configuration config;
	static Configuration language;
	
	public static Permission permission = null;
	public Economy economy = null;
	
	public static void LogInfo(String Message) {
		
		log.info(logprefix + " " + Message);
		
	}
	
	public static void LogError(String Message) {
		
		log.log(Level.SEVERE, logprefix + " " + Message);
		
	}
	
	public static void LogWarning(String Message) {
		
		log.log(Level.WARNING, logprefix + " " + Message);
		
	}
	
	private Boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            LogInfo("succesfully connected permissions support with Vault");
        }
        return (permission != null);
    }
	
	private void firstTimeCheck() {
        if(!getDataFolder().exists()){
            getDataFolder().mkdirs();
        }
        if(!configFile.exists()){
            InputStream inputThis = getClassLoader().getResourceAsStream("config.yml");
            try{
                utils.copy(inputThis, configFile);
                LogInfo("config.yml was created");
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        if(!languageFile.exists()){
            InputStream inputThis = getClassLoader().getResourceAsStream("language.yml");
            try{
                utils.copy(inputThis, languageFile);
                LogInfo("language.yml was created");
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
	
	private Boolean loadConfig()
	{
		configFile = new File(getDataFolder().getPath()+"/config.yml");
		languageFile = new File(getDataFolder().getPath()+"/language.yml");
        firstTimeCheck();
        config = new Configuration(configFile);
        config.load();
        language = new Configuration(languageFile);
        language.load();

        if(config.getString("General.Log") == null)
        	config.setProperty("General.Log", "");
        if(config.getString("General.Log.Crafting") == null)
        	config.setProperty("General.Log.Crafting", true);
        if(config.getString("Restrict.ID") == null)
        	config.setProperty("Restrict.ID", "");
        if(config.getString("Restrict.ID.271") == null)
        	config.setProperty("Restrict.ID.271", "craftingrestriction.tool.wood.axe");
        
        config.save();

        if(language.getString("Messages.ACCESS_DENIED") == null)
        	language.setProperty("Messages.ACCESS_DENIED", "You do not have permission to use this command.");
        if(language.getString("Messages.CRAFT_DENIED") == null)
        	language.setProperty("Messages.CRAFT_DENIED", "You are not allowed to craft this Item.");
        
        language.save();
		      
		return true;
	}
	
	public void onEnable() {
		
		loadConfig();
		
		LogInfo("===========================================================");
		if (!setupPermissions()) {
			System.out.println("No permissions plugin found.");
	        //use these if you require econ
	        getServer().getPluginManager().disablePlugin(this);
	        return;
	    }
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.CUSTOM_EVENT, craftListener, Priority.Normal, this);
        LogInfo("successfully linked with Spout.");
		LogInfo("was successfully initiated.");
		LogInfo("===========================================================");
		
	}
	
	public void onDisable() {
		
		LogInfo("Plugin Disabled");
		
	}
	
	private void showPluginInfo(Player player){
		player.sendMessage(ChatColor.GREEN + logprefix);
	}
	
	private void reloadConfigs(Player player){
		config.load();
		language.load();
		player.sendMessage(ChatColor.BLUE+ "Config successfully reloaded.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		final Player player = (Player)sender;
		
		if(sender instanceof Player) {
			
			if(cmd.getName().equalsIgnoreCase("craftingrestriction")){
				if(args.length==0){
                    showPluginInfo(player);
                    return true;
				}else if(args.length==1){
					if(args[0].equalsIgnoreCase("reload")){
						
						if(permission.has(player, "craftingrestriction.admin.reload")){
							reloadConfigs(player);
							return true;
						} else{
							player.sendMessage(ChatColor.RED + language.getString("Messages.ACCESS_DENIED"));
							return true;							
						}
						
					} else {
						
						player.sendMessage(ChatColor.BLUE+"Use a valid parameter: reload");
						return true;
						
					}
				}
				else {
					
					player.sendMessage(ChatColor.BLUE+"Use a valid parameter: reload");
					return true;
					
				}
			}
		}	
		return false;		
	}
	
}