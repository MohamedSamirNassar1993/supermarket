package com.supermarket.modules.barcode.application;

import com.supermarket.modules.barcode.domain.LabelTemplate;
import com.supermarket.modules.barcode.infrastructure.persistence.LabelTemplateJpaRepository;
import com.supermarket.shared.audit.AuditAction;
import com.supermarket.shared.audit.Audited;
import com.supermarket.shared.multibranch.BranchContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BarcodeLabelService {

    private final LabelTemplateJpaRepository labelTemplateRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public List<LabelTemplate> listTemplates(UUID organizationId) {
        return labelTemplateRepository.findByOrganizationIdAndActiveTrue(organizationId);
    }

    @Transactional
    @Audited(entityType = "LabelTemplate", action = AuditAction.CREATE)
    public LabelTemplate createTemplate(LabelTemplate template) {
        BranchContext.getOrganizationId().ifPresent(template::setOrganizationId);
        if (template.getTemplateData() == null) {
            template.setTemplateData(new HashMap<>());
        }
        return labelTemplateRepository.save(template);
    }

    @Transactional(readOnly = true)
    public LabelTemplate getTemplate(UUID id) {
        return labelTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label template not found: " + id));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generateProductLabel(UUID productId, UUID templateId) {
        LabelTemplate template = getTemplate(templateId);
        Map<String, Object> product = jdbcTemplate.query(
                """
                        SELECT p.id, p.sku, p.name, pb.barcode
                        FROM products p
                        LEFT JOIN product_barcodes pb ON pb.product_id = p.id AND pb.is_primary = TRUE
                        WHERE p.id = ?
                        LIMIT 1
                        """,
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }
                    Map<String, Object> row = new HashMap<>();
                    row.put("productId", rs.getObject("id"));
                    row.put("sku", rs.getString("sku"));
                    row.put("name", rs.getString("name"));
                    row.put("barcode", rs.getString("barcode"));
                    return row;
                },
                productId);

        if (product == null) {
            throw new EntityNotFoundException("Product not found: " + productId);
        }

        Map<String, Object> label = new HashMap<>();
        label.put("templateId", template.getId());
        label.put("templateCode", template.getCode());
        label.put("widthMm", template.getWidthMm());
        label.put("heightMm", template.getHeightMm());
        label.put("labelType", template.getLabelType());
        label.put("product", product);
        label.put("layout", template.getTemplateData());
        return label;
    }
}
