package ir.piana.dev.chainedcurl.curl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ir.piana.dev.chainedcurl.core.config.JsonConfig;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlCollectorService;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlDto;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlProvidable;
import ir.piana.dev.chainedcurl.core.service.ValueSupplier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { CurlProcessorTest.MyTestConfiguration.class })
public class CurlProcessorTest {
    @TestConfiguration
    @Import(JsonConfig.class)
    @ConfigurationProperties(prefix = "spring.datasource")
    @TestPropertySource("/application.yml")
    static class MyTestConfiguration extends HikariConfig {
        @Bean
        public ChainedCurlProvidable firstChainedCurlProvider(
                ResourceLoader resourceLoader,
                @Qualifier("chainedCurlYamlMapper") ObjectMapper objectMapper) {
            return () -> {
                Resource resource = resourceLoader.getResource(
                        "classpath:register-creditor-then-sign-one-tap-mandate.yaml");
                try (InputStream is = resource.getInputStream()) {
                    return objectMapper.readValue(is, ChainedCurlDto.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
        }

        @Bean
        public ValueSupplier ledgerIdSupplier(JdbcTemplate jdbcTemplate) {
            return () -> jdbcTemplate.queryForObject(
                    "select to_number(max(ledger_id), 'FM9G999999999999999') + 1 from creditor_da.creditor",
                    String.class);
        }

        @Bean
        public ValueSupplier merchantNameSupplier(JdbcTemplate jdbcTemplate) {
            return () -> jdbcTemplate.queryForObject(
                    "select 'test_' || to_number(max(ledger_id), 'FM9G999999999999999') + 1 from creditor_da.creditor",
                    String.class);
        }

        @Bean
        public ValueSupplier redirectUrlSupplier(JdbcTemplate jdbcTemplate) {
            return () -> jdbcTemplate.queryForObject(
                    "select 'http://test' || " +
                            "to_number(max(ledger_id), 'FM9G999999999999999') + 1 || " +
                            "'.ir' from creditor_da.creditor",
                    String.class);
        }

        @Bean
        public ValueSupplier merchantCodeSupplier(JdbcTemplate jdbcTemplate) {
            return () -> jdbcTemplate.queryForObject(
                    "select to_number(max(merchant_code), 'FM9G999999999999999') + 1 from creditor_da.creditor",
                    String.class);
        }

        @Bean
        public ChainedCurlCollectorService chainedCurlCollectorService(
                List<ChainedCurlProvidable> chainedCurlProvidableList) {
            return new ChainedCurlCollectorService(chainedCurlProvidableList);
        }

        @Bean("dataSource")
        public DataSource getDataSource(@Qualifier("chainedCurlYamlMapper") ObjectMapper yamlObjectMapper,
                                        ResourceLoader resourceLoader) {
            /*try (InputStream is = resourceLoader.getResource("classpath:application.yml").getInputStream()) {
                HikariConfig hikariConfig = yamlObjectMapper.readValue(is, HikariConfig.class);
                return new HikariDataSource(hikariConfig);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/

            HikariConfig config = new HikariConfig();
            /*config.setJdbcUrl( "jdbc:postgresql://localhost:26257/gateway_db" );
            config.setDriverClassName("org.postgresql.Driver");
            config.setUsername( "jibit" );
            config.setPassword( "pass" );*/
            config.setJdbcUrl( "jdbc:h2:mem:testdb" );
            config.setDriverClassName("org.h2.Driver");
            config.setUsername( "sa" );
            config.setPassword( "password" );
            config.addDataSourceProperty( "cachePrepStmts" , "true" );
            config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
            config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
            return new HikariDataSource(config);
        }

        @Bean(name = "jdbcTemplate")
        public JdbcTemplate jdbcTemplate1(@Qualifier("dataSource") DataSource ds) {
            return new JdbcTemplate(ds);
        }
    }

    @Autowired
    private ChainedCurlCollectorService chainedCurlCollectorService;

    @Test
    void testUrl() {
        List<String> stepNames = chainedCurlCollectorService.getStepNames(
                "register-creditor-then-sign-one-tap-mandate");
        Assertions.assertThat(stepNames.size()).isEqualTo(8);

        ChainedCurlDto chainedCurl = chainedCurlCollectorService.getChainedCurl(
                "register-creditor-then-sign-one-tap-mandate");
        Assertions.assertThat(chainedCurl.getName()).isEqualTo("register-creditor-then-sign-one-tap-mandate");
    }
}
