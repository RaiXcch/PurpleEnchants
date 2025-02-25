package com.badbones69.crazyenchantments.api;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author BadBones69
 * @version v1.1
 */
public class FileManager {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private boolean log = false;
    private final Map<Files, File> files = new HashMap<>();
    private final List<String> homeFolders = new ArrayList<>();
    private final List<CustomFile> customFiles = new ArrayList<>();
    private final Map<String, String> jarHomeFolders = new HashMap<>();
    private final Map<String, String> autoGenerateFiles = new HashMap<>();
    private final Map<Files, FileConfiguration> configurations = new HashMap<>();
    
    /**
     * Sets up the plugin and loads all necessary files.
     */
    public FileManager setup() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

        files.clear();
        customFiles.clear();
        configurations.clear();

        //Loads all the normal static files.
        for (Files file : Files.values()) {
            File newFile = new File(plugin.getDataFolder(), file.getFileLocation());

            if (isLogging()) plugin.getLogger().info("Loading the " + file.getFileName() + ".");

            if (!newFile.exists()) {
                try {
                    File serverFile = new File(plugin.getDataFolder(), "/" + file.getFileLocation());
                    InputStream jarFile = getClass().getResourceAsStream("/" + file.getFileJar());
                    copyFile(jarFile, serverFile);
                } catch (Exception e) {
                    if (isLogging()) plugin.getLogger().info("Failed to load file: " + file.getFileName() + ".");

                    e.printStackTrace();
                    continue;
                }
            }

            files.put(file, newFile);
            configurations.put(file, YamlConfiguration.loadConfiguration(newFile));

            if (isLogging()) plugin.getLogger().info("Successfully loaded " + file.getFileName() + ".");
        }

        // Starts to load all the custom files.
        if (!homeFolders.isEmpty()) {
            if (isLogging()) plugin.getLogger().info("Loading custom files.");

            for (String homeFolder : homeFolders) {
                File homeFile = new File(plugin.getDataFolder(), "/" + homeFolder);

                if (homeFile.exists()) {
                    String[] list = homeFile.list();

                    if (list != null) {
                        for (String name : list) {
                            if (name.endsWith(".yml")) {
                                CustomFile file = new CustomFile(name, homeFolder);

                                if (file.exists()) {
                                    customFiles.add(file);

                                    if (isLogging()) plugin.getLogger().info( "Loaded new custom file: " + homeFolder + "/" + name + ".");
                                }
                            }
                        }
                    }
                } else {
                    homeFile.mkdir();
                    if (isLogging()) plugin.getLogger().info("The folder " + homeFolder + "/ was not found so it was created.");

                    for (Entry<String, String> file : autoGenerateFiles.entrySet()) {

                        if (file.getValue().equalsIgnoreCase(homeFolder)) {
                            homeFolder = file.getValue();

                            try {
                                File serverFile = new File(plugin.getDataFolder(), homeFolder + "/" + file.getKey());
                                InputStream jarFile = getClass().getResourceAsStream((jarHomeFolders.getOrDefault(file.getKey(), homeFolder)) + "/" + file.getKey());
                                copyFile(jarFile, serverFile);

                                if (file.getKey().toLowerCase().endsWith(".yml")) {
                                    customFiles.add(new CustomFile(file.getKey(), homeFolder));
                                }

                                if (isLogging()) plugin.getLogger().info("Created new default file: " + homeFolder + "/" + file.getKey() + ".");
                            } catch (Exception e) {
                                if (isLogging()) plugin.getLogger().info("Failed to create new default file: " + homeFolder + "/" + file.getKey() + "!");

                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            if (isLogging()) plugin.getLogger().info("Finished loading custom files.");
        }
        return this;
    }
    
    /**
     * Turn on the logger system for the FileManager.
     * @param log True to turn it on and false for it to be off.
     */
    public FileManager setLog(boolean log) {
        this.log = log;
        return this;
    }
    
    /**
     * Check if the logger is logging in console.
     * @return True if it is and false if it isn't.
     */
    public boolean isLogging() {
        return log;
    }
    
    /**
     * Register a folder that has custom files in it. Make sure to have a "/" in front of the folder name.
     * @param homeFolder The folder that has custom files in it.
     */
    public FileManager registerCustomFilesFolder(String homeFolder) {
        homeFolders.add(homeFolder);
        return this;
    }
    
    /**
     * Unregister a folder that has custom files in it. Make sure to have a "/" in front of the folder name.
     * @param homeFolder The folder with custom files in it.
     */
    public FileManager unregisterCustomFilesFolder(String homeFolder) {
        homeFolders.remove(homeFolder);
        return this;
    }
    
    /**
     * Register a file that needs to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
     * @param fileName The name of the file you want to auto-generate when the folder doesn't exist.
     * @param homeFolder The folder that has custom files in it.
     */
    public FileManager registerDefaultGenerateFiles(String fileName, String homeFolder) {
        autoGenerateFiles.put(fileName, homeFolder);
        return this;
    }
    
    /**
     * Register a file that needs to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
     * @param fileName The name of the file you want to auto-generate when the folder doesn't exist.
     * @param homeFolder The folder that has custom files in it.
     * @param jarHomeFolder The folder that the file is found in the jar.
     */
    public FileManager registerDefaultGenerateFiles(String fileName, String homeFolder, String jarHomeFolder) {
        autoGenerateFiles.put(fileName, homeFolder);
        jarHomeFolders.put(fileName, jarHomeFolder);
        return this;
    }
    
    /**
     * Unregister a file that doesn't need to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
     * @param fileName The file that you want to remove from auto-generating.
     */
    public FileManager unregisterDefaultGenerateFiles(String fileName) {
        autoGenerateFiles.remove(fileName);
        jarHomeFolders.remove(fileName);
        return this;
    }
    
    /**
     * Gets the file from the system.
     * @return The file from the system.
     */
    public FileConfiguration getFile(Files file) {
        return configurations.get(file);
    }
    
    /**
     * Get a custom file from the loaded custom files instead of a hardcoded one.
     * This allows you to get custom files like Per player data files.
     * @param name Name of the crate you want. (Without the .yml)
     * @return The custom file you wanted otherwise if not found will return null.
     */
    public CustomFile getFile(String name) {
        for (CustomFile file : customFiles) {
            if (file.getName().equalsIgnoreCase(name)) {
                return file;
            }
        }

        return null;
    }
    
    /**
     * Saves the file from the loaded state to the file system.
     */
    public void saveFile(Files file) {
        try {
            configurations.get(file).save(files.get(file));
        } catch (IOException e) {
            plugin.getLogger().info("Could not save " + file.getFileName() + "!");
            e.printStackTrace();
        }
    }
    
    /**
     * Save a custom file.
     * @param name The name of the custom file.
     */
    public void saveFile(String name) {
        CustomFile file = getFile(name);
        if (file != null) {
            try {
                file.getFile().save(new File(plugin.getDataFolder(), file.getHomeFolder() + "/" + file.getFileName()));

                if (isLogging()) plugin.getLogger().info("Successfully saved the " + file.getFileName() + ".");
            } catch (Exception e) {
               plugin.getLogger().info("Could not save " + file.getFileName() + "!");
                e.printStackTrace();
            }
        } else {
            if (isLogging()) plugin.getLogger().info("The file " + name + ".yml could not be found!");
        }
    }
    
    /**
     * Save a custom file.
     * @param file The custom file you are saving.
     * @return True if the file saved correct and false if there was an error.
     */
    public boolean saveFile(CustomFile file) {
        return file.saveFile();
    }
    
    /**
     * Overrides the loaded state file and loads the file systems file.
     */
    public void reloadFile(Files file) {
        configurations.put(file, YamlConfiguration.loadConfiguration(files.get(file)));
    }
    
    /**
     * Overrides the loaded state file and loads the file systems file.
     */
    public void reloadFile(String name) {
        CustomFile file = getFile(name);
        if (file != null) {
            try {
                file.file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + file.getHomeFolder() + "/" + file.getFileName() + "."));
                if (isLogging()) plugin.getLogger().info("Successfully reload the " + file.getFileName() + ".");
            } catch (Exception e) {
                plugin.getLogger().info("Could not reload the " + file.getFileName() + "!");
                e.printStackTrace();
            }
        } else {
            if (isLogging()) plugin.getLogger().info("The file " + name + ".yml could not be found!");
        }
    }
    
    /**
     * Overrides the loaded state file and loads the filesystems file.
     * @return True if it reloaded correct and false if the file wasn't found.
     */
    public boolean reloadFile(CustomFile file) {
        return file.reloadFile();
    }
    
    /**
     * Was found here: <a href="https://bukkit.org/threads/extracting-file-from-jar.16962">...</a>
     */
    private void copyFile(InputStream in, File out) throws Exception {
        try (InputStream fis = in; FileOutputStream fos = new FileOutputStream(out)) {
            byte[] buf = new byte[1024];
            int i;

            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
    }
    
    public enum Files {
        
        // ENUM_NAME("fileName.yml", "fileLocation.yml"),
        // ENUM_NAME("fileName.yml", "newFileLocation.yml", "oldFileLocation.yml"),
        CONFIG("config.yml", "config.yml"),
        BLOCKLIST("BlockList.yml", "BlockList.yml"),
        DATA("Data.yml", "Data.yml"),
        ENCHANTMENTS("Enchantments.yml", "Enchantments.yml"),
        GKITZ("GKitz.yml", "GKitz.yml"),
        MESSAGES("Messages.yml", "Messages.yml"),
        ENCHANTMENT_TYPES("Enchantment-Types.yml", "Enchantment-Types.yml"),
        TINKER("Tinker.yml", "Tinker.yml");
        
        private final String fileName;
        private final String fileJar;
        private final String fileLocation;

        private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

        private final FileManager fileManager = plugin.getStarter().getFileManager();
        
        /**
         * The files that the server will try and load.
         * @param fileName The file name that will be in the plugin's folder.
         * @param fileLocation The location the file in the plugin's folder.
         */
        Files(String fileName, String fileLocation) {
            this(fileName, fileLocation, fileLocation);
        }
        
        /**
         * The files that the server will try and load.
         * @param fileName The file name that will be in the plugin's folder.
         * @param fileLocation The location of the file will be in the plugin's folder.
         * @param fileJar The location of the file in the jar.
         */
        Files(String fileName, String fileLocation, String fileJar) {
            this.fileName = fileName;
            this.fileLocation = fileLocation;
            this.fileJar = fileJar;
        }
        
        /**
         * Get the name of the file.
         * @return The name of the file.
         */
        public String getFileName() {
            return fileName;
        }
        
        /**
         * The location the jar it is at.
         * @return The location in the jar the file is in.
         */
        public String getFileLocation() {
            return fileLocation;
        }
        
        /**
         * Get the location of the file in the jar.
         * @return The location of the file in the jar.
         */
        public String getFileJar() {
            return fileJar;
        }
        
        /**
         * Gets the file from the system.
         * @return The file from the system.
         */
        public FileConfiguration getFile() {
            return fileManager.getFile(this);
        }
        
        /**
         * Saves the file from the loaded state to the file system.
         */
        public void saveFile() {
            fileManager.saveFile(this);
        }
        
        /**
         * Overrides the loaded state file and loads the file systems file.
         */
        public void reloadFile() {
            fileManager.reloadFile(this);
        }
    }
    
    public class CustomFile {
        
        private final String name;
        private final String fileName;
        private final String homeFolder;
        private FileConfiguration file;

        private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();
        
        /**
         * A custom file that is being made.
         * @param name Name of the file.
         * @param homeFolder The home folder of the file.
         */
        public CustomFile(String name, String homeFolder) {
            this.name = name.replace(".yml", "");
            this.fileName = name;
            this.homeFolder = homeFolder;

            if (new File(plugin.getDataFolder(), "/" + homeFolder).exists()) {
                if (new File(plugin.getDataFolder(), "/" + homeFolder + "/" + name).exists()) {
                    file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + homeFolder + "/" + name));
                } else {
                    file = null;
                }
            } else {
                new File(plugin.getDataFolder(), "/" + homeFolder).mkdir();

                if (isLogging()) plugin.getLogger().info("The folder " + homeFolder + "/ was not found so it was created.");

                file = null;
            }
        }
        
        /**
         * Get the name of the file without the .yml part.
         * @return The name of the file without the .yml.
         */
        public String getName() {
            return name;
        }
        
        /**
         * Get the full name of the file.
         * @return Full name of the file.
         */
        public String getFileName() {
            return fileName;
        }
        
        /**
         * Get the name of the home folder of the file.
         * @return The name of the home folder the files are in.
         */
        public String getHomeFolder() {
            return homeFolder;
        }
        
        /**
         * Get the ConfigurationFile.
         * @return The ConfigurationFile of this file.
         */
        public FileConfiguration getFile() {
            return file;
        }
        
        /**
         * Check if the file actually exists in the file system.
         * @return True if it does and false if it doesn't.
         */
        public boolean exists() {
            return file != null;
        }
        
        /**
         * Save the custom file.
         * @return True if it saved correct and false if something went wrong.
         */
        public boolean saveFile() {
            if (file != null) {
                try {
                    file.save(new File(plugin.getDataFolder(), homeFolder + "/" + fileName));

                    if (isLogging()) plugin.getLogger().info("Successfully saved the " + fileName + ".");
                    return true;
                } catch (Exception e) {
                    plugin.getLogger().info("Could not save " + fileName + "!");
                    e.printStackTrace();
                    return false;
                }
            } else {
                if (isLogging()) plugin.getLogger().info("There was a null custom file that could not be found!");
            }
            return false;
        }
        
        /**
         * Overrides the loaded state file and loads the filesystems file.
         * @return True if it reloaded correct and false if the file wasn't found or broke.
         */
        public boolean reloadFile() {
            if (file != null) {
                try {
                    file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + homeFolder + "/" + fileName));

                    if (isLogging()) plugin.getLogger().info("Successfully reload the " + fileName + ".");

                    return true;
                } catch (Exception e) {
                    plugin.getLogger().info("Could not reload the " + fileName + "!");
                    e.printStackTrace();
                }
            } else {
                if (isLogging()) plugin.getLogger().info("There was a null custom file that was not found!");
            }

            return false;
        }
    }
}