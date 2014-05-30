package by.zaicev.app.utils.wifi;

import by.zaicev.app.utils.wifi.Point;

import java.util.HashMap;

/**
 * Описывает контрольную точку.
 */
public class ControlPoint {

    /**
     * Мак-адресс контрольной точки.
     */
    private String mac;

    /**
     * Координаты контрольной точки.
     */
    private Point coordinates;

    /**
     * Контрольные значения уровня сигнала на определённых расстояниях от контрольной точки.
     * Ключ - расстояние от контрольной точки.
     * Значение - уровень сигнала в этой точке.
     */
    private HashMap<Integer, Integer> distanceSignalLevel;


    public ControlPoint() {
    }

    /**
     * @param mac                 Мак-адресс контрольной точки.
     * @param coordinates         Координаты контрольной точки.
     * @param distanceSignalLevel Контрольные значения уровня сигнала на определённых расстояниях от контрольной точки.
     */
    public ControlPoint(String mac, Point coordinates, HashMap<Integer, Integer> distanceSignalLevel) {
        this.mac = mac;
        this.coordinates = coordinates;
        this.distanceSignalLevel = distanceSignalLevel;
    }

    /**
     * По уровню сигнала определить расстояние.
     *
     * @param _level уровень сигнала. Если расстояние определить невозможно, то вернёт null.
     * @return ближайшее расстояние.
     */
    public Integer getDistance(Integer _level) {
        if (_level == null) {
            return 8;// максимальное расстояние в сетке
        }

        Integer result = null;

        for (Integer currentDistance : distanceSignalLevel.keySet()) {
            Integer currentLevel = distanceSignalLevel.get(currentDistance);
            if (currentLevel.equals(_level)) {
                result = currentDistance;
                return result;
            } else if (_level >= currentLevel) {
                Integer prevDistance = currentDistance - 2;
                Integer prevLevel = distanceSignalLevel.get(prevDistance);
                Integer a = (currentLevel + prevLevel) / 2 + currentLevel;
                if (a >= _level) {
                    result = prevDistance;
                } else {
                    result = currentDistance;
                }
            }
        }

        if (result == null) {
            result = 8;// максимальное расстояние в сетке
        }

        return result;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    public HashMap<Integer, Integer> getDistanceSignalLevel() {
        return distanceSignalLevel;
    }

    public void setDistanceSignalLevel(HashMap<Integer, Integer> distanceSignalLevel) {
        this.distanceSignalLevel = distanceSignalLevel;
    }
}
