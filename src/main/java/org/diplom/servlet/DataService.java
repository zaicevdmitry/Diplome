package org.diplom.servlet;

import by.zaicev.app.utils.wifi.ControlPoint;
import by.zaicev.app.utils.wifi.Point;
import by.zaicev.app.utils.wifi.SystemData;
import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 *
 */
@Controller
@RequestMapping("api")
public class DataService {

    /**
     * Приходит с телефона.
     */
    private static Map<String, Phone> phones = new HashMap<>();

    // список мак адресов и уровень сигнала для них, которые пришли с девайса и которые знает сервер
    private Map<String, Integer> finalAccessPoint = new HashMap<>();

    @PostConstruct
    public void init() {
        // проинициализировать данные по контрольным точкам
        SystemData.initControlPoints();


       /* Phone phone = new Phone(new Random().nextDouble(), new Random().nextDouble());
        phone.setId("test");
        phones.put(phone.getId(), phone);*/
    }

    /**
     * Отдаёт просчитанные данные по телефонам на UI
     *
     * @return
     */
    @RequestMapping("getData")
    @ResponseBody
    public String getData() {
        Gson gson = new Gson();
        Map<String, Phone> mapToSend = new HashMap<String, Phone>();
        for (String s : phones.keySet()) {
            Phone phone = phones.get(s);
            Phone phoneToSend = new Phone(phone.getX(), phone.getY());
            phoneToSend.setId(phone.getId());
            phoneToSend.setPhoneCoord(null);
            mapToSend.put(s, phoneToSend);
        }
        return gson.toJson(mapToSend);
    }

    /**
     * Получает данные с телефона
     *
     * @param jsonData
     */
    @RequestMapping("sendData")
    @ResponseBody
    public void sendData(@RequestBody String jsonData) {

        // список мак адресов, которые пришли с девайса
        finalAccessPoint.clear();
        Gson gson = new Gson();
        Phone newPhone = gson.fromJson(jsonData, Phone.class);

        phones.put(newPhone.getId(), newPhone);

        for (String s : phones.keySet()) {
            if (newPhone.getId().equals(s)) {
                Phone phone = phones.get(s);
                Map<String, Integer> hotSpotRegistry  = phone.levelRegistry;
                Point phonePosition = getLocation(hotSpotRegistry);

                if (phonePosition == null) {
                    // не отображаем
                } else {
                    phone.setX(phonePosition.getX());
                    phone.setY(phonePosition.getY());


                }
            }
        }
    }

    private Point checkPoint(Integer topLeftDistance, Integer topRightDistance, Integer bottomRightDistance, Integer bottomLeftDistance,
                               Point bottomLeftPoint, Point bottomRightPoint, Point topLeftPoint, Point topRightPoint ){

//        Point point =  new Point();
//        if ((topLeftDistance.equals(0))) {
//            point.setX(topLeftPoint.getX());
//            point.setY(topLeftPoint.getY());
//
//        } else if (topRightDistance.equals(0)) {
//            point.setX(topRightPoint.getX());
//            point.setY(topRightPoint.getY());
//
//        } else if (bottomLeftDistance.equals(0)) {
//            point.setX(bottomLeftPoint.getX());
//            point.setY(bottomLeftPoint.getY());
//
//        } else if (bottomRightDistance.equals(0)) {
//            point.setX(bottomRightPoint.getX());
//            point.setY(bottomRightPoint.getY());
//
//        }
//        return point;
        return null;
    }

    /**
     * @param hotSpotRegistry
     * @return координаты телефона. Если по какой-то причине просчитать нельзя, то вернёт null.
     */
    private Point getLocation(Map<String, Integer> hotSpotRegistry) {
        // список маук адресов, которые есть на сервере
        Map<String, ControlPoint> accessPointsFromBD = SystemData.getControlPointList();

        // пробегаемся по маках из базы сервера
        for (String mac : hotSpotRegistry.keySet()) {
            Integer signalLevel = hotSpotRegistry.get(mac);

            // если такой мак есть и в списке из девайса
            if (accessPointsFromBD.containsKey(mac)) {
                // то дорбавляем этот мак и уровень сигнала для него в список, который будем далее обрабатывать
                finalAccessPoint.put(mac, signalLevel);
            }
        }

        // вызываем 4 раза - по одному для каждой контрольной точки
        Integer topLeftDistance = getDistanceByLevel
                (SystemData.TOP_LEFT_MAC, finalAccessPoint.get(SystemData.TOP_LEFT_MAC));

        Integer topRightDistance = getDistanceByLevel
                (SystemData.TOP_RIGHT_MAC, finalAccessPoint.get(SystemData.TOP_RIGHT_MAC));

        Integer bottomLeftDistance = getDistanceByLevel
                (SystemData.BOTTOM_LEFT_MAC, finalAccessPoint.get(SystemData.BOTTOM_LEFT_MAC));

        Integer bottomRightDistance = getDistanceByLevel
                (SystemData.BOTTOM_RIGHT_MAC, finalAccessPoint.get(SystemData.BOTTOM_RIGHT_MAC));


        Map<String, ControlPoint> controlPointList = SystemData.getControlPointList();
        Point point = new Point();


        if ((topLeftDistance == null) && (bottomRightDistance == null)) {
            return null;
        }
        // диагональ - найти нельзя

        if (topRightDistance == null && bottomLeftDistance == null) {
            return null;
        }
        // диагональ - найти нельзя

        // завершаем и больше ничего не делаем

        Point topLeftPoint = controlPointList.get(SystemData.TOP_LEFT_MAC).getCoordinates();
        Point topRightPoint = controlPointList.get(SystemData.TOP_RIGHT_MAC).getCoordinates();
        Point bottomLeftPoint = controlPointList.get(SystemData.BOTTOM_LEFT_MAC).getCoordinates();
        Point bottomRightPoint = controlPointList.get(SystemData.BOTTOM_RIGHT_MAC).getCoordinates();



        Point leftTriangle;
        Point topTriangle;
        Point rightTriangle;
        Point bottomTriangle;


        if (checkPoint(topLeftDistance,topRightDistance,bottomRightDistance, bottomLeftDistance,
                        bottomLeftPoint, bottomRightPoint, topLeftPoint, topRightPoint ) !=null) {
            point = checkPoint(topLeftDistance,topRightDistance,bottomRightDistance, bottomLeftDistance,
                    bottomLeftPoint, bottomRightPoint, topLeftPoint, topRightPoint);
           return point;

        }else {
            leftTriangle = calculatePoint(topLeftPoint, bottomLeftPoint, topLeftDistance, bottomLeftDistance);
            rightTriangle = calculatePoint(topRightPoint, bottomRightPoint, topRightDistance, bottomRightDistance);
            topTriangle = calculatePoint(topLeftPoint, topRightPoint, topLeftDistance, topRightDistance);
            bottomTriangle = calculatePoint(bottomLeftPoint, bottomRightPoint, bottomLeftDistance, bottomRightDistance);

            // метод просчитывает так, что для верхнего и нижнего треугольика координата Х будет в У,
            // а координата У будет лежать в Х, поэтому меняем местами

            int buf;
            if (topTriangle != null) {
                buf = topTriangle.getX();
                topTriangle.setX(topTriangle.getY());
                topTriangle.setY(buf);
            }
            if (bottomTriangle != null) {
                buf = bottomTriangle.getX();
                bottomTriangle.setX(bottomTriangle.getY());
                bottomTriangle.setY(buf);
            }

            List<Point> points = new ArrayList<>();
            points.add(leftTriangle);
            points.add(rightTriangle);
            points.add(topTriangle);
            points.add(bottomTriangle);

            return calculateFinalPoint(points);
        }
    }


    /**
     * Сопоставить уровень сигнала и расстояние до контрольной точки.
     *
     * @param _mac   мак-адрес контрольной точки.
     * @param _level уровень сигнала.
     * @return расстояние до указанной точки согласно уровню сигнала. Не возвращает null.
     * При каких-нибудь ошибках возвращает 0.
     */
    public Integer getDistanceByLevel(String _mac, Integer _level) {
        Integer result = 0;
        ControlPoint controlPoint = SystemData.getControlPointList().get(_mac);

        if (controlPoint != null) {
            result = controlPoint.getDistance(_level);
        }

        if (result == null) {
            result = 0;
        }

        return result;
    }


    public boolean isTriangleExists(Integer a, Integer b, Integer c) {
        boolean result = false;

        if (a + b > c) {
            result = true;
        } else if (a + c > b) {
            result = true;
        } else if (c + b > a) {
            result = true;
        }

        return result;
    }


    /**
     * Рассчитать координаты по 2 опорным точкам и расстоянии от них до телефона.
     *
     * @param _firstControlPoint  координаты первой опорной точки.
     * @param _secondControlPoint координаты второй опорной точки.
     * @param _firstDistance      расстояние от первой опорной точки.
     * @param _secondDistance     расстояние от второй опорной точки.
     * @return просчитанные координаты точки. Если просчитать невозможно, то вернёт null.
     */
    public Point calculatePoint(Point _firstControlPoint, Point _secondControlPoint, Integer _firstDistance,
                                Integer _secondDistance) {

        Point result = new Point();

        if ((_firstDistance == null) || (_secondDistance == null)) {
            return null;
        }


        // находим расстояние между контрольными точками
        int deltaControlX = _firstControlPoint.getX() - _secondControlPoint.getX();
        int deltaControlY = _firstControlPoint.getY() - _secondControlPoint.getY();
        double sumControlPow = pow(deltaControlX, 2) + pow(deltaControlY, 2);
        int controlDistance = (int) sqrt(sumControlPow);

        if (isTriangleExists(controlDistance, _firstDistance, _secondDistance)) {
//            // расстояние от первой точки по оси У
//            double numerator = pow(controlDistance, 2) + pow(_firstDistance, 2) - pow(_secondDistance, 2); // числитель
//            int denominator = 2 * controlDistance * _firstDistance; // знаменатель
//            int deltaY = (int) ((numerator / denominator) * _firstDistance);
//
//            // расстояние от первой точки по Х
//            // высота треугольника
//            double v = pow(_firstDistance, 2) - pow(deltaY, 2);
//            int deltaX = (int) sqrt(abs(v));

            // расстояние от первой точки по оси X
            int p = (controlDistance + _firstDistance + _secondDistance) / 2;
            int deltaX = (int) ((2 * sqrt(p * (p - _secondDistance) * (p - _firstDistance) * (p - controlDistance))) / _firstDistance);

            // расстояние от первой точки по оси У
            double v = pow(_firstDistance, 2) - pow(deltaX, 2);
            int deltaY = (int) sqrt(abs(v));


            // координаты искомой точки
            int x = _firstControlPoint.getX() + deltaX;
            int y = _firstControlPoint.getY() - deltaY;

            if ((x < -4) || (x > 4)) {
                x = _firstControlPoint.getX() - deltaX;
            }

            if ((y < -4) || (y > 4)) {
                y = _firstControlPoint.getY() + deltaY;
            }

            result.setX(x);
            result.setY(y);
        } else {
            return null;
        }
        return result;
    }

    /**
     * Просчитать итоговую координату на основании координат, вычесленных для каждых из 4 треугольников.
     *
     * @param points координаты из 1 треугольника
     * @return
     */
    public Point calculateFinalPoint(List<Point> points) {

        Point result = new Point();
        int pointCount = 0;
        int sumX = 0;
        int sumY = 0;


        for (Point point : points) {
            if (point != null) {
                sumX += point.getX();
                sumY += point.getY();
                pointCount++;
            }
        }

        // среднее по оси Х
        int x = sumX / pointCount;
        result.setX(x);


        // среднее по оси У
        int y = sumY / pointCount;
        result.setY(y);

        return result;
    }



    public static Map<String, Phone> getPhones() {
        return phones;
    }

    public static void setPhones(Map<String, Phone> phones) {
        DataService.phones = phones;
    }

    public class Phone {

        String id;
        int level;
        int x;
        int y;
        byte number = 0;
        String WifiHotSpot;

        public Phone[] phoneCoord = new Phone[5];

        Map<String, Integer> levelRegistry;

        public Phone(Map<String, Integer> levelRegistry) {
            this.levelRegistry = levelRegistry;

        }

        public Phone(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String getWifiHotSpot() {
            return WifiHotSpot;
        }

        public void setWifiHotSpot(String wifiHotSpot) {
            WifiHotSpot = wifiHotSpot;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, Integer> getLevelRegistry() {
            return levelRegistry;
        }

        public void setLevelRegistry(Map<String, Integer> levelRegistry) {
            this.levelRegistry = levelRegistry;
        }


        public Phone[] getPhoneCoord() {
            return phoneCoord;
        }

        public void setPhoneCoord(Phone[] phoneCoord) {
            this.phoneCoord = phoneCoord;
        }

        public byte getNumber() {
            return number;
        }

        public void setNumber(byte number) {
            this.number = number;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
