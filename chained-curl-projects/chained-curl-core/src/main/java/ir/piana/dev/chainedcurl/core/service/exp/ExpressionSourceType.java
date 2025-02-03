package ir.piana.dev.chainedcurl.core.service.exp;

public enum ExpressionSourceType {
    RES_JSON("res-json"),
    FIX("fix"),
    SUPPLIER("supplier"),
    INPUT("input"),
    EXTRACTED("extracted");

    private String name;

    ExpressionSourceType(String name) {
        this.name = name;
    }

    public static ExpressionSourceType byName(String name) {
        for (ExpressionSourceType value : ExpressionSourceType.values()) {
            if (value.name.equalsIgnoreCase(name))
                return value;
        }
        throw new RuntimeException();
    }
}
