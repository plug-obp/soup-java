package soup.semantics;

import soup.syntax.model.SyntaxTreeElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Environment {
    SyntaxTreeElement model;
    Map<String, Object> environment = new HashMap<>();

    public Environment() {}
    public Environment(SyntaxTreeElement model) {
        this.model = model;
    }
    public Environment(Environment other) {
        if (other == null) { return; }
        this.model = other.model;
        this.environment = new HashMap<>(other.environment);
    }
    public Environment(SyntaxTreeElement model, Map<String, Object> environment) {
        this.model = model;
        this.environment = environment;
    }

    public void define(String key, Object value) {
        if (environment.containsKey(key)) {
            throw new RuntimeException("The variable '" + key + "' is already defined");
        }
        environment.put(key, value);
    }

    public Object lookup(String key) {
        var value = environment.get(key);
        if (value == null) {
            throw new RuntimeException("The variable '" + key + "' is not defined");
        }
        return value;
    }

    public void update(String key, Object value) {
        if (!environment.containsKey(key)) {
            throw new RuntimeException("The variable '" + key + "' is not defined");
        }
        environment.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Environment that)) return false;
        return Objects.equals(model, that.model) && Objects.equals(environment, that.environment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, environment);
    }
}
