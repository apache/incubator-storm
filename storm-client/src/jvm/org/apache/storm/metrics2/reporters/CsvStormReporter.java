/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.apache.storm.metrics2.reporters;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.storm.daemon.metrics.ClientMetricsUtils;
import org.apache.storm.metrics2.filters.StormMetricsFilter;
import org.apache.storm.utils.ConfigUtils;
import org.apache.storm.utils.ObjectReader;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvStormReporter extends ScheduledStormReporter {
    public static final String CSV_LOG_DIR = "csv.log.dir";
    private static final Logger LOG = LoggerFactory.getLogger(CsvStormReporter.class);

    private static Path getCsvLogDir(Map stormConf, Map reporterConf) {
        String csvMetricsLogDirectory = ObjectReader.getString(reporterConf.get(CSV_LOG_DIR), null);
        Path csvMetricsDir = null;
        if (csvMetricsLogDirectory == null) {
            csvMetricsDir = ConfigUtils.absoluteStormLocalDir(stormConf).resolve("csvmetrics");
        } else {
            csvMetricsDir = Paths.get(csvMetricsLogDirectory);
        }
        validateCreateOutputDir(csvMetricsDir);
        return csvMetricsDir;
    }

    private static void validateCreateOutputDir(Path dir){
        if (!dir.toFile().exists()) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw Utils.wrapInRuntime(e);
            }
        }
        if (!Files.isWritable(dir)) {
            throw new IllegalStateException(dir.getFileName() + " does not have write permissions.");
        }
        if (!Files.isDirectory(dir)) {
            throw new IllegalStateException(dir.getFileName() + " is not a directory.");
        }
    }

    @Override
    public void prepare(MetricRegistry metricsRegistry, Map stormConf, Map reporterConf) {
        LOG.debug("Preparing...");
        CsvReporter.Builder builder = CsvReporter.forRegistry(metricsRegistry);

        Locale locale = ClientMetricsUtils.getMetricsReporterLocale(reporterConf);
        if (locale != null) {
            builder.formatFor(locale);
        }

        TimeUnit rateUnit = ClientMetricsUtils.getMetricsRateUnit(reporterConf);
        if (rateUnit != null) {
            builder.convertRatesTo(rateUnit);
        }

        TimeUnit durationUnit = ClientMetricsUtils.getMetricsDurationUnit(reporterConf);
        if (durationUnit != null) {
            builder.convertDurationsTo(durationUnit);
        }

        StormMetricsFilter filter = getMetricsFilter(reporterConf);
        if (filter != null) {
            builder.filter(filter);
        }

        //defaults to 10
        reportingPeriod = getReportPeriod(reporterConf);

        //defaults to seconds
        reportingPeriodUnit = getReportPeriodUnit(reporterConf);

        Path csvMetricsDir = getCsvLogDir(stormConf, reporterConf);
        reporter = builder.build(csvMetricsDir.toFile());
    }
}