package io.github.lhcyh.lhmybatis.utils;

import io.github.lhcyh.lhmybatis.pojo.CodeFile;
import io.github.lhcyh.lhmybatis.pojo.Project;
import io.github.lhcyh.lhmybatis.pojo.ProjectCode;
import io.github.lhcyh.lhmybatis.pojo.Table;

public class MybatisFactory {
    private Project project;
    private ProjectCode projectCode;
    public MybatisFactory(Project project){
        this.project=project;
        generateCode();
    }

    private void generateCode(){
        this.projectCode=new ProjectCode();
        for(Table table:project.getTableList()){
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
    }

    public ProjectCode getProjectCode() {
        return projectCode;
    }
}
