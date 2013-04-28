package utils;

import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;

public class RunSql {
    private String getDriver() {
        return "";
    }
    private String getPwd() {
        return "";
    }
    private String getUser() {
        return "";
    }
    private String getUrl() {
        return "";
    }
    private String getLog() {
        return "";
    }
    private void executeSql(String sqlFilePath) {
        final class SqlExecuter extends SQLExec {
            public SqlExecuter() {
                Project project = new Project();
                project.init();
                setProject(project);
                setTaskType("sql");
                setTaskName("sql");
            }
        }

        SqlExecuter executer = new SqlExecuter();
        executer.setSrc(new File(sqlFilePath));
        executer.setDriver(getDriver());
        executer.setPassword(getPwd());
        executer.setUserid(getUser());
        executer.setUrl(getUrl());
        executer.setPrint(true);
        executer.setOutput(new File(getLog()));
        executer.execute();
    }
    
    public static void main(String args[]) {
        new RunSql().executeSql("/tmp/a.sql");
    }

}
