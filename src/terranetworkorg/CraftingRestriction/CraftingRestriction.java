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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftingRestriction extends JavaPlugin {
	
	public final static Logger log = Logger.getLogger("Minecraft");
	public static final String logprefix = "[CraftingRestriction 1.2.1]";
	
	private final CraftingRestrictionUtility utils = new CraftingRestrictionUtility(this);
	
	private FileConfiguration config = null;
    private File configFile = null;
	private FileConfiguration language = null;
    private File languageFile = null;
	
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
	
	@SuppressWarnings("unused")
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
	
	public void reloadConfig() {
	    if (configFile == null) {
	    configFile = new File(getDataFolder(), "config.yml");
	    }
	    config = YamlConfiguration.loadConfiguration(configFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = getResource("config.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
	    }
	}

	public FileConfiguration getConfig() {
	    if (config == null) {
	        reloadConfig();
	    }
	    return config;
	}
	
	public void saveConfig() {
	    if (config == null || configFile == null) {
	    return;
	    }
	    try {
	        config.save(configFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + configFile, ex);
	    }
	}
	
	public void reloadLanguage() {
	    if (languageFile == null) {
	    	languageFile = new File(getDataFolder(), "language.yml");
	    }
	    language = YamlConfiguration.loadConfiguration(languageFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = getResource("language.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        language.setDefaults(defConfig);
	    }
	}

	public FileConfiguration getLanguage() {
	    if (language == null) {
	        reloadLanguage();
	    }
	    return language;
	}
	
	public void saveLanguage() {
	    if (language == null || languageFile == null) {
	    return;
	    }
	    try {
	    	language.save(languageFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + languageFile, ex);
	    }
	}
	
	private Boolean loadConfig()
	{
		reloadConfig();
		getConfig();
		reloadLanguage();
		getLanguage();

		if(config.getString("General.Blacklist") == null)
        	config.set("General.Blacklist", "13254");
		if(config.getString("General.Log") == null)
        	config.set("General.Log", "");
        if(config.getString("General.Log.Crafting") == null)
        	config.set("General.Log.Crafting", true);
        if(config.getString("Restrict.ID") == null)
        	config.set("Restrict.ID", "");
        if(config.getString("Restrict.ID.12345") == null)
        	config.set("Restrict.ID.12345", "craftingrestriction.tool.example");
        
        saveConfig();

        if(language.getString("Messages.ACCESS_DENIED") == null)
        	language.set("Messages.ACCESS_DENIED", "You do not have permission to use this command.");
        if(language.getString("Messages.CRAFT_DENIED") == null)
        	language.set("Messages.CRAFT_DENIED", "You are not allowed to craft this Item.");
        
        saveLanguage();
		      
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
		
		getServer().getPluginManager().registerEvents(new CraftingRestrictionCraftListener(this), this);
        LogInfo("successfully linked with Spout.");
		LogInfo("was successfully initiated.");
		LogInfo("===========================================================");
		
	}
	
	public void onDisable() {
		
		LogInfo("Plugin Disabled");
		
	}
	
	private void showPluginInfo(CommandSender sender){
		sender.sendMessage(ChatColor.GREEN + logprefix);
	}
	
	private void reloadConfigs(CommandSender sender){
		reloadConfig();
		reloadLanguage();
		sender.sendMessage(ChatColor.BLUE+ "Config successfully reloaded.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if(sender instanceof Player) {
		
			final Player player = (Player)sender;
			
			if(cmd.getName().equalsIgnoreCase("craftingrestriction")){
				if(args.length==0){
                    showPluginInfo(sender);
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
		}else{
			if(cmd.getName().equalsIgnoreCase("craftingrestriction")){
				if(args.length==0){
	                showPluginInfo(sender);
	                return true;
				}else if(args.length==1){
					if(args[0].equalsIgnoreCase("reload")){						
						reloadConfigs(sender);
						return true;
					} else {						
						sender.sendMessage(ChatColor.BLUE+"Use a valid parameter: reload");
						return true;						
					}
				}
				else {
					
					sender.sendMessage(ChatColor.BLUE+"Use a valid parameter: reload");
					return true;
					
				}
			}
		}
		return false;		
	}
	
}