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

package uk.org.iay.incommon.mda.validate;

import javax.annotation.Nonnull;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.validate.BaseValidator;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * A {@link Validator} which rejects any value, returning
 * {@link net.shibboleth.metadata.validate.Validator.Action#DONE}
 * to terminate any validator sequence.
 *
 * An error status is added using the value of the given property as a format string.
 * The message defaults to a simple rejection message including the object's string value.
 */
public class RejectAllValidator extends BaseValidator implements Validator<Object> {

    /**
     * Message format string.
     *
     * The generated message is formatted using this with the object being validated passed
     * as an argument.
     *
     * Defaults to <code>"value rejected: '%s'"</code>.
     */
    @Nonnull
    private String message = "value rejected: '%s'";

    /**
     * Returns the message format string.
     *
     * @return the message format string
     */
    @Nonnull
    public String getMessage() {
        return message;
    }

    /**
     * Set the message format string.
     * 
     * @param newMessage the new message format string
     */
    public void setMessage(@Nonnull final String newMessage) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        message = Constraint.isNotNull(newMessage, "message format string may not be null");
    }

    @Override
    public Action validate(@Nonnull final Object e, @Nonnull final Item<?> item, @Nonnull final String stageId) {
        final String mess = String.format(message, e);
        addError(mess, item, stageId);
        return Action.DONE;
    }

}
