package ir.piana.dev.chainedcurl.core.service;

import ir.piana.dev.chainedcurl.core.service.exp.ExpressionSourceType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class ExpressionInterpreterService {
    private final InitiateChainedCurlExpCommandHandlerService initiateChainedCurlExpCommandHandlerService;

    public final String process(String stepName, String exp, ChainedCurlContext chainedCurlContext) {
        String[] split1 = exp.split("#");
        if (split1.length < 3)
            throw new RuntimeException();

        String[] fieldKeys = split1[1].contains(":") ?
                split1[1].split(":") :
                new String[] {stepName, split1[1]};
        final ExpressionSourceType expressionSourceType = ExpressionSourceType.byName(split1[0]);
        Optional<String> value = chainedCurlContext.getStepContext(fieldKeys[0]).get(fieldKeys[1], expressionSourceType);
        if (value.isPresent()) {
            String[] commands = split1[2].split("&");
            ChainedCurlExpCommandHandler<?> expCommandHandler = initiateChainedCurlExpCommandHandlerService.initiate(
                    value.get(), commands, chainedCurlContext);
            /*for (int i = 1; i < Objects.requireNonNull(fieldKeys).length; i++) {
                expCommandHandler = expCommandHandler.handle(commands[i], chainedCurlContext);
            }*/
            return expCommandHandler.getValue().toString();
        }
        return null;
    }
}
