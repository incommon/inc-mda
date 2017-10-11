/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.iay.incommon.mda.dom.saml.shib;

import java.util.List;

import javax.annotation.Nonnull;

import org.w3c.dom.Element;

import net.shibboleth.metadata.dom.AbstractDOMValidationStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.AttributeSupport;
import net.shibboleth.utilities.java.support.xml.ElementSupport;
import uk.org.iay.incommon.mda.validate.ValidatorSequence;

/**
 * Stage to apply a collection of validators to Shibboleth <code>shibmd:Scope</code>
 * values.
 *
 * A separate collection of validators is used for the case of the <code>regexp</code>
 * attribute being <code>true</code> and <code>false</code>.
 */
public class ScopeValidationStage extends AbstractDOMValidationStage<String> {

    /** The sequence of validators to apply to <code>regexp</code> scopes. */
    @Nonnull
    private ValidatorSequence<String> regexpValidators = new ValidatorSequence<>();

    /**
     * Set the sequence of validators to apply to each <code>regexp</code> scope.
     * 
     * @param newValidators the list of validators to set
     */
    public void setRegexpValidators(@Nonnull final List<Validator<String>> newValidators) {
        regexpValidators.setValidators(newValidators);
    }

    /**
     * Gets the sequence of validators being applied to each <code>regexp</code> scope.
     * 
     * @return list of validators
     */
    @Nonnull
    public List<Validator<String>> getRegexpValidators() {
        return regexpValidators.getValidators();
    }

    @Override
    protected boolean applicable(@Nonnull final Element element) {
        return ElementSupport.isElementNamed(element, ShibbolethMetadataSupport.SCOPE_NAME);
    }

    @Override
    protected void visit(@Nonnull final Element element, @Nonnull final TraversalContext context)
            throws StageProcessingException {
        final String text = element.getTextContent();
        final Boolean isRegexp = AttributeSupport.getAttributeValueAsBoolean(
                AttributeSupport.getAttribute(element, ShibbolethMetadataSupport.REGEXP_ATTRIB_NAME));
        if (isRegexp == null || !isRegexp.booleanValue()) {
            // non-regexp Scope, apply normal validators
            applyValidators(text, context);
        } else {
            // regexp Scope, apply secondary validators
            regexpValidators.validate(text, context.getItem(), getId());
        }
    }

    @Override
    protected void doDestroy() {
        regexpValidators.destroy();
        regexpValidators = null;
        super.doDestroy();
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        regexpValidators.setId(getId());
        regexpValidators.initialize();
    }

}
