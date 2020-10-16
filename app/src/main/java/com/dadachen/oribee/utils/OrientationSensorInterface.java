package com.dadachen.oribee.utils;

/**
 * Created by majidGolshadi on 9/22/2014.
 */
public interface OrientationSensorInterface {

    void orientation(float[] eulerAngles, float[] acc, float[] mag, float[] rotationVector,
                     float[] gyroscopeRaw);

}
