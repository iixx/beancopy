package test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import bean.BeanA;

public class Atest {

    public static void main(String[] args) {
        BeanA orig = new BeanA();
        Method[] methods = orig.getClass().getDeclaredMethods();
        ArrayList<Method> getters = new ArrayList<Method>();
        for (Method method : methods) {
            if(method.getName().startsWith("get")){
                getters.add(method);
            }
        }
        for (Method getter : getters) {
            if(getter.getName() == "java.lang.String"){
                
            }
            if(getter.getName() == "int"){
                
            }
            String name = getter.getReturnType().getName();
            if(name.equals("bean.EnumA")){
                Class<?> type = getter.getReturnType();
                boolean d = type.isEnum();
                System.out.println(d);
            }
            System.out.println("end for");
        }
        System.out.println("end main");
    }
}
