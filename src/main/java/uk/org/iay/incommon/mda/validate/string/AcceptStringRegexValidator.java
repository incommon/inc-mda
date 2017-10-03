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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.validate.BaseValidator;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;

/**
 * A <code>Validator</code> that accepts {@link String} values matching a regular expression.
 *
 * This validator returns {@link net.shibboleth.metadata.validate.Validator.Action#DONE}
 * if the entire value is matched by the regular expression, thus terminating any validator sequence.
 */
public class AcceptStringRegexValidator extends BaseValidator implements Validator<String> {

    /** Regular expression to be accepted by this validator. */
    @NonnullAfterInit
    private String regex;

    /** Compiled regular expression to use in match operations. */
    @NonnullAfterInit
    private Pattern pattern;

    /**
     * Returns the regular expression.
     *
     * @return Returns the regular expression.
     */
    @NonnullAfterInit
    public String getRegex() {
        return regex;
    }

    /**
     * Sets the regular expression to be accepted.
     *
     * @param r the regular expression to set.
     */
    public void setRegex(@Nonnull final String r) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        regex = r;
    }

    @Override
    public Action validate(@Nonnull final String e, @Nonnull final Item<?> item, @Nonnull final String stageId) {
        final Matcher matcher = pattern.matcher(e);
        if (matcher.matches()) {
            return Action.DONE;
        } else {
            return Action.CONTINUE;
        }
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (getRegex() == null) {
            throw new ComponentInitializationException("regular expression to be matched can not be null");
        }

        pattern = Pattern.compile(regex);
    }
}
