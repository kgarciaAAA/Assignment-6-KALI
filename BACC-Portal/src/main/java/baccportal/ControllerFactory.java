package baccportal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javafx.util.Callback;

// Maps controller classes to suppliers that build them with their dependencies.
// Plugged into FXMLLoader via setControllerFactory(...) in App.java
// Every controller now declares its dependencies in its constructor instead of reaching into App.
public final class ControllerFactory implements Callback<Class<?>, Object> {

    private final Map<Class<?>, Supplier<?>> bindings = new HashMap<>();

    public <T> void bind(Class<T> type, Supplier<? extends T> supplier) {
        bindings.put(type, supplier);
    }

    @Override
    public Object call(Class<?> type) {
        Supplier<?> supplier = bindings.get(type);
        if (supplier == null) {
            throw new IllegalStateException(
                    "No controller binding registered for " + type.getName());
        }
        return supplier.get();
    }
}
