import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


void setup() {
  size(400, 200);
}

void draw() {
  background(-1);
  String str = printUsage();
  fill(0);
  textSize(24);
  text(str, 20, height/2);
}

String printUsage() {
  String str = "";
  OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
    method.setAccessible(true);
    if (method.getName().startsWith("get") 
      && Modifier.isPublic(method.getModifiers())) {
      Object value;
      try {
        value = method.invoke(operatingSystemMXBean);
      } 
      catch (Exception e) {
        value = e;
      } // try 
      str = method.getName() + " \n = " + value;
    }
  }
  return str;
}