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

package uk.org.iay.incommon.mda.validate.string;

import javax.annotation.Nonnull;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.validate.BaseValidator;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;

/**
 * A <code>Validator</code> that accepts a fixed {@link String} value.
 *
 * This validator returns {@link net.shibboleth.metadata.validate.Validator.Action#DONE}
 * if the value is matched, thus terminating any validator sequence.
 */
public class AcceptStringValueValidator extends BaseValidator implements Validator<String> {

    /** Value to be accepted by this validator. */
    @NonnullAfterInit
    private String value;

    /**
     * Returns the value.
     *
     * @return Returns the value.
     */
    @NonnullAfterInit
    public String getValue() {
        return value;
    }

    /**
     * Sets the value to be accepted.
     *
     * @param v the value to set.
     */
    public void setValue(@Nonnull final String v) {
        value = v;
    }

    @Override
    public Action validate(@Nonnull final String e, @Nonnull final Item<?> item, @Nonnull final String stageId) {
        if (e.equals(value)) {
            return Action.DONE;
        } else {
            return Action.CONTINUE;
        }
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (getValue() == null) {
            throw new ComponentInitializationException("value to be matched can not be null");
        }
    }
}
