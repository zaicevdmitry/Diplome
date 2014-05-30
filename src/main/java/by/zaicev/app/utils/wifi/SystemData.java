package by.zaicev.app.utils.wifi;

import java.util.HashMap;
import java.util.Map;

/**
 * Инициализирует и хранит данные по контрольным точкам.
 */
public class SystemData {

    /* ======== FIELDS ======== */
    public static final String TOP_LEFT_MAC = "02:1a:11:fc:22:7c"; //HTC One
    public static final String TOP_RIGHT_MAC = "60:a1:0a:f2:0c:b6"; //test 1
    public static final String BOTTOM_LEFT_MAC = "78:1d:ba:27:7b:03";  // T
    public static final String BOTTOM_RIGHT_MAC = "78:1d:ba:27:7b:0b";// T2

    /**
     * Список контрольных точек.
     */
    private static Map<String, ControlPoint> controlPointList;

    /* ======== METHODS ======== */

    /**
     * Инициализирует данные по  контрольным точкам.
     */
    public static void initControlPoints() {
        controlPointList = new HashMap<String, ControlPoint>();

        // расстояние от точки и уровень сигнала на этом расстоянии
        HashMap<Integer, Integer> distanceSignalLevelTopLeft = new HashMap<Integer, Integer>();
        distanceSignalLevelTopLeft.put(0, 99);
        distanceSignalLevelTopLeft.put(2, 96);
        distanceSignalLevelTopLeft.put(4, 84);
        distanceSignalLevelTopLeft.put(6, 78);
        distanceSignalLevelTopLeft.put(8, 69);

        //1-я точка 60:a1:0a:f2:0c:b6 test 1
        ControlPoint topLeftPoint = new ControlPoint(TOP_LEFT_MAC, new Point(-4, 4), distanceSignalLevelTopLeft);
        controlPointList.put(topLeftPoint.getMac(), topLeftPoint);

        // расстояние от точки и уровень сигнала на этом расстоянии
        HashMap<Integer, Integer> distanceSignalLevelTopRight =  new HashMap<Integer, Integer>();
        distanceSignalLevelTopRight.put(0, 99);
        distanceSignalLevelTopRight.put(2, 96);
        distanceSignalLevelTopRight.put(4, 88);
        distanceSignalLevelTopRight.put(6, 83);
        distanceSignalLevelTopRight.put(8, 73);

        //2-я точка 78:1d:ba:27:7b:0b T2
        ControlPoint topRightPoint = new ControlPoint(TOP_RIGHT_MAC, new Point(4, 4), distanceSignalLevelTopRight);
        controlPointList.put(topRightPoint.getMac(), topRightPoint);

        HashMap<Integer, Integer> distanceSignalLevelBottomLeft = new HashMap<Integer, Integer>();
        distanceSignalLevelBottomLeft.put(0, 99);
        distanceSignalLevelBottomLeft.put(2, 96);
        distanceSignalLevelBottomLeft.put(4, 88);
        distanceSignalLevelBottomLeft.put(6, 76);
        distanceSignalLevelBottomLeft.put(8, 68);


        //3-я точка 02:1a:11:f7:a7:bd HTC ONE
        ControlPoint bottomLeftPoint = new ControlPoint(BOTTOM_LEFT_MAC, new Point(-4, -4), distanceSignalLevelBottomLeft);
        controlPointList.put(bottomLeftPoint.getMac(), bottomLeftPoint);

        HashMap<Integer, Integer> distanceSignalLevelBottomRight = new HashMap<Integer, Integer>();
        distanceSignalLevelBottomRight.put(0, 99);
        distanceSignalLevelBottomRight.put(2, 92);
        distanceSignalLevelBottomRight.put(4, 86);
        distanceSignalLevelBottomRight.put(6, 73);
        distanceSignalLevelBottomRight.put(8, 64);

        //4-я точка 78:1d:ba:27:7b:03 T
        ControlPoint bottomRightPoint = new ControlPoint(BOTTOM_RIGHT_MAC, new Point(4,-4), distanceSignalLevelBottomRight);
        controlPointList.put(bottomRightPoint.getMac(), bottomRightPoint);
    }

    /* ======== GETTERS & SETTERS ======== */

    public static String getTopLeftMac() {
        return TOP_LEFT_MAC;
    }

    public static String getTopRightMac() {
        return TOP_RIGHT_MAC;
    }

    public static String getBottomLeftMac() {
        return BOTTOM_LEFT_MAC;
    }

    public static String getBottomRightMac() {
        return BOTTOM_RIGHT_MAC;
    }

    public static Map<String, ControlPoint> getControlPointList() {
        return controlPointList;
    }
}
