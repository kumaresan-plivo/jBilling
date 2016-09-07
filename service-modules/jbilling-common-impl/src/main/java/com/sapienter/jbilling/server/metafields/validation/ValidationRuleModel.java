package com.sapienter.jbilling.server.metafields.validation;

import com.sapienter.jbilling.server.metafields.MetaContent;
import com.sapienter.jbilling.server.metafields.db.ValidationRule;

import java.util.List;

/**
 *  Defines models for validation rules;
 *  Performs validation on entities
 *
 *  @author Panche Isajeski
 */
public interface ValidationRuleModel<T> {

    public List<MetaFieldAttributeDefinition> getAttributeDefinitions();

    public ValidationReport doValidation(MetaContent source, T object, ValidationRule validationRule, Integer languageId);

}
