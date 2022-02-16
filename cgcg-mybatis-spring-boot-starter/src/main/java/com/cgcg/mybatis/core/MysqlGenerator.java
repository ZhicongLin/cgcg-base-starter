package com.cgcg.mybatis.core;

import java.text.SimpleDateFormat;
import java.util.*;

import lombok.Setter;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.cgcg.mybatis.plus.BaseEntity;
import com.cgcg.mybatis.plus.IMapper;

/**
 * 代码生成配置
 *
 * @author zhicong.lin
 * @date 2020/01/13
 */
@Setter
@Accessors(chain = true)
public class MysqlGenerator {

    private String[] table;

    private MybatisGenerationProperties properties;

    public static void builder(MybatisGenerationProperties properties, String... tables) {
        new MysqlGenerator().setTable(tables).setProperties(properties).build();
    }

    /**
     * 主函数
     *
     * @author zhicong.lin
     * @date 2020/8/13
     */
    private void build() {
        GenerationType type = properties.getGenerationType();
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + type.getDir() + "/main/java");
        gc.setAuthor(properties.getAuthor());
        gc.setOpen(false);
        // 实体属性 Swagger2 注解
        gc.setSwagger2(true);
        // service 命名方式
        gc.setServiceName("%sService");
        // service impl 命名方式
        gc.setServiceImplName("%sServiceImpl");
        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setFileOverride(true);
        gc.setActiveRecord(false);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(true);
        gc.setIdType(IdType.AUTO);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(properties.getDataSourceUrl());
        dsc.setDriverName(properties.getDataSourceDriver());
        dsc.setUsername(properties.getDataSourceUser());
        dsc.setPassword(properties.getDataSourcePwd());
        dsc.setTypeConvert(new MySqlTypeConvertCustom());
        mpg.setDataSource(dsc);
        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.chux.pest");
        pc.setEntity("domain.entity");
        pc.setService("domain.service");
        pc.setServiceImpl("infrastructure.service");
        pc.setMapper("domain.mapper");
        pc.setController("application");
        mpg.setPackageInfo(pc);
        mpg.setCfg(setMapperXmlDir(projectPath));
        setStrategyConfig(mpg);
        mpg.execute();
    }

    private InjectionConfig setMapperXmlDir(String projectPath) {
        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = this.getMap();
                if (map == null) {
                    map = new HashMap<>();
                    this.setMap(map);
                }
                map.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            }
        };
        // 如果模板引擎是 velocity
        String templatePath = "/templates/mapper.xml.vm";
        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + properties.getGenerationType().getDir() + "\\main\\resources\\mapper\\" + tableInfo.getMapperName() + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        return cfg;
    }

    private void setStrategyConfig(AutoGenerator mpg) {
        // 自定义需要填充的字段
        List<TableFill> tableFillList = new ArrayList<>();
        // 公共字段填充功能
        TableFill createField = new TableFill("create_time", FieldFill.INSERT);
        TableFill modifiedField = new TableFill("update_time", FieldFill.INSERT_UPDATE);
        TableFill createByField = new TableFill("create_by", FieldFill.INSERT);
        TableFill modifyByField = new TableFill("update_by", FieldFill.INSERT_UPDATE);
        tableFillList.add(createField);
        tableFillList.add(modifiedField);
        tableFillList.add(createByField);
        tableFillList.add(modifyByField);
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        // 设置逻辑删除键
        strategy.setLogicDeleteFieldName(properties.getLogicDelete());
        // 自增id
        strategy.setInclude(table);
        // uuid
        strategy.setTableFillList(tableFillList);
        strategy.setSuperEntityClass(BaseEntity.class);
        strategy.setSuperEntityColumns("create_by", "create_time", "update_by", "update_time");
        // 驼峰转连字符
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(properties.getTablePrefix());
        strategy.setSuperMapperClass(IMapper.class.getName());
        mpg.setStrategy(strategy);
    }
}

