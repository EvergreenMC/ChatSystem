package eu.adventuria.chatsystem;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private String configPath;
    private String configFile;
    private FileConfiguration cfg;
    private File file;

    public ConfigManager(String configPath, String configName) {
        this.configPath = configPath;
        this.configFile = configName;
        this.file = this.getFile();
        this.cfg = this.getFileConfiguration();
        this.standartConfigInput();
    }

    private File getFile() {
        return new File(this.configPath, this.configFile);
    }

    private FileConfiguration getFileConfiguration() {
        return YamlConfiguration.loadConfiguration(this.file);
    }

    private void save() {
        try {
            this.cfg.save(this.getFile());
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    private void updateFileConfiguration() {
        this.cfg = this.getFileConfiguration();
    }

    private void standartConfigInput() {
        this.updateFileConfiguration();
        this.cfg.options().header("+ File created by AdviSystem +");
        this.save();
    }

    public void addHeader(String text) {
        this.updateFileConfiguration();
        this.cfg.options().copyHeader(true);
        this.cfg.options().header(text);
        this.save();
    }

    public void create(String path, Object object) {
        this.updateFileConfiguration();
        if (!this.cfg.isSet(path)) {
            this.cfg.options().copyDefaults(true);
            this.cfg.addDefault(path, object);
            this.save();
        }

    }

    public void set(String path, Object object) {
        this.updateFileConfiguration();
        if (!this.cfg.isSet(path)) {
            this.cfg.options().copyDefaults(true);
            this.cfg.addDefault(path, object);
        } else {
            this.cfg.set(path, object);
        }

        this.save();
    }

    public void del(String path) {
        this.updateFileConfiguration();
        if (this.cfg.isSet(path)) {
            this.cfg.set(path, (Object)null);
        }

        this.save();
    }

    public String getString(String path) {
        this.updateFileConfiguration();
        return this.cfg.isSet(path) ? this.cfg.getString(path) : null;
    }

    public boolean getBoolean(String path) {
        this.updateFileConfiguration();
        return this.cfg.isSet(path) && this.cfg.getBoolean(path);
    }

    public int getInt(String path) {
        this.updateFileConfiguration();
        return this.cfg.isSet(path) ? this.cfg.getInt(path) : 0;
    }
}
