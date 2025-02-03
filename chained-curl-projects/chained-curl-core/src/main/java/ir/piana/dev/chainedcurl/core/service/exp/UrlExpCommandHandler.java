package ir.piana.dev.chainedcurl.core.service.exp;

import ir.piana.dev.chainedcurl.core.service.ChainedCurlContext;
import ir.piana.dev.chainedcurl.core.service.ChainedCurlExpCommandHandler;

public class UrlExpCommandHandler extends ChainedCurlExpCommandHandler<String> {
    public UrlExpCommandHandler(String value) {
        super(value);
    }

    @Override
    public ChainedCurlExpCommandHandler handle(
            String command, ChainedCurlContext chainedCurlContext) {
        switch (command) {
            case "last-section" -> {
                String temp = value.charAt(value.length() - 1) == '/' ?
                        value.substring(0, value.length() - 1) : value;
                temp = temp.substring(temp.lastIndexOf("/") + 1);
                return new StringExpCommandHandler(
                        temp.contains("?") ? temp.substring(0, temp.indexOf("?")) : temp);
            }
        }
        throw new RuntimeException("command not exist!");
    }
}
