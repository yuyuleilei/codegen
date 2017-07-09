package ${appBaseDir}.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration  
@AutoConfigureAfter({ DatabaseConfig.class })  
@MapperScan(basePackages={"${appBaseDir}.mapper","${appBaseDir}.mapper"})  
@EnableTransactionManagement
public class MyBatisConfig implements TransactionManagementConfigurer{
	private static final Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);

    @Value("${r'${mybatis.typeAliasesPackage}'}")
	private String typeAliasesPackage;
    
    @Value("${r'${mybatis.mapperLocations}'}")
   	private String mapperLocations;
    
    @Resource(name="dataSource")  
    private DataSource dataSource;  
    
    @Bean  
    public SqlSessionFactory sqlSessionFactory() {  
        try {  
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();  
            sessionFactory.setDataSource(dataSource);  
            sessionFactory.setTypeAliasesPackage(typeAliasesPackage);  
            sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));  
            return sessionFactory.getObject();  
        } catch (Exception e) {  
        	logger.warn("Could not confiure mybatis session factory");  
        	 throw new RuntimeException(e);
        }  
    }  
    
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
      
    @Bean
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}  
}
