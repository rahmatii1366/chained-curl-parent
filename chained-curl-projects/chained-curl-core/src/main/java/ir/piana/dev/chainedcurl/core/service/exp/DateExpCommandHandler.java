package ir.piana.dev.chainedcurl.core.service.exp;

import com.github.mfathi91.time.PersianDate;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlContext;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlExpCommandHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateExpCommandHandler extends ChainedCurlExpCommandHandler<LocalDate> {
    public DateExpCommandHandler(String value, String pattern) {
        super(Objects.nonNull(value) ?
                (pattern == null ?
                        LocalDate.parse(value, dtf) :
                        LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern))) :
                LocalDate.now());
    }

    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public ChainedCurlExpCommandHandler handle(
            String command, ChainedCurlContext chainedCurlContext) {
        String[] split = command.split(":");
        switch (split[0]) {
            case "now" -> {
                value = LocalDate.now();
                return this;
            }
            case "as-persian-date" -> {
                return new PersianDateExpCommandHandler(
                        PersianDate.fromGregorian(value).format(dtf), null);
            }
            case "format" -> {
                return new StringExpCommandHandler(value.format(split.length == 2 ?
                        DateTimeFormatter.ofPattern(split[1]) : dtf));
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
