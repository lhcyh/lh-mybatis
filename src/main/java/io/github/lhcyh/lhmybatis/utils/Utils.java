package io.github.lhcyh.lhmybatis.utils;

import io.github.lhcyh.lhmybatis.pojo.Field;
import io.github.lhcyh.lhmybatis.pojo.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String underscoreToCamel(String underscore,boolean startUpperCase){
        String[] words=underscore.split("_");
        String camel="";
        for(int i=0;i<words.length;i++){
            if(i==0&&!startUpperCase){
                camel+=words[i];
                continue;
            }
            StringBuilder stringBuilder=new StringBuilder(words[i]);
            stringBuilder.setCharAt(0,Character.toUpperCase(words[i].charAt(0)));
            camel+=stringBuilder.toString();
        }
        return camel;
    }

//    private static Connection getConnection(String url,String user,String password){
//        Connection connection = null;
//        try {
//            //connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lhhomepage?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8", "root","123418abc");//连接数据库
//            connection = DriverManager.getConnection(url, user,password);//连接数据库
//        } catch(SQLException e){
//            e.printStackTrace();
//        }
//        return connection;
//    }

//    private static Statement getStatement(Connection connection){
//        Statement statement = null;
//        try {
//            statement = connection.createStatement();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return statement;
//    }

    private static ResultSet executeQuery(Statement statement, String sql){
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    //关闭结果集
    public static void closeResultSet(ResultSet rs){
        try {
            if(rs != null ){
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //关闭执行方法
    public static void closeStatement(Statement statement){
        try {
            if(statement != null ){
                statement.close();
                statement = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    //关闭连接
    public static void closeConnection(Connection connection){
        try {
            if(connection != null ){
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Table> getTableList(Connection connection){
        List<Table> tableList=new ArrayList<>();
        Statement statement= null;
        try {
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return tableList;
        }
        ResultSet resultSet=executeQuery(statement,"SHOW TABLES");

        while (true){
            try {
                if (!resultSet.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
//                System.out.println(resultSet.getString(1));
                //tableList.add(resultSet.getString(1));
                Table table=new Table();
                table.setName(resultSet.getString(1));
                List<Field> fileList=getFieldList(connection,table.getName());
                table.setFieldList(fileList);
                tableList.add(table);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        closeResultSet(resultSet);
        closeStatement(statement);
        //closeConnection(connection);
        return tableList;
    }

    private static boolean getAllowNull(String value){
        if(value.equals("YES")){
            return true;
        }else {
            return false;
        }
    }
    private static boolean getPrimaryKey(String value){
        if(value.equals("PRI")){
            return true;
        }else {
            return false;
        }
    }

    private static boolean getUnique(String value){
        if(value.equals("PRI")||value.equals("UNI")){
            return true;
        }else {
            return false;
        }
    }

    private static Integer getSize(String value){
//        System.out.println("size:"+value);
        try {
            for(int i=0;i<value.length();i++){
                char c=value.charAt(i);
                if(c=='('){
                    String size="";
                    for(int j=i+1;j<value.length();j++){
                        if(value.charAt(j)==')'){
                            return Integer.parseInt(size);
                        }else {
                            size += value.charAt(j);
                        }
                    }
                }
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }

    private static Field.Type getType(String value){
        String typeString="";
        for(int i=0;i<value.length();i++){
            if(value.charAt(i)=='('){
                break;
            }else {
                typeString+=value.charAt(i);
            }
        }
        return Field.Type.getTypeByValue(typeString);
    }

    public static List<Field> getFieldList(Connection connection,String table){
        List<Field> fieldList=new ArrayList<>();
        String sql="SHOW COLUMNS FROM `"+table+"`";
        try {
            Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery(sql);
            while (resultSet.next()){
                Field field=new Field();
                field.setName(resultSet.getString("Field"));
                field.setAllowNull(getAllowNull(resultSet.getString("Null")));
                field.setIsPrimaryKey(getPrimaryKey(resultSet.getString("Key")));
                field.setIsUnique(getUnique(resultSet.getString("key")));
                field.setSize(getSize(resultSet.getString("Type")));
                field.setType(getType(resultSet.getString("Type")));
                fieldList.add(field);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return fieldList;
    }
//    public static String getPropertyByTableName(String tableName,String fill){
//        String property=fill+"private "+underscoreToCamel(tableName,true)+" "+underscoreToCamel(tableName,false)+";";
//        return property;
//    }
//
//    public static String getListPropertyByTableName(String tableName,String fill){
//        String listProperty=fill+"private List<"+underscoreToCamel(tableName,true)+"> "+underscoreToCamel(tableName,false)+";";
//        return listProperty;
//    }
//
//    public static String getJavaGetByTableName(String tableName,String fill){
//        String javaGet=fill+"public "+underscoreToCamel(tableName,true)+" get"+underscoreToCamel(tableName,true)+"(){\n";
//        javaGet+=fill+"  return "+underscoreToCamel(tableName,false)+";";
//        javaGet+=fill+"}";
//        return javaGet;
//    }
//
//    public static String getJavaListGetByTableName(String tableName,String fill){
//        String javaList=fill+"public List<"+underscoreToCamel(tableName,true)+"> get"+underscoreToCamel(tableName,true)+"List(){\n";
//        javaList+=fill+"    return "+underscoreToCamel(tableName,false)+";\n";
//        javaList+=fill+"}";
//        return javaList;
//    }
//
//    public static String getJavaSetByTableName(String tableName,String fill){
//        String javaSet=fill+"public void set"+underscoreToCamel(tableName,true)+"("+underscoreToCamel(tableName,true)+" "+underscoreToCamel(tableName,false)+"){\n";
//        javaSet+=fill+"   return "+underscoreToCamel(tableName,false)+";\n";
//        javaSet+=fill+"}";
//        return javaSet;
//    }
//
//    public static String getJavaListSetByTableName(String tableName,String fill){
//        String javaListSet=fill+"public void set"+underscoreToCamel(tableName,true)+"List("+underscoreToCamel(tableName,true)+" "+underscoreToCamel(tableName,false)+"){\n";
//        javaListSet+=fill+"   return "+underscoreToCamel(tableName,false)+"List;\n";
//        javaListSet+=fill+"}";
//        return javaListSet;
//    }
}
