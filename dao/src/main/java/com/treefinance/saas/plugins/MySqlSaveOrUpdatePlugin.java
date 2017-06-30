package com.treefinance.saas.plugins;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/20.
 */
public class MySqlSaveOrUpdatePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    public static void main(String[] args) throws  Exception{
        File configFile = new File("");
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

}
