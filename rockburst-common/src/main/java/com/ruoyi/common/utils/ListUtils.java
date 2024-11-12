package com.ruoyi.common.utils;

import org.apache.commons.compress.utils.Lists;
import org.springframework.cglib.beans.BeanMap;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ListUtils {

    /***
     * list 内部去重
     * @param list
     * @param filedName
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <E> List<E> deWeight(List<E> list, String filedName) throws Exception {
        if (null == list || list.size() <= 0) {
            throw new Exception("入参不能空");
        }
        if (null == filedName || "".equals(filedName)) {
            throw new Exception("入参不能空");
        }
        Map<String, E> map = new HashMap<String, E>();
        for (E s : list) {
            String key = "";
            String[] filedNameArr = filedName.split(",");
            /**
             * 合并key
             */
            for (int i = 0; i < filedNameArr.length; i++) {
                String methodName = "get" + filedNameArr[i];
                Class<?> cl = s.getClass();
                Method me = cl.getMethod(methodName);
                if (null == me.invoke(s)) {
                    key = key + "";
                } else {
                    key = key + me.invoke(s).toString();
                }
                if (i < filedNameArr.length - 1) {
                    key = key + ",";
                }
            }
            map.put(key, s);
        }
        List<E> returnList = new ArrayList<E>();
        for (Object o : map.values()) {
            returnList.add((E) o);
        }
        return returnList;
    }

    /***
     * @param list
     * @param filedName 去重的字段
     * @param type 0 保存第一个 1保存最后一个
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <E> List<E> deWeight1(List<E> list, String filedName, String type) throws Exception {
        if (null == list || list.size() <= 0) {
            throw new Exception("入参不能空");
        }
        if (null == filedName || "".equals(filedName)) {
            throw new Exception("入参不能空");
        }
        Map<String, E> map = new HashMap<String, E>();
        boolean isMap = (list.get(0) instanceof Map);
        for (E s : list) {
            String key = "";
            String[] filedNameArr = filedName.split(",");
            /**
             * 合并key
             */
            for (int i = 0; i < filedNameArr.length; i++) {
                if (isMap) {// 是Map类型
                    Map<String, Object> m = (Map<String, Object>) s;
                    key = key + m.get(filedNameArr[i]).toString();
                } else {
                    String methodName = "get" + filedNameArr[i];
                    Class<?> cl = s.getClass();
                    Method me = cl.getMethod(methodName);
                    if (null == me.invoke(s)) {
                        key = key + "";
                    } else {
                        key = key + me.invoke(s).toString();
                    }
                }
                if (i < filedNameArr.length - 1) {
                    key = key + ",";
                }
            }
            if ("0".equals(type)) {// 保存第一个
                if (!map.containsKey(key)) {// 有这个key
                    map.put(key, s);
                }
            } else {
                map.put(key, s);
            }
        }
        List<E> returnList = new ArrayList<E>();
        for (Object o : map.values()) {
            returnList.add((E) o);
        }
        return returnList;
    }

    /**
     * 去重list<String>中重复的数据
     *
     * @param list
     * @return
     */
    public static List<String> duplicateRemoval(List<String> list) {
        if (null == list || list.size() <= 0) {
            return null;
        }
        List<String> listTemp = new ArrayList<String>();
        for (String s : list) {
            s = s.trim();
            if (!listTemp.contains(s)) {
                listTemp.add(s);
            }
        }
        return listTemp;
    }

    /**
     * 判断list<String>有无重复的数据，没有则返回true 有则返回false
     *
     * @param list
     * @return
     */
    public static boolean duplicateCheck(List<String> list) {
        if (null == list || list.size() <= 0) {
            return true;
        }
        List<String> listTemp = new ArrayList<>();
        for (String s : list) {
            s = s.trim();
            if (!listTemp.contains(s)) {
                listTemp.add(s);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 去重list<object>中重复的数据 对象中需要实现equals 和hashcode两个方法
     *
     * @param list
     * @return
     */
    public static <E> List<E> duplicateList(List<E> list) {
        if (null == list || list.size() <= 0) {
            return null;
        }
        return new ArrayList<>(new HashSet<>(list));
    }

    /**
     * 判断 list<String> 是否存在重复数据 存在重复返回true
     *
     * @param list
     * @return
     */
    public static <E> boolean isObjectRepeat(List<E> list) {
        if (null == list || list.size() <= 0) {
            return false;
        }
        List<E> temp = new ArrayList<E>();
        for (E s : list) {
            if (temp.contains(s)) {
                return true;
            } else {
                temp.add(s);
            }
        }
        return false;
    }

    /***
     * 将List<String> 转成字符串 以str分割
     * @param list
     * @param str 分隔符
     * @return
     */
    public static String listToStr(List<String> list, String str) {
        if (null == list || list.size() <= 0) {
            return null;
        }
        String returnStr = "";
        String s = (str == null || "".equals(str)) ? "," : str;
        for (int i = 0; i < list.size(); i++) {
            returnStr = returnStr + list.get(i);
            if (i < list.size() - 1) {
                returnStr = returnStr + s;
            }
        }
        return returnStr;
    }

    /***
     * 将List<String> 转成字符串 以str分割
     * @param list
     * @param str 分隔符
     * @return
     */
    public static String intlistToStr(List<Integer> list, String str) {
        if (null == list || list.size() <= 0) {
            return null;
        }
        String returnStr = "";
        String s = (str == null || "".equals(str)) ? "," : str;
        for (int i = 0; i < list.size(); i++) {
            returnStr = returnStr + list.get(i) + "";
            if (i < list.size() - 1) {
                returnStr = returnStr + s;
            }
        }
        return returnStr;
    }

    /***
     * 将List<String> 转成字符串 以str分割
     * @param list
     * @param str 分隔符
     * @return
     */
    public static String datelistToStr(List<Date> list, String str) {
        if (null == list || list.size() <= 0) {
            return null;
        }
        String returnStr = "";
        String s = (str == null || "".equals(str)) ? "," : str;
        for (int i = 0; i < list.size(); i++) {
            returnStr = returnStr + "'" + DateUtil.formatDate(list.get(i), "yyyy-MM-dd") + "'";
            if (i < list.size() - 1) {
                returnStr = returnStr + s;
            }
        }
        return returnStr;
    }

    /**
     * @param <E>
     * @param targetList 目标排序List
     * @param sortField  排序字段(实体类属性名) 必须是数字
     * @param sortMode   排序方式（asc or desc）
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E> void sort(List<E> targetList, final String sortField, final String sortMode, Class<E> cls) throws NoSuchFieldException {
        if (cls.isAssignableFrom(Map.class)) {
            sortMapList(targetList, sortField, sortMode);
        } else {
            Field field = cls.getDeclaredField(sortField);
            field.setAccessible(true);
            Collections.sort(targetList, (Comparator) (obj1, obj2) -> {
                int retVal;
                try {
                    //倒序
                    if (sortMode != null && "desc".equals(sortMode)) {
                        retVal = field.get(obj2).toString().compareTo(field.get(obj1).toString());
                    }
                    //正序
                    else {
                        retVal = field.get(obj1).toString().compareTo(field.get(obj2).toString());
                    }
                } catch (Exception e) {
                    throw new RuntimeException();
                }
                return retVal;
            });
        }
    }

    /**
     * mapList排序
     *
     * @param targetList
     * @param sortField
     * @param sortMode
     * @param <E>
     */
    public static <E> void sortMapList(List<E> targetList, final String sortField, final String sortMode) {
        Collections.sort(targetList, (obj1, obj2) -> {
            int retVal;
            try {
                String str1 = ((Map) obj1).get(sortField).toString();
                String str2 = ((Map) obj2).get(sortField).toString();
                if (sortMode != null && "desc".equals(sortMode)) {//倒序
                    retVal = str2.compareTo(str1);
                }else {//正序
                    retVal = str1.compareTo(str2);
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
            return retVal;
        });
    }

    /**
     * 分割List
     *
     * @param list     待分割的list
     * @param pageSize 每段list的大小
     * @return List<< List < T>>
     */
    public static <T> List<List<T>> splitList(List<T> list, int pageSize) {
        if (null == list || list.size() <= 0) {
            return null;
        }
        int listSize = list.size();// list的大小
        int page = (listSize + (pageSize - 1)) / pageSize;// 页数
        List<List<T>> listArray = new ArrayList<List<T>>();// 创建list数组
        // ,用来保存分割后的list
        for (int i = 0; i < page; i++) {// 按照数组大小遍历
            List<T> subList = new ArrayList<T>();// 数组每一位放入一个分割后的list
            for (int j = 0; j < listSize; j++) {// 遍历待分割的list
                int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize; // 当前记录的页码(第几页)
                if (pageIndex == (i + 1)) { // 当前记录的页码等于要放入的页码时
                    subList.add(list.get(j)); // 放入list中的元素到分割后的list(subList)
                }
                if ((j + 1) == ((j + 1) * pageSize)) {// 当放满一页时退出当前循环
                    break;
                }
            }
            listArray.add(subList);// 将分割后的list放入对应的数组的位中
        }
        return listArray;
    }

    /**
     * 判断 list<String> 是否存在重复数据 存在重复返回true
     *
     * @param list
     * @return
     */
    public static boolean isRepeat(List<String> list) {
        if (null == list || list.size() <= 0) {
            return false;
        }
        List<String> temp = new ArrayList<String>();
        for (String s : list) {
            if (temp.contains(s)) {
                return true;
            } else {
                temp.add(s);
            }
        }
        return false;
    }

    /**
     * @param list
     * @return
     */
    public static Map<String, List<Map<String, Object>>> mergeListMap(List<Map<String, Object>> list) {
        if (null == list || list.size() <= 0) {
            return null;
        }
        Map<String, List<Map<String, Object>>> retrunMap = new HashMap<String, List<Map<String, Object>>>();
        for (Map<String, Object> m : list) {
            if (m.get("site_id") != null) {
                if (retrunMap.containsKey(m.get("site_id").toString())) {// 已经存在
                    // key
                    retrunMap.get(m.get("site_id").toString()).add(m);
                } else {
                    List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
                    temp.add(m);
                    retrunMap.put(m.get("site_id").toString(), temp);
                }
            }
        }
        return retrunMap;
    }

    /**
     * 将List泛型为string类型的集合转化为List泛型为Integer类型
     *
     * @param list
     * @return
     */
    public static List<Integer> listStrToInteger(List<String> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return null;
        }
        List<Integer> res = new ArrayList<Integer>();
        for (String string : list) {
            if (null != string && !"".equals(string)) {
                res.add(toInteger(string));
            }
        }
        return res;
    }
    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val) {
        return toLong(val).intValue();
    }
    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val) {
        return toDouble(val).longValue();
    }
    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val) {
        if (val == null || "".equals(val) || "null".equals(val)) {
            return 0D;
        }
        try {
            return Double.valueOf(val.toString().trim());
        } catch (Exception e) {
            return 0D;
        }
    }



    /**
     * 将List泛型为Integer类型的集合转化为List泛型为String类型
     *
     * @param list
     * @return
     */
    public static List<String> listIntegerToStr(List<Integer> list) throws Exception {
        if (null == list || list.size() <= 0) {
            return null;
        }
        List<String> res = new ArrayList<String>();
        for (Integer item : list) {
            if (null != item) {
                res.add(item + "");
            }
        }
        return res;
    }

    /***
     * 判断是否为空 为空返回真
     * @param list
     * @return
     */
    public static boolean isNull(List<?> list) {
        return null == list || list.size() <= 0;
    }

    /***
     * 判断是否为空 为空返回真
     * @param list
     * @return
     */
    public static boolean isNullSet(Collection<?> list) {
        return null == list || list.size() <= 0;
    }
    /***
     * 判断是否为空,不为空返回真
     * @param list
     * @return
     */
    public static boolean isNotNull(List<?> list) {
        return null != list && list.size() > 0;
    }

    /**
     * 深度拷贝
     *
     * @param src
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    /**
     * 返回bean中 id的集合 (去重的)
     *
     * @param list
     * @param idName
     * @param cls
     * @param <E>
     * @return
     * @throws Exception
     */
    public static <E> List<String> getBeanIdListOnStr(List<E> list, final String idName, Class<E> cls) throws Exception {
        if (isNull(list) || null == idName || idName.isEmpty()) {
            throw new NullPointerException();
        }
        Field field = cls.getDeclaredField(idName);
        field.setAccessible(true);
        Set<String> idSet = new HashSet<>();
        for (E obj : list) {
            idSet.add(field.get(obj).toString());
        }
        return new ArrayList<>(idSet);
    }

    /**
     * 返回bean中 id的集合 (去重的)
     *
     * @param list
     * @param idName
     * @param cls
     * @param <E>
     * @return
     * @throws Exception
     */
    public static <E> List<Integer> getBeanIdListOnInt(List<E> list, final String idName, Class<E> cls) throws Exception {
        if (isNull(list) || null == idName || idName.isEmpty()) {
            throw new NullPointerException();
        }
        Field field = cls.getDeclaredField(idName);
        field.setAccessible(true);
        Set<Integer> idSet = new HashSet<>();
        for (E obj : list) {
            idSet.add(Integer.parseInt(field.get(obj).toString()));
        }
        return new ArrayList<>(idSet);
    }

    /**
     * 将key相同的数据整合到map中
     *
     * @param dtos
     * @param keyField
     * @param cls
     * @param <E>
     * @return
     * @throws Exception
     */
    public static <E> Map<String, List<E>> getBeanListToMaps(List<E> dtos, final String keyField, Class<E> cls) throws NoSuchFieldException, IllegalAccessException {
        if (isNull(dtos) || null == keyField || keyField.isEmpty()) {
            throw new NullPointerException();
        }
        // 排序
        sort(dtos, keyField, null, cls);

        Field field = cls.getDeclaredField(keyField);
        field.setAccessible(true);

        Map<String, List<E>> map = new HashMap<>();
        List<E> dtoList = null;
        String id = "", temporary;
        for (E dto : dtos) {
            temporary = field.get(dto).toString();
            if (!id.equals(temporary)) {
                id = temporary;
                dtoList = new ArrayList<>();
                map.put(id, dtoList);
                dtoList.add(dto);
            } else {
                dtoList.add(dto);
            }
        }
        return map;
    }

    /**
     * 将key相同的数据整合到map中
     *
     * @param keyField
     * @return
     * @throws Exception
     */
    public static Map<String, List<Map<String, Object>>> getMapListToMaps(List<Map<String, Object>> maps, final String keyField, boolean isSort) throws NoSuchFieldException, IllegalAccessException {
        if (isNull(maps) || null == keyField || keyField.isEmpty()) {
            throw new NullPointerException();
        }
        // 排序
        if (isSort) {
            sortMapList(maps, keyField, null);
        }

        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<>();
        List<Map<String, Object>> dtoList = null;
        String id = "", temporary;
        for (Map<String, Object> dto : maps) {
            temporary = dto.get(keyField).toString();
            if (!id.equals(temporary)) {
                id = temporary;
                dtoList = new ArrayList<>();
                map.put(id, dtoList);
                dtoList.add(dto);
            } else {
                dtoList.add(dto);
            }
        }
        return map;
    }

    public static Map<String, List<Map<String, Object>>> getMapListToMaps(List<Map<String, Object>> maps, final String keyField) throws NoSuchFieldException, IllegalAccessException {
        return getMapListToMaps(maps, keyField, true);
    }

    /**
     * 将List<Map<String,Object>>转换为List<T>
     * @param maps
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps,Class<T> clazz) throws InstantiationException, IllegalAccessException {
        List<T> list = Lists.newArrayList();
        if (maps != null && maps.size() > 0) {
            Map<String, Object> map = null;
            T bean = null;
            for (int i = 0,size = maps.size(); i < size; i++) {
                map = maps.get(i);
                bean = clazz.newInstance();
                mapToBean(map, bean);
                list.add(bean);
            }
        }
        return list;
    }

    /**
     * 将map装换为javabean对象
     * @param map
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map,T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

//    public static void main(String[] args) {
//        Class<OutProductTypeDiscountDto> cls = OutProductTypeDiscountDto.class;
//        Field[] fields = cls.getDeclaredFields();
//        for (Field field : fields) {
//            Class c = field.getType();
//            System.out.println(c.isAssignableFrom(List.class));
//        }
//    }

}
