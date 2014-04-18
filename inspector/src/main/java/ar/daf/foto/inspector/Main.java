package ar.daf.foto.inspector;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import ar.daf.foto.inspector.config.CoreConfig;


@ComponentScan
public class Main {

//	@Autowired
//	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(CoreConfig.class, args);
	}

//	@Bean
//	@Autowired
//	public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
//		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//		sessionFactory.setDataSource(dataSource);
//		sessionFactory.setPackagesToScan(new String[] { "ar.daf.foto.inspector.model" });
////		sessionFactory.setHibernateProperties(hibernateProperties());
//		return sessionFactory;
//	}
//
//	@Bean
//	@Autowired
//	public HibernateTransactionManager transactionManager(
//			SessionFactory sessionFactory) {
//		HibernateTransactionManager txManager = new HibernateTransactionManager();
//		txManager.setSessionFactory(sessionFactory);
//		return txManager;
//	}

//	@Bean
//	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
//		return new PersistenceExceptionTranslationPostProcessor();
//	}

//	private Properties hibernateProperties() {
//		Properties result = new Properties() {
//			{
//				setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
//				setProperty("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
//				setProperty("hibernate.cache.provider_class", "spring.jpa.properties.hibernate.cache.provider_class");
//				setProperty("hibernate.show_sql", "spring.jpa.properties.hibernate.show_sql");
//				setProperty("hibernate.globally_quoted_identifiers", "spring.jpa.properties.hibernate.globally_quoted_identifiers");
//			}
//		};
//		return result;
//	}
}
