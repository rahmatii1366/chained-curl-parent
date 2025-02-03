package ir.piana.dev.chainedcurl.core.service;

import lombok.Getter;

@Getter
public class ChainedCurlProcessRuntimeException extends RuntimeException {
    private final ChainedCurlContext chainedCurlContext;

    public ChainedCurlProcessRuntimeException(ChainedCurlContext chainedCurlContext) {
        this("In process operation, occurred an exception!", null, chainedCurlContext);
    }

    public ChainedCurlProcessRuntimeException(String message, Throwable cause, ChainedCurlContext chainedCurlContext) {
        super(message, cause);
        this.chainedCurlContext = chainedCurlContext;
    }
}
