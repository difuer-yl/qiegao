package club.qiegaoshijie.qiegao.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

public class FileConfig
        extends YamlConfiguration
{
    protected final DumperOptions yamlOptions = new DumperOptions();
    protected final Representer yamlRepresenter = new YamlRepresenter();
    protected final Yaml yaml = new Yaml(new YamlConstructor(), this.yamlRepresenter, this.yamlOptions);
    protected File file;
    protected Logger loger;
    protected Plugin plugin;

    public FileConfig(Plugin plugin)
    {
        this(plugin, "config.yml");
    }

    public FileConfig(Plugin plugin, File file)
    {
        Validate.notNull(file, "File cannot be null");
        Validate.notNull(plugin, "Plugin cannot be null");
        this.plugin = plugin;
        this.file = file;
        this.loger = plugin.getLogger();
        check(file);
        init(file);
    }

    public FileConfig(Plugin plugin, String filename)
    {
        this(plugin, new File(plugin.getDataFolder(), filename));
    }

    private void check(File file)
    {
        String filename = file.getName();
        InputStream stream = this.plugin.getResource(filename);
        try
        {
            if (!file.exists())
            {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                if (stream != null) {
                    this.plugin.saveResource(filename, true);
                }
            }
        }
        catch (IOException e)
        {
            this.loger.info("配置文件 " + filename + " 创建失败...");
        }
    }

    private void init(File file)
    {
        Validate.notNull(file, "File cannot be null");
        try
        {
            FileInputStream stream = new FileInputStream(file);
            init(stream);
        }
        catch (FileNotFoundException e)
        {
            this.loger.info("配置文件 " + file.getName() + " 不存在...");
        }
    }

    private void init(InputStream stream)
    {
        Validate.notNull(stream, "Stream cannot be null");
        try
        {
            load(new InputStreamReader(stream, Charsets.UTF_8));
        }
        catch (IOException ex)
        {
            this.loger.info("配置文件 " + this.file.getName() + " 读取错误...");
        }
        catch (InvalidConfigurationException ex)
        {
            this.loger.info("配置文件 " + this.file.getName() + " 格式错误...");
        }
    }

    public void load(File file)
            throws FileNotFoundException, IOException, InvalidConfigurationException
    {
        Validate.notNull(file, "File cannot be null");
        FileInputStream stream = new FileInputStream(file);
        load(new InputStreamReader(stream, Charsets.UTF_8));
    }

    public void load(Reader reader)
            throws IOException, InvalidConfigurationException
    {
        BufferedReader input = (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);

        StringBuilder builder = new StringBuilder();
        try
        {
            String line;
            while ((line = input.readLine()) != null)
            {
                builder.append(line);
                builder.append('\n');
            }
        }
        finally
        {
            input.close();
        }
        loadFromString(builder.toString());
    }

    public void reload()
    {
        init(this.file);
    }

    public void save()
    {
        if (this.file == null) {
            this.loger.info("未定义配置文件路径 保存错误!");
        }
        try
        {
            save(this.file);
        }
        catch (IOException e)
        {
            this.loger.info("配置文件 " + this.file.getName() + " 保存错误...");
            e.printStackTrace();
        }
    }
    public void update()
    {
        if (this.file == null) {
            this.loger.info("未定义配置文件路径 保存错误!");
        }
        try
        {
            save(this.file);
        }
        catch (IOException e)
        {
            this.loger.info("配置文件 " + this.file.getName() + " 保存错误...");
            e.printStackTrace();
        }
    }

    public void save(File file)
            throws IOException
    {
        Validate.notNull(file, "File cannot be null");
        Files.createParentDirs(file);
        String data = saveToString();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
        try
        {
            writer.write(data);
        }
        finally
        {
            writer.close();
        }
    }

    public String saveToString()
    {
        this.yamlOptions.setIndent(options().indent());
        this.yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        String header = buildHeader();
        String dump = this.yaml.dump(getValues(false));
        if (dump.equals("{}\n")) {
            dump = "";
        }
        return header + dump;
    }
}

