package ir.piana.dev.chainedcurl.core.service;

import lombok.Getter;

@Getter
public abstract class ChainedCurlExpCommandHandler<T> {
    protected T value;

    protected ChainedCurlExpCommandHandler(T value) {
        this.value = value;
    }

    public abstract ChainedCurlExpCommandHandler handle(
            String command, ChainedCurlContext curlChainContext);
}
