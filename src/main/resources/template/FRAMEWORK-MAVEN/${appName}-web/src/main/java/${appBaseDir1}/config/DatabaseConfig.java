package ${appBaseDir}.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig  {
	private static final Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);
	
	@Value("${r'${spring.datasource.url}'}")
	private String dbUrl;

	@Value("${r'${spring.datasource.username}'}")
	private String username;

	@Value("${r'${spring.datasource.password}'}")
	private String password;

	@Value("${r'${spring.datasource.driver-class-name}'}")
	private String driverClassName;

	@Value("${r'${spring.datasource.initialSize}'}")
	private int initialSize;

	@Value("${r'${spring.datasource.minIdle}'}")
	private int minIdle;

	@Value("${r'${spring.datasource.maxActive}'}")
	private int maxActive;

	@Value("${r'${spring.datasource.maxWait}'}")
	private int maxWait;

	@Value("${r'${spring.datasource.timeBetweenEvictionRunsMillis}'}")
	private int timeBetweenEvictionRunsMillis;

	@Value("${r'${spring.datasource.minEvictableIdleTimeMillis}'}")
	private int minEvictableIdleTimeMillis;

	@Value("${r'${spring.datasource.validationQuery}'}")
	private String validationQuery;

	@Value("${r'${spring.datasource.testWhileIdle}'}")
	private boolean testWhileIdle;

	@Value("${r'${spring.datasource.testOnBorrow}'}")
	private boolean testOnBorrow;

	@Value("${r'${spring.datasource.testOnReturn}'}")
	private boolean testOnReturn;

	@Value("${r'${spring.datasource.poolPreparedStatements}'}")
	private boolean poolPreparedStatements;
	@Value("${r'${spring.datasource.maxPoolPreparedStatementPerConnectionSize}'}")
	private int maxPoolPreparedStatementPerConnectionSize;
	
	@Value("${r'${spring.datasource.filters}'}")
	private String filters;

	@Bean(name = "dataSource", destroyMethod = "close", initMethod = "init")
	public DataSource dataSource() throws SQLException {
		logger.debug("Configruing Write DataSource");
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUrl(dbUrl);
		druidDataSource.setDriverClassName(driverClassName);
		druidDataSource.setUsername(username);
		druidDataSource.setPassword(password);
		druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        druidDataSource.setFilters(filters);
		return druidDataSource;
	}

}