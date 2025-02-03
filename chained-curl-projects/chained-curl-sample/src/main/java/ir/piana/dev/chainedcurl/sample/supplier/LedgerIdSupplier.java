package ir.piana.dev.chainedcurl.sample.supplier;

import ir.piana.dev.chainedcurl.core.service.ValueSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("ledgerIdSupplier")
@RequiredArgsConstructor
public class LedgerIdSupplier implements ValueSupplier {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public String get() {
        Integer i = jdbcTemplate.queryForObject(
                "select max(merchant_code) from creditor_da.creditor",
                Integer.class);
        return String.valueOf(i + 1);
    }
}
