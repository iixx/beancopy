package service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 深度复制工具类
 * Bean中必须有对应的getter, setter方法.
 * 可以复制包含 "8种基本类型, String, java.util.Date, enum, Set, List 以及多层嵌套" 的Bean.
 *
 *@author Song
 */
public class BeanCopy {
	
    /**
     * 深度复制Bean.
     * @param orig 复制源
     * @param dest 复制目标
     * @param clazz 制定复制的类型. 即dest.getClass()
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static void deepCopy(Object orig, Object dest, Class<?> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        if(orig == null || dest == null || clazz == null){
            return;
        }
        Method[] methods = clazz.getDeclaredMethods();
        ArrayList<Method> getters = new ArrayList<Method>();
        for (Method method : methods) {
            String name = method.getName();
            if(name.startsWith("get") && ! name.equals("getClass")){
                getters.add(method);
                continue;
            }
            if(name.startsWith("is")
                    && (method.getReturnType().getName().equals("boolean")
                            || method.getReturnType().getName().equals("java.lang.Boolean"))
                    && hasThisMethod(methods, "set" + name.substring(2))){
                getters.add(method);
            }
        }
        methods = clazz.getMethods();
        HashMap<String, Method> setters = new HashMap<String, Method>();
        for (Method method : methods) {
            if(method.getName().startsWith("set")){
                setters.put("" + method.getName().substring(1), method);
            }
        }
        for (Method getter : getters) {
            Class<?> type = getter.getReturnType();
//            System.out.println(type.getName());
            if(isBasicType(type) || type.isEnum()){
                String setterName = getter.getName().substring(1);
                if(getter.getName().startsWith("is")){
                    setterName = "set" + getter.getName().substring(2);
                    setterName = setterName.substring(1);
                }
                try {
                    Object value = getter.invoke(orig);
                    Method setter = setters.get(setterName);
                    setter.invoke(dest, value);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if(isCollection(type)){
                if(type.isInterface()){
                    if(type.getName().equals("java.util.Set")){
                        Class<?> cls = theGenericTypeOfCollection(getter);
                        Set<?> origSet = (Set<?>) getter.invoke(orig);
                        Set<Object> destSet = new HashSet<Object>();
                        for (Object newOrig : origSet) {
                            Object newDest = cls.newInstance();
                            deepCopy(newOrig, newDest, cls);
                            destSet.add(newDest);
                        }
                        Method setter = setters.get(getter.getName().substring(1));
                        setter.invoke(dest, destSet);
                    }else if(type.getName().equals("java.util.List")){
                        Class<?> cls = theGenericTypeOfCollection(getter);
                        List<?> origList = (List<?>) getter.invoke(orig);
                        List<Object> destList = new ArrayList<Object>();
                        for (Object newOrig : origList) {
                            Object newDest = cls.newInstance();
                            deepCopy(newOrig, newDest, cls);
                            destList.add(newDest);
                        }
                        Method setter = setters.get(getter.getName().substring(1));
                        setter.invoke(dest, destList);
                    }
                    else{
                        System.out.println(type.getName());
                    }
                }else{
                    throw new IllegalArgumentException("未定义的接口类型, 无法实例化");
                }
                continue;
            }else{
            Object newDest = type.newInstance();
            Object newOrig = getter.invoke(orig);
            deepCopy(newOrig, newDest, type);
            Method setter = setters.get(getter.getName().substring(1));
            setter.invoke(dest, newDest);
            continue;
            }
        }
    }

    /**
     * 是否含有指定的方法
     * @param methods
     * @param string
     * @return
     */
    private static boolean hasThisMethod(Method[] methods, String string) {
        for (Method method : methods) {
            if(method.getName().equals(string)){
                return true;
            }
        }
        return false;
    }

    /**
     * 得到集合中声明的泛型类型 例如: Set<String>中的String
     * @param method
     * @return 
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static Class<?> theGenericTypeOfCollection(Method method) {
        Method[] methods = method.getGenericReturnType().getClass()
                .getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("getActualTypeArguments")) {
                m.setAccessible(true);
                Type[] types = null;
                try {
                    types = (Type[]) m.invoke(method.getGenericReturnType());
                } catch (IllegalArgumentException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
                String className = types[0].toString().split(" ")[1];
                Class<?> theClazz = null;
                try {
                    theClazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                }
                return theClazz;
            }
        }
        return null;
    }
    
    private static boolean isBasicType(Class<?> clazz){
        return clazz.isPrimitive() || basicTypes.contains(clazz.getName());
    }

    private static boolean isCollection(Class<?> type) {
        return collectionTypes.contains(type.getName());
    }
    
    /*
     * 除八大基本类型外的基本类型, 对数据库而言.
     */
    private static final List<String> basicTypes = Arrays.asList(new String[] {
            String.class.getName(), Date.class.getName() });

    private static final List<String> collectionTypes = Arrays
            .asList(new String[] { Set.class.getName(), List.class.getName(),
                    Map.class.getName() });
}
