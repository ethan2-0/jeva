package jeva;

import java.util.HashMap;
import java.lang.reflect.*;

public class ClassAnnotationScanner {

	public static HashMap<String, Method> getResponders(Object o) {
		HashMap<String, Method> methods = new HashMap<>();
		for(Method method : o.getClass().getMethods()) {
			Serves serves = method.getAnnotation(Serves.class);
			if(serves != null) {
				if(method.getParameterCount() == 1) {
					if(!method.getParameterTypes()[0].isAssignableFrom(Request.class)) {
						System.out.println("WARN: Found method " + method.getName() + " that has the annotation Serves but "
							+ "a paramater of type " + method.getParameterTypes()[0].getCanonicalName() + " where Request was expected.");
					}
				} else if(method.getParameterCount() > 1) {
					System.out.println("WARN: Found method " + method.getName() + " that has the annotation Serves but "
							+ method.getParameterCount() + " parameters, whereas 1 was expected.");
				}
				String path = serves.path();
				methods.put(path, method);
			}
		}
		return methods;
	}

}
