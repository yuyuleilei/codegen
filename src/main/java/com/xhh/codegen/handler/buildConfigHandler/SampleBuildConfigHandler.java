package com.xhh.codegen.handler.buildConfigHandler;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.xhh.codegen.service.BuildConfig;
import com.xhh.codegen.service.BuildConfigHandler;
import com.xhh.codegen.service.impl.ProjectBuildConfig;
import com.xhh.codegen.utils.FilenameUtil;

@SuppressWarnings("serial")
public class SampleBuildConfigHandler implements BuildConfigHandler, Serializable {


    public void initConfig(BuildConfig buildConfig) {
        ProjectBuildConfig pbConfig = (ProjectBuildConfig) buildConfig;
        String tableName = pbConfig.getTableName();

        //过滤掉tb_
        /*if (tableName.substring(0, 3).equalsIgnoreCase("tb_")) {
			tableName = tableName.substring(3);
		}*/

        //从表名中取得组名（包名）和设置模块名
        if (StringUtils.isNotBlank(tableName)) {
            int splitorCount = StringUtils.countMatches(tableName, "_");
            //去掉第一个下划线前面的应用前缀
            if (splitorCount >= 1) {
                tableName = StringUtils.substringAfter(tableName, "_");
            }
            //把表名中第一个和第三个下划线之间的内容作为组名
            if (tableName.contains("_")) {
                String groupName = StringUtils.substringBefore(tableName, "_");
                pbConfig.setGroupName(groupName);
            }

            pbConfig.setModuleName(formatForCamel(tableName, "_"));
        }

        //表标签格式化
        String tableLabel = pbConfig.getTableLabel();
        if (StringUtils.isNotBlank(tableLabel)) {
            if (tableLabel.indexOf("|") > 0) {
                tableLabel = StringUtils.substringBefore(tableLabel, "|");
            }
            //去掉后缀字符“表”后再作为表标签
            tableLabel = StringUtils.removeEnd(tableLabel, "表");
            pbConfig.setTableLabel(tableLabel);
        }
    }

    public void afterParseDataModel(BuildConfig buildConfig) {
        ProjectBuildConfig pbConfig = (ProjectBuildConfig) buildConfig;
        //获取输出文件夹
        String outDir = (String) pbConfig.getDataModel().get(ProjectBuildConfig.DMK_OUTPUTDIRECTORY);
        /*if (OSinfo.isWindows()) {
            outDir = "f:" + outDir;
        }*/
        outDir = FilenameUtil.normalize(outDir + File.separatorChar);
        pbConfig.getDataModel().put(ProjectBuildConfig.DMK_OUTPUTDIRECTORY, outDir);
    }

    public void afterParseOutputModel(BuildConfig buildConfig) {

    }

    public void beforeParseDataModel(BuildConfig buildConfig) {

    }

    public void beforeParseOutputModel(BuildConfig buildConfig) {

    }

    /**
     * 下划线转驼峰式
     *
     * @param str
     * @param splitor
     * @return
     */
    private String formatForCamel(String str, String splitor) {
        str = StringUtils.capitalize(str);
        StringBuffer sb = new StringBuffer();
        sb.append(str);
        int count = sb.indexOf(splitor);
        while (count != 0) {
            int num = sb.indexOf(splitor, count);
            count = num + 1;
            if (num != -1) {
                char ss = sb.charAt(count);
                char ia = (char) (ss - 32);
                sb.replace(count, count + 1, ia + "");
            }
        }
        String ss = sb.toString().replaceAll(splitor, "");
        return ss;
    }
}
