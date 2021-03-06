package entity.component;

import com.google.gson.JsonObject;
import entity.Attribute;
import entity.Entity;
import shaders.Shader;

/**
 * Created by Bailey on 9/1/2017.
 */
public class ComponentRender extends Component{

    Attribute<Boolean> shouldRender = new Attribute<Boolean>("shouldRender", true);

    public ComponentRender(Entity e, JsonObject object) {
        super(EnumComponentType.RENDER, e);
        shouldRender = super.addAttribute(shouldRender);
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Shader shader) {

    }
}
