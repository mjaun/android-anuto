package ch.logixisland.anuto.data;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import ch.logixisland.anuto.util.math.Vector2;

@Root
public class VectorConverter implements Converter<Vector2> {

    @Override
    public Vector2 read(InputNode node) throws Exception {
        return new Vector2(
                Float.valueOf(node.getAttribute("x").getValue()),
                Float.valueOf(node.getAttribute("y").getValue())
        );
    }

    @Override
    public void write(OutputNode node, Vector2 value) throws Exception {
        node.setAttribute("x", String.valueOf(value.x()));
        node.setAttribute("y", String.valueOf(value.y()));
    }

}
