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
        String code="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"Common\">\n\n" +
                "    <sql id=\"exampleClause\">\n" +
                "        <foreach collection=\"leftJoinList\" item=\"joinInfo\">\n" +
                "            left join ${joinInfo.rightTable} on ${joinInfo.leftTable}.${joinInfo.leftKey}=${joinInfo.rightTable}.${joinInfo.rightKey}\n" +
                "        </foreach>\n" +
                "        <where>\n" +
                "            <foreach collection=\"orCriterionList\" item=\"criterionList\" separator=\"or\">\n" +
                "                <trim prefix=\"(\" prefixOverrides=\"and\" suffix=\")\">\n" +
                "                    <foreach collection=\"criterionList\" item=\"criterion\">\n" +
                "                        and ${criterion.table}.${criterion.field}\n" +
                "                        <choose>\n" +
                "                            <when test=\"criterion.valueType=='noValue'\">\n" +
                "                                ${criterion.condition}\n" +
                "                            </when>\n" +
                "                            <when test=\"criterion.valueType=='singleValue'\">\n" +
                "                                ${criterion.condition} #{criterion.value}\n" +
                "                            </when>\n" +
                "                            <when test=\"criterion.valueType=='betweenValue'\">\n" +
                "                                ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
                "                            </when>\n" +
                "                            <when test=\"criterion.valueType=='listValue'\">\n" +
                "                                ${criterion.condition}\n" +
                "                                <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\n" +
                "                                    #{listItem}\n" +
                "                                </foreach>\n" +
                "                            </when>\n" +
                "                        </choose>\n" +
                "                    </foreach>\n" +
                "                </trim>\n" +
                "            </foreach>\n" +
                "        </where>\n" +
                "        <if test=\"orderBy!=null\">\n" +
                "            order by ${orderBy.table}.${orderBy.field} ${orderBy.condition}\n" +
                "        </if>\n" +
                "        <if test=\"limitNum!=null\">\n" +
                "            limit\n"+
                "            <if test=\"limitStart!=null\">\n" +
                "                 #{limitStart},\n" +
                "            </if>\n" +
                "            #{limitNum}\n" +
                "        </if>\n" +
                "    </sql>\n" +
                "\n" +
                "</mapper>\n";
        common.setCode(code);
        common.setName("Common");
        return common;
    }

    public ProjectCode getProjectCode() {
        return projectCode;
    }
}
