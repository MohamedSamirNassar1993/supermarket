package com.supermarket.modules.reports.application;

import com.supermarket.modules.reports.domain.ReportJob;
import com.supermarket.modules.reports.infrastructure.persistence.ReportJobJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportGeneratorService {

    private final ReportJobJpaRepository reportJobRepository;

    @Transactional
    public void processJob(UUID jobId) {
        ReportJob job = reportJobRepository.findById(jobId).orElse(null);
        if (job == null) {
            log.warn("Report job not found: {}", jobId);
            return;
        }
        job.setStatus("PROCESSING");
        job.setStartedAt(Instant.now());
        reportJobRepository.save(job);

        try {
            Path outputDir = Path.of(System.getProperty("java.io.tmpdir"), "supermarket-reports");
            Files.createDirectories(outputDir);
            Path outputFile = "XLSX".equalsIgnoreCase(job.getFormat())
                    ? generateExcel(job, outputDir)
                    : generatePdf(job, outputDir);
            job.setFilePath(outputFile.toString());
            job.setFileSizeBytes(Files.size(outputFile));
            job.setStatus("COMPLETED");
            job.setCompletedAt(Instant.now());
        } catch (Exception ex) {
            log.error("Report generation failed for job {}", job.getId(), ex);
            job.setStatus("FAILED");
            job.setErrorMessage(ex.getMessage());
            job.setCompletedAt(Instant.now());
        }
        reportJobRepository.save(job);
    }

    private Path generatePdf(ReportJob job, Path outputDir) throws Exception {
        String jrxml = """
                <jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports"
                    name="report" pageWidth="595" pageHeight="842">
                  <title><band height="50"><staticText>
                    <reportElement x="0" y="0" width="555" height="30"/>
                    <text><![CDATA[Supermarket ERP Report]]></text>
                  </staticText></band></title>
                  <detail><band height="20"/></detail>
                </jasperReport>
                """;
        JasperReport report = JasperCompileManager.compileReport(
                new java.io.ByteArrayInputStream(jrxml.getBytes()));
        Map<String, Object> params = job.getParameters() != null ? job.getParameters() : Map.of();
        params.put("reportType", job.getReportType());
        JasperPrint print = JasperFillManager.fillReport(report, params, new JREmptyDataSource());
        Path file = outputDir.resolve(job.getId() + ".pdf");
        JasperExportManager.exportReportToPdfFile(print, file.toString());
        return file;
    }

    private Path generateExcel(ReportJob job, Path outputDir) throws Exception {
        Path file = outputDir.resolve(job.getId() + ".xlsx");
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(job.getReportType());
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Report Type");
            header.createCell(1).setCellValue(job.getReportType());
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("Generated At");
            row.createCell(1).setCellValue(Instant.now().toString());
            workbook.write(out);
            Files.write(file, out.toByteArray());
        }
        return file;
    }
}
