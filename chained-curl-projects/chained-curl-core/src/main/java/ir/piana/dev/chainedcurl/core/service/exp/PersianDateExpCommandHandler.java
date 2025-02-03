package ir.piana.dev.chainedcurl.core.service.exp;

import com.github.mfathi91.time.PersianDate;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlContext;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlExpCommandHandler;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PersianDateExpCommandHandler extends ChainedCurlExpCommandHandler<PersianDate> {
    public PersianDateExpCommandHandler(String value, String pattern) {
        super(Objects.nonNull(value) ?
                (pattern == null ?
                        PersianDate.parse(value, dtf) :
                        PersianDate.parse(value, DateTimeFormatter.ofPattern(pattern))) :
                PersianDate.now());
    }

    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public ChainedCurlExpCommandHandler handle(
            String command, ChainedCurlContext chainedCurlContext) {
        String[] split = command.split(":");
        switch (split[0].trim().toLowerCase()) {
            case "now" -> {
                value = PersianDate.now();
                return this;
            }
            case "as-date" -> {
                return new DateExpCommandHandler(value.toGregorian().format(dtf), null);
            }
            case "format" -> {
                return new StringExpCommandHandler(value.format(DateTimeFormatter.ofPattern(split[1])));
            }
            case "plus" -> {
                switch (split[1].trim().toLowerCase()) {
                    case "year":
                        value = value.plusYears(Integer.parseInt(split[2]));
                        return this;
                    case "month":
                        value = value.plusMonths(Integer.parseInt(split[2]));
                        return this;
                    case "day":
                        value = value.plusDays(Integer.parseInt(split[2]));
                        return this;
                }
            }
        }
        throw new RuntimeException("command not exist!");
    }
}
