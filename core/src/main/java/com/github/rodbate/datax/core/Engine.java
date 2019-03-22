package com.github.rodbate.datax.core;

import com.github.rodbate.datax.common.element.ColumnCast;
import com.github.rodbate.datax.common.exception.DataXException;
import com.github.rodbate.datax.common.spi.ErrorCode;
import com.github.rodbate.datax.common.statistics.PerfTrace;
import com.github.rodbate.datax.common.statistics.VMInfo;
import com.github.rodbate.datax.common.util.Configuration;
import com.github.rodbate.datax.core.job.JobContainer;
import com.github.rodbate.datax.core.statistics.communication.Communication;
import com.github.rodbate.datax.core.taskgroup.TaskGroupContainer;
import com.github.rodbate.datax.core.util.ConfigParser;
import com.github.rodbate.datax.core.util.ConfigurationValidate;
import com.github.rodbate.datax.core.util.ExceptionTracker;
import com.github.rodbate.datax.core.util.FrameworkErrorCode;
import com.github.rodbate.datax.core.util.container.CoreConstant;
import com.github.rodbate.datax.core.util.container.LoadUtil;
import com.github.rodbate.datax.dataxservice.face.domain.enums.State;
import com.github.rodbate.datax.remotingserver.common.ApplicationManager;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Engine是DataX入口类，该类负责初始化Job或者Task的运行容器，并运行插件的Job或者Task逻辑
 */
public class Engine {
    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);
    private static String RUNTIME_MODE;
    private static Engine engine;
    private final ApplicationManager applicationManager;
    private JobContainer jobContainer;
    private long jobId;

    private Engine() {
        this.applicationManager = new ApplicationManager(this);
        //start core job client
        this.applicationManager.start();
    }

    // 注意屏蔽敏感信息
    public static String filterJobConfiguration(final Configuration configuration) {
        Configuration jobConfWithSetting = configuration.getConfiguration("job").clone();

        Configuration jobContent = jobConfWithSetting.getConfiguration("content");

        filterSensitiveConfiguration(jobContent);

        jobConfWithSetting.set("content", jobContent);

        return jobConfWithSetting.beautify();
    }

    public static Configuration filterSensitiveConfiguration(Configuration configuration) {
        Set<String> keys = configuration.getKeys();
        for (final String key : keys) {
            boolean isSensitive = StringUtils.endsWithIgnoreCase(key, "password")
                || StringUtils.endsWithIgnoreCase(key, "accessKey");
            if (isSensitive && configuration.get(key) instanceof String) {
                configuration.set(key, configuration.getString(key).replaceAll(".", "*"));
            }
        }
        return configuration;
    }

    public static void entry(final String[] args) throws Throwable {
        Options options = new Options();
        options.addOption("job", true, "Job config.");
        options.addOption("jobid", true, "Job unique id.");
        options.addOption("mode", true, "Job runtime mode.");

        BasicParser parser = new BasicParser();
        CommandLine cl = parser.parse(options, args);

        String jobPath = cl.getOptionValue("job");

        // 如果用户没有明确指定jobid, 则 datax.py 会指定 jobid 默认值为-1
        String jobIdString = cl.getOptionValue("jobid");
        RUNTIME_MODE = cl.getOptionValue("mode");

        long jobId;
        if (!"-1".equalsIgnoreCase(jobIdString)) {
            jobId = Long.parseLong(jobIdString);
        } else {
            // only for dsc & ds & datax 3 update
            String dscJobUrlPatternString = "/instance/(\\d{1,})/config.xml";
            String dsJobUrlPatternString = "/inner/job/(\\d{1,})/config";
            String dsTaskGroupUrlPatternString = "/inner/job/(\\d{1,})/taskGroup/";
            List<String> patternStringList = Arrays.asList(dscJobUrlPatternString, dsJobUrlPatternString, dsTaskGroupUrlPatternString);
            jobId = parseJobIdFromUrl(patternStringList, jobPath);
        }

        Engine.engine.jobId = jobId;

        //register job to datax server
        Engine.engine.applicationManager.registerJob(jobId);

        Configuration configuration = ConfigParser.parse(jobPath);
        configuration.set(CoreConstant.DATAX_CORE_CONTAINER_JOB_ID, jobId);

        //打印vmInfo
        VMInfo vmInfo = VMInfo.getVmInfo();
        if (vmInfo != null) {
            LOG.info(vmInfo.toString());
        }

        LOG.info("\n" + Engine.filterJobConfiguration(configuration) + "\n");

        LOG.debug(configuration.toJSON());

        ConfigurationValidate.doValidate(configuration);

        Engine.engine.start(configuration);

    }

    /**
     * -1 表示未能解析到 jobId
     * <p>
     * only for dsc & ds & datax 3 update
     */
    private static long parseJobIdFromUrl(List<String> patternStringList, String url) {
        long result = -1;
        for (String patternString : patternStringList) {
            result = doParseJobIdFromUrl(patternString, url);
            if (result != -1) {
                return result;
            }
        }
        return result;
    }

    private static long doParseJobIdFromUrl(String patternString, String url) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }

        return -1;
    }

    public static void main(String[] args) {
        Engine.engine = new Engine();

        int exitCode = 0;
        Throwable ex = null;

        try {
            Engine.entry(args);
        } catch (Throwable e) {
            exitCode = 1;
            ex = e;

            LOG.error("\n\n经DataX智能分析,该任务最可能的错误原因是:\n" + ExceptionTracker.trace(e));

            if (e instanceof DataXException) {
                DataXException tempException = (DataXException) e;
                ErrorCode errorCode = tempException.getErrorCode();
                if (errorCode instanceof FrameworkErrorCode) {
                    FrameworkErrorCode tempErrorCode = (FrameworkErrorCode) errorCode;
                    exitCode = tempErrorCode.toExitValue();
                }
            }

        } finally {
            //report job info to datax server
            Communication communication = null;
            if (Engine.engine.jobContainer != null) {
                communication = Engine.engine.jobContainer.getContainerCommunicator().collect();
            }

            if (communication == null) {
                communication = new Communication();
            }
            communication.setState(State.FAILED, true);

            if (ex instanceof DataXException) {
                DataXException dataXException = (DataXException) ex;
                if (dataXException.getErrorCode() == FrameworkErrorCode.KILLED_EXIT_VALUE) {
                    communication.setState(State.KILLED, true);
                }
            }
            Engine.engine.applicationManager.reportJob(Engine.engine.jobId, communication, ex);

            //close application manager
            Engine.engine.applicationManager.shutdown();

            System.exit(exitCode);
        }

    }

    public JobContainer getJobContainer() {
        return jobContainer;
    }

    /* check job model (job/task) first */
    public void start(Configuration allConf) {

        // 绑定column转换信息
        ColumnCast.bind(allConf);

        /**
         * 初始化PluginLoader，可以获取各种插件配置
         */
        LoadUtil.bind(allConf);

        boolean isJob = !("taskGroup".equalsIgnoreCase(allConf
            .getString(CoreConstant.DATAX_CORE_CONTAINER_MODEL)));
        //JobContainer会在schedule后再行进行设置和调整值
        int channelNumber = 0;
        AbstractContainer container;
        long instanceId;
        int taskGroupId = -1;
        if (isJob) {
            allConf.set(CoreConstant.DATAX_CORE_CONTAINER_JOB_MODE, RUNTIME_MODE);
            container = this.jobContainer = new JobContainer(allConf);
            instanceId = allConf.getLong(
                CoreConstant.DATAX_CORE_CONTAINER_JOB_ID, 0);

        } else {
            container = new TaskGroupContainer(allConf);
            instanceId = allConf.getLong(
                CoreConstant.DATAX_CORE_CONTAINER_JOB_ID);
            taskGroupId = allConf.getInt(
                CoreConstant.DATAX_CORE_CONTAINER_TASKGROUP_ID);
            channelNumber = allConf.getInt(
                CoreConstant.DATAX_CORE_CONTAINER_TASKGROUP_CHANNEL);
        }

        //缺省打开perfTrace
        boolean traceEnable = allConf.getBool(CoreConstant.DATAX_CORE_CONTAINER_TRACE_ENABLE, true);
        boolean perfReportEnable = allConf.getBool(CoreConstant.DATAX_CORE_REPORT_DATAX_PERFLOG, true);

        //standlone模式的datax shell任务不进行汇报
        if (instanceId == -1) {
            perfReportEnable = false;
        }

        int priority = 0;
        try {
            priority = Integer.parseInt(System.getenv("SKYNET_PRIORITY"));
        } catch (NumberFormatException e) {
            LOG.warn("prioriy set to 0, because NumberFormatException, the value is: " + System.getProperty("PROIORY"));
        }

        Configuration jobInfoConfig = allConf.getConfiguration(CoreConstant.DATAX_JOB_JOBINFO);
        //初始化PerfTrace
        PerfTrace perfTrace = PerfTrace.getInstance(isJob, instanceId, taskGroupId, priority, traceEnable);
        perfTrace.setJobInfo(jobInfoConfig, perfReportEnable, channelNumber);
        container.start();

    }

}
