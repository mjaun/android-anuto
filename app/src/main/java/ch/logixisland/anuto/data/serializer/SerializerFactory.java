package ch.logixisland.anuto.data.serializer;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import ch.logixisland.anuto.util.math.Vector2;

public class SerializerFactory {

    public Serializer createSerializer() {
        Registry registry = new Registry();

        try {
            registry.bind(Vector2.class, VectorConverter.class);
        } catch (Exception e) {
            throw new RuntimeException("Error binding converters!");
        }

        Strategy strategy = new RegistryStrategy(registry);
        return new Persister(strategy);
    }

}
