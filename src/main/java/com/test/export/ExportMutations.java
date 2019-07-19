package com.test.export;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将关系型数据库内容转化为适用于dgraph突变的格式
 * 
 * @author GXLO
 *
 */
public class ExportMutations {

	public static void main(String[] args) {
		try {
			String content = getContent();
			System.out.println(content);
			File file = new File("dataDgraph.txt");
			//如果不存在,创建
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getContent(){
		
		//模拟数据
		List<Map> listClass = new ArrayList();
		//班级数据
		Map<String,Object> mapClass1 = new HashMap<>();
		mapClass1.put("classId", "201901");
		mapClass1.put("className", "初一一班");
		//班级学生数据
		Map<String,Object> mapStu = new HashMap<>();
		mapStu.put("stuId", "2019110101");
		mapStu.put("stuName", "张三");
		mapStu.put("sex", "男");
		mapStu.put("birth", "20080102");
		mapStu.put("class", "201901");
		mapClass1.put("stu", mapStu);
		listClass.add(mapClass1);
		
		//班级数据
		Map<String,Object> mapClass2 = new HashMap<>();
		mapClass2.put("classId", "201902");
		mapClass2.put("className", "初一二班");
		//班级学生数据
		Map<String,Object> mapStu2 = new HashMap<>();
		mapStu2.put("stuId", "2019110201");
		mapStu2.put("stuName", "李四");
		mapStu2.put("sex", "男");
		mapStu2.put("birth", "20080102");
		mapStu2.put("class", "201902");
		mapClass2.put("stu", mapStu2);
		
		listClass.add(mapClass2);
		
		//组装数据
		StringBuilder sb = new StringBuilder();
		sb.append("{\r\n");
		sb.append("set{\r\n"); 
		for(Map cla:listClass){
			String classId = (String) cla.get("classId");
			String className = (String) cla.get("className");
			String stuId = (String) ((Map)cla.get("stu")).get("stuId");
			
			sb.append("_:"+classId+" <name> \""+className+"\" .\r\n");
			sb.append("_:"+classId+" <stuent> _:"+stuId+" .\r\n");
			
			String stuName = (String) ((Map)cla.get("stu")).get("stuName");
			String sex = (String) ((Map)cla.get("stu")).get("sex");
			String birth = (String) ((Map)cla.get("stu")).get("birth");
			String clas = (String) ((Map)cla.get("stu")).get("class");
			
			sb.append("_:"+stuId+" <stuname> \""+stuName+"\" .\r\n");
			sb.append("_:"+stuId+" <sex> \""+sex+"\" .\r\n");
			sb.append("_:"+stuId+" <birth> \""+birth+"\" .\r\n");
			sb.append("_:"+stuId+" <clas> _:"+classId+" .\r\n");
			
		}
		
		sb.append("}"); 
		sb.append("}"); 
		return sb.toString();
	}
	
}
