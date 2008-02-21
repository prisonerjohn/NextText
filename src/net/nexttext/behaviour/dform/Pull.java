//
// Copyright (C) 2005 Jason Lewis
//

package net.nexttext.behaviour.dform;

import net.nexttext.CoordinateSystem;
import net.nexttext.Locatable;
import net.nexttext.TextObjectGlyph;
import net.nexttext.Vector3;
import net.nexttext.behaviour.TargetingAction;
import net.nexttext.property.NumberProperty;
import net.nexttext.property.Vector3PropertyList;
import net.nexttext.property.Vector3Property;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A DForm which pulls the TextObject towards the mouse.
 */
public class Pull extends DForm implements TargetingAction {
    
    static final String REVISION = "$CVSHeader: NextText/src/net/nexttext/behaviour/dform/Pull.java,v 1.3 2005/05/20 14:22:16 david_bo Exp $";

    Locatable target;

    public void setTarget( Locatable target ) {
        this.target = target;
    }

    /**
     * @param speed is the speed with which the points are pulled.
     * @param reach will pull farther points faster with a higher value.
     */
    public Pull(Locatable target, double speed, double reach) {
        this.target = target;
        properties().init("Speed", new NumberProperty(speed));
        properties().init("Reach", new NumberProperty(reach));
    }

    public ActionResult behave(TextObjectGlyph to) {
        double speed = ((NumberProperty)properties().get("Speed")).get();
        double reach = ((NumberProperty)properties().get("Reach")).get();

        // Get the position of the target relative to the TextObject.
        CoordinateSystem ac = to.getAbsoluteCoordinateSystem();
        Vector3 targetV = target.getLocation();
        targetV = ac.transformInto( targetV );
         
        // Traverse the control points of the glyph, determine the distance
        // from it to the target and move it part way there.
        Vector3PropertyList cPs = getControlPoints(to);
        Iterator i = cPs.iterator();
        while (i.hasNext()) {
            Vector3Property cP = (Vector3Property) i.next();
            Vector3 p = cP.get();

            Vector3 offset = new Vector3(targetV);
            offset.sub(p);

            offset.scalar(1 / Math.pow(1 + 1 / reach, offset.length() / speed));

            p.add(offset);
            cP.set(p);
        }
        return new ActionResult(false, false, false);
    }
}
