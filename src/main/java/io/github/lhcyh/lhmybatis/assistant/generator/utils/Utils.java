package io.github.lhcyh.lhmybatis.assistant.generator.utils;

import io.github.lhcyh.lhmybatis.assistant.generator.enums.Type;

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

    public static List<TableHandle> getTableList(Connection connection){
        List<TableHandle> tableList=new ArrayList<>();
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
                TableHandle table=new TableHandle();
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

    private static Type getType(String value){
        String typeString="";
        for(int i=0;i<value.length();i++){
            if(value.charAt(i)=='('){
                break;
            }else {
                typeString+=value.charAt(i);
            }
        }
        typeString=typeString.replaceAll("\\s.*", ""); // 删除第一个空格及其后的所有内容
        return Type.getTypeByMysqlTypeValue(typeString);
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
}
