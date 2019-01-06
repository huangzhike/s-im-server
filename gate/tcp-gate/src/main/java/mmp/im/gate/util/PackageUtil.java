package mmp.im.gate.util;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


public class PackageUtil {

    private static final String[] CLASS_PATH_PROP = {"java.class.path", "java.ext.dirs", "sun.boot.class.path"};
    private static final List<File> CLASS_PATH_ARRAY = getClassPath();

    private static List<File> getClassPath() {
        List<File> ret = new ArrayList<>();

        String delim = ":";
        if (System.getProperty("os.name").contains("Windows")) {
            delim = ";";
        }

        for (String pro : CLASS_PATH_PROP) {
            String[] paths = System.getProperty(pro).split(delim);
            for (String path : paths)
                ret.add(new File(path));
        }
        String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File rootFile = new File(root);
        if (!ret.contains(rootFile)) {
            ret.add(rootFile);
        }

        return ret;
    }

    private static List<String> getClassInPackage(String pkgName) {
        List<String> ret = new ArrayList<>();
        String rPath = pkgName.replace('.', '/') + "/";
        try {
            for (File classPath : CLASS_PATH_ARRAY) {
                if (!classPath.exists())
                    continue;
                if (classPath.isDirectory()) {
                    File dir = new File(classPath, rPath);
                    if (!dir.exists())
                        continue;
                    for (File file : dir.listFiles()) {
                        if (file.isFile()) {
                            String clsName = file.getName();
                            clsName = pkgName + "." + clsName.substring(0, clsName.length() - 6);
                            ret.add(clsName);
                        }
                    }
                } else {
                    FileInputStream fis = new FileInputStream(classPath);
                    JarInputStream jis = new JarInputStream(fis, false);
                    JarEntry jarEntry;
                    while ((jarEntry = jis.getNextJarEntry()) != null) {
                        String eName = jarEntry.getName();
                        if (eName.startsWith(rPath) && !eName.endsWith("/")) {
                            ret.add(eName.replace('/', '.').substring(0, eName.length() - 6));
                        }
                        jis.closeEntry();
                    }
                    jis.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ret;
    }


    public static List<Class<?>> getSubClasses(String packageName, Class<?> superClass) {
        List<Class<?>> classes = new ArrayList<>();
        List<String> list = PackageUtil.getClassInPackage(packageName);
        for (String name : list) {
            try {
                Class<?> cls = Class.forName(name);
                // 父类或接口
                if (superClass.isAssignableFrom(cls)) {
                    classes.add(cls);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

    /**
     * 获取指定包内某种注解的类
     *
     * @param annotationClass 注解类型
     * @param packageName     包名
     * @return 类列表
     */
    public static List<Class<?>> getAnnotationClass(String packageName, Class<? extends Annotation> annotationClass) {
        List<Class<?>> classes = new ArrayList<>();
        List<String> classNames = PackageUtil.getClassInPackage(packageName);

        for (String className : classNames) {
            try {
                Class<?> type = Class.forName(className);
                if (type.isAnnotationPresent(annotationClass)) {
                    classes.add(type);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }
}
