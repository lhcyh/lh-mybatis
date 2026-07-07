package io.github.lhcyh.lhmybatis.assistant.generator.utils;

import io.github.lhcyh.lhmybatis.assistant.generator.pojo.CodeFile;
import io.github.lhcyh.lhmybatis.assistant.generator.pojo.ProjectCode;

public class MybatisFactory {
    private Project project;
    private ProjectCode projectCode;
    public MybatisFactory(Project project){
        this.project=project;
        generateCode();
    }

    private void generateCode(){
        this.projectCode=new ProjectCode();
        for(TableHandle table:project.getTableList()){
            CodeFile pojoCodeFile=new CodeFile();
            pojoCodeFile.setName(table.getFileName(""));
            pojoCodeFile.setCode(table.getPojoCode(project));
            this.projectCode.getPojo().add(pojoCodeFile);

            String code=table.getEntityCode(project);
            if(code!=null) {
                CodeFile entityCodeFile = new CodeFile();
                entityCodeFile.setName(table.getFileName("Entity"));
                entityCodeFile.setCode(code);
                this.projectCode.getEntity().add(entityCodeFile);
            }

            CodeFile mapperCodeFile=new CodeFile();
            code=table.getMapperCode(project);
            mapperCodeFile.setName(table.getFileName("Mapper"));
            mapperCodeFile.setCode(code);
            this.projectCode.getMapper().add(mapperCodeFile);

            CodeFile serviceCodeFile=new CodeFile();
            code=table.getServiceCode(project);
            serviceCodeFile.setName(table.getFileName("Service"));
            serviceCodeFile.setCode(code);
            this.projectCode.getService().add(serviceCodeFile);

            CodeFile controllerCodeFile=new CodeFile();
            code=table.getControllerCode();
            controllerCodeFile.setName(table.getFileName("Controller"));
            controllerCodeFile.setCode(code);
            this.projectCode.getController().add(controllerCodeFile);

            CodeFile xmlCodeFile=new CodeFile();
            code=table.getXml(project);
            xmlCodeFile.setName(table.getFileName("Mapper"));
            xmlCodeFile.setCode(code);
            this.projectCode.getMapperXml().add(xmlCodeFile);
        }
        CodeFile codeFile=getCommonFile();
        this.projectCode.getMapperXml().add(codeFile);
    }

    public CodeFile getCommonFile(){
        CodeFile common=new CodeFile();
        StringBuilder code=new StringBuilder();
        code.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        code.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
        code.append("<mapper namespace=\"Common\">\n\n");
        code.append("    <sql id=\"aggSelectClause\">\n");
        code.append("        ${aggregateSelect}\n");
        code.append("    </sql>\n");
        code.append("\n");
        code.append("    <sql id=\"joinClause\">\n");
        code.append("        <foreach collection=\"leftJoinList\" item=\"joinInfo\">\n");
        code.append("            LEFT JOIN ${joinInfo.rightTable} on ${joinInfo.leftTable}.${joinInfo.leftKey}=${joinInfo.rightTable}.${joinInfo.rightKey}\n");
        code.append("            <if test=\"joinInfo.key!=null\">\n");
        code.append("                AND ${joinInfo.leftTable}.${joinInfo.key} = ${joinInfo.value}\n");
        code.append("            </if>\n");
        code.append("        </foreach>\n");
        code.append("    </sql>\n");
        code.append("\n");
        code.append("    <sql id=\"conditionClause\">\n");
        code.append("        <if test=\"criterion.prefix!=null\">\n");
        code.append("            ${criterion.prefix}\n");
        code.append("        </if>\n");
        code.append("        <if test=\"criterion.attribute!=null\">\n");
        code.append("            ${criterion.attribute}\n");
        code.append("        </if>\n");
        code.append("        <choose>\n");
        code.append("            <when test=\"criterion.valueType=='noValue'\">\n");
        code.append("                ${criterion.condition}\n");
        code.append("            </when>\n");
        code.append("            <when test=\"criterion.valueType=='singleValue'\">\n");
        code.append("                ${criterion.condition} #{criterion.value}\n");
        code.append("            </when>\n");
        code.append("            <when test=\"criterion.valueType=='betweenValue'\">\n");
        code.append("                ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n");
        code.append("            </when>\n");
        code.append("            <when test=\"criterion.valueType=='listValue'\">\n");
        code.append("                ${criterion.condition}\n");
        code.append("                <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\n");
        code.append("                    #{listItem}\n");
        code.append("                </foreach>\n");
        code.append("            </when>\n");
        code.append("        </choose>\n");
        code.append("    </sql>\n");
        code.append("\n");
        code.append("    <sql id=\"whereClause\">\n");
        code.append("        <where>\n");
        code.append("            <foreach collection=\"criterionList\" item=\"criterion\">\n");
        code.append("                <include refid=\"Common.conditionClause\"></include>\n");
        code.append("            </foreach>\n");
        code.append("        </where>\n");
        code.append("    </sql>\n");
        code.append("\n");
        code.append("    <sql id=\"footClause\">\n");
        code.append("        <if test=\"gbAttribute!=null\">\n");
        code.append("            group by\n");
        code.append("            ${gbAttribute}\n");
        code.append("            <if test=\"havingList!=null\">\n");
        code.append("                having\n");
        code.append("                <foreach collection=\"havingList\" item=\"criterion\">\n");
        code.append("                    <include refid=\"Common.conditionClause\"></include>\n");
        code.append("                </foreach>\n");
        code.append("            </if>\n");
        code.append("        </if>\n");
        code.append("        <if test=\"orderList!=null\">\n");
        code.append("            order by\n");
        code.append("            <foreach collection=\"orderList\" item=\"criterion\" separator=\",\">\n");
        code.append("                ${criterion.attribute} ${criterion.value}\n");
        code.append("            </foreach>\n");
        code.append("        </if>\n");
        code.append("        <if test=\"limitNum!=null\">\n");
        code.append("            limit\n");
        code.append("            <if test=\"limitStart!=null\">\n");
        code.append("                #{limitStart},\n");
        code.append("            </if>\n");
        code.append("            #{limitNum}\n");
        code.append("        </if>\n");
        code.append("    </sql>\n");
        code.append("\n");
        code.append("    <sql id=\"exampleClause\">\n");
        code.append("        <include refid=\"Common.joinClause\"></include>\n");
        code.append("        <include refid=\"Common.whereClause\"></include>\n");
        code.append("        <include refid=\"Common.footClause\"></include>\n");
        code.append("    </sql>\n");
        code.append("</mapper>\n");
        common.setCode(code.toString());
        common.setName("Common");
        return common;
    }

    public ProjectCode getProjectCode() {
        return projectCode;
    }
}
