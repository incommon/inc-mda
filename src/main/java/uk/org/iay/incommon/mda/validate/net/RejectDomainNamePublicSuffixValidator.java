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

package uk.org.iay.incommon.mda.validate.net;

import javax.annotation.Nonnull;

import com.google.common.net.InternetDomainName;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.validate.BaseValidator;
import net.shibboleth.metadata.validate.Validator;

/**
 * A validator that rejects an {@link InternetDomainName} if it is a public suffix.
 */
public class RejectDomainNamePublicSuffixValidator extends BaseValidator
    implements Validator<InternetDomainName> {

    @Override
    public Action validate(@Nonnull final InternetDomainName domain, @Nonnull final Item<?> item,
            @Nonnull final String stageId) {
        if (domain.isPublicSuffix()) {
            addErrorMessage(domain, item, stageId);
            return Action.DONE;
        } else {
            return Action.CONTINUE;
        }
    }

}
