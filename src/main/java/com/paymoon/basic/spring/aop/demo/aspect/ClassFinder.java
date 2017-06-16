package com.paymoon.basic.spring.aop.demo.aspect;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class ClassFinder {
	private static Log logger = LogFactory.getLog(ClassFinder.class);
	public static void main(String[] args) {
//		List<Class> list = getAllClassByInterface(IAspect.class);
		List<Class> list = getAllClassByInterfaceViaSpring(IAspect.class);
		System.out.println(list.size());
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		
	}

	public static List<Class> getAllClassByInterface(Class clazz) {
		 List<Class>  list = new ArrayList<Class>();
		 String packagename = clazz.getPackage().getName();
		// System.out.println(packagename);//根据包名寻找
		Set<String> classnames = getClassName(packagename, true);
		for (Iterator iterator = classnames.iterator(); iterator.hasNext();) {
			String classname = (String) iterator.next();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			try {
				Class	c = loader.loadClass(classname);
				if (clazz.isAssignableFrom(c)&&!c.equals(clazz)){
					list.add(c);
				 }
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return list;
		
	}
	public static Set<String> getClassName(String packageName, boolean isRecursion) {
		Set<String> classNames = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String packagePath = packageName.replace(".", "/");

		URL url = loader.getResource(packagePath);
		if (url != null) {
			String protocol = url.getProtocol();
			if (protocol.equals("file")) {
				logger.debug("find url:"+url.getPath()+",packageName:"+packageName);
				classNames = getClassNameFromDir(url.getPath(), packageName, isRecursion);
			} else if (protocol.equals("jar")) {
				JarFile jarFile = null;
				try {
					jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (jarFile != null) {
					getClassNameFromJar(jarFile.entries(), packageName, isRecursion);
				}
			}
		} else {
			/* 从所有的jar包中查找包名 */
			classNames = getClassNameFromJars(((URLClassLoader) loader).getURLs(), packageName, isRecursion);
		}

		return classNames;
	}
	private static ArrayList<Class> findClass(File file, String packagename) {
		ArrayList<Class> list = new ArrayList<>();
		if (!file.exists()) {
			return list;
		}
		File[] files = file.listFiles();
		for (File file2 : files) {
			if (file2.isDirectory()) {
				assert !file2.getName().contains(".");// 添加断言用于判断
				ArrayList<Class> arrayList = findClass(file2, packagename + "." + file2.getName());
				list.addAll(arrayList);
			} else if (file2.getName().endsWith(".class")) {
				try {
					// 保存的类文件不需要后缀.class
					list.add(Class.forName(packagename + '.' + file2.getName().substring(0, file2.getName().length() - 6)));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	/**
	 * 从项目文件获取某包下所有类
	 * 
	 * @param filePath
	 *            文件路径
	 * @param className
	 *            类名集合
	 * @param isRecursion
	 *            是否遍历子包
	 * @return 类的完整名称
	 */
	private static Set<String> getClassNameFromDir(String filePath, String packageName, boolean isRecursion) {
		Set<String> className = new HashSet<String>();
		File file = new File(filePath);
		File[] files = file.listFiles();
		for (File childFile : files) {
			if (childFile.isDirectory()) {
				if (isRecursion) {
					className.addAll(getClassNameFromDir(childFile.getPath(), packageName + "." + childFile.getName(), isRecursion));
				}
			} else {
				String fileName = childFile.getName();
				if (fileName.endsWith(".class") && !fileName.contains("$")) {
					className.add(packageName + "." + fileName.replace(".class", ""));
				}
			}
		}

		return className;
	}
	private static Set<String> getClassNameFromJar(Enumeration<JarEntry> jarEntries, String packageName, boolean isRecursion) {
		Set<String> classNames = new HashSet<String>();

		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			if (!jarEntry.isDirectory()) {
				/*
				 * 这里是为了方便，先把"/" 转成 "." 再判断 ".class" 的做法可能会有bug (FIXME: 先把"/" 转成
				 * "." 再判断 ".class" 的做法可能会有bug)
				 */
				String entryName = jarEntry.getName().replace("/", ".");
				if (entryName.endsWith(".class") && !entryName.contains("$") && entryName.startsWith(packageName)) {
					entryName = entryName.replace(".class", "");
					if (isRecursion) {
						classNames.add(entryName);
					} else if (!entryName.replace(packageName + ".", "").contains(".")) {
						classNames.add(entryName);
					}
				}
			}
		}

		return classNames;
	}
	/**
	 * 从所有jar中搜索该包，并获取该包下所有类
	 * 
	 * @param urls
	 *            URL集合
	 * @param packageName
	 *            包路径
	 * @param isRecursion
	 *            是否遍历子包
	 * @return 类的完整名称
	 */
	private static Set<String> getClassNameFromJars(URL[] urls, String packageName, boolean isRecursion) {
		Set<String> classNames = new HashSet<String>();

		for (int i = 0; i < urls.length; i++) {
			String classPath = urls[i].getPath();

			// 不必搜索classes文件夹
			if (classPath.endsWith("classes/")) {
				continue;
			}

			JarFile jarFile = null;
			try {
				jarFile = new JarFile(classPath.substring(classPath.indexOf("/")));
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (jarFile != null) {
				classNames.addAll(getClassNameFromJar(jarFile.entries(), packageName, isRecursion));
			}
		}

		return classNames;
	}
	/**
	 * 在Java，你怎么找到给定类的所有子类？_java_帮酷问答
		http://ask.helplib.com/250765
	 * @param clazz
	 * @return
	 */
	public static List<Class> getAllClassByInterfaceViaSpring(Class clazz) {
		 List<Class>  list = new ArrayList<Class>();
		 String packagename = clazz.getPackage().getName();
		 //子类最好在packagename.impl下
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
		provider.addIncludeFilter(new AssignableTypeFilter(clazz));

		//scan in org.example.package
		//这个包名是不写死的，会找下一级，比getAllClassByInterface简化好多
		Set<BeanDefinition> components = provider.findCandidateComponents(packagename);
		for (BeanDefinition component : components)
		{
			 try {
				Class c = Class.forName(component.getBeanClassName());
				//isAssignableFrom是用native写的，判断当前类是否有继承关系
				if (clazz.isAssignableFrom(c)&&!c.equals(clazz)){
					list.add(c);
				 }
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		return list;
	}
	
	
}
