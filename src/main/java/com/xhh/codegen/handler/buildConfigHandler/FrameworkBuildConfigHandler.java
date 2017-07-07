package com.xhh.codegen.handler.buildConfigHandler;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.xhh.codegen.model.InOutType;
import com.xhh.codegen.model.OutputModel;
import com.xhh.codegen.model.TemplateModel;
import com.xhh.codegen.service.BuildConfig;
import com.xhh.codegen.service.BuildConfigHandler;
import com.xhh.codegen.service.impl.ProjectBuildConfig;
import com.xhh.codegen.utils.BuilderHelper;
import com.xhh.codegen.utils.ClassLoaderUtil;
import com.xhh.codegen.utils.FileUtil;
import com.xhh.codegen.utils.FilenameUtil;

@SuppressWarnings("serial")
public class FrameworkBuildConfigHandler implements BuildConfigHandler, Serializable {


    public void initConfig(BuildConfig buildConfig) {

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
        ProjectBuildConfig pbConfig = (ProjectBuildConfig) buildConfig;
        //获取模板文件夹
        String tplDir = (String) pbConfig.getDataModel().get(ProjectBuildConfig.DMK_TEMPLATEDIRECTORY);
        tplDir = new File(ClassLoaderUtil.getResource(tplDir).getFile()).getAbsolutePath();
        tplDir = FilenameUtil.normalize(tplDir + File.separatorChar);

        //获取模板文件列表
        List<String> tplFileList = new ArrayList<String>();
        getAllFiles(new File(tplDir), tplFileList);

        //获取输出文件夹
        String outDir = (String) pbConfig.getDataModel().get(ProjectBuildConfig.DMK_OUTPUTDIRECTORY);

        //先清理输出文件夹，防止打包下载时包含历史文件
        File outDirFile = new File(outDir);
        if (outDirFile.exists()) {
            FileUtil.deleteDirectory(outDirFile);
        }

        //先把模板文件夹拷贝到目标文件夹，然后再解析输出替换
        //FileUtil.copyFolder(tplDir, outDir);

        //根据模板文件结构组装同结构的输出文件列表
        TemplateModel templateModel;
        OutputModel outputModel;
        Map<String, OutputModel> outputModelMap = pbConfig.getOutputModel();
        Map<String, OutputModel> excludeOutputMap = new LinkedHashMap<String, OutputModel>();
        for (String tplFile : tplFileList) {
            templateModel = new TemplateModel();
            templateModel.setName(tplFile);
            templateModel.setType(InOutType.FILE);
            templateModel.setTemplate(tplFile);

            String outFile = outDir + StringUtils.removeStart(tplFile, tplDir);
            //解析带有构建参数的字符串
            outFile = BuilderHelper.parseBuildParams(pbConfig.getDataModel(), outFile);
            outputModel = new OutputModel(outFile);
            outputModel.setType(InOutType.FILE);
            outputModel.setOutput(outFile);
            outputModel.setTemplateModel(templateModel);
            if (includeFile(outputModel) == false) {
                excludeOutputMap.put(outFile, outputModel);
            } else {
                outputModelMap.put(outFile, outputModel);
            }
        }

        //不需要解析构建的文件直接复制
        for (Entry<String, OutputModel> entry : excludeOutputMap.entrySet()) {
            File file = new File(entry.getValue().getOutput());
            if (file.getParentFile().exists() == false) {
                file.getParentFile().mkdirs();
            }
            String outputPath = entry.getValue().getOutput();
            //特殊文件处理
            if (outputPath.endsWith("gitignore") && !outputPath.endsWith(".gitignore")) {
                outputPath = outputPath.replace("gitignore", ".gitignore");
            }
            FileUtil.copyFile(entry.getValue().getTemplateModel().getTemplate(), outputPath);
            System.out.println("复制原始文件=" + entry.getValue().getOutput());
        }
    }

    public void beforeParseDataModel(BuildConfig buildConfig) {

    }

    public void beforeParseOutputModel(BuildConfig buildConfig) {

    }

    private static void getAllFiles(File dir, List<String> fileList) {
        File[] fs = dir.listFiles();
        for (int i = 0; i < fs.length; i++) {
            /*if(includeFile(fs[i])==false){
				continue;
			}*/
            if (fs[i].isDirectory()) {
                try {
                    getAllFiles(fs[i], fileList);
                } catch (Exception e) {
                }
            } else {
                fileList.add(fs[i].getAbsolutePath());
                System.out.println(fs[i].getAbsolutePath());
            }
        }
    }

    /**
     * 是否包含文件，返回true则包含，否则不包含
     *
     * @param outputModel
     * @return
     */
    private static boolean includeFile(OutputModel outputModel) {
        String[] excludeKeys = new String[]{".git", "gitignore", ".eot", ".svg", ".ttf", ".woff", ".jpg", ".png", ".js", ".css"};
        for (String key : excludeKeys) {
            if (outputModel.getOutput().contains(key)) {
                return false;
            }
        }

        return true;
    }
}
