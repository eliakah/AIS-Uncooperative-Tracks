package vectors;

import prediction.Point;

/**
 * Created by Research on 9/6/2016.
 */
public class VesselVector extends GeoVector {

    public VesselVector(Point location, float latComponent, float lonComponent) {
        super(location, latComponent, lonComponent);
    }

    public VesselVector(Point location, float speed, int vectorAngle) {
        super(location, speed, vectorAngle);
    }

    public void simulate(int numSeconds, WindVector windVector, CurrentVector currentVector) {

    }

}
